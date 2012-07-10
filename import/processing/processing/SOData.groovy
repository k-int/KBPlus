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
  system.exit(1);
}


// To clear down the gaz: curl -XDELETE 'http://localhost:9200/gaz'
// CSVReader r = new CSVReader( new InputStreamReader(getClass().classLoader.getResourceAsStream("./IEEE_IEEEIEL_2012_2012.csv")))
println("Processing ${args[0]}");
CSVReader r = new CSVReader( new InputStreamReader(new FileInputStream(args[0])))

def bad_rows = []

String [] nl;

String [] so_name_line = r.readNext()
String [] so_identifier_line = r.readNext()
String [] so_provider_line = r.readNext()
String [] so_package_identifier_line = r.readNext()
String [] so_package_name_line = r.readNext()
String [] so_agreement_term_start_yr_line = r.readNext()
String [] so_agreement_term_end_yr_line = r.readNext()
String [] so_consortium_line = r.readNext()
String [] so_num_prop_id_cols_line = r.readNext()
int num_prop_id_cols = Integer.parseInt(so_num_prop_id_cols_line[1] ?: "0");
String [] so_num_platforms_listed_line = r.readNext()
int num_platforms_listed = Integer.parseInt(so_num_platforms_listed_line[1] ?: "0");
String [] so_header_line = r.readNext()

if ( num_platforms_listed == 0 ) {
  num_platforms_listed = 1
  println("**WARNING** num_platforms_listed = 0, defaulting to 1!");
}

def default_tags = []
if ( args.length > 1 ) {
  default_tags.add(args[1]);
}

println("Read column headings: ${so_header_line}");

def stats = [:]

def org = lookupOrCreateOrg(name:so_provider_line[1], db:db, stats:stats);

def sub = db.subscriptions.findOne(identifier:so_identifier_line[1])
if ( !sub ) {
  sub = [:]
  sub._id = new org.bson.types.ObjectId();
}
sub.identifier = so_identifier_line[1]
sub.name = so_name_line[1];
sub.start_date_str = so_agreement_term_start_yr_line[1]
sub.end_date_str=so_agreement_term_end_yr_line[1]
sub.start_date = parseDate(so_agreement_term_start_yr_line[1],possible_date_formats)
sub.end_date = parseDate(so_agreement_term_end_yr_line[1],possible_date_formats)

def consortium = null;
if ( ( so_consortium_line[1] != null ) && ( so_consortium_line[1].length() > 0 ) )  {
  consortium = lookupOrCreateOrg(name:so_consortium_line[1], db:db, stats:stats);
  sub.consortium = consortium._id;
}

db.subscriptions.save(sub);

def pkg = lookupOrCreatePackage(identifier:so_package_identifier_line[1], 
                                provider:so_provider_line[1],
                                name:so_package_name_line[1], 
                                db:db, 
                                stats:stats)

pkg.subs.add(sub._id);


// Verify that the pkg has a "contentProvider" of the org! If not, add and update.
if ( pkg.contentProvider == null ) {
  println("Set ${pkg.name}(${pkg._id}) content provider to ${org.name}(${org._id})");
  pkg.contentProvider = org._id;
  pkg.lastmod = System.currentTimeMillis()
  db.pkgs.save(pkg)
}

def so_count = 0
def so_bad = 0
def so_good = 0
def rownum = 12;

while ((nl = r.readNext()) != null) {
  rownum++
  boolean bad = false;
  String badreason = null;
  // publication_title,print_identifier,online_identifier,date_first_issue_online,
  // num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,
  // num_last_issue_online,title_id,embargo_info,coverage_depth,coverage_notes,
  // publisher_name,DOI,platform_name,platform_role,platform_title_url,platform_name,platform_role,platform_title_url

  boolean has_data = false
  nl.each {
    if ( ( it != null ) && ( it.trim() != '' ) ) 
      has_data = true;
  }

  if ( !has_data )
    continue;

  try {
    // Data Quality
    if ( present(nl[0] ) ) {
      println "**Processing pub title:${nl[0]}, print identifier ${nl[1]} (${num_prop_id_cols} prop cols, ${num_platforms_listed} plat cols)"
      so_count++;
      so_good++;
      def target_identifiers = [];
      def tipp_private_identifiers = [];
  
      def publisher = null
      if ( present(nl[13]) ) {
        println("Publisher name: ${nl[13]}")
        publisher = lookupOrCreateOrg(name:nl[13], db:db, stats:stats);
      }
      else {
        // println("*** Publisher is null - Linking to UNKNOWN PUBLISHER");
        inc('bad_null_publisher',stats);
        // publisher = lookupOrCreateOrg(name:'Unknown Publisher', db:db, stats:stats);
      }
  
      // If there is an identifier, set up the appropriate matching...
      if ( present(nl[1]) ) 
        target_identifiers.add([type:'ISSN', value:nl[1].trim()])
      if ( present(nl[2]) ) 
        target_identifiers.add([type:'eISSN', value:nl[2].trim()])
      if ( present(nl[9]) ) 
        // tipp_private_identifiers.add([type:'KBART', value:nl[9].trim()])
        tipp_private_identifiers.add([type:pkg.identifier, value:nl[9].trim()])
      if ( present(nl[14]) ) 
        target_identifiers.add([type:'DOI', value:nl[14].trim()])
  
      for ( int i=0; i<num_prop_id_cols; i++ ) {
        tipp_private_identifiers.add([type:'EXTERNAL', value:nl[15+i].trim()])
      }
  
      def title = lookupOrCreateTitle(title:nl[0],
                                      identifier:target_identifiers,
                                      publisher:publisher,
                                      db:db, 
                                      stats:stats)
  
      // Work out exactly how many columns there should be
      // II : It doesn't appear possible to do this, cant see any logic to number of cols that appear.
      int expected_num_cols = 15+num_prop_id_cols+num_platforms_listed+1;
      // if ( nl.size() != expected_num_cols ) {
      //   println("WARNING row ${so_count} contains ${nl.size()} columns, but we expect ${expected_num_cols}");
      //   inc('bad_col_count',stats);
      //   bad=true
      //   badreason="unexpected number of columns (expected ${expected_num_cols}, got ${nl.size()}"
      //   so_bad++;
      // }
      def parsed_start_date = parseDate(nl[3],possible_date_formats)
      def parsed_end_date = parseDate(nl[6],possible_date_formats)

      if ( ( parsed_start_date == null ) || ( parsed_start_date.getYear() > 2090 ) ) {
        println("Unable to parse start date ${nl[3]}")
        inc('bad_start_date',stats);
        bad=true
        badreason="Cannot parse start date: ${nl[3]}"
        parsed_start_date = sub.start_date
      }

      if ( parsed_end_date == null ) {
        parsed_end_date = sub.end_date
      }

      // else {
  
        def host_platform = null;
        def host_platform_url = null;
        def additional_platform_links = []
  
        for ( int i=0; i<num_platforms_listed; i++ ) {
          int position = 15+num_prop_id_cols+(i*3)   // Offset past any proprietary identifiers.. This needs a test case.. it's fraught with danger
  
          if ( ( nl.size() >= position+2 ) && 
               ( nl[position] ) && 
               ( nl[position].length() > 0 ) ) {
            def platform = lookupOrCreatePlatform(name:nl[position], 
                                                  prov:"Platform for SO ${args[0]}:${rownum}",
                                                  type:nl[position+1],
                                                  db:db, 
                                                  sourceContext:'KBPlus',
                                                  stats:stats)
  
            def platform_role = nl[position+1]
            def platform_url = nl[position+2]
  
            println("Process platform ${nl[position]} / ${platform_role} / ${platform_url}");
  
            if ( platform_role.trim() == 'host' ) {
              host_platform = platform;
              host_platform_url = platform_url
            }
            else {
              // TODO: Add to additional TIPP_Platform
              println("Non host platform: ${platform_role} : ${platform_url}");
              def additional_platform = lookupOrCreatePlatform(name:nl[position],type:nl[position+1],db:db,stats:stats,prov:"Add Platform for SO ${args[0]}:${rownum}")
              additional_platform_links.add([platformId:additional_platform._id, role:platform_role, platformUrl:platform_url]);
            }
          }
        }
    
        // Find tipp
        if ( title && pkg && host_platform && title._id && pkg._id && host_platform._id ) {
          def tipp = createTipp(titleid:title._id, pkgid:pkg._id, platformid:host_platform._id, db:db, stats:stats)
          tipp.startDateString = nl[3]
          tipp.startDate = parsed_start_date
          tipp.startVolume = nl[4]
          tipp.startIssue = nl[5]
          tipp.endDateString = nl[6]
          tipp.endDate = parsed_end_date
          tipp.endVolume = nl[7]
          tipp.endIssue = nl[8]
          tipp.title_id = nl[9]
          tipp.embargo = nl[10]
          tipp.coverageDepth = nl[11]
          tipp.coverageNote = nl[12]
          tipp.identifiers = tipp_private_identifiers
          tipp.hostPlatformURL = host_platform_url
          tipp.additionalPlatformLinks = additional_platform_links
          tipp.source = "${args[0]}:${rownum}"
          tipp.sourceContext = 'KBPlus'
          tipp.ies.add(sub._id)
          tipp.tags = default_tags;
  
          db.tipps.save(tipp)
        }
        else {
          println("One of title-${title}, pkg-${pkg} or platform-${host_platform} are missing!!!");
          inc('missing_critical_data',stats);
        }
      //}
    }
    else {
      println("Row is missing critical data, add to bad file");
      bad = true
      so_bad++;
      badreason="Row is missing critical data"
    }
  }
  catch ( Exception e ) {
    System.err.println("${args[0]} - row ${rownum}");
    e.printStackTrace();
    bad = true
    so_bad++;
    badreason=e.message
  }
 
  if ( bad ) {
    bad_rows.add([row:nl,reason:badreason, rownum:rownum]);
  }

}

println("All done - processed ${so_count} so records. Bad count=${so_bad}, good=${so_good}");
println("Stats: ${stats}");

def statsfile = new File("stats.txt");
statsfile << "${new Date().toString()}\n\nSO import\n--------\n\n"
stats.each { stat ->
  statsfile << "${stat.key} : ${stat.value}\n"
}

def so_statsfile = new File("so_stats.csv");
so_statsfile << "${args[0]},${stats.pkgs_created},${stats.titles_matched_by_identifier},${stats.tipp_created},${stats.titles_matched_by_title},${bad_rows.size()}\n"


if ( bad_rows.size() > 0 ) {
  println("file contained bad rows, dumping to ${args[0]}_BAD");
  File badfile = new File("${args[0]}_BAD");
  bad_rows.each { row ->
    badfile << row.row
    badfile << ",\"row ${row.rownum}/${rownum} - ${row.reason}\"\n"
  }
}

println("All done processing for ${args[0]}");

def present(v) {
  if ( ( v != null ) && 
       ( v.trim().length() > 0 ) && 
       ( ! ( v.trim().equalsIgnoreCase('n/a') ) ) )
    return true

  return false
}

def lookupOrCreateOrg(Map params = [:]) {
  // println("lookupOrCreateOrg(${params})");

  def target_norm_name = params.name?.trim().toLowerCase();

  def org = params.db.orgs.findOne(normName:target_norm_name)

  if ( org == null ) {
    org = [
      _id:new org.bson.types.ObjectId(),
      name:params.name,
      normName:params.name?.trim().toLowerCase(),
      lastmod:System.currentTimeMillis()
    ]
    params.db.orgs.save(org)
    inc('orgs_created',params.stats);
  }
  
  org
}

def lookupOrCreatePackage(Map params=[:]) {
  // println("lookupOrCreatePackage(${params})");
  def compound_identifier = "${params.provider}:${params.identifier}"
  def norm_identifier = compound_identifier.replaceAll("\\W", "");

  def pkg = params.db.pkgs.findOne(normIdentifier:norm_identifier)
  if ( pkg == null ) {
    pkg = [
      _id:new org.bson.types.ObjectId(),
      identifier:compound_identifier,
      normIdentifier:norm_identifier,
      name:params.name,
      lastmod:System.currentTimeMillis(),
      sourceContext:'KBPlus',
      subs:[]
    ]
    params.db.pkgs.save(pkg)
    inc('pkgs_created',params.stats);
  }

  pkg
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
      // If the located title doesn't have a publisher, but the current record does, add the default
      if ( !title.publisher && params.publisher ) {
        title.publisher = params.publisher?._id;
        title.lastmod = System.currentTimeMillis()
        params.db.titles.save(title);
      }
    }
    else {
      println("POSSIBLE ERROR : Unable to match on any of ${params.identifier}");
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

def lookupOrCreatePlatform(Map params=[:]) {
  // println("lookupOrCreatePlatform(${params})");
  def platform = null;

  String normname = params.name.trim().toLowerCase();

  platform = params.db.platforms.findOne(normname:normname)

  if ( !platform ) {
    platform = [
      _id:new org.bson.types.ObjectId(),
      name:params.name,
      normname:normname,
      provenance:params.prov,
      type:params.type,
      sourceContext:'KBPlus',
      lastmod:System.currentTimeMillis()
    ]
    params.db.platforms.save(platform)
    inc('platforms_created',params.stats);

  }

  platform;
}

def lookupOrCreateTipp(Map params=[:]) {
  def tipp = null;

  tipp = params.db.tipps.findOne(titleid:params.titleid, pkgid:params.pkgid, platformid:params.platformid)

  if ( !tipp ) {
    tipp = createTipp(params);
  }

  tipp
}

def createTipp(Map params=[:]) {
  def tipp = null;
  tipp = [
    _id:new org.bson.types.ObjectId(),
    titleid:params.titleid,
    pkgid:params.pkgid,
    platformid:params.platformid,
    sourceContext:'KBPlus',
    lastmod:System.currentTimeMillis(),
    ies:[]
  ]
  params.db.tipps.save(tipp)
  inc('tipp_created',params.stats);

  tipp
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
