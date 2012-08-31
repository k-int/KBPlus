package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class PublicExportController {

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

         response.setHeader("Content-disposition", "attachment; filename=${result.subscriptionInstance.id}.csv")
         response.contentType = "text/csv"
         def out = response.outputStream
         out.withWriter { writer ->
           if ( ( params.omitHeader == null ) || ( params.omitHeader != 'Y' ) ) {
             writer.write("FileType,SpecVersion,JD_ID,TermStartDate,TermEndDate,SubURI\n")
             writer.write("${result.subscriptionInstance.type.value},\"2.0\",${jc_id},start,end,\"uri://kbplus/sub/${result.subscriptionInstance.id}\"\n")
           }

           // Output the body text
           writer.write("publication_title,print_identifier,online_identifier,date_first_issue_subscribed,num_first_vol_subscribed,num_first_issue_subscribed,date_last_issue_subscribed,num_last_vol_subscribed,num_last_issue_subscribed,embargo_info\n");

           result.entitlements.each { e ->
             writer.write("\"${e.tipp.title.title}\",\"${e.tipp?.title?.getIdentifierValue('ISSN')}\",\"${e.tipp?.title?.getIdentifierValue('eISSN')}\",${e.startDate?:''},${e.startVolume?:''},${e.startIssue?:''},${e.endDate?:''},${e.endVolume?:''},${e.endIssue?:''},${e.embargo?:''}\n");
           }
           writer.flush()
           writer.close()
         }
         out.close()
      }
    }
  }
}
