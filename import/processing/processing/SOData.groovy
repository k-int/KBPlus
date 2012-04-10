#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.2')
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader

def starttime = System.currentTimeMillis();

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
CSVReader r = new CSVReader( new InputStreamReader(getClass().classLoader.getResourceAsStream("./IEEE_IEEEIEL_2012_2012.csv")))

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
int num_prop_id_cols = Integer.parseInt(so_num_prop_id_cols_line[1] ?: 0);
String [] so_num_platforms_listed_line = r.readNext()
int num_platforms_listed = Integer.parseInt(so_num_platforms_listed_line[1] ?: 0);
String [] so_header_line = r.readNext()

def stats = [:]

def org = lookupOrCreateOrg(name:so_provider_line[1], db:db, stats:stats);

def consortium = null;
if ( ( so_consortium_line[1] != null ) && ( so_consortium_line[1].length() > 0 ) ) 
  consortium = lookupOrCreateOrg(name:so_consortium_line[1], db:db, stats:stats);

def pkg = lookupOrCreatePackage(identifier:so_package_identifier_line[1], name:so_package_name_line[1], db:db, stats:stats)

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

while ((nl = r.readNext()) != null) {
  // publication_title,print_identifier,online_identifier,date_first_issue_online,
  // num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,
  // num_last_issue_online,title_id,embargo_info,coverage_depth,coverage_notes,
  // publisher_name,DOI,platform_name,platform_role,platform_title_url,platform_name,platform_role,platform_title_url

  // Data Quality
  if ( present(nl[0] ) ) {
    println "**Processing pub title:${nl[0]}, print identifier ${nl[1]} (${num_prop_id_cols} prop cols, ${num_platforms_listed} plat cols)"
    so_count++;
    so_good++;
    def target_identifiers = [];

    def publisher = null
    if ( present(nl[13]) ) {
      println("Publisher name: ${nl[13]}")
      publisher = lookupOrCreateOrg(name:nl[13], db:db, stats:stats);
    }

    // If there is an identifier, set up the appropriate matching...
    if ( present(nl[1]) ) 
      target_identifiers.add([type:'ISSN', value:nl[1]])
    if ( present(nl[2]) ) 
      target_identifiers.add([type:'eISSN', value:nl[2]])
    if ( present(nl[9]) ) 
      target_identifiers.add([type:'KBART', value:nl[9]])

    def title = lookupOrCreateTitle(title:nl[0],
                                    identifier:target_identifiers,
                                    publisher:publisher,
                                    db:db, 
                                    stats:stats)

    for ( int i=0; i<num_platforms_listed; i++ ) {
      int position = 14+num_prop_id_cols+i
      def platform = lookupOrCreatePlatform(name:nl[position], db:db, stats:stats)

      // Find tipp
      if ( title._id && pkg._id && platform._id ) {
        def tipp = lookupOrCreateTipp(titleid:title._id, pkgid:pkg._id, platformid:platform._id, db:db, stats:stats)
        tipp.startDate = nl[3]
        tipp.startVolume = nl[4]
        tipp.startIssue = nl[5]
        tipp.endDate = nl[6]
        tipp.endVolume = nl[7]
        tipp.endIssue = nl[8]
        tipp.embargo = nl[10]
        tipp.coverageDepth = nl[11]
        tipp.coverageNote = nl[12]
        db.tipps.save(tipp)

      }
      else {
        println("One of title, pkg or platform are missing!!!");
        inc('missing_critical_data',params.stats);
      }
    }
  }
  else {
    println("Row is missing critical data, add to bad file");
    so_bad++;
  }

}

println("All done - processed ${so_count} so records. Bad count=${so_bad}, good=${so_good}");
println("Stats: ${stats}");

def present(v) {
  if ( ( v != null ) && ( v.length() > 0 ) )
    return true

  return false
}

def lookupOrCreateOrg(Map params = [:]) {
  // println("lookupOrCreateOrg(${params})");
  def org = params.db.orgs.findOne(name:params.name)
  if ( org == null ) {
    org = [
      _id:new org.bson.types.ObjectId(),
      name:params.name,
      lastmod:System.currentTimeMillis()
    ]
    params.db.orgs.save(org)
    inc('orgs_created',params.stats);
  }
  
  org
}

def lookupOrCreatePackage(Map params=[:]) {
  // println("lookupOrCreatePackage(${params})");
  def pkg = params.db.pkgs.findOne(identifier:params.identifier)
  if ( pkg == null ) {
    pkg = [
      _id:new org.bson.types.ObjectId(),
      identifier:params.identifier,
      name:params.name,
      lastmod:System.currentTimeMillis()
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
    }
    else {
      // println("Unable to match on any of ${params.identifier}");
    }
  }
  else {
    inc('titles_without_identifiers',params.stats);
  }

  if ( !title && params.title) { // If no match, and title present, try to match on title
    title = params.db.titles.findOne(title:params.title);
    if ( title ) {
      inc('titles_matched_by_title',params.stats);
    }
  }

  if ( title == null ) {
    // Unable to locate title with identifier given... Try other dedup matches on other props if needed

    title = [
      _id:new org.bson.types.ObjectId(),
      title:params.title,
      identifier:params.identifier,    // identifier is a list, catering for many different values
      lastmod:System.currentTimeMillis()
    ]
    params.db.titles.save(title)
    inc('titles_created',params.stats);
  }

  title
}

def lookupOrCreatePlatform(Map params=[:]) {
  // println("lookupOrCreatePlatform(${params})");
  def platform = null;

  platform = params.db.platforms.findOne(name:params.name)

  if ( !platform ) {
    platform = [
      _id:new org.bson.types.ObjectId(),
      name:params.name,
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
    tipp = [
      _id:new org.bson.types.ObjectId(),
      titleid:params.titleid,
      pkgid:params.pkgid,
      platformid:params.platformid,
      lastmod:System.currentTimeMillis()
    ]
    params.db.tipps.save(tipp)
    inc('tipp_created',params.stats);
  }

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
