package com.k_int.kbplus

import com.k_int.kbplus.*
import grails.plugins.springsecurity.Secured


// DEPRECATED!!!
class DataloadController {

  def mongoService
  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("Secured index action on dataload controller accessed");
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def update() {

    log.debug("DataloadController::update");

    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')

    def stats = [:]

    mdb.tipps.ensureIndex('lastmod');

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

      // Create a combo to link this org with NESLI2 (So long as this isn't the NESLI2 record itself of course
      if ( org.name != 'NESLI2' ) {
        o = Org.findByName(org.name.trim());
        def cons_org = Org.findByName('NESLI2') ?: new Org(name:'NESLI2', links:[]).save();
        if ( cons_org ) {
          def new_combo = new Combo(type:lookupOrCreateRefdataEntry('Combo Type','Consortium'),
                                    fromOrg:o,
                                    toOrg:cons_org).save(flush:true);
        }
      }

    }
    orgs_cursor.close();
  
    log.debug("stats after org import: ${stats}");

    // Subscriptions
    mdb.subscriptions.find().sort(lastmod:1).each { sub ->
      log.debug("Adding subscription ${sub.name} (${sub.identifier})");
      def dbsub = Subscription.findByImpId(sub._id.toString()) ?: new Subscription(name:sub.name, 
                                                                                   identifier:sub.identifier,
                                                                                   impId:sub._id.toString(),
                                                                                   startDate:sub.start_date,
                                                                                   endDate:sub.end_date,
                                                                                   type: RefdataValue.findByValue('Subscription Offered')).save(flush:true);

      if ( sub.consortia ) {
        def cons = Org.findByImpId(sub.consortia.toString());
        if ( cons ) {
          def sc_role = lookupOrCreateRefdataEntry('Organisational Role', 'Subscription Consortia');
          def or = new OrgRole(org: cons, sub:dbsub, roleType:sc_role).save();
        }
      }
    }

    // Platforms
    mdb.platforms.find().sort(lastmod:1).each { plat ->
      log.debug("update platform ${plat}");
      def p = Platform.findByImpId(plat._id.toString()) ?: new Platform(name:plat.name, 
                                                                        normname:plat.normname,
                                                                        primaryUrl:plat.primaryUrl,
                                                                        provenance:plat.provenance,
                                                                        impId:plat._id.toString()).save()
    }

    // Title instances
    mdb.titles.find().sort(lastmod:1).each { title ->
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
            log.debug("Assert publisher org link with ${publisher_org.name}");
            def pub_role = lookupOrCreateRefdataEntry('Organisational Role', 'Publisher');
            assertOrgTitleLink(publisher_org, t, pub_role);
          }
          else {
            log.error("Title referenced a publisher org that cannot be found!!!! ${title.publisher.toString()}");
          }
        }
      }
    }

    // Packages
    mdb.pkgs.find().sort(lastmod:1).each { pkg ->
      log.debug("update package ${pkg}");
      def p = Package.findByImpId(pkg._id.toString())
      if ( p == null ) {
        def pkg_type = null;
        if (pkg.type) {
          pkg_type = lookupOrCreateRefdataEntry('PackageTypes',pkg.type);
        }
        log.debug("New package: ${pkg.identifier}, ${pkg.name}, ${pkg_type}, ${pkg._id.toString()}, ${pkg.contentProvider.toString()}");
        def cp = pkg.contentProvider != null ? Org.findByImpId(pkg.contentProvider.toString()) : null;
        p = new Package(identifier:pkg.identifier,
                        name:pkg.name,
                        type:pkg_type,
                        contentProvider:cp,
                        impId:pkg._id.toString());

        if ( p.save(flush:true) ) {
          log.debug("New package ${pkg.identifier} saved");
          if ( cp ) {
            def cp_role = lookupOrCreateRefdataEntry('Organisational Role', 'Content Provider');
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
        log.debug("got package ${pkg._id.toString()}");
      }

      pkg.subs.each { sub ->
        log.debug("Processing subscription ${sub}");
        def dbsub = Subscription.findByImpId(sub.toString());
        def sp = SubscriptionPackage.findBySubscriptionAndPkg(dbsub, p) ?: new SubscriptionPackage(subscription:dbsub, pkg: p).save();
      }

    }

    // Finally... tipps
    def cursor = mdb.tipps.find().sort(lastmod:1)
    cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    cursor.each { tipp ->
      try {
        log.debug("update tipp ${tipp}");
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
                log.debug("lookup and add tippid ${tippid}");
                def canonical_identifier = lookupOrCreateCanonicalIdentifier(tippid.type, tippid.value);
                dbtipp.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, tipp:dbtipp));
              }
            }
            else {
              log.debug("no tipp identifiers");
            }
    
            if ( ! dbtipp.save() ) {
              log.error("ERROR Saving tipp");
              dbtipp.errors.each { err ->
                log.error("  -> ${err}");
              }
            }
            else {
              log.debug("dbtipp updated");
              if ( tipp.additionalPlatformLinks ) {
                log.debug("additional platform links");
                tipp.additionalPlatformLinks.each { apl ->
                  log.debug("Additional platform link : ${apl}");
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
                  log.debug("Checking issue entitlement ${ie.toString()}");
                  def sub = Subscription.findByImpId(ie.toString());
                  IssueEntitlement dbie = IssueEntitlement.findBySubscriptionAndTipp(sub, dbtipp) ?: new IssueEntitlement(subscription:sub, 
                                              status: RefdataValue.findByValue('UnknownEntitlement'),
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
        }
        else {
          log.error("Null title, package or platform for ${tipp}");
        }
      }
      catch ( Exception e ) {
        log.error("Problem loading tipp instance",e);
      }
    }
    cursor.close();

    redirect(controller:'home')
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reloadSTData() {
    log.debug("reloadSTData()");
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    int subcount = 0

    // Orgs
    mdb.subs.find().sort(lastmod:1).each { sub ->
      log.debug("load ST sub[${subcount++}] ${sub}");

      try {
  
        // Join together the subscription and the organisation
        def db_sub = Subscription.findByImpId(sub.sub.toString());
        def db_org = Org.findByImpId(sub.org.toString())
  
        log.debug("Create a link between ${db_org} and ${db_sub}");
  
        // create a new subscription - an instance of an actual org taking up a specific subscription
        def new_subscription = new Subscription(
                                      identifier: "${db_sub.identifier}:${sub.org.toString()}",
                                      impId:sub._id.toString(),
                                      name: db_sub.name,
                                      startDate: db_sub.startDate,
                                      endDate: db_sub.endDate,
                                      instanceOf: db_sub,
                                      type: RefdataValue.findByValue('Subscription Taken') )
  
        if ( new_subscription.save() ) {
          log.debug("New subscriptionT saved...");
        }
        else {
          log.error("Problem saving new subscription, ${new_subscription.errors}");
        }
  
        // assert an org-role
        def org_link = new OrgRole(org:db_org, 
                                   sub: new_subscription, 
                                   roleType: RefdataValue.findByValue('Subscriber'))
  

        new_subscription.save(flush:true);

        if ( org_link.save() ) {
          log.debug("New org link saved...");
        }
        else {
          log.error("Problem saving new org link, ${org_link.errors}");
        }
  
        // List all actual st_title records, and diff that against the default from the ST file
        def sub_titles = mdb.stTitle.find(owner:sub._id)
  
        if ( !new_subscription.issueEntitlements ) {
          new_subscription.issueEntitlements = []
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
                                              ieReason:'No ST specific data, defaulting from SO').save(flush:true)
            // new_subscription.issueEntitlements.add(new_ie)
          }
          log.debug("Added ${count} issue entitlements from subscription ${db_sub.id}")
        }
        else {
          log.debug("ST title data present, processing");
          sub_titles.each { st ->
            if ( st.included_st in [ 'Y', 'y', '', ' ', null ] ) {
              log.debug("${st} is to be included");
              TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.findByImpId(st.tipp_id.toString())
              if ( tipp ) {
                boolean is_core = false;
                if ( st.core_title == 'y' || st.core_title == 'Y' )
                  is_core=true;
  
                log.debug("Adding new entitlement for looked up tipp ${tipp.id}, is_core=${is_core}")

                def new_ie = new IssueEntitlement(status: RefdataValue.findByValue('UnknownEntitlement'),
                                                  subscription: new_subscription,
                                                  tipp: tipp,
                                                  startDate: nvl(st.date_first_issue_subscribed, tipp.startDate),
                                                  startVolume:nvl(st.num_first_vol_subscribed,tipp.startVolume),
                                                  startIssue:nvl(st.num_first_issue_subscribed,tipp.startIssue),
                                                  endDate:nvl(st.date_first_issue_subscribed,tipp.endDate),
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
              log.debug("Done st...")
            }
            else {
              log.debug("omit ${st}");
            }
          }
        }

        // new_subscription.save(flush:true)

  
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
    }

    log.debug("Processed ${subcount} subscriptions");
    redirect(controller:'home')
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reloadLicenses() {
    log.debug("reloadLicenses()");
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    int subcount = 0

    // Licenses
    mdb.license.find().sort(lastmod:1).each { lic ->
      log.debug("load ${lic}");
      def licensor_org = null;
      def licensee_org = null;

      if ( lic.licensor )
        licensor_org = Org.findByImpId(lic.licensor.toString())

      if ( lic.licensee )
        licensee_org = Org.findByImpId(lic.licensee.toString())

      License l = new License (
                               reference:lic.license_reference,
                               concurrentUsers:lic.concurrent_users,
                               remoteAccess:lic.remote_access,
                               walkinAccess:lic.walkin_access,
                               multisiteAccess:lic.multisite_access,
                               partnersAccess:lic.partners_access,
                               alumniAccess:lic.alumni_access,
                               ill:lic.ill,
                               coursepack:lic.coursepack,
                               vle:lic.vle,
                               enterprise:lic.enterprise,
                               pca:lic.pca,
                               noticePeriod:lic.notice_period,
                               licenseUrl:lic.license_url,
                               licensorRef:lic.licensor_ref,
                               licenseeRef:lic.licensee_ref,
                               licenseType:lic.license_type,
                               licenseStatus:lic.license_status,
                               lastmod:lic.lastmod);

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
        log.debug("Process license subscription ${ls}");
        def sub = Subscription.findByImpId(ls.toString());
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
          log.error("Unable to locate subscription with impid ${ls} in db");
        }
      }

      possibleNote(lic.concurrent_users_note,'ConcurrentUsageNote',l);
      possibleNote(lic.remote_access_note,'RemoteAccessNote',l);
      possibleNote(lic.walkin_access_note,'WalkinAccessNote',l);
      possibleNote(lic.multisite_access_note,'MultisiteAccessNote',l);
      possibleNote(lic.partners_access_note,'PartnersAccessNote',l);
      possibleNote(lic.alumni_access_note,'AlumniAccessNote',l);
      possibleNote(lic.ill_note,'ILLNote',l);
      possibleNote(lic.coursepack_note,'CoursepackNote',l);
      possibleNote(lic.vle_note,'VLENote',l);
      possibleNote(lic.enterprise_note,'EnterpriseNote',l);
      possibleNote(lic.pca_note,'PCANote',l);

    }

    log.debug("License processing complete");

    redirect(controller:'home')
  }

  def possibleNote(content, type, license) {
    if ( content && content.toString().length() > 0 ) {
      def doc_content = new Doc(content:content.toString()).save();
      def doc_context = new DocContext(license:license,owner:doc_content,doctype:lookupOrCreateRefdataEntry('Document Type',type)).save();
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
    def link = OrgRole.find{ pkg==ppkg && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(pkg:ppkg, org:porg, roleType:prole);
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)
      porg.save();
    }
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

}
