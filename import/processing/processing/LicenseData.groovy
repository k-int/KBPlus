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
