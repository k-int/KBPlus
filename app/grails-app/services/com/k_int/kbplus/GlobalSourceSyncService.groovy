package com.k_int.kbplus

import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat

class GlobalSourceSyncService {

  def genericOIDService

  def packageReconcile = { grt ,oldpkg, newpkg ->
    log.debug("\n\nreconcile package\n");
    def pkg = null;
    // Firstly, make sure that there is a package for this record
    if ( grt.localOid != null ) {
      pkg = genericOIDService.resolveOID(grt.localOid)
    }
    else {
      // create a new package
      pkg = new Package(
                         identifier:grt.identifier,
                         name:newpkg.packageName,
                         impId:grt.owner.identifier
                       )

      if ( pkg.save() ) {
        grt.localOid = "com.k_int.kbplus.package:${pkg.id}"
        grt.save()
      }
    }

    def onNewTipp = { ctx, tipp, auto_accept ->
      def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      println("new tipp: ${tipp}");
      println("identifiers: ${tipp.title.identifiers}");

      def title_instance = TitleInstance.lookupOrCreate(tipp.title.identifiers,tipp.title.name)
      println("Result of lookup or create for ${tipp.title.name} with identifiers ${tipp.title.identifiers} is ${title_instance}");

      def plat_instance = Platform.lookupOrCreatePlatform([name:tipp.platform]);
    
      def new_tipp = new TitleInstancePackagePlatform()
      new_tipp.pkg = ctx;
      new_tipp.platform = plat_instance;
      new_tipp.title = title_instance;

      // We rely upon there only being 1 coverage statement for now, it seems likely this will need
      // to change in the future.
      tipp.coverage.each { cov ->
        new_tipp.startDate=((cov.startDate != null ) && ( cov.startDate.length() > 0 ) ) ? sdf.parse(cov.startDate) : null;
        new_tipp.startVolume=cov.startVolume;
        new_tipp.startIssue=cov.startIssue;
        new_tipp.endDate= ((cov.endDate != null ) && ( cov.endDate.length() > 0 ) ) ? sdf.parse(cov.endDate) : null;
        new_tipp.endVolume=cov.endVolume;
        new_tipp.endIssue=cov.endIssue;
        new_tipp.embargo=cov.embargo;
        new_tipp.coverageDepth=cov.coverageDepth;
        new_tipp.coverageNote=cov.coverageNote;
      }
      new_tipp.hostPlatformURL=tipp.url;

      new_tipp.save();
    }

    def onUpdatedTipp = { ctx, tipp, auto_accept ->
      println("updated tipp");
      // Find title with ID tipp... in package ctx
      def title_of_tipp_to_update = TitleInstance.lookupOrCreate(tipp.title.identifiers,tipp.title.name)
      def db_tipp = ctx.tipps.find { it.title == title_of_tipp_to_update }
    }

    def onDeletedTipp = { ctx, tipp ->
      println("deletd tipp");
    }

    def onPkgPropChange = { ctx, propname, value, auto_accept ->
      println("updated pkg prop");
    }

    boolean auto_accept = true
    com.k_int.kbplus.GokbDiffEngine.diff(pkg, oldpkg, newpkg, onNewTipp, onUpdatedTipp, onDeletedTipp, onPkgPropChange, auto_accept)
  }

  def packageConv = { md ->
    // Convert XML to internal structure ansd return
    def result = [:]
    // result.parsed_rec = xml.text().getBytes();
    result.title = md.gokb.package.name.text()

    result.parsed_rec = [:]
    result.parsed_rec.packageName = md.gokb.package.name.text()
    result.parsed_rec.packageId = md.gokb.package.'@id'.text()
    result.parsed_rec.tipps = []
    md.gokb.package.TIPPs.TIPP.each { tip ->
      def newtip = [
                     title: [
                       name:tip.title.name.text(), 
                       identifiers:[]
                     ],
                     titleId:tip.title.'@id'.text(),
                     platform:tip.platform.name.text(),
                     platformId:tip.platform.'@id'.text(),
                     coverage:[],
                     url:tip.url.text(),
                     identifiers:[]
                   ];

      tip.coverage.each { cov ->
        newtip.coverage.add([
                       startDate:cov.'@startDate'.text(),
                       endDate:cov.'@endDate'.text(),
                       startVolume:cov.'@startVolume'.text(),
                       endVolume:cov.'@endVolume'.text(),
                       startIssue:cov.'@startIssue'.text(),
                       endIssue:cov.'@endIssue'.text(),
                       coverageDepth:cov.'@coverageDepth'.text(),
                       coverageNote:cov.'@coverageNote'.text(),
                     ]);
      }

      tip.title.identifiers.identifier.each { id ->
        newtip.title.identifiers.add([namespace:id.'@namespace'.text(), value:id.'@value'.text()]);
      }

      result.parsed_rec.tipps.add(newtip)
    }

    result.parsed_rec.tipps.sort{it.titleId}
    println("Rec conversion for package returns object with title ${result.parsed_rec.title} and ${result.parsed_rec.tipps.size()} tipps");
    println(result.parsed_rec);

    return result
  }

  def rectypes = [
    [ name:'Package', converter:packageConv, reconciler:packageReconcile ]
  ]

  def executorService

  def runAllActiveSyncTasks() {
    // def future = executorService.submit({ internalRunAllActiveSyncTasks() } as java.util.concurrent.Callable)
    internalRunAllActiveSyncTasks()
  }

  def internalRunAllActiveSyncTasks() {

     log.debug("internalRunAllActiveSyncTasks() running...");

     def jobs = GlobalRecordSource.findAll() 

     jobs.each { sync_job ->
       log.debug(sync_job);
       // String identifier
       // String name
       // String type
       // Date haveUpTo
       // String uri
       // String listPrefix
       // String fullPrefix
       // String principal
       // String credentials
       switch ( sync_job.type ) {
         case 'OAI':
           log.debug("start internal sync");
           this.doOAISync(sync_job)
           log.debug("Complete internal sync");
           break;
         default:
           log.error("Unhandled sync job type: ${sync_job.type}");
           break;
       }
     }
  }

  def private doOAISync(sync_job) {
    log.debug("doOAISync");
    def future = executorService.submit({ intOAI(sync_job.id) } as java.util.concurrent.Callable)
    log.debug("doneOAISync");
  }
 
  def intOAI(sync_job_id) {

    def sync_job = GlobalRecordSource.get(sync_job_id)

    try {
      log.debug("internalOAISync records from ${sync_job.uri} since ${sync_job.haveUpTo} using ${sync_job.fullPrefix}");

      int rectype = sync_job.rectype.longValue()
      def cfg = rectypes[rectype]

      def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      def date = sync_job.haveUpTo

      log.debug("upto: ${date}");

      def oai_client = new OaiClient(host:sync_job.uri)
      def max_timestamp = 0

      log.debug("Collect changes since ${date}");

      oai_client.getChangesSince(date, sync_job.fullPrefix) { rec ->
        log.debug(rec.header.identifier)
        log.debug(rec.header.datestamp)
        def qryparams = [sync_job.id, rec.header.identifier.text()]
        def record_timestamp = sdf.parse(rec.header.datestamp.text())
        log.debug("Find: ${qryparams}");
        def existing_record_info = GlobalRecordInfo.executeQuery('select r from GlobalRecordInfo as r where r.source.id = ? and r.identifier = ?',qryparams);
        if ( existing_record_info.size() == 1 ) {
          log.debug("Update to an existing record....");

          def parsed_rec = cfg.converter.call(rec.metadata)

          // Deserialize
          def bais = new ByteArrayInputStream((byte[])(existing_record_info[0].record))
          def ins = new ObjectInputStream(bais);
          def old_rec_info = ins.readObject()
          ins.close()
          def new_record_info = parsed_rec.parsed_rec

          // Call this for each __tracker__
          // cfg.reconciler(existing_record_info[0], old_rec_info, new_record_info)

          // Finally, update our local copy of the remote object
          def baos = new ByteArrayOutputStream()
          def out= new ObjectOutputStream(baos)
          out.writeObject(new_record_info)
          out.close()
          existing_record_info[0].record = baos.toByteArray();
          existing_record_info[0].save()
        }
        else {
          log.debug("First time we have seen this record - converting");
          def parsed_rec = cfg.converter.call(rec.metadata)
          log.debug("Converter thinks this rec is ${parsed_rec.title}");

          def baos = new ByteArrayOutputStream()
          def out= new ObjectOutputStream(baos)
          out.writeObject(parsed_rec.parsed_rec)
          out.close()

          // Because we don't know about this record, we can't possibly be already tracking it. Just create a local tracking record.
          existing_record_info = new GlobalRecordInfo(
                                                      ts:record_timestamp,
                                                      name:parsed_rec.title,
                                                      identifier:rec.header.identifier.text(),
                                                      source: sync_job,
                                                      rectype:sync_job.rectype,
                                                      record: baos.toByteArray());

          if ( ! existing_record_info.save() ) {
            log.error("Problem saving record info: ${existing_record_info.errors}");
          }
        }

        if ( record_timestamp.getTime() > max_timestamp ) {
          max_timestamp = record_timestamp.getTime()
          log.debug("Max timestamp is now ${record_timestamp}");
        }

	log.debug("--");
      }

      log.debug("Updating sync job max timestamp");
      sync_job.haveUpTo=new Date(max_timestamp)
      sync_job.save();
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
    finally {
      log.debug("internalOAISync completed");
    }
  }

  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    if ( datestr && ( datestr.toString().trim().length() > 0 ) ) {
      for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
        try {
          parsed_date = i.next().parse(datestr.toString());
        }
        catch ( Exception e ) {
        }
      }
    }
    parsed_date
  }

  def dumpPkgRec(pr) {
    log.debug(pr);
  }

  def initialiseTracker(grt) {
    int rectype = grt.owner.rectype.longValue()
    def cfg = rectypes[rectype]

    def oldrec = [:]
    oldrec.tipps=[]
    def bais = new ByteArrayInputStream((byte[])(grt.owner.record))
    def ins = new ObjectInputStream(bais);
    def newrec = ins.readObject()
    ins.close()

    cfg.reconciler(grt,oldrec,newrec)
  }
}
