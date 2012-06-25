#!/usr/bin/groovy

@GrabResolver(name='home', root='http://repo1.maven.org/maven2')
// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.5'),
  @Grab(group='gov.loc', module='bagit', version='4.0'),
  @Grab(group='commons-fileupload', module='commons-fileupload', version='1.2.2'),
  @Grab(group='classworlds', module='classworlds', version='1.1')
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils

def starttime = System.currentTimeMillis();
def possible_date_formats = [
  new SimpleDateFormat('dd/MM/yy'),
  new SimpleDateFormat('yyyy/MM'),
  new SimpleDateFormat('yyyy')
];


// Setup mongo
def options = new com.mongodb.MongoOptions()
options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def db = mongo.getDB('kbplus_ds_reconciliation')

if ( db == null ) {
  println("ERROR: Failed to configure db.. abort");
  System.exit(1);
}


def target_web_service = 'http://knowplusdev.edina.ac.uk:8080/oledocstore/KBPlusServlet'

println("Processing zip ${args[0]}");

def zipFile = new java.util.zip.ZipFile(new File(args[0]))

def csventry = null;
def docstore_components = []

def testds=true

zipFile.entries().each {
   // println("zip entry: ${it.name}");
   if ( it.name?.endsWith(".csv") ) {
     if ( !testds)
       csventry = it;
   }
   else {
     docstore_components.add([file:it, id:java.util.UUID.randomUUID().toString()]);
   }
}

if ( testds )
    docstoreUpload(zipFile, docstore_components);

def stats = [:]
def bad_rows = []

if ( csventry ) {
  // println("Processing csv: ${csventry.name}");
  CSVReader r = new CSVReader( new InputStreamReader(zipFile.getInputStream(csventry)))
  String [] nl;
  String [] lic_header_line = r.readNext()
  // println("Read column headings: ${lic_header_line}");
  int rownum = 0;
  int lic_bad = 0;
  int processed = 0;

  while ((nl = r.readNext()) != null) {
    rownum++
    boolean bad = false;
    String badreason = null;
    boolean has_data = false

    docstoreUpload(zipFile, docstore_components);

    // println("Lookung up licensor \"${nl[25]}\"");
    def norm_licensor_name = nl[25].trim().toLowerCase()
    def licensor_org = db.orgs.findOne(normName:norm_licensor_name);

    if ( !licensor_org ) {
      licensor_org = [:]
      licensor_org._id = new org.bson.types.ObjectId()
      licensor_org.name = nl[25].trim()
      licensor_org.normName = norm_licensor_name
      db.orgs.save(licensor_org);
    }

    def norm_licensee_name = nl[27].trim().toLowerCase()
    // println("Lookung up licensee \"${nl[27]}\" - find db.org.({normNam:\"${norm_licensee_name}\"})");
    def licensee_org = db.orgs.findOne(normName:norm_licensee_name);

    if ( licensor_org  ) {
      // println("got licensor ${licensor_org}");
      // println("got licensee ${licensee_org}");

      def license = [
        _id:new org.bson.types.ObjectId(),
        license_reference : nl[0],
        concurrent_users : nl[1],
        concurrent_users_note : nl[2],
        remote_access : nl[3],
        remote_access_note : nl[4],
        walkin_access : nl[5],
        walkin_access_note : nl[6],
        multisite_access : nl[7],
        multisite_access_note : nl[8],
        partners_access : nl[9],
        partners_access_note : nl[10],
        alumni_access : nl[11],
        alumni_access_note : nl[12],
        ill : nl[13],
        ill_note : nl[14],
        coursepack : nl[15],
        coursepack_note : nl[16],
        vle : nl[17],
        vle_note : nl[18],
        enterprise : nl[19],
        enterprise_note : nl[20],
        pca : nl[21],
        pca_note : nl[22],
        notice_period : nl[23],
        license_url : nl[24],
        licensor : licensor_org?._id,
        licensor_ref : nl[26],
        licensee : licensee_org?._id,
        licensee_ref : nl[28],
        license_type : nl[29],
        license_status : nl[30],
        subscriptions : [],
        lastmod: System.currentTimeMillis()
      ]

      for ( int i=31; i<nl.length; i++ ) {
        // println("Process subscription identifier ${nl[i]}");
        def sub_lookup = db.subscriptions.findOne(identifier:nl[i])
        if ( sub_lookup ) {
          // println("located subscription : ${sub_lookup}");
          license.subscriptions.add(sub_lookup._id);
        }
        else {
          println("ERROR: Unable to locate subscription ${nl[i]} whilst processing license");
        }
      }

      db.license.save(license);

      println(license)
    }
    else {
      println("-> ERROR: Unable to lookup licensor \"${norm_licensor_name}\"");
    }
  }

  println("Stats: ${stats}");

}
else {
  println("ERROR: NO CSV In zipfile");
}


def statsfile = new File("stats.txt");
statsfile << "${new Date().toString()}\n\nLicense import\n--------\n\n"
stats.each { stat ->
  statsfile << "${stat.key} : ${stat.value}\n"
}

if ( bad_rows.size() > 0 ) {
  println("file contained bad rows, dumping to ${args[0]}_BAD");
  File badfile = new File("${args[0]}_BAD");
  bad_rows.each { row ->
    badfile << row.row
    badfile << ",\"row ${row.rownum}/${rownum} - ${row.reason}\"\n"
  }
}

println("All done processing for ${args[0]}");

def docstoreUpload(zipfile, filelist) {
  // Create a new identifier
  def workdir = java.util.UUID.randomUUID().toString();
   
  File tempdir = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+workdir);
  println("tmpdir :${tempdir}");

  tempdir.mkdirs();

  filelist.each { f ->
    println("Copying ${f.file.getName()}");
    def target_file = new File("${tempdir}${System.getProperty('file.separator')}${f.file.getName()}")
    FileUtils.copyInputStreamToFile(zipfile.getInputStream(f.file), target_file);
  }
  
  // FileUtils.deleteQuietly(tempdir);
}
