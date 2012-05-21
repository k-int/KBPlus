#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.5')
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat


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
  println("Failed to configure db.. abort");
  System.exit(1);
}


println("Processing zip ${args[0]}");

def zipFile = new java.util.zip.ZipFile(new File(args[0]))

def csventry = null;
def docstore_components = []

zipFile.entries().each {
   println("zip entry: ${it.name}");
   if ( it.name?.endsWith(".csv") ) {
     csventry = it;
   }
   else {
     docstore_components.add(it);
   }
}

def stats = [:]
def bad_rows = []

if ( csventry ) {
  println("Processing csv: ${csventry.name}");
  CSVReader r = new CSVReader( new InputStreamReader(zipFile.getInputStream(csventry)))
  String [] nl;
  String [] lic_header_line = r.readNext()
  println("Read column headings: ${lic_header_line}");
  int rownum = 0;
  int lic_bad = 0;
  int processed = 0;

  while ((nl = r.readNext()) != null) {
    rownum++
    boolean bad = false;
    String badreason = null;
    boolean has_data = false

    println("Lookung up licensor ${nl[25]}");
    def licensor_org = db.orgs.findOne(name:nl[25]);
    println("Lookung up licensee ${nl[27]}");
    def licensee_org = db.orgs.findOne(name:nl[27]);
    if ( licensor_org && licensee_org ) {
      println("got licensor ${licensor_org}");
      println("got licensee ${licensee_org}");

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
        licensor : licensor_org._id,
        licensor_ref : nl[26],
        licensee : licensee_org._id,
        licensee_ref : nl[28],
        license_type : nl[29],
        license_status : nl[30],
        subscriptions : [],
        lastmod: System.currentTimeMillis()
      ]

      for ( int i=31; i<nl.length; i++ ) {
        println("Process subscription identifier ${nl[i]}");
        def sub_lookup = db.subscriptions.findOne(identifier:nl[i])
        if ( sub_lookup ) {
          println("located subscription : ${sub_lookup}");
          license.subscriptions.add(sub_lookup._id);
        }
      }

      db.license.save(license);

      println(license)
    }
    else {
      println("ERROR: Unable to lookup licensor or licensee");
    }
  }

  println("Stats: ${stats}");

}
else {
  println("NO CSV In zipfile");
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
