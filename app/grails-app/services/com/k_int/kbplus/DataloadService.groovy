package com.k_int.kbplus

import com.k_int.kbplus.*
import org.hibernate.ScrollMode
import java.nio.charset.Charset

class DataloadService {

  def stats = [:]

  def update_stages = [
    'Organisations Data',
    'Subscriptions Offered Data',
    'Subscriptions Taken Data',
    'License Data'
  ]

  def executorService
  def ESWrapperService
  def mongoService
  def sessionFactory
  def edinaPublicationsAPIService
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP 

  def dataload_running=false
  def dataload_stage=-1
  def dataload_message=''

  def updateFTIndexes() {
    log.debug("updateFTIndexes");
    def future = executorService.submit({
      doFTUpdate()
    } as java.util.concurrent.Callable)
    log.debug("updateFTIndexes returning");
  }

  def doFTUpdate() {
    log.debug("doFTUpdate");
    
    log.debug("Execute IndexUpdateJob starting at ${new Date()}");
    def start_time = System.currentTimeMillis();

    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    updateES(esclient, com.k_int.kbplus.Org.class) { org ->
      def result = [:]
      result._id = org.impId
      result.name = org.name
      result.sector = org.sector
      result.dbId = org.id
      result.visible = ['Public']
      result.rectype = 'Organisation'
      result
    }

    updateES(esclient, com.k_int.kbplus.TitleInstance.class) { ti ->
      def result = [:]
      result._id = ti.impId
      result.title = ti.title
      result.dbId = ti.id
      result.visible = ['Public']
      result.rectype = 'Title'
      result.identifiers = []
      ti.ids?.each { id ->
        result.identifiers.add([type:id.identifier.ns.ns, value:id.identifier.value])
      }

      result
    }

    updateES(esclient, com.k_int.kbplus.Package.class) { pkg ->
      def result = [:]
      result._id = pkg.impId
      result.name = "${pkg.name} (${pkg.contentProvider?.name})"
      result.dbId = pkg.id
      result.visible = ['Public']
      result.rectype = 'Package'
      result
    }

    updateES(esclient, com.k_int.kbplus.Platform.class) { plat ->
      def result = [:]
      result._id = plat.impId
      result.name = plat.name
      result.dbId = plat.id
      result.visible = ['Public']
      result.rectype = 'Platform'
      result
    }

    updateES(esclient, com.k_int.kbplus.Subscription.class) { sub ->
      def result = [:]
      result._id = sub.impId
      result.name = sub.name
      result.identifier = sub.identifier
      result.dbId = sub.id
      result.visible = ['Public']
      if ( sub.subscriber ) {
        result.visible.add(sub.subscriber.shortcode)
      }
      result.type = sub.type?.value
      result.rectype = 'Subscription'
      result
    }


    def elapsed = System.currentTimeMillis() - start_time;
    log.debug("IndexUpdateJob completed in ${elapsed}ms at ${new Date()}");
  }

  def updateES(esclient, domain, recgen_closure) {

    try {
      log.debug("updateES - ${domain.name}");

      def latest_ft_record = FTControl.findByDomainClassNameAndActivity(domain.name,'ESIndex')

      log.debug("result of findByDomain: ${latest_ft_record}");
      if ( !latest_ft_record) {
        latest_ft_record=new FTControl(domainClassName:domain.name,activity:'ESIndex',lastTimestamp:0)
      }

      log.debug("updateES ${domain.name} since ${latest_ft_record.lastTimestamp}");
      def count = 0;
      def total = 0;
      Date from = new Date(latest_ft_record.lastTimestamp);
      // def qry = domain.findAllByLastUpdatedGreaterThan(from,[sort:'lastUpdated']);

      def c = domain.createCriteria()
      c.setReadOnly(true)
      c.setCacheable(false)
      c.setFetchSize(Integer.MIN_VALUE);

      c.buildCriteria{
          gt('lastUpdated', from)
          order("lastUpdated", "asc")
      }

      def results = c.scroll(ScrollMode.FORWARD_ONLY)
    
      log.debug("Query completed.. processing rows...");

      while (results.next()) {
        Object r = results.get(0);
        def idx_record = recgen_closure(r)

        def future = esclient.index {
          index "kbplus"
          type domain.name
          id idx_record['_id']
          source idx_record
        }

        latest_ft_record.lastTimestamp = r.lastUpdated?.getTime()

        count++
        total++
        if ( count > 100 ) {
          count = 0;
          log.debug("processed ${++total} records");
          latest_ft_record.save(flush:true);
          cleanUpGorm();
        }
      }
      results.close();

      println("Processed ${total} records for ${domain.name}");

      // update timestamp
      latest_ft_record.save(flush:true);
    }
    catch ( Exception e ) {
      log.error("Problem with FT index",e);
    }
    finally {
      log.debug("Completed processing on ${domain.name} - saved ${count} records");
    }
  }

  def getReconStatus() {
    
    def result = [
      active:dataload_running,
      stage:dataload_stage,
      stats:stats
    ]

    result
  }

  def synchronized requestReconciliation() {
    if ( !dataload_running ) {
      dataload_running = true;
      Thread.start {
        doReconciliation();
      }
    }
  }

  def doReconciliation() {
    try {
      stats.overallStatus="Runnin"
      stats.startTime=System.currentTimeMillis();
      // Do it!
      update();
      reloadSTData();
      reloadLicenses();
    }
    catch ( Exception e ) {
      log.error(e);
      stats.lastException=e.message()
    }
    finally {
      dataload_running = false;
      stats.overallStatis="Complete"
      stats.elapsed=System.currentTimeMillis() -  stats.startTime;
    }
  }

  def update() {

    System.out.println("file.encoding=" + System.getProperty("file.encoding"));
    System.out.println("Default Charset=" + Charset.defaultCharset());

    Org.withTransaction { transaction_status ->
      log.debug("DataloadController::update");
  
      def cp_role = lookupOrCreateRefdataEntry('Organisational Role', 'Content Provider');
      def pub_role = lookupOrCreateRefdataEntry('Organisational Role', 'Publisher');
      def sc_role = lookupOrCreateRefdataEntry('Organisational Role', 'Subscription Consortia');
      def live_issue_entitlement = lookupOrCreateRefdataEntry('Entitlement Issue Status', 'Live');
      def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
  
      mdb.tipps.ensureIndex('lastmod');
  
      dataload_stage=0
  
      // Orgs - Fail fast by trying cursor options here
      def orgs_cursor = mdb.orgs.find().sort(lastmod:1)
      orgs_cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      stats.org_seen_count = 0;
      stats.org_insert_count = 0;
      orgs_cursor.each { org ->
        stats.org_seen_count++;
        log.debug("update org ${org}");
        
        def o = Org.findByImpId(org._id.toString())
        if ( o == null ) {
          o = Org.findByName(org.name.trim())
          if ( o == null ) {
            o=new Org(impId:org._id.toString(), 
                      name:org.name.trim(),
                      ipRange:org.ipRange,
                      sector:org.sectorName,
                      scope:org.scope,
                      links:[]);

            log.debug("New org ${org.name} trimmed to ${org.name.trim()} bytes are ${org.name.getBytes()}");

            o.ids=[]
          }
        }
  
        if ( ( org.ringoldId ) && ( org.ringoldId.trim().length() > 0 ) && ( org.ringoldId != 'NULL' ) ) {
          def ringold_id = lookupOrCreateCanonicalIdentifier('Ringold',org.ringoldId);
          o.ids.add(new IdentifierOccurrence(identifier:ringold_id,org:o));
        }
  
        if ( ( org.ingentaId ) && ( org.ingentaId.trim().length() > 0 ) && ( org.ingentaId != 'NULL' ) ) {
          def ingenta_id = lookupOrCreateCanonicalIdentifier('Ingenta',org.ingentaId);
          o.ids.add(new IdentifierOccurrence(identifier:ingenta_id,org:o));
        }
  
        if ( ( org.jcId ) && ( org.jcId.trim().length() > 0 ) && ( org.jcId != 'NULL' ) ) {
          def jc_id = lookupOrCreateCanonicalIdentifier('JC',org.jcId);
          o.ids.add(new IdentifierOccurrence(identifier:jc_id,org:o));
        }
  
        if ( ( org.athensId ) && ( org.athensId.trim().length() > 0 ) && ( org.athensId != 'NULL' ) ) {
          def athens_id = lookupOrCreateCanonicalIdentifier('Athens',org.athensId);
          o.ids.add(new IdentifierOccurrence(identifier:athens_id,org:o));
        }
  
        if ( ( org.famId ) && ( org.famId.trim().length() > 0 ) && ( org.famId != 'NULL' ) ) {
          def amf_id = lookupOrCreateCanonicalIdentifier('UKAMF',org.famId);
          o.ids.add(new IdentifierOccurrence(identifier:amf_id,org:o));
        }

        if ( o.save(flush:true) ) {
        }
        else {
          log.error("Problem saving org...");
          o.errors.each { oe -> 
            log.error(oe);
          }
        }
        stats.org_insert_count++;
  
        // Guessing this is no longer needed.
        // Create a combo to link this org with NESLI2 (So long as this isn't the NESLI2 record itself of course
        // if ( org.name != 'NESLI2' ) {
        //   o = Org.findByName(org.name.trim());
        //   def cons_org = Org.findByName('NESLI2') ?: new Org(name:'NESLI2', links:[]).save();
        //   if ( cons_org ) {
        //     def new_combo = new Combo(type:lookupOrCreateRefdataEntry('Combo Type','Consortium'),
        //                               fromOrg:o,
        //                               toOrg:cons_org).save(flush:true);
        //   }
        // }

        org.consortia?.each {  cm ->
          o = Org.findByName(org.name.trim());
          def cons_org = Org.findByImpId(cm.toString())
          if ( o && cons_org ) {
            def new_combo = new Combo(type:lookupOrCreateRefdataEntry('Combo Type','Consortium'),
                                      fromOrg:o,
                                      toOrg:cons_org).save(flush:true);
          }

        }
  
      }
      orgs_cursor.close();
    
      log.debug("stats after org import: ${stats}");
  
      dataload_stage=1
      cleanUpGorm()
  
      // Subscriptions
      mdb.subscriptions.find().sort(lastmod:1).each { sub ->
        log.debug("Adding subscription ${sub.name} (${sub.identifier}) (cons:${sub.consortium})");
        def dbsub = Subscription.findByImpId(sub._id.toString()) ?: new Subscription(name:sub.name, 
                                                                                     status:lookupOrCreateRefdataEntry('Subscription Status','Current'),
                                                                                     identifier:sub.identifier,
                                                                                     impId:sub._id.toString(),
                                                                                     startDate:sub.start_date,
                                                                                     endDate:sub.end_date,
                                                                                     type: RefdataValue.findByValue('Subscription Offered')).save(flush:true);
  
        if ( sub.consortium ) {
          def cons = Org.findByImpId(sub.consortium.toString());
          if ( cons ) {
            def or = new OrgRole(org: cons, sub:dbsub, roleType:sc_role).save();
          }
          else {
            log.error("Trying to make consortia link to non-existent org: ${sub.consortium?.toString()}");
          }
        }
      }
  
      cleanUpGorm()

      // Platforms
      mdb.platforms.find().sort(lastmod:1).each { plat ->
        log.debug("update platform ${plat}");
        def p = Platform.findByImpId(plat._id.toString()) ?: new Platform(name:plat.name, 
                                                                          normname:plat.normname,
                                                                          primaryUrl:plat.primaryUrl,
                                                                          provenance:plat.provenance,
                                                                          impId:plat._id.toString()).save()
      }
  
      cleanUpGorm()

      int tcount = 0;

      // Title instances
      def titles_cursor = mdb.titles.find().sort(lastmod:1)
      titles_cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      titles_cursor.each { title ->
        log.debug("update title ${title}");
        def t = TitleInstance.findByImpId(title._id.toString())
        if ( t == null ) {
          t = new TitleInstance(title:title.title, impId:title._id.toString(), ids:[])
          title.identifier?.each { id ->
            def canonical_identifier = lookupOrCreateCanonicalIdentifier(id.type,id.value);
            t.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, ti:t));
            // t.addToIds(new TitleSID(namespace:id.type,identifier:id.value));
          }
  
          if ( t.save() ) {
          }
          else {
            log.error("Problem saving title instance");
            t.errors.each { te ->
              log.error(te);
            }
          }
  
          // create a new OrgRole for this title that links it with the publisher org.
          if ( title.publisher ) {
            def publisher_org = Org.findByImpId(title.publisher.toString());
            if ( publisher_org ) {
              // log.debug("Assert publisher org link with ${publisher_org.name}");
              assertOrgTitleLink(publisher_org, t, pub_role);
            }
            else {
              log.error("Title referenced a publisher org that cannot be found!!!! ${title.publisher.toString()}");
            }
          }
        }

        if ( tcount++ == 100 ) {
          tcount=0
          cleanUpGorm();
        }
      }
  

      int pcount = 0;

      // Packages
      mdb.pkgs.find().sort(lastmod:1).each { pkg ->

        log.debug("process package ${pkg}");

        def p = Package.findByImpId(pkg._id.toString())
        if ( p == null ) {
          def pkg_type = lookupOrCreateRefdataEntry('PackageTypes',pkg.type);
          log.debug("New package: ${pkg.identifier}, ${pkg.name}, ${pkg_type}, ${pkg._id.toString()}, ${pkg.contentProvider.toString()}. Looking up org");
          def cp = pkg.contentProvider != null ? Org.findByImpId(pkg.contentProvider.toString()) : null;
          log.debug("Create new package..");
          p = new Package(identifier:pkg.identifier,
                          name:pkg.name,
                          type:pkg_type,
                          contentProvider:cp,
                          impId:pkg._id.toString());
          log.debug("Package created, save...");
          if ( p.save(flush:true) ) {
            log.debug("Package save completed fm=${Runtime.getRuntime().freeMemory()}")
            //log.debug("New package ${pkg.identifier} saved");
            if ( cp ) {
              assertOrgPackageLink(cp, p, cp_role);
            }
          }
          else {
            log.error("Problem saving new package ${pkg.identifier}");
            p.errors.each { pe ->
              log.error("Problem saving package: ${pe}");
            }
          }
        }
        else {
          // log.debug("got package ${pkg._id.toString()}");
        }
  
        log.debug("Processing subscriptions for this package");
        pkg.subs.each { sub ->
          log.debug("Processing subscription ${sub}");
          def dbsub = Subscription.findByImpId(sub.toString());
          def sp = SubscriptionPackage.findBySubscriptionAndPkg(dbsub, p) ?: new SubscriptionPackage(subscription:dbsub, pkg: p).save();
        }
  
        log.debug("Package processing completed");  
        if ( pcount++ == 100 ) {
          pcount=0
          cleanUpGorm();
        }
      }
 
      int tippcount = 0;
 
      // Finally... tipps
      def cursor = mdb.tipps.find().sort(lastmod:1)
      cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
      cursor.each { tipp ->
        try {
          // log.debug("update tipp ${tipp}");
          def title = TitleInstance.findByImpId(tipp.titleid.toString())
          def pkg = Package.findByImpId(tipp.pkgid.toString())
          def platform = Platform.findByImpId(tipp.platformid.toString())
  
          if ( title && pkg && platform ) {
      
            def dbtipp = TitleInstancePackagePlatform.findByPkgAndPlatformAndTitle(pkg,platform,title)
            if ( dbtipp == null ) {
              dbtipp = new TitleInstancePackagePlatform(pkg:pkg,
                                                        platform:platform,
                                                        title:title,
                                                        startDate:tipp.startDate,
                                                        startVolume:tipp.startVolume,
                                                        startIssue:tipp.startIssue,
                                                        endDate:tipp.endDate,
                                                        endVolume:tipp.endVolume,
                                                        endIssue:tipp.endIssue,
                                                        embargo:tipp.embargo,
                                                        coverageDepth:tipp.coverageDepth,
                                                        coverageNote:tipp.coverageNote,
                                                        hostPlatformURL:tipp.hostPlatformURL,
                                                        impId:tipp._id.toString(),
                                                        ids:[])
      
              if ( ! dbtipp.save() ) {
                log.error("ERROR Saving tipp");
                dbtipp.errors.each { err ->
                  log.error("  -> ${err}");
                }
              }
              else {
                log.debug("TIPP Save OK ${tipp._id}");
              }
      
              if ( tipp.identifiers ) {
                tipp.identifiers.each { tippid ->
                  // log.debug("lookup and add tippid ${tippid}");
                  def canonical_identifier = lookupOrCreateCanonicalIdentifier(tippid.type, tippid.value);
                  dbtipp.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, tipp:dbtipp));
                }
              }
              else {
                // log.debug("no tipp identifiers");
              }
      
              if ( ! dbtipp.save() ) {
                log.error("ERROR Saving tipp");
                dbtipp.errors.each { err ->
                  log.error("  -> ${err}");
                }
              }
              else {
                // log.debug("dbtipp updated");
                if ( tipp.additionalPlatformLinks ) {
                  // log.debug("additional platform links");
                  tipp.additionalPlatformLinks.each { apl ->
                    // log.debug("Additional platform link : ${apl}");
                    def admin_platform = Platform.findByImpId(apl.platformId.toString());
                    if ( admin_platform ) {
                      new PlatformTIPP(tipp:dbtipp,
                                       platform:admin_platform,
                                       rel:apl.role,
                                       titleUrl:apl.platformUrl).save();
                    }
                  }
                }
                else { 
                  log.debug("No additional platform links");
                }
  
                if ( tipp.ies ) {
                  log.debug("Issue Entitlements");
                  tipp.ies.each { ie ->
                    log.debug("Create new issue entitlement for tipp ${dbtipp.impId} sub imp id:${ie.toString()}");
                    def sub = Subscription.findByImpId(ie.toString());
                    IssueEntitlement dbie = IssueEntitlement.findBySubscriptionAndTipp(sub, dbtipp) ?: new IssueEntitlement(subscription:sub, 
                                                status: live_issue_entitlement,
                                                tipp: dbtipp,
                                                startDate:tipp.startDate,
                                                startVolume:tipp.startVolume,
                                                startIssue:tipp.startIssue,
                                                endDate:tipp.endDate,
                                                endVolume:tipp.endVolume,
                                                endIssue:tipp.endIssue,
                                                embargo:tipp.embargo,
                                                coverageDepth:tipp.coverageDepth,
                                                coverageNote:tipp.coverageNote
                                                ).save();
                  }
                }
                else { 
                  log.debug("No Issue Entitlements");
                }
              }
            }
            else {
              log.error("WARN: Located existing tipp.. proably shoudn't happen");
            }
          }
          else {
            log.error("WARN: Null title, package or platform for ${tipp}");
          }
        }
        catch ( Exception e ) {
          log.error("WARN: Problem loading tipp instance",e);
        }

        if ( tippcount++ == 100 ) {
          tippcount=0
          cleanUpGorm();
        }
      }
      cursor.close();
    }
  }

  def reloadSTData() {
    log.debug("reloadSTData()");
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    int subcount = 0
  

    Org.withTransaction { transaction_status ->
      // Orgs

      mdb.subs.find().sort(lastmod:1).each { sub ->

        def live_issue_entitlement = lookupOrCreateRefdataEntry('Entitlement Issue Status', 'Live');

        log.debug("load ST sub[${subcount++}] ${sub}");
  
        try {

          def sub_start_date = sub.stsy ?: db_sub.startDate
          def sub_end_date = sub.stey ?: db_sub.endDate
    
          // Join together the subscription and the organisation
          // Look up the SO that this ST is based on
          def db_sub = Subscription.findByImpId(sub.sub.toString());
          def db_org = Org.findByImpId(sub.org.toString())
    
          // log.debug("Create a link between ${db_org} and ${db_sub}");
    
          // create a new subscription - an instance of an actual org taking up a specific subscription
          def new_subscription = new Subscription(
                                        identifier: "${db_sub.identifier}:${sub.org.toString()}",
                                        status:lookupOrCreateRefdataEntry('Subscription Status','Current'),
                                        impId:sub._id.toString(),
                                        name: db_sub.name,
                                        startDate: sub_start_date,
                                        endDate: sub_end_date,
                                        instanceOf: db_sub,
                                        type: RefdataValue.findByValue('Subscription Taken') )
    
          if ( new_subscription.save() ) {
            // log.debug("New subscriptionT saved...");
            // Copy package links from SO to ST
            db_sub.packages.each { sopkg ->
              def new_package_link = new SubscriptionPackage(subscription:new_subscription, pkg:sopkg.pkg).save();
            }

            // assert an org-role
            def org_link = new OrgRole(org:db_org, 
                                       sub: new_subscription, 
                                       roleType: lookupOrCreateRefdataEntry('Organisational Role','Subscriber')).save();

            // Copy any links from SO
            db_sub.orgRelations.each { or ->
              if ( or.roleType?.value != 'Subscriber' ) {
                def new_or = new OrgRole(org: or.org, sub: new_subscription, roleType: or.roleType).save();
              }
            }
          }
          else {
            log.error("Problem saving new subscription, ${new_subscription.errors}");
          }
    
          new_subscription.save(flush:true);
  

          // List all actual st_title records, and diff that against the default from the ST file
          def sub_titles = mdb.stTitle.find(owner:sub._id)
    
          if ( !new_subscription.issueEntitlements ) {
            new_subscription.issueEntitlements = new java.util.TreeSet()
          }
  
          if ( sub_titles.size() == 0 ) {
            log.debug("No ST title data present, defaulting in from SO ${db_sub.id}");
            int count = 0;
            IssueEntitlement.findAllBySubscription(db_sub).each { ie ->
              log.debug("Adding default entitlement based on entitlement ${ie.id}. Target tipp is ${ie.tipp.id} new sub-imp-id is ${}");
              count++;
              def new_ie = new IssueEntitlement(status: ie.status,
                                                subscription: new_subscription,
                                                tipp: ie.tipp,
                                                startDate:ie.tipp.startDate,
                                                startVolume:ie.tipp.startVolume,
                                                startIssue:ie.tipp.startIssue,
                                                endDate:ie.tipp.endDate,
                                                endVolume:ie.tipp.endVolume,
                                                endIssue:ie.tipp.endIssue,
                                                embargo:ie.tipp.embargo,
                                                coverageDepth:ie.tipp.coverageDepth,
                                                coverageNote:ie.tipp.coverageNote,
                                                ieReason:'No ST specific data, defaulting from SO').save()
              // new_subscription.issueEntitlements.add(new_ie)
            }
            log.debug("Added ${count} issue entitlements from subscription ${db_sub.id}")
          }
          else {
            log.debug("ST title data present, processing");
            sub_titles.each { st ->
              if ( st.included_st in [ 'Y', 'y', '', ' ', null ] ) {
                log.debug("ST row for tipp ${st.tipp_id.toString()} is to be included");
                TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.findByImpId(st.tipp_id.toString())
                if ( tipp ) {
                  boolean is_core = false;
                  if ( st.core_title == 'y' || st.core_title == 'Y' )
                    is_core=true;
    
                  // log.debug("Adding new entitlement for looked up tipp ${tipp.id}, is_core=${is_core}")
  
                  def new_ie = new IssueEntitlement(status: live_issue_entitlement,
                                                    subscription: new_subscription,
                                                    tipp: tipp,
                                                    startDate: nvl(st.date_first_issue_subscribed, tipp.startDate),
                                                    startVolume:nvl(st.num_first_vol_subscribed,tipp.startVolume),
                                                    startIssue:nvl(st.num_first_issue_subscribed,tipp.startIssue),
                                                    endDate:nvl(st.date_last_issue_subscribed,tipp.endDate),
                                                    endVolume:nvl(st.num_last_vol_subscibed,tipp.endVolume),
                                                    endIssue:nvl(st.num_last_issue_subscribed,tipp.endIssue),
                                                    embargo:nvl(st.embargo,tipp.embargo),
                                                    coverageDepth:tipp.coverageDepth,
                                                    coverageNote:tipp.coverageNote,
                                                    coreTitle: is_core,
                                                    ieReason:"Subscription Taken data lists this titles incluson as \"${st.included_st}\"")
                  if ( !new_ie.save() ) {
                    new_ie.errors.each { e ->
                      log.error("Problem saving ie ${e}")
                    }
                  }
                  // new_subscription.issueEntitlements.add(new_ie)
                }
                else {
                  log.error("Unable to locate TIPP instance for ${st.tipp_id.toString()}");
                }
                // log.debug("Done st...")
              }
              else {
                log.debug("omit ${st}");
              }
            }
          }
  
          new_subscription.save(flush:true)
  
    
          // Iterate all issue entitlements that appear as a part of this SO
          //IssueEntitlement.findAllBySubscription(db_sub).each { ie ->
          //  log.debug("Determine if ${ie} should be copied forward into the actual ST data");
          //}
    
          log.debug("Done listing issue entitlements for ST:${db_sub.impId}");
        }
        catch ( Exception e ) {
          log.error("Problem",e)
          e.printStackTrace();
        }
        finally {
          log.debug("Completed sub processing for ${sub}")
        }

        cleanUpGorm();
      }
  
    } 
    log.debug("Processed ${subcount} subscriptions");
  }

  def reloadLicenses() {
    log.debug("reloadLicenses()");
    dataload_stage=3;

    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    int subcount = 0
  
    License.withTransaction { trans_status ->

      // Licenses
      mdb.license.find().sort(lastmod:1).each { lic ->
        log.debug("load ${lic}");
        def licensor_org = null;
        def licensee_org = null;
  
        if ( lic.licensor )
          licensor_org = Org.findByImpId(lic.licensor.toString())
  
        if ( lic.licensee )
          licensee_org = Org.findByImpId(lic.licensee.toString())
  
        def license_type = null;
        if ( lic.license_type ) {
          license_type=lookupOrCreateRefdataEntry('License Type',lic.license_type);
        }
        else {
          license_type=lookupOrCreateRefdataEntry('License Type','Unknown');
        }
  
        def license_status = null;
        if ( lic.license_status ) {
          license_status=lookupOrCreateRefdataEntry('License Status',lic.license_status);
        }
        else {
          license_status=lookupOrCreateRefdataEntry('License Status','Unknown');
        }
  
        // Try to look up a license by the license reference
        License l = License.findByReference(lic.license_reference)

        if ( !l ) {
          l = new License (reference:lic.license_reference,
                                   concurrentUsers:RefdataCategory.lookupOrCreate('Concurrent Access',lic.concurrent_users),
                                   remoteAccess:RefdataCategory.lookupOrCreate('YNO',lic.remote_access),
                                   walkinAccess:RefdataCategory.lookupOrCreate('YNO',lic.walkin_access),
                                   multisiteAccess:RefdataCategory.lookupOrCreate('YNO',lic.multisite_access),
                                   partnersAccess:RefdataCategory.lookupOrCreate('YNO',lic.partners_access),
                                   alumniAccess:RefdataCategory.lookupOrCreate('YNO',lic.alumni_access),
                                   ill:RefdataCategory.lookupOrCreate('YNO',lic.ill),
                                   coursepack:RefdataCategory.lookupOrCreate('YNO',lic.coursepack),
                                   vle:RefdataCategory.lookupOrCreate('YNO',lic.vle),
                                   enterprise:RefdataCategory.lookupOrCreate('YNO',lic.enterprise),
                                   pca:RefdataCategory.lookupOrCreate('YNO',lic.pca),
                                   noticePeriod:lic.notice_period,
                                   licenseUrl:lic.license_url,
                                   licensorRef:lic.licensor_ref,
                                   licenseeRef:lic.licensee_ref,
                                   licenseType:lic.license_type,
                                   licenseStatus:lic.license_status,
                                   lastmod:lic.lastmod,
                                   type:license_type,
                                   status:license_status);
        }
        else {
          log.debug("Updating existing license");
        }

        if ( l.save() ) {
        }
        else {
          l.errors.each { le ->
            log.error(le);
          }
        }
  
        if ( licensor_org )
          assertOrgLicenseLink(licensor_org, l, lookupOrCreateRefdataEntry('Organisational Role', 'Licensor'));
  
        if ( licensee_org )
          assertOrgLicenseLink(licensee_org, l, lookupOrCreateRefdataEntry('Organisational Role', 'Licensee'));
  
        lic.subscriptions?.each() { ls ->
          def sub = Subscription.findByImpId(ls.toString());
          log.debug("Process license subscription ${ls} - sub id=${sub?.id}");
          if ( sub ) {
            sub.owner = l
            sub.noticePeriod = lic.notice_period
            if ( sub.save() ) {
            }
            else {
              sub.errors.each { se ->
                log.error(se);
              }
            }
            log.debug("Updated license information");
          }
          else {
            log.error("**Possible Error** Unable to locate subscription with impid ${ls} in db");
          }
        }
  
        possibleNote(lic.concurrent_users_note,'concurrentUsers',l,'License Concurrent Usage Note');
        possibleNote(lic.remote_access_note,'remoteAccess',l,'License Remote Access Note');
        possibleNote(lic.walkin_access_note,'walkinAccess',l,'License Walkin Access Note');
        possibleNote(lic.multisite_access_note,'multisiteAccess',l,'License Multisite Access Note');
        possibleNote(lic.partners_access_note,'partnersAccess',l,'License Partner Access Note');
        possibleNote(lic.alumni_access_note,'alumniAccess',l,'License Alumni Access Note');
        possibleNote(lic.ill_note,'ill',l,'License ILL Note');
        possibleNote(lic.coursepack_note,'coursepack',l,'License Course Pack Note');
        possibleNote(lic.vle_note,'vle',l,'License VLE Note');
        possibleNote(lic.enterprise_note,'enterprise',l,'License Enterprise Note');
        possibleNote(lic.pca_note,'pca',l,'License PCA Note');
  
        lic.datafiles.each { attached_doc ->
          // Link to attached_doc[0] - Should contain remote uuid
          def doc_content = new Doc(contentType:1, 
                                    uuid: attached_doc[0], 
                                    filename: attached_doc[1],
                                    title: attached_doc[2],
                                    type:lookupOrCreateRefdataEntry('Document Type','License')).save()
          def doc_context = new DocContext(license:l,
                                           owner:doc_content,
                                           doctype:lookupOrCreateRefdataEntry('Document Type','License')).save();
        }
  
      }
    }
    log.debug("License processing complete");
  }

  def possibleNote(content, type, license, note_title) {
    if ( content && content.toString().length() > 0 ) {
      def doc_content = new Doc(content:content.toString(), 
                                title: "${type} note",
                                type: lookupOrCreateRefdataEntry('Doc Type','Note') ).save()
      def doc_context = new DocContext(license:license,
                                       domain:type,
                                       owner:doc_content,
                                       doctype:lookupOrCreateRefdataEntry('Document Type','Note')).save();
    }
  }

  def nvl(val,defval) {
    def result = defval
    if ( ( val ) && ( val.toString().trim().length() > 0 ) )
      result = val

    result
  }

  def lookupOrCreateCanonicalIdentifier(ns, value) {
    log.debug("lookupOrCreateCanonicalIdentifier(${ns},${value})");
    def namespace = IdentifierNamespace.findByNs(ns) ?: new IdentifierNamespace(ns:ns).save();
    Identifier.findByNsAndValue(namespace,value) ?: new Identifier(ns:namespace, value:value).save();
  }


  def lookupOrCreateRefdataEntry(refDataCategory, refDataCode) {
    def category = RefdataCategory.findByDesc(refDataCategory) ?: new RefdataCategory(desc:refDataCategory).save(flush:true)
    def result = RefdataValue.findByOwnerAndValue(category, refDataCode) ?: new RefdataValue(owner:category,value:refDataCode).save(flush:true)
    result;
  }

  def assertOrgTitleLink(porg, ptitle, prole) {
    // def link = OrgRole.findByTitleAndOrgAndRoleType(ptitle, porg, prole) ?: new OrgRole(title:ptitle, org:porg, roleType:prole).save();
    def link = OrgRole.find{ title==ptitle && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(title:ptitle, org:porg, roleType:prole)
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)

      porg.save();
    }
  }

  def assertOrgPackageLink(porg, ppkg, prole) {
    // def link = OrgRole.findByPkgAndOrgAndRoleType(pkg, org, role) ?: new OrgRole(pkg:pkg, org:org, roleType:role).save();
    log.debug("assertOrgPackageLink()");
    def link = OrgRole.find{ pkg==ppkg && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(pkg:ppkg, org:porg, roleType:prole);
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)
      porg.save();
    }
    log.debug("assertOrgPackageLink() complete");
  }

  def assertOrgLicenseLink(porg, plic, prole) {
    def link = OrgRole.find{ lic==plic && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(lic:plic, org:porg, roleType:prole);
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)
      porg.save();
    }

  }

  def handleChangesSince(db,
                         collname,
                         timestamp,
                         processingClosure) {

    def cursor = db."${collname}".find().sort(lastmod:1)
    cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    cursor.each { item ->
      def local_copy = db."${collname}_localcopy".findOne([_id:item._id])
      if ( local_copy ) {
        log.debug("Got local copy");
        if ( item.equals(local_copy.original) ) {
          log.debug("No change detected in source item since last processing");
        }
        else {
          log.debug("Record has changed... process");
        }
      }
      else {
        log.debug("No local copy found");
        def copy_item = [
          _id:item._id,
          original:item
        ]
        db."${collname}_localcopy".save(copy_item);
      }

      processingClosure(item)
    }
  }


  def dataCleanse() {
    log.debug("dataCleanse");
    def future = executorService.submit({
      doDataCleanse()
    } as java.util.concurrent.Callable)
    log.debug("dataCleanse returning");
  }

  def doDataCleanse() {
    log.debug("dataCleansing");
    // 1. Find all packages that do not have a nominal platform
    Package.findAllByNominalPlatformIsNull().each { p ->
      def platforms = [:]
      p.tipps.each{ tipp ->
        if ( !platforms.keySet().contains(tipp.platform.id) ) {
          platforms[tipp.platform.id] = [count:1, platform:tipp.platform]
        }
        else {
          platforms[tipp.platform.id].count++
        }
      }

      def selected_platform = null;
      def largest = 0;
      platforms.values().each { pl ->
        log.debug("Processing ${pl}");
        if ( pl['count'] > largest ) {
          selected_platform = pl['platform']
        }
      }

      log.debug("Nominal platform is ${selected_platform} for ${p.id}");
      p.nominalPlatform = selected_platform
      p.save(flush:true)
    }
  }

  def titleAugment() {
    // edinaPublicationsAPIService.lookup('Acta Crystallographica. Section F, Structural Biology and Crystallization Communications');
    def future = executorService.submit({
      doTitleAugment()
    } as java.util.concurrent.Callable)
    log.debug("titleAugment returning");
  }

  def doTitleAugment() {
    TitleInstance.findAll().each { ti ->
      if ( ti.getIdentifierValue('SUNCAT' ) == null ) {
        def lookupResult = edinaPublicationsAPIService.lookup(ti.title)
        if ( lookupResult ) {
          def record = lookupResult.records.record
          if ( record ) {
            boolean matched = false;
            def suncat_identifier = null;
            record.modsCollection.mods.identifier.each { id ->
              if ( id.text().equalsIgnoreCase(ti.getIdentifierValue('ISSN')) || id.text().equalsIgnoreCase(ti.getIdentifierValue('eISSN'))  ) {
                matched = true
              }

              if ( id.@type == 'suncat' ) {
                suncat_identifier = id.text();
              }
            }
            if ( matched && suncat_identifier ) {
              log.debug("set suncat identifier to ${suncat_identifier}");
              def canonical_identifier = Identifier.lookupOrCreateCanonicalIdentifier('SUNCAT',suncat_identifier);
              ti.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, ti:ti));
              ti.save(flush:true);
            }
            else {
              log.debug("No match for title ${ti.title}, ${ti.id}");
            }
          }
          else {
          }
        }
        else {
        }
        synchronized(this) {
          Thread.sleep(250);
        }
      }
    }
  }

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }
}
