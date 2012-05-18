package com.k_int.kbplus

import com.k_int.kbplus.*

class DataloadController {

  def mongoService

  def update() {

    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')

    // Orgs
    mdb.orgs.find().sort(lastmod:1).each { org ->
      log.debug("update org ${org}");
      def o = Org.findByImpId(org._id.toString())
      if ( o == null ) {
        o = Org.findByName(org.name.trim())
        if ( o == null ) {
          o=new Org(impId:org._id.toString(), 
                    name:org.name.trim(),
                    ipRange:org.ipRange,
                    sector:org.sectorName,
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
      o.save(flush:true);

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

    // Subscriptions
    mdb.subscriptions.find().sort(lastmod:1).each { sub ->
      log.debug("Adding subscription ${sub.name} (${sub.identifier})");
      def dbsub = Subscription.findByImpId(sub._id.toString()) ?: new Subscription(name:sub.name, 
                                                                                   identifier:sub.identifier,
                                                                                   impId:sub._id.toString(),
                                                                                   startDate:sub.start_date,
                                                                                   endDate:sub.end_date,
                                                                                   type: RefdataValue.findByValue('Subscription Offered')).save(flush:true);
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
      log.debug("update ${title}");
      def t = TitleInstance.findByImpId(title._id.toString())
      if ( t == null ) {
        t = new TitleInstance(title:title.title, impId:title._id.toString(), ids:[])
        title.identifier?.each { id ->
          def canonical_identifier = lookupOrCreateCanonicalIdentifier(id.type,id.value);
          t.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, ti:t));
          // t.addToIds(new TitleSID(namespace:id.type,identifier:id.value));
        }

        t.save();

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
    mdb.tipps.find().sort(lastmod:1).each { tipp ->
      try {
        log.debug("update ${tipp}");
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
                  IssueEntitlement dbie = IssueEntitlement.findBySubscriptionAndTipp(sub, dbtipp) ?: new IssueEntitlement(subscription:sub, tipp: dbtipp).save();
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

    redirect(controller:'home')
  }

  def reloadSTData() {
    log.debug("reloadSTData()");
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')
    int subcount = 0

    // Orgs
    mdb.subs.find().sort(lastmod:1).each { sub ->
      log.debug("load sub[${subcount++}] ${sub}");

      // Join together the subscription and the organisation
      def db_sub = Subscription.findByImpId(sub.sub.toString());
      def db_org = Org.findByImpId(sub.org.toString())

      log.debug("Create a link between ${db_org} and ${db_sub}");

      // create a new subscription - an instance of an actual org taking up a specific subscription
      def new_subscription = new Subscription(
                                    identifier: "${db_sub.identifier}:${sub.org.toString()}",
                                    name: db_sub.name,
                                    startDate: db_sub.startDate,
                                    endDate: db_sub.endDate,
                                    instanceOf: db_sub,
                                    type: RefdataValue.findByValue('Subscription Taken') )

      if ( new_subscription.save() ) {
        log.debug("New subscription saved...");
      }
      else {
        log.error("Problem saving new subscription, ${new_subscription.errors}");
      }

      // assert an org-role
      def org_link = new OrgRole(org:db_org, 
                                 sub: new_subscription, 
                                 roleType: RefdataValue.findByValue('Subscriber'))

      if ( org_link.save() ) {
        log.debug("New org link saved...");
      }
      else {
        log.error("Problem saving new org link, ${org_link.errors}");
      }

      // List all actual st_title records, and diff that against the default from the ST file
      def sub_titles = mdb.stTitle.find(owner:sub._id)

      if ( sub_titles.size() == 0 ) {
        log.debug("No ST title data present, defaulting in from SO");
        IssueEntitlement.findAllBySubscription(db_sub).each { ie ->
          log.debug("Adding default entitlement based on entitlement ${ie.id}");
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
                                            coverageNote:ie.tipp.coverageNote).save();
        }
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
                                                coreTitle: is_core).save();
            }
            else {
              log.error("Unable to locate TIPP instance for ${st.tipp_id.toString()}");
            }
          }
          else {
            log.debug("omit ${st}");
          }
        }
      }

      // Iterate all issue entitlements that appear as a part of this SO
      //IssueEntitlement.findAllBySubscription(db_sub).each { ie ->
      //  log.debug("Determine if ${ie} should be copied forward into the actual ST data");
      //}

      log.debug("Done listing issue entitlements for ${db_sub.impId}");
    }

    log.debug("Processed ${subcount} subscriptions");
    redirect(controller:'home')
  }

  def nvl(val,defval) {
    def result = defval
    if ( val && val.toString().trim().length() > 0 )
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
}
