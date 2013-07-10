package com.k_int.kbplus

class JuspSyncService {

  def executorService

  def doSync() {
    log.debug("JuspSyncService::doSync");
//     def future = executorService.submit({ internalDoSync() } as java.util.concurrent.Callable)
//     log.debug("doSync returning");
//   }

//   def internalDoSync() {

    // Select distinct list of Title+Provider (TIPP->Package-CP->ID[jusplogin] with jusp identifiers
    def q = "select distinct tipp.title, po.org, tipp.pkg from TitleInstancePackagePlatform as tipp " +
              "join tipp.pkg.orgs as po where po.roleType.value='Content Provider' " +
              "and exists ( select tid from tipp.title.ids as tid where tid.identifier.ns.ns = 'jusp' ) " +
              "and exists ( select oid from po.org.ids as oid where oid.identifier.ns.ns = 'juspsid' ) "
                // And exists title.identifier where namespace = jusp
                // And exists org.identifier where namespace = jusplogin

    log.debug("JUSP Sync Task - Running query ${q}");

    def l1 = TitleInstancePackagePlatform.executeQuery(q)

    l1.each { to ->
      log.debug("Processing titile/provider pair: ${to[0].title}, ${to[1].name} ${to[2].id}");
      log.debug(" -> Title jusp id: ${to[0].getIdentifierValue('jusp')}");
      log.debug(" -> Suppllirt jusp did: ${to[1].getIdentifierByType('juspsid')}");
    }

 
    
    // Set update date point
    // For each org with a JUSP ID
      // For each title
        // Fetch all data between org last update date
        // Update/create any records
      
    log.debug("internalDoSync exit");
  }
}
