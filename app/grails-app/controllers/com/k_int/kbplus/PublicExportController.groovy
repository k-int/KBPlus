package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import groovy.xml.MarkupBuilder

class PublicExportController {
  def ESSearchService
  def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
  def exportService
  def transformerService
  
  def index() { 
    def result = [:]
    params.max = 30

    params.rectype = "Package" // Tells ESSearchService what to look for
    if(params.q == "")  params.remove('q');
    params.isPublic="Yes"
    if(params.lastUpdated){
      params.lastModified ="[${params.lastUpdated} TO 2100]"
    }
    if (!params.sort){
      params.sort="sortname"
      params.order = "asc"
    }
    if(params.search.equals("yes")){
      //when searching make sure results start from first page
      params.offset = 0
      params.search = null
    }

    result =  ESSearchService.search(params)   
    result.transforms = grailsApplication.config.packageTransforms

    result  
  }

  def so() {
    log.debug("subscriptionDetails id:${params.id} format=${response.format}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : ( ( response.format == "csv" || response.format == "json" ) ? 10000 : 10 );
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    log.debug("max = ${result.max}");
    result.subscriptionInstance = Subscription.findByIdentifier(params.id)

    if ( result.subscriptionInstance.type.value != 'Subscription Offered' ) {
      redirect action:'index'
    }

    def base_qry = null;

    def qry_params = [result.subscriptionInstance]

    if ( params.filter ) {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) and ( ( lower(ie.tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = ie.tipp.title.id and io.identifier.value like ? ) ) )"
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
    }
    else {
      base_qry = " from IssueEntitlement as ie where ie.subscription = ? and ( ie.status.value != 'Deleted' ) "
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by ie.${params.sort} ${params.order} "
    }
    result.num_sub_rows = IssueEntitlement.executeQuery("select count(ie) "+base_qry, qry_params )[0]

    result.entitlements = IssueEntitlement.executeQuery("select ie "+base_qry, qry_params, [max:result.max, offset:result.offset]);

    log.debug("subscriptionInstance returning... ${result.num_sub_rows} rows ");
    withFormat {
      html result
      csv {
         def jc_id = result.subscriptionInstance.getSubscriber()?.getIdentifierByType('JC')?.value

         response.setHeader("Content-disposition", "attachment; filename=${result.subscriptionInstance.identifier}.csv")
         response.contentType = "text/csv"
         def out = response.outputStream
         out.withWriter { writer ->
           if ( ( params.omitHeader == null ) || ( params.omitHeader != 'Y' ) ) {
             writer.write("FileType,SpecVersion,JC_ID,TermStartDate,TermEndDate,SubURI,SystemIdentifier\n")
             writer.write("${result.subscriptionInstance.type.value},\"2.0\",${jc_id?:''},${result.subscriptionInstance.startDate},${result.subscriptionInstance.endDate},\"uri://kbplus/sub/${result.subscriptionInstance.identifier}\",${result.subscriptionInstance.impId}\n")
           }

           // Output the body text
           // writer.write("publication_title,print_identifier,online_identifier,date_first_issue_subscribed,num_first_vol_subscribed,num_first_issue_subscribed,date_last_issue_subscribed,num_last_vol_subscribed,num_last_issue_subscribed,embargo_info,title_url,first_author,title_id,coverage_note,coverage_depth,publisher_name\n");
           writer.write("publication_title,print_identifier,online_identifier,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,title_url,first_author,title_id,embargo_info,coverage_depth,coverage_notes,publisher_name,identifier.jusp\n");

           result.entitlements.each { e ->

             def start_date = e.startDate ? formatter.format(e.startDate) : '';
             def end_date = e.endDate ? formatter.format(e.endDate) : '';
             def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
             def publisher = e.tipp?.title?.publisher
             def print_identifier = e.tipp?.title?.getIdentifierValue('ISSN')
             if ( print_identifier == null ) 
               print_identifier = e.tipp?.title?.getIdentifierValue('ISBN')

             writer.write("\"${e.tipp.title.title}\",\"${print_identifier?:''}\",\"${e.tipp?.title?.getIdentifierValue('eISSN')?:''}\",${start_date},${e.startVolume?:''},${e.startIssue?:''},${end_date},${e.endVolume?:''},${e.endIssue?:''},\"${e.tipp?.hostPlatformURL?:''}\",,\"${title_doi}\",\"${e.embargo?:''}\",\"${e.tipp?.coverageDepth?:''}\",\"${e.tipp?.coverageNote?:''}\",\"${publisher?.name?:''}\",\"${e.tipp?.title?.getIdentifierValue('jusp')?:''}\"\n");
           }
           writer.flush()
           writer.close()
         }
         out.close()
      }
      json {
         def jc_id = result.subscriptionInstance.getSubscriber()?.getIdentifierByType('JC')?.value
         def response = [:]
         response.header = [:]
         response.entitlements = []

         response.header.type = result.subscriptionInstance.type.value
         response.header.version = "2.0"
         response.header.jcid = jc_id
         response.header.url = "uri://kbplus/sub/${result.subscriptionInstance.identifier}"

         result.entitlements.each { e ->

             def start_date = e.startDate ? formatter.format(e.startDate) : '';
             def end_date = e.endDate ? formatter.format(e.endDate) : '';
             def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
             def publisher = e.tipp?.title?.publisher

             def entitlement = [:]
             entitlement.title=e.tipp.title.title
             entitlement.issn=e.tipp?.title?.getIdentifierValue('ISSN')
             if ( entitlement.issn == null ) {
               entitlement.issn=e.tipp?.title?.getIdentifierValue('ISBN')
             }
             entitlement.eissn=e.tipp?.title?.getIdentifierValue('eISSN')
             entitlement.jusp=e.tipp?.title?.getIdentifierValue('jusp')
             entitlement.startDate=start_date;
             entitlement.endDate=end_date;
             entitlement.startVolume=e.startVolume?:''
             entitlement.endVolume=e.endVolume?:''
             entitlement.startIssue=e.startIssue?:''
             entitlement.endIssue=e.endIssue?:''
             entitlement.embargo=e.embargo?:''
             entitlement.titleUrl=e.tipp.hostPlatformURL?:''
             entitlement.doi=title_doi
             entitlement.coverageDepth = e.tipp.coverageDepth
             entitlement.coverageNote = e.tipp.coverageNote
             if ( publisher != null )
               entitlement.publisher = publisher?.name

             response.entitlements.add(entitlement);
         }
         render response as JSON
      }
    }
  }

  def pkg() {
    log.debug("package export id:${params.id} format=${response.format}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : ( ["csv","json","xml"].contains(response.format) ? 10000 : 10 );
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    log.debug("max = ${result.max}");
    def packageInstance = Package.get(params.id)

    def base_qry = null;
    def tipp_status_del = RefdataCategory.lookupOrCreate("TIPP Status", "Deleted")
    def publisher_org = RefdataCategory.lookupOrCreate("Organisational Role","Publisher")
    def qry_params = [ packageInstance]

    def filename = "publicExport_${packageInstance.name}"
    
    if ( params.filter ) {
      base_qry = " from TitleInstancePackagePlatform as tipp  where tipp.pkg = ? and ( tipp.status != ? ) and ( ( lower(tipp.title.title) like ? ) or ( exists ( from IdentifierOccurrence io where io.ti.id = tipp.title.id and io.identifier.value like ? ) ) ) and ( ( ? >= coalesce(tipp.accessStartDate, tipp.pkg.startDate) ) and ( ( ? <= tipp.accessEndDate ) or ( tipp.accessEndDate is null ) ) )"
      qry_params.add(tipp_status_del)
      qry_params.add("%${params.filter.trim().toLowerCase()}%")
      qry_params.add("%${params.filter}%")
      qry_params.add(new Date());
      qry_params.add(new Date());
    }
    else {
      base_qry = " from TitleInstancePackagePlatform as tipp where tipp.pkg = ?  and ( tipp.status != ? ) and ( ( ? >= coalesce(tipp.accessStartDate, tipp.pkg.startDate) ) and ( ( ? <= tipp.accessEndDate ) or ( tipp.accessEndDate is null ) ) )"
      qry_params.add(tipp_status_del)
      qry_params.add(new Date());
      qry_params.add(new Date());
    }

    if ( ( params.sort != null ) && ( params.sort.length() > 0 ) ) {
      base_qry += "order by tipp.${params.sort} ${params.order} "
    }
    
    log.debug("Base query for pkg export: ${base_qry}");
    
    result.num_pkg_rows = TitleInstancePackagePlatform.executeQuery("select count(tipp) "+base_qry, qry_params )[0]

    result.titlesList = TitleInstancePackagePlatform.executeQuery("select tipp "+base_qry, qry_params, [max:result.max, offset:result.offset]);
    result.transforms = grailsApplication.config.packageTransforms

    log.debug("Found ${result.num_pkg_rows} tipps. These will be exported")
    

    withFormat {
      html result
      json {
        def map = exportService.getPackageMap(packageInstance, result.titlesList)
        
        def json = map as JSON
        
          response.setHeader("Content-disposition", "attachment; filename=\"${filename}.json\"")
          response.contentType = "application/json"
          render json
      }
      xml {
        def starttime = exportService.printStart("Building XML Doc")
        def doc = exportService.buildDocXML("Packages")
        exportService.addPackageIntoXML(doc, doc.getDocumentElement(), packageInstance, result.titlesList)
        exportService.printDuration(starttime, "Building XML Doc")
        
        if( ( params.transformId ) && ( result.transforms[params.transformId] != null ) ) {
          String xml = exportService.streamOutXML(doc, new StringWriter()).getWriter().toString();
          transformerService.triggerTransform(null, filename, result.transforms[params.transformId], xml, response)
        }else{ // send the XML to the user
          response.setHeader("Content-disposition", "attachment; filename=\"${filename}.xml\"")
          response.contentType = "text/xml"
          exportService.streamOutXML(doc, response.outputStream)
        }

      }
    }
  }

  /**
  * Iterate the result of indentifiers HQL query in pkg() method, and return the required ident ns.
  **/
  def getIdentFromMap(id,ns,results){
    def idents = results.get(id)
    def ident_value = null
    idents.each{ array ->
      if(array[1].toLowerCase() == ns){
        ident_value = array[0]
      }
    }
    return ident_value
  }

  def idx() {
    def base_qry = " from Package as p order by p.name asc"
    def qry_params = []

    def num_pkg_rows = Subscription.executeQuery("select count(p) "+base_qry, qry_params )[0]
    def packages = Subscription.executeQuery("select p ${base_qry}", qry_params);

    withFormat {
      csv {
        response.setHeader("Content-disposition", "attachment; filename=KBPlusExportIndex.csv")
        response.contentType = "text/csv"
        def out = response.outputStream
        out.withWriter { writer ->
           writer.write("name,uri,identifier\n")
           packages.each { p ->
             writer.write("\"${p.name}\",\"publicExport/pkg/${p.id}?format=csv\",\"${p.id}\"\n");
           }
        }
        writer.flush()
        writer.close()
      }
      xml {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.KbPlusPackages {
          packages.each { p->
            packages {
              name(p.name)
              identifier(p.id)
            }
          }
        }
        render(contentType:'application/xml', text: writer.toString())
      }
      json {
        def response = [:]
        response.packages = []
        packages.each { p->
          response.packages.add([name:p.name,identifier:p.id])
        }
        render response as JSON
      }
    }

  }
}
