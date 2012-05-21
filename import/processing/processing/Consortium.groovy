#!/usr/bin/groovy

@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.2'),
])


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


def badfile = new File("${args[0]}-BAD");

println("Processing ${args[0]}");
CSVReader r = new CSVReader( new InputStreamReader(new FileInputStream(args[0])))

def bad_rows = []

String [] nl;

String [] so_header_line = r.readNext()

println("Read column headings: ${so_header_line}");

int rownum = 0;
def stats = [:]
stats.added = 0;
stats.bad = 0;
stats.new = 0;
stats.existing = 0;
stats.total = 0;

while ((nl = r.readNext()) != null) {
  // institutional_name,ringold_id,ingenta_id,jc_id,ip_range,ukfamf_idp,athens_id,sector_name
  def reason = ""
  rownum++
  stats.total++
  boolean bad = false;
  String badreason = null;

  if ( ( nl[0] != null ) && ( nl[0].trim().length() > 0 ) ) {
    def cons_org = db.orgs.findOne(normName:nl[0].trim().toLowerCase())
    def org = db.orgs.findOne(jcId:nl[1]);

    if ( ! cons_org ) {
      cons_org = [
        _id:new org.bson.types.ObjectId(),
        name:nl[1],
        normName:nl[1].trim().toLowerCase(),
        lastmod:System.currentTimeMillis()
      ]
      db.orgs.save(cons_org);
    }

    if ( cons_org && org ) {
      println("Located org(${nl[1]}) to be a member of ${nl[0]}");
      if ( org.consortia == null ) {
        org.consortia = []
      }
      if ( ! org.consortia.contains(cons_org._id) ) {
        org.consortia.add(cons_org._id)
        db.orgs.save(org);
      }
    }
    else {
      println("Failed to locate org(${nl[1]}) to be a member of(${nl[0]})");
      badfile << "${nl[0]},${nl[1]},${nl[2]},\"Failure to lookup org\"\n"
      bad=true
    }
  }
  else {
    println("No name for row ${rownum}");
    badfile << "${nl[0]},${nl[1]},${nl[2]},\"No name for row ${rownum}\"\n"
    stats.bad++
  }
}


println("${stats}");

def statsfile = new File("stats.txt");
statsfile << "${new Date().toString()}\n\nConsortia import\n-----------\n\n"
stats.each { stat ->
  statsfile << "${stat.key} : ${stat.value}\n"
}
