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


def starttime = System.currentTimeMillis();


// Setup mongo
def options = new com.mongodb.MongoOptions()

options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def mdb = mongo.getDB('kbplus_ds_reconciliation')


if ( mdb == null ) {
  println("Failed to configure db.. abort");
  System.exit(1);
}

handleChangesSince(mdb, 'orgs',0) {
  println("*");
}

System.exit(0);

def handleChangesSince(db,
                       collname, 
                       timestamp, 
                       processingClosure) {

  def cursor = db."${collname}".find().sort(lastmod:1)
  cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
  cursor.each { item ->
    def local_copy = db."${collname}_localcopy".findOne([_id:item._id])
    if ( local_copy ) {
      println("Got local copy");
    }
    else {
      println("No local copy found");
      def copy_item = [
        _id:item._id,
        original:item
      ]
      db."${collname}_localcopy".save(copy_item);
    }

    processingClosure(item)
  }
}

