package com.k_int.kbplus

import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat

class GlobalSourceSyncService {

  def genericOIDService
  def changeNotificationService

  def packageReconcile = { grt ,oldpkg, newpkg ->
    log.debug("\n\nreconcile package\n");
    def pkg = null;
    boolean auto_accept_flag = false

    def scope = RefdataCategory.lookupOrCreate('Package.Scope',(newpkg?.scope)?:'Unknown');
    def listStatus = RefdataCategory.lookupOrCreate('Package.ListStatus',(newpkg?.listStatus)?:'Unknown');
    def breakable = RefdataCategory.lookupOrCreate('Package.Breakable',(newpkg?.breakable)?:'Unknown');
    def consistent = RefdataCategory.lookupOrCreate('Package.Consistent',(newpkg?.consistent)?:'Unknown');
    def fixed = RefdataCategory.lookupOrCreate('Package.Fixed',(newpkg?.fixed)?:'Unknown');
    def paymentType = RefdataCategory.lookupOrCreate('Package.PaymentType',(newpkg?.paymentType)?:'Unknown');
    def global = RefdataCategory.lookupOrCreate('Package.Global',(newpkg?.global)?:'Unknown');
    def isPublic = RefdataCategory.lookupOrCreate('YN','Yes');

    // Firstly, make sure that there is a package for this record
    if ( grt.localOid != null ) {
      pkg = genericOIDService.resolveOID(grt.localOid)
    }
    else {
      // create a new package

      // Auto accept everything whilst we load the package initially
      auto_accept_flag = true;

      pkg = new Package(
                         identifier:grt.identifier,
                         name:grt.name,
                         impId:grt.owner.identifier,
                         autoAccept:false,
                         packageType:null,
                         packageStatus:null,
                         packageListStatus:listStatus,
                         breakable:breakable,
                         consistent:consistent,
                         fixed:fixed,
                         isPublic:isPublic,
                         packageScope:scope
                       )


      if ( pkg.save() ) {
        grt.localOid = "com.k_int.kbplus.Package:${pkg.id}"
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
      def tipp_status = RefdataCategory.lookupOrCreate('TIPP Status',tipp.status?:'Current');

      if ( auto_accept ) {
        def new_tipp = new TitleInstancePackagePlatform()
        new_tipp.pkg = ctx;
        new_tipp.platform = plat_instance;
        new_tipp.title = title_instance;
        new_tipp.status = tipp_status;

        // We rely upon there only being 1 coverage statement for now, it seems likely this will need
        // to change in the future.
        // tipp.coverage.each { cov ->
        def cov = tipp.coverage[0]
          new_tipp.startDate=((cov.startDate != null ) && ( cov.startDate.length() > 0 ) ) ? sdf.parse(cov.startDate) : null;
          new_tipp.startVolume=cov.startVolume;
          new_tipp.startIssue=cov.startIssue;
          new_tipp.endDate= ((cov.endDate != null ) && ( cov.endDate.length() > 0 ) ) ? sdf.parse(cov.endDate) : null;
          new_tipp.endVolume=cov.endVolume;
          new_tipp.endIssue=cov.endIssue;
          new_tipp.embargo=cov.embargo;
          new_tipp.coverageDepth=cov.coverageDepth;
          new_tipp.coverageNote=cov.coverageNote;
        // }
        new_tipp.hostPlatformURL=tipp.url;

        new_tipp.save();
      }
      else {
        println("Register new tipp event for user to accept or reject");

        def cov = tipp.coverage[0]
        def change_doc = [ 
                           pkg:[id:ctx.id],
                           platform:[id:plat_instance.id],
                           title:[id:title_instance.id],
                           status:[id:tipp_status.id],
                           startDate:((cov.startDate != null ) && ( cov.startDate.length() > 0 ) ) ? sdf.parse(cov.startDate) : null,
                           startVolume:cov.startVolume,
                           startIssue:cov.startIssue,
                           endDate:((cov.endDate != null ) && ( cov.endDate.length() > 0 ) ) ? sdf.parse(cov.endDate) : null,
                           endVolume:cov.endVolume,
                           endIssue:cov.endIssue,
                           embargo:cov.embargo,
                           coverageDepth:cov.coverageDepth,
                           coverageNote: cov.coverageNote];

        changeNotificationService.registerPendingChange('pkg',
                                                        ctx,
                                                        "New TIPP for ${title_instance.title} from ${plat_instance.name}",
                                                        null,
                                                        [
                                                          newObjectClass:"com.k_int.kbplus.TitleInstancePackagePlatform",
                                                          changeType:'New Object',
                                                          changeDoc:change_doc
                                                        ])

      }
    }

    def onUpdatedTipp = { ctx, tipp, changes, auto_accept ->
      println("updated tipp, ctx = ${ctx.toString()}");

      // Find title with ID tipp... in package ctx
      def title_of_tipp_to_update = TitleInstance.lookupOrCreate(tipp.title.identifiers,tipp.title.name)

      def db_tipp = ctx.tipps.find { it.title.id == title_of_tipp_to_update.id }

      if ( db_tipp != null) {
        changes.each { chg ->

          def change_doc = [ 
              startDate:tipp.coverage[0].startDate,
              startVolume:tipp.coverage[0].startVolume,
              startIssue:tipp.coverage[0].startIssue,
              endDate:tipp.coverage[0].endDate,
              endVolume:tipp.coverage[0].endVolume,
              endIssue:tipp.coverage[0].endIssue,
              embargo:tipp.coverage[0].embargo,
              coverageDepth:tipp.coverage[0].coverageDepth,
              coverageNote:tipp.coverage[0].coverageNote,
              // status:null,
              // option:null,
              // delayedOA:null,
              // hybridOA:null,
              // statusReason:null,
              // payment:null,
              hostPlatformURL:tipp.url
          ]

          changeNotificationService.registerPendingChange('pkg',
                                                          ctx,
                                                          "A tipp/coverage update for \"${title_of_tipp_to_update.title}\" (Start Date:${tipp.coverage[0].startDate}, Start Volume:${tipp.coverage[0].startVolume}, Start Issue:${tipp.coverage[0].startIssue}, End Date:${tipp.coverage[0].endDate} , End Volume:${tipp.coverage[0].endVolume}, End Issue:${tipp.coverage[0].endIssue}, Embargo:${tipp.coverage[0].embargo}, Coverage Depth:${tipp.coverage[0].coverageDepth}, Coverage Note:${tipp.coverage[0].coverageNote}, url:${tipp.url}",
                                                          null,
                                                          [
                                                            changeTarget:"com.k_int.kbplus.TitleInstancePackagePlatform:${db_tipp.id}",
                                                            changeType:'Update Object',
                                                            changeDoc:change_doc
                                                          ])
        }
      }
      else {
        throw new RuntimeException("Unable to locate TIPP for update. ctx:${ctx}, tipp:${tipp}");
      }
    }

    def onDeletedTipp = { ctx, tipp ->
      println("deletd tipp");
    }

    def onPkgPropChange = { ctx, propname, value, auto_accept ->
      println("updated pkg prop");
    }

    def onTippUnchanged = {ctx, tippa ->
    }

    com.k_int.kbplus.GokbDiffEngine.diff(pkg, oldpkg, newpkg, onNewTipp, onUpdatedTipp, onDeletedTipp, onPkgPropChange, onTippUnchanged, auto_accept_flag)
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
      newtip.title.identifiers.add([namespace:'uri',value:newtip.titleId]);

      harmoniseTitleIdentifiers(newtip);

      result.parsed_rec.tipps.add(newtip)
    }

    result.parsed_rec.tipps.sort{it.titleId}
    println("Rec conversion for package returns object with title ${result.parsed_rec.title} and ${result.parsed_rec.tipps?.size()} tipps");
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

          // For each tracker we need to update the local object which reflects that remote record
          existing_record_info[0].trackers.each { tracker ->
            cfg.reconciler(tracker, old_rec_info, new_record_info)
          }

          // Finally, update our local copy of the remote object
          def baos = new ByteArrayOutputStream()
          def out= new ObjectOutputStream(baos)
          out.writeObject(new_record_info)
          out.close()
          existing_record_info[0].record = baos.toByteArray();
          existing_record_info[0].desc="Package ${parsed_rec.title} consisting of ${parsed_rec.parsed_rec.tipps?.size()} titles"
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
                                                      desc:"Package ${parsed_rec.title} consisting of ${parsed_rec.parsed_rec?.tipps?.size()} titles",
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

        log.debug("Updating sync job max timestamp");
        sync_job.haveUpTo=new Date(max_timestamp)
        sync_job.save();
      }

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

  def initialiseTracker(grt, localPkgOID) {
    int rectype = grt.owner.rectype.longValue()
    def cfg = rectypes[rectype]
    def localPkg = genericOIDService.resolveOID(localPkgOID)

    def oldrec = localPkg.toComparablePackage()

    def bais = new ByteArrayInputStream((byte[])(grt.owner.record))
    def ins = new ObjectInputStream(bais);
    def newrec = ins.readObject()
    ins.close()

    log.debug("Generated comparable for existing package : ${oldrec}");
    log.debug("new package : ${newrec}");

    cfg.reconciler(grt,oldrec,newrec)
  }

  /**
   *  When this system sees a title from a remote source, we need to try and find a common canonical identifier. We will use the
   *  GoKB TitleID for this. Each time a title is seen we make sure that we locally know what the GoKB Title ID is for that remote
   *  record.
   */
  def harmoniseTitleIdentifiers(titleinfo) {
    // println("harmoniseTitleIdentifiers");
    // println("Remote Title ID: ${titleinfo.titleId}");
    // println("Identifiers: ${titleinfo.title.identifiers}");
    def title_instance = TitleInstance.lookupOrCreate(titleinfo.title.identifiers,titleinfo.title.name, true)
  }

  def diff(localPackage, globalRecordInfo) {

    def result = []

    def oldpkg = localPackage ? localPackage.toComparablePackage() : [tipps:[]];

    def bais = new ByteArrayInputStream((byte[])(globalRecordInfo.record))
    def ins = new ObjectInputStream(bais);
    def newpkg = ins.readObject()
    ins.close()

    def onNewTipp = { ctx, tipp, auto_accept -> ctx.add([tipp:tipp, action:'i']); }
    def onUpdatedTipp = { ctx, tipp, changes, auto_accept -> ctx.add([tipp:tipp, action:'u', changes:changes]); }
    def onDeletedTipp = { ctx, tipp  -> ctx.add([tipp:tipp, action:'d']); }
    def onPkgPropChange = { ctx, propname, value, auto_accept -> null; }
    def onTippUnchanged = { ctx, tipp -> ctx.add([tipp:tipp, action:'-']);  }

    com.k_int.kbplus.GokbDiffEngine.diff(result, oldpkg, newpkg, onNewTipp, onUpdatedTipp, onDeletedTipp, onPkgPropChange, onTippUnchanged, false)

    return result
  }
}
