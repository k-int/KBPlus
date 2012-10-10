package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import groovy.xml.MarkupBuilder

class PublicExportController {

  def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")

  def index() { 
    def result = [:]

    def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered' order by s.name asc"
    def qry_params = []

    result.num_sub_rows = Subscription.executeQuery("select count(s) "+base_qry, qry_params )[0]
    result.subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params, [max:result.num_sub_rows]);

    result
  }

  def so() {
    log.debug("subscriptionDetails id:${params.id} format=${response.format}");
    def result = [:]

    def paginate_after = params.paginate_after ?: 19;
    result.max = params.max ? Integer.parseInt(params.max) : ( response.format == "csv" ? 10000 : 10 );
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
           writer.write("publication_title,print_identifier,online_identifier,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,title_url,embargo_info,first_author,title_id,coverage_depth,coverage_notes,publisher_name\n");

           result.entitlements.each { e ->

             def start_date = e.startDate ? formatter.format(e.startDate) : '';
             def end_date = e.endDate ? formatter.format(e.endDate) : '';
             def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
             def publisher = e.tipp?.title?.publisher

             writer.write("\"${e.tipp.title.title}\",\"${e.tipp?.title?.getIdentifierValue('ISSN')?:''}\",\"${e.tipp?.title?.getIdentifierValue('eISSN')?:''}\",${start_date},${e.startVolume?:''},${e.startIssue?:''},${end_date},${e.endVolume?:''},${e.endIssue?:''},\"${e.tipp?.hostPlatformURL?:''}\",\"${e.embargo?:''}\",,\"${title_doi}\",\"${e.tipp?.coverageDepth?:''}\",\"${e.tipp?.coverageNote?:''}\",\"${publisher?.name?:''}\"\n");
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
             entitlement.eissn=e.tipp?.title?.getIdentifierValue('eISSN')
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
             entitlement.publisher = publisher.name
             response.entitlements.add(entitlement);
         }
         render response as JSON
      }
    }
  }

  def idx() {
    def base_qry = " from Subscription as s where s.type.value = 'Subscription Offered' order by s.name asc"
    def qry_params = []

    def num_sub_rows = Subscription.executeQuery("select count(s) "+base_qry, qry_params )[0]
    def subscriptions = Subscription.executeQuery("select s ${base_qry}", qry_params);

    withFormat {
      csv {
        response.setHeader("Content-disposition", "attachment; filename=KBPlusExportIndex.csv")
        response.contentType = "text/csv"
        def out = response.outputStream
        out.withWriter { writer ->
           writer.write("name,uri,identifier\n")
           subscriptions.each { s ->
             writer.write("\"${s.name}\",\"publicExport/so/${s.identifier}?format=csv\",\"${s.identifier}\"\n");
           }
        }
        writer.flush()
        writer.close()
      }
      xml {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.KbPlusSubscriptionsOffered {
          subscriptions.each { s->
            subscriptionOffered {
              name(s.name)
              identifier(s.identifier)
            }
          }
        }
        render(contentType:'application/xml', text: writer.toString())
      }
      json {
        def response = [:]
        response.subscriptionsOffered = []
        subscriptions.each { s->
          response.subscriptionsOffered.add([name:s.name,identifier:s.identifier])
        }
        render response as JSON
      }
    }

  }
}
