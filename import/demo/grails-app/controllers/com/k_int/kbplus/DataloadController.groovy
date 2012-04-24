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
        o=new Org(impId:org._id.toString(), name:org.name);
        o.save();
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
}
