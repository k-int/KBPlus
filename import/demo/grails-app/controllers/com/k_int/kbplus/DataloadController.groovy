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
          log.debug("Adding identifier to title: ${id.type}:${id.value}");
          t.addToIds(new TitleSID(namespace:id.type,identifier:id.value));
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
  }
}
