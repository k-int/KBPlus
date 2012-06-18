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

// Setup mongo
def options = new com.mongodb.MongoOptions()
options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def db = mongo.getDB('kbplus_ds_reconciliation')

if ( db == null ) {
  println("Failed to configure db.. abort");
  system.exit(1);
}


// def cufts_knowledgebase_website = new HTTPBuilder('http://cufts2.lib.sfu.ca')

loadCuftsFile('CUFTS_complete_20120601.tgz');

def loadCuftsFile(filename) {
  println("loading data from ${filename}");
  def fsManager = VFS.getManager();
  // def tgz_file = fsManager.resolveURI("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${filename}");
  // def tgz_file = fsManager.resolveFile("tar:gz:http://cufts2.lib.sfu.ca/knowledgebase/${filename}");
  def tgz_file = fsManager.resolveFile("tgz:/home/ibbo/CUFTS_complete_20120601.tgz");
  def update_file = tgz_file.getChild("update.xml");
  if ( update_file ) {
    println("Located update xml file in ${filename}");
  }
  else {
    println("Unable to locate update xml...");
    FileObject[] children = tgz_file.getChildren();
    System.out.println( "Children of " + tgz_file.getName().getURI() );
    for ( int i = 0; i < children.length; i++ ) {
        System.out.println( children[ i ].getName().getBaseName() );
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

