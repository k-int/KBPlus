#!/usr/bin/groovy

// @GrabResolver(name='home', root='http://repo1.maven.org/maven2')
// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.5'),
  @Grab(group='gov.loc', module='bagit', version='4.0'),
  @Grab(group='commons-fileupload', module='commons-fileupload', version='1.2.2'),
  @Grab(group='classworlds', module='classworlds', version='1.1'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2' ),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2' )
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import groovy.xml.MarkupBuilder

import groovyx.net.http.ContentType.*
import groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset


println("\n\n\nLicense Mappings File");

def starttime = System.currentTimeMillis();

println("Processing file ${args[0]}");

def charset = java.nio.charset.Charset.forName('ISO-8859-1');

if ( args.length > 1 ) {
  charset = java.nio.charset.Charset.forName(args[1]);
}

CSVReader r = new CSVReader( new InputStreamReader(new FileInputStream(args[0]),charset) )

// Setup mongo
def options = new com.mongodb.MongoOptions()
options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def db = mongo.getDB('kbplus_ds_reconciliation')

// Skip header line
r.readNext()

String [] nl;
while ((nl = r.readNext()) != null) {
  // println("Got row ${nl[0]} ${nl[1]}");
  // def lic = db.license.findOne([license_reference:nl[0]});
  // def sub = db.license.findOne([license_reference:nl[0]});
  def normalised_identifier = nl[0].trim().toLowerCase().replaceAll('-','_')
  // println("Processing subscription ${nl[0]} normalised to ${normalised_identifier}");
  def sub = db.subscriptions.findOne(identifier:normalised_identifier);
  def lic = db.license.findOne(license_reference:nl[1]);
  if ( sub && lic ) {
    println("Located sub: ${sub._id} for identifier ${nl[0]} and lic: ${lic._id} for identifier ${nl[1]}");
    sub.linkedLicense = lic._id
    db.subscriptions.save(sub);
  }
  else {
    println("Unable to locate one of sub or lic.... for identifier ${nl[0]}");
  }
}


