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


// To clear down the gaz: curl -XDELETE 'http://localhost:9200/gaz'
// CSVReader r = new CSVReader( new InputStreamReader(getClass().classLoader.getResourceAsStream("./IEEE_IEEEIEL_2012_2012.csv")))
println("Processing ${args[0]}");
CSVReader r = new CSVReader( new InputStreamReader(new FileInputStream(args[0])))

def bad_rows = []

String [] nl;

String [] st_jc_id = r.readNext()
String [] st_so_identifier = r.readNext()
String [] st_start_year = r.readNext()
String [] st_end_year = r.readNext()
String [] so_header_line = r.readNext()

println("Read column headings: ${so_header_line}");

def stats = [:]

def sub_org = db.orgs.findOne(jcId:st_jc_id[1]);
if ( !sub_org ) {
  println("Unable to locate org with identifier ${st_jc_id[1]}");
  System.exit(1);
}
else {
  println("Located org : ${sub_org}");
}

// Before we start.. lookup a subscription with so_identifier st_so_identifier
def sub = db.subscriptions.findOne(identifier:st_so_identifier[1])
if ( !sub ) {
  println("unable to locate subscription with identifier ${st_so_identifier[1]}");
  System.exit(1);
}

def new_sub_record = [
   _id:new org.bson.types.ObjectId(),
   sub: sub._id,
   org: sub_org._id,
   lastmod:System.currentTimeMillis()
]

db.subs.save(new_sub_record);

int rownum = 0;
int st_bad = 0;
int processed = 0;

while ((nl = r.readNext()) != null) {
  rownum++
  boolean bad = false;
  String badreason = null;
  boolean has_data = false
 
  // included_st, publication_title, print_identifier, online_identifier, date_first_issue_subscribed, num_first_vol, num_first_iss, last_vo, last_iss
  // embargo, core_title

  // Lookup title based on print_identifier, target_identifiers ['ISSN'] = print_identifier
  def title = null;
  if ( nl[2]?.length() > 0 ) {
    println("Attempting lookup by ISSN: \"${nl[2]?.trim()}\"");
    title = db.titles.findOne(identifier:[type:'ISSN', value: nl[2]?.trim()])
  }

  if ( ( !title ) && ( nl[3]?.length() > 0 ) ) {
    println("Attempting lookup by eISSN: \"${nl[3]?.trim()}\"");
    title = db.titles.findOne(identifier:[type:'eISSN', value: nl[3]?.trim()])
  }

  if ( title) {
    println("Matched title ${title}");
    inc('titles_matched',stats);

    def pkg = db.pkgs.findOne(subs:sub._id);
    if ( pkg ) {
      println("Located package ${pkg}");


      def tipp  = locateTitle(db,title,pkg)

      if ( tipp ) {
        def new_st_record = db.st.findOne(tipp_id:tipp._id, 
                                           org_id:sub_org._id, 
                                           sub_id:sub._id)

        if ( ! new_st_record ) {
          new_st_record = [
            _id: new org.bson.types.ObjectId(),
            owner: new_sub_record._id,
            tipp_id : tipp._id,
            org_id : sub_org._id,
            sub_id : sub._id,
            stsy: st_start_year,
            stey: st_end_year,
            included: nl[0],
            date_first_issue_subscribed: nl[4],
            num_first_vol_subscribed: nl[5],
            num_first_issue_subscribed: nl[6],
            date_last_issue_subscribed: nl[7],
            num_last_vol_subscribed: nl[8],
            num_last_issue_subscribed: nl[9],
            embargo: nl[10],
            core_title: nl[11],
            lastmod:System.currentTimeMillis()
          ]
          db.stTitle.save(new_st_record);
        }
        else {
          println("Located existing st record...");
        }
      }
      else {
      }
      processed++;
      // tipps.each { tipp ->
      //   println(tipp)
      // }
    }
    else {
      println("Failed to locate package matching this subscription");
    }
  }
  else {
    println("Failed to match title with ISSN \"${nl[2]}\" or eISSN \"${nl[3]}\"");
    inc('titles_unmatched',stats);
    bad = true
    st_bad++;
    badreason="Unable to locate title for ISSN \"${nl[2]}\"  or eISSN \"${nl[3]}\"";
  }

  if ( bad ) {
    bad_rows.add([row:nl,reason:badreason, rownum:rownum]);
  }
}

if ( processed == 0 ) {
  println("NO tipp rows, copy subscription forward");
}

println("Stats: ${stats}");

def statsfile = new File("stats.txt");
statsfile << "${new Date().toString()}\n\nST import\n--------\n\n"
stats.each { stat ->
  statsfile << "${stat.key} : ${stat.value}\n"
}

def so_statsfile = new File("so_stats.csv");
// so_statsfile << "${args[0]},${stats.pkgs_created},${stats.titles_matched_by_identifier},${stats.tipp_created},${stats.titles_matched_by_title},${bad_rows.size()}\n"


if ( bad_rows.size() > 0 ) {
  println("file contained bad rows, dumping to ${args[0]}_BAD");
  File badfile = new File("${args[0]}_BAD");
  bad_rows.each { row ->
    badfile << row.row
    badfile << ",\"row ${row.rownum}/${rownum} - ${row.reason}\"\n"
  }
}

println("All done processing for ${args[0]}");


def locateTitle(db, title, pkg) {
  def result = null
  def tipps = db.tipps.find(titleid: title._id, pkgid: pkg._id)
  println("Located ${tipps.size()} tipps for that title");
  if ( tipps.size() == 1 ) {
    result=tipps[0]
  }
  else {
    println("Unable to locate unique tipp");
  }
  result
}


def present(v) {
  if ( ( v != null ) && ( v.trim().length() > 0 ) )
    return true

  return false
}

def inc(countername, statsmap) {
  if ( statsmap[countername] == null ) {
    statsmap[countername] = 1
  }
  else {
    statsmap[countername]++
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
