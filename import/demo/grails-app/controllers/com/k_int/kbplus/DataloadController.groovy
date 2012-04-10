package com.k_int.kbplus

import com.k_int.kbplus.*

class DataloadController {

  def mongoService

  def update() {

    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')

    // Orgs
    mdb.orgs.find().order(lastmod:1).each { org ->
      log.debug("update org ${org}");
      def o = Org.findByImpId(org._id)
      if ( o == null ) {
        o=new Org(impId:org._id, name:org.name);
      }
    }

    // Platforms
    mdb.platforms.find().order(lastmod:1).each { plat ->
      log.debug("update platform ${plat}");
      def p = Platform.findByImpId(plat._id) ?: new Platform(name:plat.name, impId:plat._id).save(flush:true)
    }

    mdb.titles.find().order(lastmod:1).each { title ->
      log.debug("update ${title}");
      def t = TitleInstance.findByImpId(title._id)
      if ( t == null ) {
        t = new TitleInstance(title:title.title, impId:title._id)
        title.identifier?.each { id ->
          t.ids.add(new TitleSID(namespace:id.type,identifier:id.value));
        }
        title.save(flush:true);
      }
    }

    mdb.pkgs.find().order(lastmod:1).each { pkg ->
      log.debug("update package ${pkg}");
      def p = Package.findByImpId(pkg._id)
      if ( p == null ) {
        p = new Package(identifier:pkg.identifier,
                        name:pkg.name,
                        impId:pkg._id,
                        contentProvider: pkg.contentProvider ? Org.findByImpId(pkg.contentProvider) : null );
      }
      p.save(flush:true)
    }
  }
}
