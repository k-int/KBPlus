package com.k_int.kbplus

import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat

class GlobalSourceSyncService {

  def packageReconcile = { info,oldpkg,newpkg ->
    log.debug("\n\nreconcile package\n");
    com.k_int.kbplus.GokbDiffEngine.diff(oldpkg, newpkg)
  }

  def packageConv = { xml ->
    // Convert XML to internal structure ansd return
    println("packageConv");
    def result = [:]
    // result.parsed_rec = xml.text().getBytes();
    result.title = xml.package.packageName.text()

    result.parsed_rec = [:]
    result.parsed_rec.packageName = xml.package.packageName.text()
    result.parsed_rec.packageId = xml.package.packageId.text()
    result.parsed_rec.tipps = []
    xml.package.packageTitles.TIP.each { tip ->
      def newtip = [
                     title:tip.title.text(), 
                     titleId:tip.titleId.text(),
                     platform:tip.platform.text(),
                     platformId:tip.platformId.text(),
                     coverage:[
                       startDate:tip.coverage.'@startDate'.text(),
                       endDate:tip.coverage.'@endDate'.text(),
                       startVolume:tip.coverage.'@startVolume'.text(),
                       endVolume:tip.coverage.'@endVolume'.text(),
                       startIssue:tip.coverage.'@startIssue'.text(),
                       endIssue:tip.coverage.'@endIssue'.text(),
                       coverageDepth:tip.coverage.'@coverageDepth'.text(),
                       coverageNote:tip.coverage.'@coverageNote'.text(),
                     ],
                     identifiers:[]
                   ];

      tip.titleIdentifiers.each { id ->
        newtip.identifiers.add([ns:id.'@namespace'.text(), value:id.'@value'.text()]);
      }

      result.parsed_rec.tipps.add(newtip)
    }

    result.parsed_rec.tipps.sort{it.titleId}

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

          cfg.reconciler(existing_record_info[0], old_rec_info, new_record_info)
        }
        else {
          def parsed_rec = cfg.converter.call(rec.metadata)

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
}
