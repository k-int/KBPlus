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
CSVReader r = new CSVReader( new InputStreamReader(getClass().classLoader.getResourceAsStream("./platforms.csv")))

def stats=[:]

String [] nl;
nl = r.readNext()
println("Read column headings: ${nl}");

while ((nl = r.readNext()) != null) {
  //platform_name,primary_url,host,gateway,administrative,software,administered_by
  try {
    def platform = lookupOrCreatePlatform(name:nl[0],
                                          primaryUrl:nl[1],
                                          host:nl[2],
                                          gateway:nl[3],
                                          administrative:nl[4],
                                          software:nl[5],
                                          administeredBy:nl[6],
                                          db:db,
                                          stats:stats)
  }
  catch ( Exception e ) {
    e.printStackTrace();
  }
}

println("Stats: ${stats}");

def present(v) {
  if ( ( v != null ) && ( v.length() > 0 ) )
    return true

  return false
}

def lookupOrCreatePlatform(Map params=[:]) {
  // println("lookupOrCreatePlatform(${params})");
  def platform = null;

  def norm_name = params.name.trim().toLowerCase()

  platform = params.db.platforms.findOne(normname:norm_name)

  if ( !platform ) {
    platform = [
      _id:new org.bson.types.ObjectId(),
      name:params.name,
      normname:norm_name,
      primaryUrl:params.primaryUrl,
      host:params.host,
      gateway:params.gateway,
      administrative:params.administrative,
      software:params.software,
      administeredBy:params.administeredBy,
      provenance:"Direct import",
      lastmod:System.currentTimeMillis()
    ]
    params.db.platforms.save(platform)
    inc('platforms_created',params.stats);
  }
  else {
    platform.primaryUrl = params.primaryUrl
    platform.host = params.host
    platform.gateway = params.gateway
    platform.administrative = params.administrative
    platform.software = params.software
    platform.administeredBy = params.administeredBy
    params.db.platforms.save(platform)
    inc('platforms_updated',params.stats);
  }

  platform;
}

def inc(countername, statsmap) {
  if ( statsmap[countername] == null ) {
    statsmap[countername] = 1
  }
  else {
    statsmap[countername]++
  }
}

