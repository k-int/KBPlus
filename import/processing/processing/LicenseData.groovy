#!/usr/bin/groovy

// @GrabResolver(name='home', root='http://repo1.maven.org/maven2')
// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
  @Grab(group='com.gmongo', module='gmongo', version='0.9.5'),
  @Grab(group='gov.loc', module='bagit', version='4.0'),
  @Grab(group='commons-fileupload', module='commons-fileupload', version='1.2.2'),
  @Grab(group='classworlds', module='classworlds', version='1.1'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2' ),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2' )
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import groovy.xml.MarkupBuilder

import groovyx.net.http.ContentType.*
import groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset


println("Starting");

def starttime = System.currentTimeMillis();
def possible_date_formats = [
  new SimpleDateFormat('dd/MM/yy'),
  new SimpleDateFormat('yyyy/MM'),
  new SimpleDateFormat('yyyy')
];

final BagFactory bf = new BagFactory();

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


def target_web_service = 'http://knowplus.edina.ac.uk:8080/oledocstore/KBPlusServlet'

println("Processing zip ${args[0]}");

def zipFile = new java.util.zip.ZipFile(new File(args[0]))

def csventry = null;
def docstore_components = []

def testds=false

zipFile.entries().each {
   // println("zip entry: ${it.name}");
   if ( it.name?.endsWith(".csv") ) {
     if ( !testds)
       csventry = it;
   }
   else {
     docstore_components.add([file:it, id:java.util.UUID.randomUUID().toString(), name:it.getName(), filename:it.getName()]);
   }
}

if ( testds )
    docstoreUpload(bf, zipFile, docstore_components);

println(docstore_components.collect{[ it.remote_uuid, it.name ]})

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

    docstoreUpload(bf, zipFile, docstore_components);

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
        lastmod: System.currentTimeMillis(),
        datafiles:docstore_components.collect{ [ it.remote_uuid, it.name, it.filename ] }
      ]

      for ( int i=31; i<nl.length; i++ ) {
        def norm_identifier = nl[i].trim().toLowerCase().replaceAll('-','_')

        // println("Process subscription identifier ${nl[i]}");
        def sub_lookup = db.subscriptions.findOne(identifier:norm_identifier)
        if ( sub_lookup ) {
          // println("located subscription : ${sub_lookup}");
          license.subscriptions.add(sub_lookup._id);
        }
        else {
          println("ERROR: Unable to locate subscription ${nl[i]} (norm id=${norm_identifier}) whilst processing license");
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

def docstoreUpload(bf, zipfile, filelist) {
  // Create a new identifier
  def workdir = java.util.UUID.randomUUID().toString();
  File tempdir = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+workdir);
  println("tmpdir :${tempdir}");

  File bag_dir = new File(tempdir, 'bag_dir');

  // tempdir.mkdirs();
  bag_dir.mkdirs();

  filelist.each { f ->
    println("Copying ${f.file.getName()} - ${f.id}");
    def target_file = new File("${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}${f.file.getName()}")
    f.fn=f.file.getName()
    FileUtils.copyInputStreamToFile(zipfile.getInputStream(f.file), target_file);
  }

  // Create request.xml file with a request per document in the zip
  createRequest(filelist, "${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}request.xml".toString())
  
  // Create bagit structures
  PreBag preBag;
  synchronized (bf) {
    preBag = bf.createPreBag(bag_dir);
  }
  preBag.makeBagInPlace(BagFactory.Version.V0_96, false);

  def result = zipDirectory(tempdir)

  FileUtils.deleteQuietly(tempdir);

  // Upload
  uploadBag(result, filelist);

  FileUtils.deleteQuietly(result);
}

def uploadBag(bagfile,filelist) {
  println("uploading bagfile ${bagfile}");
  // def http = new groovyx.net.http.HTTPBuilder('http://knowplusdev.edina.ac.uk:8080/oledocstore/KBPlusServlet')
  def http = new groovyx.net.http.HTTPBuilder('http://knowplus.edina.ac.uk:8080/oledocstore/KBPlusServlet')

  http.request(groovyx.net.http.Method.POST) {request ->
    requestContentType = 'multipart/form-data'

    def multipart_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    // multipart_entity.addPart( "owner", new StringBody( feed_definition.dataProvider, "text/plain", Charset.forName( "UTF-8" )))  // Owner
    // def uploaded_file_body_part = new org.apache.http.entity.mime.content.ByteArrayBody(resource_to_deposit, 'text/xml', 'filename')
    def uploaded_file_body_part = new org.apache.http.entity.mime.content.FileBody(bagfile);
    multipart_entity.addPart( "upload-file", uploaded_file_body_part)

    request.entity = multipart_entity;

    response.success = { resp, data ->
      println("Got response ${resp}");
      def tempfile_name = java.util.UUID.randomUUID().toString();
      File tempfile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempfile_name);
      tempfile << data

      java.util.zip.ZipFile zf = new java.util.zip.ZipFile(tempfile);
      java.util.zip.ZipEntry bad_dir_entry = zf.getEntry('bag_dir');

      InputStream is = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));

      def result_doc = new groovy.util.XmlSlurper().parse(is);
      int i=0;
      result_doc.documents.document.each { rd ->
        println("** Doc added to docstore ($filelist[i].name):  ${rd.uuid.text()}");
        filelist[i++].remote_uuid = rd.uuid.text()
      }
    }

    response.failure = { resp ->
      println("Error response ${resp}");
      // log.error("Failure - ${resp}");
      // assert resp.status >= 400
    }
  }

}


def createRequest(list, target_file) {
  // def writer = new StringWriter()
  def writer = new FileWriter(target_file)
  println("Create ${target_file}");
  def xml = new MarkupBuilder(writer)
  int seq = 1
  // uuid(dl.id)
  xml.request('xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance',
              'xmlns':'http://jisc.kbplus.docstore.com/Request',
              'xsi:schemaLocation':'http://jisc.kbplus.docstore.com/Request http://jisc.kbplus.docstore.com/Request/request.xsd') {
    user('kbplus')
    operation('store')
    requestDocuments {
      list.each { dl ->
        ingestDocument(id:seq++,category:'kbplus',type:'kbplus',format:'doc') {
          uuid()
          documentName(dl.file.getName())
          documentLinkId("${dl.id}")
          documentTitle("Unknown - with UUID ${dl.id}")
          documentType('kbplusdoc')
        }
      }
    }
  }
  // uuid(dl.id)
  writer.flush();
  writer.close();

  // println("upload.xml: ${writer.toString()}");
}

File zipDirectory(File directory) throws IOException {
  File testZip = File.createTempFile("bag.", ".zip");
  String path = directory.getAbsolutePath();
  ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(testZip));

  ArrayList<File> fileList = getFileList(directory);
  for (File file : fileList) {
    ZipEntry ze = new ZipEntry(file.getAbsolutePath().substring(path.length() + 1));
    zos.putNextEntry(ze);

    FileInputStream fis = new FileInputStream(file);
    IOUtils.copy(fis, zos);
    fis.close();

    zos.closeEntry();
  }

  zos.close();
  return testZip;
}

static ArrayList<File> getFileList(File file) {
  ArrayList<File> fileList = new ArrayList<File>();
  if (file.isFile()) {
    fileList.add(file);
  }
  else if (file.isDirectory()) {
    for (File innerFile : file.listFiles()) {
      fileList.addAll(getFileList(innerFile));
    }
  }
  return fileList;
}

