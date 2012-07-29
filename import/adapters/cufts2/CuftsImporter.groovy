#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.2'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.commons', module='commons-vfs2', version='2.0')
  // @Grab(group='commons-vfs', module='commons-vfs', version='20050307052300')
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

// Setup mongo
def options = new com.mongodb.MongoOptions()
options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def db = mongo.getDB('kbplus_ds_reconciliation')


// Good example: aaa_pinnacle_allenpress
//
// title	issn	e_issn	ft_start_date	ft_end_date	embargo_months	embargo_days	current_months	current_years	coverage	vol_ft_start	vol_ft_end	iss_ft_start	iss_ft_end	cit_start_date	cit_end_date	vol_cit_start	vol_cit_end	iss_cit_start	iss_cit_end	db_identifier	toc_url	journal_url	urlbase	publisher	abbreviation

// Copy without parameters will copy forward to same name in NoSQL DB

def CUFTSProcessingRules = [
   'title': [action:'copy', targetProperty:'title'],
   'issn': [ action:'identifier', IdType:'issn', targetProperty:'identifier'],
   'e_issn': [ action:'identifier', IdType:'e_issn', targetProperty:'identifier'],
   'ft_start_date': [ action:'copy'],
   'ft_end_date': [ action:'copy'],
   'embargo_months': [ action:'copy'],
   'embargo_days': [ action:'copy'],
   'current_months': [ action:'copy'],
   'current_years': [ action:'copy'],
   'coverage': [ action:'copy'],
   'vol_ft_start': [ action:'copy'],
   'vol_ft_end': [ action:'copy'],
   'iss_ft_start': [ action:'copy'],
   'iss_ft_end': [ action:'copy'],
   'cit_start_date': [ action:'copy'],
   'cit_end_date': [ action:'copy'],
   'vol_cit_start': [ action:'copy'],
   'vol_cit_end': [ action:'copy'],
   'iss_cit_start': [ action:'copy'],
   'iss_cit_end': [ action:'copy'],
   'db_identifier': [ action:'copy'],
   'toc_url': [ action:'copy'],
   'journal_url': [ action:'copy'],
   'urlbase': [ action:'copy'],
   'publisher': [ action:'copy'],
   'abbreviation': [ action:'copy']
]

if ( db == null ) {
  println("Failed to configure db.. abort");
  system.exit(1);
}


// def cufts_knowledgebase_website = new HTTPBuilder('http://cufts2.lib.sfu.ca')

loadCuftsFile('/home/ibbo/dev/KBPlus/import/adapters/cufts2/CUFTS_complete_20120701.tgz');

def loadCuftsFile(filename) {
  println("loading data from ${filename}");
  def fsManager = VFS.getManager();
  // def tgz_file = fsManager.resolveURI("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${filename}");
  // def tgz_file = fsManager.resolveFile("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${filename}");
  //def tgz_file = fsManager.resolveFile("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${filename}");
  def tgz_file = fsManager.resolveFile("tgz:file://${filename}");
  def update_file = tgz_file.getChild("update.xml");
  if ( update_file ) {
    println("Located update xml file in ${filename}");
    processUpdateFile(update_file);
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


def processUpdateFile(update_file) {
  def s = new XmlSlurper(new org.cyberneko.html.parsers.SAXParser())
  def xml = s.parse(update_file.inputStream)
  println("root name ${xml.name()}")
  // dumpChildren(xml);
  xml.BODY.XML.RESOURCE.each { r ->
    println("Processing resource...${r}\n\n");
    processResourceEntry(r);
  }
  println("Completed processing of update file");
}

def processResourceEntry(r) {
  println("Provider ${r.PROVIDER.text()}");
  println("Key ${r.KEY.text()}");
  println("Module ${r.MODULE.text()}");
  println("Name ${r.NAME.text()}");
  println("Type ${r.RESOURCE.text()}");
  r.SERVICES.SERVICE.each { e ->
    println("Service: ${e.text()}");
  }
}

def dumpChildren(n) {
  n.children().each { c ->
    println("Chid name ${c.name()}");
    dumpChildren(c);
  }
}

def processUpdateFile2(update_file) {
  
  def s = new XmlSlurper()
  def t = update_file.inputStream.text
  def cleaned_file = t.replaceAll('&ntilde;','ñ')
                      .replaceAll('&eacute;','é')
                      .replaceAll('&ecirc;','ê')
  def xml = s.parseText(cleaned_file)
  xml.resource.each { r ->
    println("Processing resource...${r}\n\n");
  }
}

def loadCuftsTitleData(file) {
  println("Loading ${file.name}")
  // Line one is the header and tells us what info we will get in *this* file
  def fr = new FileReader(file.inputStream)
  def header = fr.readNext()
  def header_cols = header.split('\t');
  println("Header cols: ${header_cols}");
  //def line
  // while ( line = fr.readNext() ) {
  //   String[] 
  // }
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
          examineCUFTSUpdateFile(db, cufts_knowledgebase_website, url);
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



def examineCUFTSUpdateFile(db, cufts_knowledgebase_website, name) {
  println("Test update file ${name}");
  def head_result = cufts_knowledgebase_website.request(HEAD) {
    uri.path="/knowledgebase/${name}"
    response.success = { resp ->
      println("resp parans: ${resp.params}");
      def content_length = resp.getLastHeader("Content-Length")?.value
      def last_modified = resp.getLastHeader("Last-Modified")?.value
      println("Content length for ${name} is ${content_length}, last modified is ${last_modified}");

      def file_info = db.sourceDataInfo.findOne(dataContext:'CUFTS', file:name);
      if ( !file_info ) {
        println("No current info about this file.. create and update");
        file_info = [
          dataContext:'CUFTS',
          file:name,
          lastModified:last_modified,
          lastSize:content_length
        ]

        processCUFTSUpdateFile(db, cufts_knowledgebase_website, name, file_info)
      }
      else {
        // File is present.. see if datestamp or size is different
        println("Checking file timestamp and size");
      }
    }
    // handler for any failure status code:
    response.failure = { resp ->
      println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
    }
  }
}

def processCUFTSUpdateFile(db, cufts_knowledgebase_website, name, file_info) {
  println("processCUFTSUpdateFile...");

  // InputStream is = new GZIPInputStream(new FileInputStream(file));
  // FileSystemManager fsManager = VFS.getManager();
  def fsManager = VFS.getManager();
  def tgz_file = fsManager.resolveURI("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${name}");

  println("Done");
  // List the children of the Jar file
  // FileObject[] children = jarFile.getChildren();
  // System.out.println( "Children of " + jarFile.getName().getURI() );
  // for ( int i = 0; i < children.length; i++ ) {
  //     System.out.println( children[ i ].getName().getBaseName() );
  // }

  // db.sourceDataInfo.save(file_info);
}

def lookupOrCreateTitle(Map params=[:]) {
  // println("lookupOrCreateTitle(${params})");
  // Old style: lookup by Title : def title = params.db.titles.findOne(title:params.title)
  def title = null
  if ( ( params.identifier ) && ( params.identifier.size() > 0 ) ) { // Try to match on identifier if present
    // Loop through all the available identifers and see if any match.. Repeat until a match is found.
    for ( int i=0; ( ( !title ) && ( i < params.identifier.size() ) ); i++ ) {
      // println("Attempting match.. ${params.identifier[i].type} ${params.identifier[i].value}");
      title = params.db.titles.findOne(identifier:[type:params.identifier[i].type, value: params.identifier[i].value])
    }
    if ( title ) {
      inc('titles_matched_by_identifier',params.stats);
      // If the located title doesn't have a publisher, but the current record does, add the default
      if ( !title.publisher && params.publisher ) {
        title.publisher = params.publisher?._id;
        title.lastmod = System.currentTimeMillis()
        params.db.titles.save(title);
      }
    }
    else {
      // println("Unable to match on any of ${params.identifier}");
    }
  }
  else {
    inc('titles_without_identifiers',params.stats);
  }

  if (!title)  {
    // Unable to locate title with identifier given... Try other dedup matches on other props if needed
    println("Create New title : ${params.title}, title=${title}, publisher=${params.publisher}");

    try {
      title = [
        _id:new org.bson.types.ObjectId(),
        title:params.title,
        identifier:params.identifier,    // identifier is a list, catering for many different values
        publisher:params.publisher?._id,
        sourceContext:'KBPlus',
        lastmod:System.currentTimeMillis()
      ]

      params.db.titles.save(title)
      inc('titles_created',params.stats);
    }
    catch ( Exception e ) {
      e.printStackTrace()
      println("Problem creating new title ${title} for t:${params.title} (id:${params.identifier}): ${e.message}");
    }
  }

  title
}

