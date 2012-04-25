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
        o = Org.findByName(org.name)
        if ( o == null ) {
          o=new Org(impId:org._id.toString(), 
                    name:org.name,
                    ipRange:org.ipRange,
                    sector:org.sectorName);
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
        o = Org.findByName(org.name);
        def cons_org = Org.findByName('NESLI2') ?: new Org(name:'NESLI2').save();
        if ( cons_org ) {
          def new_combo = new Combo(type:lookupOrCreateRefdataEntry('Combo Type','Consortium'),
                                    fromOrg:o,
                                    toOrg:cons_org).save(flush:true);
        }
      }

    }

    // Platforms
    mdb.platforms.find().sort(lastmod:1).each { plat ->
      log.debug("update platform ${plat}");
      def p = Platform.findByImpId(plat._id.toString()) ?: new Platform(name:plat.name, impId:plat._id.toString()).save()
    }

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
      }
    }

    mdb.pkgs.find().sort(lastmod:1).each { pkg ->
      log.debug("update package ${pkg}");
      def p = Package.findByImpId(pkg._id.toString())
      if ( p == null ) {
        p = new Package(identifier:pkg.identifier,
                        name:pkg.name,
                        impId:pkg._id.toString(),
                        contentProvider: pkg.contentProvider ? Org.findByImpId(pkg.contentProvider.toString()) : null );
      }
      p.save()
    }

    // Finally... tipps
    mdb.tipps.find().sort(lastmod:1).each { tipp ->
      log.debug("update ${tipp}");
      def title = TitleInstance.findByImpId(tipp.titleid.toString())
      def pkg = Package.findByImpId(tipp.pkgid.toString())
      def platform = Platform.findByImpId(tipp.platformid.toString())

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
                                                ids:[]).save()

        if ( tipp.identifiers ) {
          tipp.identifiers.each { tippid ->
            log.debug("lookup and add tippid ${tippid}");
            def canonical_identifier = lookupOrCreateCanonicalIdentifier(tippid.type, tippid.value);
            dbtipp.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, tipp:dbtipp));
          }
        }

        dbtipp.save();
      }
    }


  }

  def updateOrgs() {
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
}
