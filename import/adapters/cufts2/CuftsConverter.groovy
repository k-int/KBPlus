
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.apache.commons', module='commons-vfs2', version='2.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.codehaus.plexus', module='plexus-utils', version='3.0.9')
])

import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import org.apache.http.*
import org.apache.http.protocol.*
import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat
import org.apache.commons.vfs2.*

// Custom entity resolution
import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;

// Good example: aaa_pinnacle_allenpress
//
// title	issn	e_issn	ft_start_date	ft_end_date	embargo_months	embargo_days	current_months	current_years	coverage	vol_ft_start	vol_ft_end	iss_ft_start	iss_ft_end	cit_start_date	cit_end_date	vol_cit_start	vol_cit_end	iss_cit_start	iss_cit_end	db_identifier	toc_url	journal_url	urlbase	publisher	abbreviation

public class CuftsConverter {
  
  def possible_date_formats = [
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
  ];

  // Copy without parameters will copy forward to same name in NoSQL DB
  def kbplus_column_config = [
     [ outputCol:'publication_title', source:'CUFTS', inputCol:'title', pos:-1 ],
     [ outputCol:'ID.issn',source:'CUFTS', inputCol:'issn', pos:-1 ],
     [ outputCol:'ID.eissn',source:'CUFTS', inputCol:'e_issn', pos:-1 ],
     [ outputCol:'date_first_issue_online',source:'CUFTS', inputCol:'ft_start_date', pos:-1 ],
     [ outputCol:'num_first_vol_online',source:'CUFTS', inputCol:'vol_ft_start', pos:-1 ],
     [ outputCol:'num_first_issue_online',source:'CUFTS', inputCol:'iss_ft_start', pos:-1 ],
     [ outputCol:'date_last_issue_online',source:'CUFTS', inputCol:'ft_end_date', pos:-1 ],
     [ outputCol:'num_last_vol_online',source:'CUFTS', inputCol:'vol_ft_end', pos:-1 ],
     [ outputCol:'num_last_issue_online',source:'CUFTS', inputCol:'iss_ft_end', pos:-1 ],
     [ outputCol:'ID.kbart_title_id',source:null, inputCol:null, pos:-1 ],
     [ outputCol:'embargo_info',source:null, inputCol:null, pos:-1 ],
     [ outputCol:'coverage_depth',source:'CUFTS', inputCol:'coverage', pos:-1 ],
     [ outputCol:'coverage_notes',source:'CUFTS', inputCol:'coverage', pos:-1 ],
     [ outputCol:'publisher_name',source:'CUFTS', inputCol:'', pos:-1 ],
     [ outputCol:'ID.doi',source:null, inputCol:null, pos:-1 ],
     [ outputCol:'platform.host.name',source:'CUFTS', inputCol:'', pos:-1 ],
     [ outputCol:'platform.host.url',source:'CUFTS', inputCol:'journal_url', pos:-1 ],
     [ outputCol:'platform.administrative.name',source:'CUFTS', inputCol:'', pos:-1 ],
     [ outputCol:'platform.administrative.url',source:'CUFTS', inputCol:'', pos:-1 ]
  ]
  
  
  public static void main(String[] args) {
    // def cufts_knowledgebase_website = new HTTPBuilder('http://cufts2.lib.sfu.ca')
    // loadCuftsFile('/home/ibbo/dev/KBPlus/import/adapters/cufts2/CUFTS_complete_20120701.tgz');
    def cc=new CuftsConverter()
    cc.loadCuftsFile(args[0])
  }
  
  def loadCuftsFile(filename) {
    println("loading data from ${filename}");
    def fsManager = VFS.getManager();
    def tgz_file = fsManager.resolveFile("tgz:file://${filename}");
    def update_file = tgz_file.getChild("update.xml");
    if ( update_file ) {
      println("Located update xml file in ${filename}");
      processUpdateFile(update_file, tgz_file)
    }
    else {
      println("Unable to locate update xml...");
      FileObject[] children = tgz_file.getChildren();
      System.out.println( "Children of " + tgz_file.getName().getURI() );
    
      for ( int i = 0; i < children.length; i++ ) {
          System.out.println( children[ i ].getName().getBaseName() );
          // loadCuftsTitleData(children[i]);
      }
    }
  }
  
  
  def processUpdateFile(update_file, archive) {
    def s = new XmlSlurper(new org.cyberneko.html.parsers.SAXParser())
    def xml = s.parse(update_file.inputStream)
    println("root name ${xml.name()}")
    xml.BODY.XML.RESOURCE.each { r ->
      println("Processing resource...${r}\n\n");
      processResourceEntry(r, archive);
    }
    println("Completed processing of update file");
  }
  
  def processResourceEntry(r, archive) {
    println("Provider ${r.PROVIDER.text()}");
    println("Key ${r.KEY.text()}");
    println("Module ${r.MODULE.text()}");
    println("Name ${r.NAME.text()}");
    println("Type ${r.RESOURCE.text()}");
  
    def resource_meta_info = [
      provider:r.PROVIDER.text(),
      key:r.KEY.text(),
      module:r.MODULE.text(),
      name:r.NAME.text(),
      resource:r.RESOURCE.text,
      services:[]
    ]
  
    r.SERVICES.SERVICE.each { e ->
      println("Service: ${e.text()}");
      resource_meta_info.services.add(e.text())
    }
  
    def prov_file = archive.getChild(r.KEY.text())
    if ( prov_file ) {
      println("Got provider file ${prov_file}");
      doConversion(prov_file, resource_meta_info)
    }
    else {
      println("Unable to locate ${r.KEY.text()}")
    }
  }
  
  def doConversion(file, resource_meta_info) {
    println("Loading ${file.name}")
    char separator = '\t'
    def possible_date_formats = [
      new SimpleDateFormat('yyyy/MM/dd')
    ]
  
    def target_file = new File("./target")
    target_file.mkdirs();
  
    new File("./target/${resource_meta_info.key}").withWriter { out ->
  
      println("Processing cufts FILE ${resource_meta_info.key}");
  
      out.writeLine("Provider,${resource_meta_info.provider}")
      out.writeLine("Package Identifier,CUFTS-${resource_meta_info.key}")
      out.writeLine("Package Name,${resource_meta_info.name}")
      out.writeLine("Agreement Term Start Year,")
      out.writeLine("Agreement Term End Year,")
      out.writeLine("Consortium,")
  
      def sub = [:]
      sub.identifier = resource_meta_info.key
      sub.name = resource_meta_info.name;
  
      CSVReader fr = new CSVReader( new InputStreamReader(file.inputStream), separator)
  
      // Line one is the header and tells us what info we will get in *this* file
      // String[] header = fr.readNext()
      def header = Arrays.asList(fr.readNext())
  
      println("Got file header (length = ${header.size()}) ${header}");
  
      mapHeadings(header)
      out.writeLine(kbplus_column_config.collect{it.outputCol}.join(','))
  
      String[] nl = null;
  
      while ((nl = fr.readNext()) != null) {
        if (nl.length == header.size()) {
          def cufts_row = [:]
          int num_cols = header.size()
          // println("Cool, row has right number of cols...${nl.length}, header length=${num_cols}");
          for ( int i=0; i < num_cols; i++ ) {
            cufts_row[header[i]] = nl[i];
          }
  
          // println("process row ${cufts_row}");
  
          // Title level processing begins here
          def target_identifiers = [];
  
          def publisher = null
          // publisher = lookupOrCreateOrg(name:____, db:db, stats:stats);
  
          // If there is an identifier, set up the appropriate matching...
          if ( cufts_row.issn && cufts_row.issn.trim().length() > 0 )
            target_identifiers.add([value:cufts_row.issn.trim(), type:'ISSN'])
        
          if ( cufts_row.e_issn && cufts_row.e_issn.trim()?.length() > 0 )
            target_identifiers.add([value:cufts_row.e_issn.trim(), type:'eISSN'])
  
          if ( target_identifiers.size() > 0 ) {
  
            def parsed_start_date = parseDate(cufts_row.ft_start_date,possible_date_formats)
            def parsed_end_date = parseDate(cufts_row.ft_end_date,possible_date_formats)
  
          }
        }
      }
    }
  }
  
  
  def processCUFTSIndexPage(cufts_knowledgebase_website) {
    try {
      def cufts_data_index_page = cufts_knowledgebase_website.get(path:'/knowledgebase/')
  
      println ("Got doc...");
      println (cufts_data_index_page.BODY.size());
  
      cufts_data_index_page?.each { row ->
        println(" doc level ${row}");
      }
  
      def files_examined = 0 
  
      cufts_data_index_page?.BODY?.TABLE?.TR?.each { row ->
        println("processing row ${row}");
        row.depthFirst().collect { it }.findAll { it.name() == "A" }.each {
          def url = it.@href.text();
          if ( url.endsWith("tgz") ) {
            files_examined++
            examineCUFTSUpdateFile(cufts_knowledgebase_website, url);
          }
          else {
            println("skipping file ${url} does not end with tgz");
          }
        }
      }
  
      println ("All done... ${files_examined} files checked");
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  }
  
  
  
  def examineCUFTSUpdateFile(cufts_knowledgebase_website, name) {
    println("Test update file ${name}");
    def head_result = cufts_knowledgebase_website.request(HEAD) {
      uri.path="/knowledgebase/${name}"
      response.success = { resp ->
        println("resp parans: ${resp.params}");
        def content_length = resp.getLastHeader("Content-Length")?.value
        def last_modified = resp.getLastHeader("Last-Modified")?.value
        println("Content length for ${name} is ${content_length}, last modified is ${last_modified}");
  
          println("No current info about this file.. create and update");
          file_info = [
            dataContext:'CUFTS',
            file:name,
            lastModified:last_modified,
            lastSize:content_length
          ]
  
          processCUFTSUpdateFile(db, cufts_knowledgebase_website, name, file_info)
      }
      // handler for any failure status code:
      response.failure = { resp ->
        println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
      }
    }
  }
  
  def parseDate(datestr, possible_formats) {
    def parsed_date = null;
    for(i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
      try {
        parsed_date = i.next().parse(datestr);
      }
      catch ( Exception e ) {
      }
    }
    parsed_date
  }
  
  def convertHeader(header, out) {
    output_cols = []
    header.each { elem ->
      def processing_config = CUFTSProcessingRules[elem]
      if ( processing_config != null ) {
        output_cols.add(processing_config.target_property)
      }
    }
  }
  
  def mapHeadings(cufts_headers) {
    kbplus_column_config.each { kbc ->
      kbc.pos = cufts_headers.indexOf(kbc.inputCol)
    }
    println("After map headings: ${kbplus_column_config}");
  }
}
