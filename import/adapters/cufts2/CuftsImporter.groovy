#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.2'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2')
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


def cufts_knowledgebase_website = new HTTPBuilder('http://cufts2.lib.sfu.ca')
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
        processCUFTSUpdate(cufts_knowledgebase_website, url);
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



def processCUFTSUpdate(cufts_knowledgebase_website, name) {
  println("Test update file ${name}");
  def head_result = cufts_knowledgebase_website.request(HEAD) {
    uri.path="/knowledgebase/${name}"
    response.success = { resp ->
      println("resp parans: ${resp.params}");
      def content_length = resp.getLastHeader("Content-Length")?.value
      println("Content length for ${name} is ${content_length}");
    }
    // handler for any failure status code:
    response.failure = { resp ->
      println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
    }
  }
}
