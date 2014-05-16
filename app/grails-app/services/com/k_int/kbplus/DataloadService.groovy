package com.k_int.kbplus

import com.k_int.kbplus.*
import org.hibernate.ScrollMode
import java.nio.charset.Charset
import java.util.GregorianCalendar

class DataloadService {

  def stats = [:]

  def update_stages = [
    'Organisations Data',
    'Subscriptions Offered Data',
    'Subscriptions Taken Data',
    'License Data'
  ]

  def executorService
  def ESWrapperService
  def mongoService
  def sessionFactory
  def edinaPublicationsAPIService
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP 

  def dataload_running=false
  def dataload_stage=-1
  def dataload_message=''

  def updateFTIndexes() {
    log.debug("updateFTIndexes");
    new EventLog(event:'kbplus.updateFTIndexes',message:'Update FT indexes',tstp:new Date(System.currentTimeMillis())).save(flush:true)
    def future = executorService.submit({
      doFTUpdate()
    } as java.util.concurrent.Callable)
    log.debug("updateFTIndexes returning");
  }

  def doFTUpdate() {
    log.debug("doFTUpdate");
    
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
      result.visible = ['Public']
      result.rectype = 'Organisation'
      result
    }

    updateES(esclient, com.k_int.kbplus.TitleInstance.class) { ti ->
      def result = [:]
      if ( ti.title != null ) {
        def new_key_title =  com.k_int.kbplus.TitleInstance.generateKeyTitle(ti.title)
        if ( ti.keyTitle != new_key_title ) {
          ti.normTitle = com.k_int.kbplus.TitleInstance.generateNormTitle(ti.title)
          ti.keyTitle = com.k_int.kbplus.TitleInstance.generateKeyTitle(ti.title)
          //
          // This alone should trigger before update to do the necessary...
          //
          ti.save()
        }
        else {
        }
        result._id = ti.impId
        result.title = ti.title
        result.normTitle = ti.normTitle
        result.keyTitle = ti.keyTitle
        result.dbId = ti.id
        result.visible = ['Public']
        result.rectype = 'Title'
        result.identifiers = []
        ti.ids?.each { id ->
          result.identifiers.add([type:id.identifier.ns.ns, value:id.identifier.value])
        }
      }
      else {
        log.warn("Title with no title string - ${ti.id}");
      }
      result
    }

    updateES(esclient, com.k_int.kbplus.Package.class) { pkg ->
      def result = [:]
      result._id = pkg.impId
      result.name = "${pkg.name}"
      result.sortname = "${pkg.name.toLowerCase()}"
      result.tokname = result.name.replaceAll(':',' ')
      result.dbId = pkg.id
      result.visible = ['Public']
      result.rectype = 'Package'
      result.consortiaId = pkg.getConsortia()?.id
      result.consortiaName = pkg.getConsortia()?.name
      result.cpname = pkg.contentProvider?.name
      result.cpid = pkg.contentProvider?.id
      def lastmod = pkg.lastUpdated ?: pkg.dateCreated
      if ( lastmod != null ) {
        def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
        result.lastModified = formatter.format(lastmod)
      }

      if ( pkg.startDate ) {
        GregorianCalendar c = new GregorianCalendar()
        c.setTime(pkg.startDate) 
        result.startYear = "${c.get(Calendar.YEAR)}"
        result.startYearAndMonth = "${c.get(Calendar.YEAR)}-${(c.get(Calendar.MONTH))+1}"
      }

      if ( pkg.endDate ) {
        GregorianCalendar c = new GregorianCalendar()
        c.setTime(pkg.endDate) 
        result.endYear = "${c.get(Calendar.YEAR)}"
        result.endYearAndMonth = "${c.get(Calendar.YEAR)}-${(c.get(Calendar.MONTH))+1}"
      }

      result
    }

    updateES(esclient, com.k_int.kbplus.Platform.class) { plat ->
      def result = [:]
      result._id = plat.impId
      result.name = plat.name
      result.dbId = plat.id
      result.visible = ['Public']
      result.rectype = 'Platform'
      result
    }

    updateES(esclient, com.k_int.kbplus.Subscription.class) { sub ->
      
      def result = [:]
      result._id = sub.impId
      result.name = sub.name
      result.identifier = sub.identifier
      result.dbId = sub.id
      result.visible = ['Public']
      result.consortiaId = sub.getConsortia()?.id
      result.consortiaName = sub.getConsortia()?.name
      result.packages = []

      if ( sub.startDate ) {
        GregorianCalendar c = new GregorianCalendar()
        c.setTime(sub.startDate) 
        result.startYear = "${c.get(Calendar.YEAR)}"
        result.startYearAndMonth = "${c.get(Calendar.YEAR)}-${(c.get(Calendar.MONTH))+1}"
      }

      sub.packages.each { sp ->
        def pgkinfo = [:]
        if ( sp.pkg != null ) {
          // Defensive - it appears that there can be a SP without a package. 
          pgkinfo.pkgname = sp.pkg.name
          pgkinfo.pkgidstr= sp.pkg.identifier
          pgkinfo.pkgid= sp.pkg.id
          pgkinfo.cpname = sp.pkg.contentProvider?.name
          pgkinfo.cpid = sp.pkg.contentProvider?.id
          result.packages.add(pgkinfo);
        }
      }

      if ( sub.subscriber ) {
        result.visible.add(sub.subscriber.shortcode)
      }
      result.subtype = sub.type?.value
      result.rectype = 'Subscription'
      result
    }


    def elapsed = System.currentTimeMillis() - start_time;
    log.debug("IndexUpdateJob completed in ${elapsed}ms at ${new Date()}");
  }

  def updateES(esclient, domain, recgen_closure) {

    def count = 0;
    try {
      log.debug("updateES - ${domain.name}");

      def latest_ft_record = FTControl.findByDomainClassNameAndActivity(domain.name,'ESIndex')

      log.debug("result of findByDomain: ${latest_ft_record}");
      if ( !latest_ft_record) {
        latest_ft_record=new FTControl(domainClassName:domain.name,activity:'ESIndex',lastTimestamp:0)
      }

      log.debug("updateES ${domain.name} since ${latest_ft_record.lastTimestamp}");
      def total = 0;
      Date from = new Date(latest_ft_record.lastTimestamp);
      // def qry = domain.findAllByLastUpdatedGreaterThan(from,[sort:'lastUpdated']);

      def c = domain.createCriteria()
      c.setReadOnly(true)
      c.setCacheable(false)
      c.setFetchSize(Integer.MIN_VALUE);

      c.buildCriteria{
          or {
            gt('lastUpdated', from)
            and {
              gt('dateCreated', from)
              isNull('lastUpdated')
            }
          }
          order("lastUpdated", "asc")
      }

      def results = c.scroll(ScrollMode.FORWARD_ONLY)
    
      log.debug("Query completed.. processing rows...");

      while (results.next()) {
        Object r = results.get(0);
        def idx_record = recgen_closure(r)

        def future = esclient.index {
          index "kbplus"
          type domain.name
          id idx_record['_id']
          source idx_record
        }

        latest_ft_record.lastTimestamp = r.lastUpdated?.getTime()

        count++
        total++
        if ( count > 100 ) {
          count = 0;
          log.debug("processed ${++total} records (${domain.name})");
          latest_ft_record.save(flush:true);
          cleanUpGorm();
        }
      }
      results.close();

      println("Processed ${total} records for ${domain.name}");

      // update timestamp
      latest_ft_record.save(flush:true);
    }
    catch ( Exception e ) {
      log.error("Problem with FT index",e);
    }
    finally {
      log.debug("Completed processing on ${domain.name} - saved ${count} records");
    }
  }

  def getReconStatus() {
    
    def result = [
      active:dataload_running,
      stage:dataload_stage,
      stats:stats
    ]

    result
  }



  def possibleNote(content, type, license, note_title) {
    if ( content && content.toString().length() > 0 ) {
      def doc_content = new Doc(content:content.toString(), 
                                title: "${type} note",
                                type: lookupOrCreateRefdataEntry('Doc Type','Note') ).save()
      def doc_context = new DocContext(license:license,
                                       domain:type,
                                       owner:doc_content,
                                       doctype:lookupOrCreateRefdataEntry('Document Type','Note')).save();
    }
  }

  def nvl(val,defval) {
    def result = defval
    if ( ( val ) && ( val.toString().trim().length() > 0 ) )
      result = val

    result
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

  def assertOrgTitleLink(porg, ptitle, prole) {
    // def link = OrgRole.findByTitleAndOrgAndRoleType(ptitle, porg, prole) ?: new OrgRole(title:ptitle, org:porg, roleType:prole).save();
    def link = OrgRole.find{ title==ptitle && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(title:ptitle, org:porg, roleType:prole)
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)

      porg.save();
    }
  }

  def assertOrgPackageLink(porg, ppkg, prole) {
    // def link = OrgRole.findByPkgAndOrgAndRoleType(pkg, org, role) ?: new OrgRole(pkg:pkg, org:org, roleType:role).save();
    log.debug("assertOrgPackageLink()");
    def link = OrgRole.find{ pkg==ppkg && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(pkg:ppkg, org:porg, roleType:prole);
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)
      porg.save();
    }
    log.debug("assertOrgPackageLink() complete");
  }

  def assertOrgLicenseLink(porg, plic, prole) {
    def link = OrgRole.find{ lic==plic && org==porg && roleType==prole }
    if ( ! link ) {
      link = new OrgRole(lic:plic, org:porg, roleType:prole);
      if ( !porg.links )
        porg.links = [link]
      else
        porg.links.add(link)
      porg.save();
    }

  }

  def handleChangesSince(db,
                         collname,
                         timestamp,
                         processingClosure) {

    def cursor = db."${collname}".find().sort(lastmod:1)
    cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    cursor.each { item ->
      def local_copy = db."${collname}_localcopy".findOne([_id:item._id])
      if ( local_copy ) {
        log.debug("Got local copy");
        if ( item.equals(local_copy.original) ) {
          log.debug("No change detected in source item since last processing");
        }
        else {
          log.debug("Record has changed... process");
        }
      }
      else {
        log.debug("No local copy found");
        def copy_item = [
          _id:item._id,
          original:item
        ]
        db."${collname}_localcopy".save(copy_item);
      }

      processingClosure(item)
    }
  }


  def dataCleanse() {
    log.debug("dataCleanse");
    def future = executorService.submit({
      doDataCleanse()
    } as java.util.concurrent.Callable)
    log.debug("dataCleanse returning");
  }

  def doDataCleanse() {
    log.debug("dataCleansing");
    // 1. Find all packages that do not have a nominal platform
    Package.findAllByNominalPlatformIsNull().each { p ->
      def platforms = [:]
      p.tipps.each{ tipp ->
        if ( !platforms.keySet().contains(tipp.platform.id) ) {
          platforms[tipp.platform.id] = [count:1, platform:tipp.platform]
        }
        else {
          platforms[tipp.platform.id].count++
        }
      }

      def selected_platform = null;
      def largest = 0;
      platforms.values().each { pl ->
        log.debug("Processing ${pl}");
        if ( pl['count'] > largest ) {
          selected_platform = pl['platform']
        }
      }

      log.debug("Nominal platform is ${selected_platform} for ${p.id}");
      p.nominalPlatform = selected_platform
      p.save(flush:true)
    }
  }

  def titleAugment() {
    // edinaPublicationsAPIService.lookup('Acta Crystallographica. Section F, Structural Biology and Crystallization Communications');
    def future = executorService.submit({
      doTitleAugment()
    } as java.util.concurrent.Callable)
    log.debug("titleAugment returning");
  }

  def doTitleAugment() {
    TitleInstance.findAll().each { ti ->
      if ( ti.getIdentifierValue('SUNCAT' ) == null ) {
        def lookupResult = edinaPublicationsAPIService.lookup(ti.title)
        if ( lookupResult ) {
          def record = lookupResult.records.record
          if ( record ) {
            boolean matched = false;
            def suncat_identifier = null;
            record.modsCollection.mods.identifier.each { id ->
              if ( id.text().equalsIgnoreCase(ti.getIdentifierValue('ISSN')) || id.text().equalsIgnoreCase(ti.getIdentifierValue('eISSN'))  ) {
                matched = true
              }

              if ( id.@type == 'suncat' ) {
                suncat_identifier = id.text();
              }
            }

            if ( matched && suncat_identifier ) {
              log.debug("set suncat identifier to ${suncat_identifier}");
              def canonical_identifier = Identifier.lookupOrCreateCanonicalIdentifier('SUNCAT',suncat_identifier);
              ti.ids.add(new IdentifierOccurrence(identifier:canonical_identifier, ti:ti));
              ti.save(flush:true);
            }
            else {
              log.debug("No match for title ${ti.title}, ${ti.id}");
            }
          }
          else {
          }
        }
        else {
        }
        synchronized(this) {
          Thread.sleep(250);
        }
      }
    }
  }

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }

  def clearDownAndInitES() {
    log.debug("Clear down and init ES");
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()

    // Get hold of an index admin client
    org.elasticsearch.groovy.client.GIndicesAdminClient index_admin_client = new org.elasticsearch.groovy.client.GIndicesAdminClient(esclient);

    try {
      // Drop any existing kbplus index
      log.debug("Dropping old ES index....");
      def future = index_admin_client.delete {
        indices 'kbplus'
      }
      future.get()
      log.debug("Drop old ES index completed OK");
    }
    catch ( Exception e ) {
      log.warn("Problem deleting index...",e);
    }

    // Create an index if none exists
    log.debug("Create new ES index....");
    def future = index_admin_client.create {
      index 'kbplus'
    }
    future.get()

    log.debug("Clear down and init ES completed... AS OF 4.1 MAPPINGS -MUST- Be installed in ESHOME/mappings/kbplus");
    
  }


}
