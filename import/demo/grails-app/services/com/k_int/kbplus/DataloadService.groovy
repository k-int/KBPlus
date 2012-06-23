package com.k_int.kbplus

class DataloadService {

  def executorService
  def ESWrapperService

  def dataload_running=false
  def dataload_stage=-1

  def updateFTIndexes() {
    System.out.println("updateFTIndexes");
    log.debug("updateFTIndexes");
    def future = executorService.submit({
          doFTUpdate()
    } as java.util.concurrent.Callable)
    log.debug("updateFTIndexes returning");
  }

  def doFTUpdate() {
    System.out.println("doFTUpdate");
    
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
      result.rectype = 'Organisation'
      result
    }

    updateES(esclient, com.k_int.kbplus.TitleInstance.class) { ti ->
      def result = [:]
      result._id = ti.impId
      result.title = ti.title
      result.dbId = ti.id
      result.rectype = 'Title'
      result
    }

    updateES(esclient, com.k_int.kbplus.Package.class) { pkg ->
      def result = [:]
      result._id = pkg.impId
      result.name = "${pkg.name} (${pkg.contentProvider?.name})"
      result.dbId = pkg.id
      result.rectype = 'Package'
      result
    }

    updateES(esclient, com.k_int.kbplus.Platform.class) { pkg ->
      def result = [:]
      result._id = pkg.impId
      result.name = pkg.name
      result.dbId = pkg.id
      result.rectype = 'Platform'
      result
    }

    def elapsed = System.currentTimeMillis() - start_time;
    log.debug("IndexUpdateJob completed in ${elapsed}ms at ${new Date()}");
  }

  def updateES(esclient, domain, recgen_closure) {

    def count = 0;
    Date from = new Date(0);
    def qry = domain.findAllByLastUpdatedGreaterThan(from);
    qry.each { i ->
      def idx_record = recgen_closure(i)

      // log.debug("Generated record ${idx_record}");
      def future = esclient.index {
        index "kbplus"
        type domain.name
        id idx_record['_id']
        source idx_record
      }

      count++
    }

    log.debug("Completed processing on ${domain.name} - saved ${count} records");
  }

  def getReconStatus() {
    
    def result = [
      active:dataload_running,
      stage:dataload_stage
    ]

    result
  }
}
