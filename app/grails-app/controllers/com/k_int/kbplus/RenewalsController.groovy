package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;  

class RenewalsController {

  def genericOIDService

  // Map the parameter names we use in the webapp with the ES fields
  def reversemap = ['subject':'subject', 
                    'provider':'provid', 
                    'pkgname':'name'
                   ]


  def springSecurityService
  def ESWrapperService

  def index() { 
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def search() {

    log.debug("Search : ${params}");
    // Be mindful that the behavior of this controller is strongly influenced by the schema setup in ES.
    // Specifically, see KBPlus/import/processing/processing/dbreset.sh for the mappings that control field type and analysers
    // Internal testing with http://localhost:9200/kbplus/_search?q=subtype:'Subscription%20Offered'
    def result=[:]

    // Get hold of some services we might use ;)
    org.elasticsearch.groovy.node.GNode esnode = ESWrapperService.getNode()
    org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
    result.user = springSecurityService.getCurrentUser()

    def shopping_basket = UserFolder.findByUserAndShortcode(result.user,'SOBasket') ?: new UserFolder(user:result.user, shortcode:'SOBasket').save();

    if ( params.addBtn ) {
      log.debug("Add item ${params.addBtn} to basket");
      def oid = "com.k_int.kbplus.Subscription:${params.addBtn}"
      shopping_basket.addIfNotPresent(oid)
      shopping_basket.save(flush:true);
    }
    else if ( params.clearBasket=='yes' ) {
      log.debug("Clear basket....");
      shopping_basket.items?.clear();
      shopping_basket.save(flush:true)
    }
    else if ( params.generate=='yes' ) {
      log.debug("Generate");
      generate(materialiseFolder(shopping_basket.items))
      return
    }

    result.basket = materialiseFolder(shopping_basket.items)

    if (springSecurityService.isLoggedIn()) {

      try {

          params.max = Math.min(params.max ? params.int('max') : 10, 100)
          params.offset = params.offset ? params.int('offset') : 0

          //def params_set=params.entrySet()

          def query_str = buildQuery(params)
          log.debug("query: ${query_str}");

          def search = esclient.search{
            indices "kbplus"
            source {
              from = params.offset
              size = params.max
              query {
                query_string (query: query_str)
              }
              facets {
                consortia {
                  terms {
                    field = 'consortiaName'
                  }
                }
                contentProvider {
                  terms {
                    field = 'packages.cpname'
                  }
                }
              }

            }

          }

          if ( search?.response ) {
            result.hits = search.response.hits
            result.resultsTotal = search.response.hits.totalHits

            // We pre-process the facet response to work around some translation issues in ES
            if ( search.response.facets != null ) {
              result.facets = [:]
              search.response.facets.facets.each { facet ->
                def facet_values = []
                facet.value.entries.each { fe ->
                  facet_values.add([term: fe.term,display:fe.term,count:"${fe.count}"])
                }
                result.facets[facet.key] = facet_values
              }
            }
          }
      }
      finally {
        try {
        }
        catch ( Exception e ) {
          log.error("problem",e);
        }
      }

    }  // If logged in

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def selectPackages() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result.subscriptionInstance = Subscription.get(params.id)

    result.candidates = [:]
    def title_list = []
    def package_list = []

    result.titles_in_this_sub = result.subscriptionInstance.issueEntitlements.size();

    result.subscriptionInstance.issueEntitlements.each { e ->
      def title = e.tipp.title
      log.debug("Looking for packages offering title ${title.id} - ${title?.title}");

      title.tipps.each { t ->
        log.debug("  -> This title is provided by package ${t.pkg.id} on platform ${t.platform.id}");

        def title_idx = title_list.indexOf("${title.id}");
        def pkg_idx = package_list.indexOf("${t.pkg.id}:${t.platform.id}");

        if ( title_idx == -1 ) {
          log.debug("  -> Adding title ${title.id} to matrix result");
          title_list.add("${title.id}");
          title_idx = title_list.size();
        }

        if ( pkg_idx == -1 ) {
          log.debug("  -> Adding package ${t.pkg.id} to matrix result");
          package_list.add("${t.pkg.id}:${t.platform.id}");
          pkg_idx = package_list.size();
        }

        log.debug("  -> title_idx is ${title_idx} pkg_idx is ${pkg_idx}");

        def candidate = result.candidates["${t.pkg.id}:${t.platform.id}"]
        if ( !candidate ) {
          candidate = [:]
          result.candidates["${t.pkg.id}:${t.platform.id}"] = candidate;
          candidate.pkg=t.pkg.id
          candidate.platform=t.platform
          candidate.titlematch=0
          candidate.pkg = t.pkg
          candidate.pkg_title_count = t.pkg.tipps.size();
        }
        candidate.titlematch++;
        log.debug("  -> updated candidate ${candidate}");
      }
    }

    log.debug("titles list ${title_list}");
    log.debug("package list ${package_list}");

    log.debug("titles list size ${title_list.size()}");
    log.debug("package list size ${package_list.size()}");
    result
  }

  def buildQuery(params) {
    log.debug("BuildQuery...");

    StringWriter sw = new StringWriter()

    sw.write("subtype:'Subscription Offered'")

    reversemap.each { mapping ->

      // log.debug("testing ${mapping.key}");

      if ( params[mapping.key] != null ) {
        if ( params[mapping.key].class == java.util.ArrayList) {
          params[mapping.key].each { p ->
                sw.write(" AND ")
                sw.write(mapping.value)
                sw.write(":")
                sw.write("\"${p}\"")
          }
        }
        else {
          // Only add the param if it's length is > 0 or we end up with really ugly URLs
          // II : Changed to only do this if the value is NOT an *
          if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
            sw.write(" AND ")
            sw.write(mapping.value)
            sw.write(":")
            sw.write("\"${params[mapping.key]}\"")
          }
        }
      }
    }


    def result = sw.toString();
    result;
  }

  def materialiseFolder(f) {
    def result = []
    f.each {
      result.add(genericOIDService.resolveOID(it.referencedOid))
    }
    result
  }

  def generate(slist) {
    def m = generateMatrix(slist)
    exportWorkbook(m)
  }

  def generateMatrix(slist) {
    def titleMap = [:]
    def subscriptionMap = [:]

    log.debug("pre-pre-process");

    // Step one - Assemble a list of all titles and packages
    slist.each { sub ->

      def sub_info = [
        sub_idx : subscriptionMap.size(),
        sub_name : sub.name,
        sub_id : sub.id
      ]

      subscriptionMap[sub.id] = sub_info

      // For each subscription in the shopping basket
      sub.issueEntitlements.each { ie ->
        def title_info = titleMap[ie.tipp.title.id]
        if ( !title_info ) {
          title_info = [:]
          title_info.title_idx = titleMap.size()
          title_info.id = ie.tipp.title.id;
          title_info.issn = ie.tipp.title.getIdentifierValue('ISSN');
          title_info.eissn = ie.tipp.title.getIdentifierValue('eISSN');
          title_info.title = ie.tipp.title.title
          titleMap[ie.tipp.title.id] = title_info;
        }
      }
    }

    log.debug("Result will be a matrix of size ${titleMap.size()} by ${subscriptionMap.size()}");

    // Object[][] result = new Object[subscriptionMap.size()+1][titleMap.size()+1]
    Object[][] ti_info_arr = new Object[titleMap.size()][subscriptionMap.size()]
    Object[] sub_info_arr = new Object[subscriptionMap.size()]
    Object[] title_info_arr = new Object[titleMap.size()]

    subscriptionMap.values().each { v ->
      sub_info_arr[v.sub_idx] = v
    }

    titleMap.values().each { v ->
      title_info_arr[v.title_idx] = v
    }

    slist.each { sub ->
      def sub_info = subscriptionMap[sub.id]
      sub.issueEntitlements.each { ie ->
        def title_info = titleMap[ie.tipp.title.id]
        def ie_info = [:]
        ie_info.tipp_id = ie.tipp.id;
        ie_info.core = ie.coreTitle
        ti_info_arr[title_info.title_idx][sub_info.sub_idx] = ie_info
      }
    }


    [ti_info:ti_info_arr,title_info:title_info_arr,sub_info:sub_info_arr]
  }

  def exportWorkbook(m) {

    // read http://stackoverflow.com/questions/2824486/groovy-grails-how-do-you-stream-or-buffer-a-large-file-in-a-controllers-respon

    HSSFWorkbook workbook = new HSSFWorkbook();
 
    //
    // Create two sheets in the excel document and name it First Sheet and
    // Second Sheet.
    //
    HSSFSheet firstSheet = workbook.createSheet("FIRST SHEET");
 
    // Cell style for a present TI
    HSSFCellStyle present_cell_style = workbook.createCellStyle();  
    present_cell_style.setFillForegroundColor(HSSFColor.GREEN.index);  
    present_cell_style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

    // Cell style for a core TI
    HSSFCellStyle core_cell_style = workbook.createCellStyle();  
    core_cell_style.setFillForegroundColor(HSSFColor.YELLOW.index);  
    core_cell_style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

    int rc=0;
    // header
    int cc=0;
    HSSFRow row = null;
    HSSFCell cell = null;

    // Blank rows
    row = firstSheet.createRow(rc++);
    row = firstSheet.createRow(rc++);

    // Key
    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Key"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Title In Subscription"));
    cell.setCellStyle(present_cell_style);  
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Core Title"));
    cell.setCellStyle(core_cell_style);  
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Not In Subscription"));
    

    row = firstSheet.createRow(rc++);
    cc=4
    m.sub_info.each { sub ->
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${sub.sub_id}"));
    }
    
    // headings
    row = firstSheet.createRow(rc++);
    cc=0;
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("internal ID"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("Title"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("ISSN"));
    cell = row.createCell(cc++);
    cell.setCellValue(new HSSFRichTextString("eISSN"));
    
    m.sub_info.each { sub ->
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${sub.sub_name}"));
    }

    m.title_info.each { title ->

      row = firstSheet.createRow(rc++);
      cc = 0;

      // Internal title ID
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.id}"));
      // Title
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.title?:''}"));

      // ISSN
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.issn?:''}"));
      // eISSN
      cell = row.createCell(cc++);
      cell.setCellValue(new HSSFRichTextString("${title.eissn?:''}"));

      m.sub_info.each { sub ->
        cell = row.createCell(cc++);
        def ie_info = m.ti_info[title.title_idx][sub.sub_idx]
        if ( ie_info ) {
          if ( ie_info.core ) {
            cell.setCellValue(new HSSFRichTextString(""));
            cell.setCellStyle(core_cell_style);  
          }
          else {
            cell.setCellValue(new HSSFRichTextString(""));
            cell.setCellStyle(present_cell_style);  
          }
        }

      }
    }

    response.setHeader "Content-disposition", "attachment; filename='comparison.xls'"
    response.contentType = 'application/xls'
    workbook.write(response.outputStream)
    response.outputStream.flush()
 
  }
}
