package demo



class IndexUpdateJob {

  def mongoService 

  static triggers = {
    cron name:'cronTrigger', startDelay:10000, cronExpression: "0 0/2 * * * ?"
  }

  def execute() {

    def start_time = System.currentTimeMillis();
    def mdb = mongoService.getMongo().getDB('kbplus_ds_reconciliation')

    updateOrgs(mdb, com.k_int.kbplus.Org.class);
    updateOrgs(mdb, com.k_int.kbplus.TitleInstance.class);

    // execute task
    log.debug("Execute IndexUpdateJob starting at ${new Date()}");
    def elapsed = System.currentTimeMillis() - start_time;

    log.debug("IndexUpdateJob completed in ${elapsed}ms at ${new Date()}");
  }

  def updateOrgs(mdb, domain) {

    def timestamp_record = mdb.timestamps.findOne(domain:domain.name)
    def max_ts_so_far = 0;

    if ( !timestamp_record ) {
      timestamp_record = [
        _id:new org.bson.types.ObjectId(),
        domain:domain.name,
        latest:0
      ]
      mdb.timestamps.save(timestamp_record);
    }

    // Class clazz = grailsApplication.getDomainClass(domain)
    def qry = domain.findAllByLastModifiedGreaterThan(timestamp_record.latest)
    qry.each { i ->
      log.debug(i);
      max_ts_so_far = i.lastModified
    }

    timestamp_record.latest = max_ts_so_far
    mdb.timestamps.save(timestamp_record);
  }
}
