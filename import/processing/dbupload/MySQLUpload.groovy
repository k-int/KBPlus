#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.2')
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader

def starttime = System.currentTimeMillis();

def sql = [
  'LookupOrg':[sql:'select org_id from organisation where org_imp_id = ?'],
  'InsertOrg':[sql:'insert into organisation(org_name, org_imp_id) values (?,?)'],
  'LookupPlatform':[sql:'select plat_id from platform where plat_imp_id = ?'],
  'InsertPlatform':[sql:'insert into platform(plat_name,plat_url, plat_type, plat_admin, plat_imp_id) values (?,?,?,?,?)'],
  'LookupTitle':[sql:'select ti_id from title_instance where ti_imp_id = ?'],
  'InsertTitle':[sql:'insert into title_instance(ti_title, ti_imp_id) values (?,?)'],
  'LookupPackage':[sql:'select pkg_id from package where pkg_imp_id = ?'],
  'InsertPackage':[sql:'insert into package(pkg_org_id, pkg_name, pkg_imp_id) values (?,?,?)'],
  'LookupTIPP':[sql:'select tipp_title_id, tipp_pkg_id, tipp_plat_id from title_instance_package_platform where tipp_title_id=? and tipp_pkg_id=? and tipp_plat_id=?'],
  'InsertTIPP':[sql:'insert into title_instance_package_platform(tipp_title_id, tipp_pkg_id, tipp_plat_id, tipp_imp_id, tipp_start_date, tipp_start_volume, tipp_start_issue, tipp_end_date, tipp_end_volume, tipp_end_issue, tipp_embargo, tipp_coverage_depth, tipp_coverage_note) values (?,?,?,?,?,?,?,?,?,?,?,?,?)']
]

// Setup mongo
def options = new com.mongodb.MongoOptions()
options.socketKeepAlive = true
options.autoConnectRetry = true
options.slaveOk = true
def mongo = new com.gmongo.GMongo('127.0.0.1', options);
def mdb = mongo.getDB('kbplus_ds_reconciliation')

if ( mdb == null ) {
  println("Failed to configure db.. abort");
  system.exit(1);
}


// Connect to jdbc driver

// Parse SQL and store prepared statements

// Orgs
mdb.orgs.find().sort(lastmod:1).each { org ->
  log.debug("update org ${org}");
  def o = Org.findByImpId(org._id.toString())
  if ( o == null ) {
    o=new Org(impId:org._id.toString(), name:org.name);
    o.save();
  }
}

// Platforms
mdb.platforms.find().sort(lastmod:1).each { plat ->
  log.debug("update platform ${plat}");
  def p = Platform.findByImpId(plat._id.toString()) ?: new Platform(name:plat.name, impId:plat._id.toString()).save()
}

mdb.titles.find().sort(lastmod:1).each { title ->
  log.debug("update ${title}");
  def t = TitleInstance.findByImpId(title._id.toString())
  if ( t == null ) {
    t = new TitleInstance(title:title.title, impId:title._id.toString(), ids:[])
    title.identifier?.each { id ->
      log.debug("Adding identifier to title: ${id.type}:${id.value}");
      t.addToIds(new TitleSID(namespace:id.type,identifier:id.value));
    }
    t.save();
  }
}

mdb.pkgs.find().sort(lastmod:1).each { pkg ->
  log.debug("update package ${pkg}");
  def p = Package.findByImpId(pkg._id.toString())
  if ( p == null ) {
    p = new Package(identifier:pkg.identifier,
                    name:pkg.name,
                    impId:pkg._id.toString(),
                    contentProvider: pkg.contentProvider ? Org.findByImpId(pkg.contentProvider.toString()) : null );
  }
  p.save()
}


// Finally... tipps
mdb.tipps.find().sort(lastmod:1).each { tipp ->
  log.debug("update ${tipp}");
  def title = TitleInstance.findByImpId(tipp.titleid.toString())
  def pkg = Package.findByImpId(tipp.pkgid.toString())
  def platform = Platform.findByImpId(tipp.platformid.toString())

  def dbtipp = TitleInstancePackagePlatform.findByPkgAndPlatformAndTitle(pkg,platform,title)
  if ( dbtipp == null ) {
    dbtipp = new TitleInstancePackagePlatform(pkg:pkg,
                                              platform:platform,
                                              title:title,
                                              startDate:tipp.startDate,
                                              startVolume:tipp.startVolume,
                                              startIssue:tipp.startIssue,
                                              endDate:tipp.endDate,
                                              endVolume:tipp.endVolume,
                                              endIssue:tipp.endIssue,
                                              embargo:tipp.embargo,
                                              coverageDepth:tipp.coverageDepth,
                                              coverageNote:tipp.coverageNote).save()
  }
}

