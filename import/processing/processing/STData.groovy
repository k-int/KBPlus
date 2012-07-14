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

if ( st_so_identifier.length < 2 ) {
  println("ERROR: No SO Identifier found in ${args[0]}");
  System.out.println("ERROR: No SO Identifier found in ${args[0]}");
  System.exit(1);
}

try {
  if ( st_so_identifier[1]?.endsWith('.csv') ) {
    st_so_identifier[1] = st_so_identifier[1].replace('.csv','')
  }
}
catch ( Exception e ) {
  e.printStackTrace();
  println("**ERROR processing ${args[0]} : {e.message}");
  System.err.println("**ERROR processing ${args[0]} : {e.message}");
}

println("Read column headings: ${so_header_line}");

def normalised_identifier = st_so_identifier[1].trim().toLowerCase()

def stats = [:]

if ( st_jc_id.length < 2 ) {
  println("**ERROR** No JC ID in file ${args[0]}. Exiting");
  System.exit(1);
}

def sub_org = db.orgs.findOne(jcId:st_jc_id[1]);
if ( !sub_org ) {
  println("Unable to locate org with identifier ${st_jc_id[1]}");
  System.exit(1);
}
else {
  println("Located org : ${sub_org}");
}

// Before we start.. lookup a subscription with so_identifier st_so_identifier
def sub = db.subscriptions.findOne(identifier:normalised_identifier)
if ( !sub ) {
  println("**ERROR** unable to locate subscription with identifier ${normalised_identifier}");
  System.exit(1);
}

def new_sub_record = [
   _id:new org.bson.types.ObjectId(),
   sub: sub._id,
   org: sub_org._id,
   lastmod:System.currentTimeMillis()
]

println("Created new subscription record with ID ${new_sub_record._id}");

db.subs.save(new_sub_record);

int rownum = 0;
int st_bad = 0;
int processed = 0;

try {
  while ((nl = r.readNext()) != null) {
    rownum++
    boolean bad = false;
    String badreason = null;
    boolean has_data = false
   
    // included_st, publication_title, print_identifier, online_identifier, date_first_issue_subscribed, num_first_vol, num_first_iss, last_vo, last_iss
    // embargo, core_title
  
    // Lookup title based on print_identifier, target_identifiers ['ISSN'] = print_identifier
    def title = null;
    def title_identifiers = []
  
    if ( nl[2]?.length() > 0 ) {
      println("Attempting lookup by ISSN: \"${nl[2]?.trim()}\"");
      title = db.titles.findOne(identifier:[type:'ISSN', value: nl[2]?.trim()])
      title_identifiers.add([type:'ISSN', value:nl[2]])
    }
  
    if ( ( !title ) && ( nl[3]?.length() > 0 ) ) {
      println("Attempting lookup by eISSN: \"${nl[3]?.trim()}\"");
      title = db.titles.findOne(identifier:[type:'eISSN', value: nl[3]?.trim()])
      title_identifiers.add([type:'eISSN', value:nl[2]])
    }
  
    // If we don't have a title here, it's likely that the ST file references a journal not defined
    // in an SO file. Maybe the institution negotiated an addition, or there is an error in the SO?
    // Either way, if there is a title and at least 1 identifier, we can add the item here
    if ( nl[1] && ( nl[1].length() > 0 ) && (!title) && ( title_identifiers.size() > 0 ) ) {
      title = lookupOrCreateTitle(title:nl[1],
                                  identifier:title_identifiers,
                                  publisher:null,
                                  db:db,
                                  stats:stats)
  
    }
  
    if ( title) {
      println("Matched title ${title}");
      inc('titles_matched',stats);
  
      def pkg = db.pkgs.findOne(subs:sub._id);
      if ( pkg ) {
        println("Located package ${pkg}");
  
  
        def tipp  = locateTIPP(db,title,pkg)
  
        if ( tipp ) {
          def new_st_record = db.st.findOne(tipp_id:tipp._id, 
                                             org_id:sub_org._id, 
                                             sub_id:sub._id)
  
          if ( ! new_st_record ) {
  
            def parsed_start_date = parseDate(nl[4],possible_date_formats)
            def parsed_end_date = parseDate(nl[7],possible_date_formats)
  
            new_st_record = [
              _id: new org.bson.types.ObjectId(),
              owner: new_sub_record._id,
              tipp_id : tipp._id,
              org_id : sub_org._id,
              sub_id : sub._id,
              stsy: st_start_year[1],
              stey: st_end_year[1],
              included: nl[0],
              date_first_issue_subscribed: parsed_start_date,
              num_first_vol_subscribed: nl[5],
              num_first_issue_subscribed: nl[6],
              date_last_issue_subscribed: parsed_end_date,
              num_last_vol_subscribed: nl[8],
              num_last_issue_subscribed: nl[9],
              embargo: nl[10],
              core_title: nl[11],
              lastmod:System.currentTimeMillis(),
              sourcefile:args[0]
            ]
            db.stTitle.save(new_st_record);
            println("Saved new st record with id ${new_st_record._id}");
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
}
catch ( Exception e ) {
  println("*** Problem processing ST file ${args[0]}...");
  System.err.println("*** Problem processing ST file ${args[0]}...");
  e.printStackTrace();
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


def locateTIPP(db, title, pkg) {
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

