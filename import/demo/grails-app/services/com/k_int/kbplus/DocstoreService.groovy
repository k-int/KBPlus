package com.k_int.kbplus

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
import org.codehaus.groovy.grails.commons.ApplicationHolder

class DocstoreService {

  def uploadStream(source_stream, original_filename, title) {

    final BagFactory bf = new BagFactory();

    // Create a new identifier
    def workdir = "up-req-${java.util.UUID.randomUUID().toString()}"
    File tempdir = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+workdir);
    println("tmpdir :${tempdir}");

    File bag_dir = new File(tempdir, 'bag_dir');

    // tempdir.mkdirs();
    bag_dir.mkdirs();

    // Copy the input source stream to the target file
    def target_file = new File("${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}${original_filename}")
    FileUtils.copyInputStreamToFile(source_stream, target_file);

    // Create request.xml file with a single entry, which is the new uploaded file
    createRequest(original_filename, "${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}request.xml".toString(), title)

    // Create bagit structures
    PreBag preBag;
    synchronized (bf) {
      preBag = bf.createPreBag(bag_dir);
    }
    preBag.makeBagInPlace(BagFactory.Version.V0_96, false);

    def zippedbag = zipDirectory(tempdir)

    // Upload
    def result = uploadBag(zippedbag)

    def uuid = extractDocId(result.tempfile);

    FileUtils.deleteQuietly(result.tempfile)

    FileUtils.deleteQuietly(zippedbag);
    FileUtils.deleteQuietly(tempdir);

    uuid
  }

  def retrieve(uuid, response, mimetype, filename) {
    log.debug("Retrieve");
    final BagFactory bf = new BagFactory();

    // Create a new identifier
    def workdir = "ret-req-${java.util.UUID.randomUUID().toString()}"
    File tempdir = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+workdir);
    println("tmpdir :${tempdir}");

    File bag_dir = new File(tempdir, 'bag_dir');

    // tempdir.mkdirs();
    bag_dir.mkdirs();

    // Create request.xml file with a single entry, which is the new uploaded file
    createRetrieveRequest("${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}request.xml".toString(), uuid)

    // Create bagit structures
    PreBag preBag;
    synchronized (bf) {
      preBag = bf.createPreBag(bag_dir);
    }
    preBag.makeBagInPlace(BagFactory.Version.V0_96, false);

    def zippedbag = zipDirectory(tempdir)

    // Upload
    def result = uploadBag(zippedbag)

    // FileUtils.deleteQuietly(result.tempfile)
    // FileUtils.deleteQuietly(zippedbag);
    // FileUtils.deleteQuietly(tempdir);

    response.setContentType(mimetype)
    response.addHeader("content-disposition", "attachment; filename=${filename}")
    def outs = response.outputStream
    streamResponseDoc(result.tempfile, outs)
    // outs << 
    outs.flush()
    outs.close()


    uuid
  }


  def uploadBag(bagfile) {
    println("uploading bagfile ${bagfile}");
    // def http = new groovyx.net.http.HTTPBuilder('http://knowplus.edina.ac.uk/oledocstore/KBPlusServlet')
    def docstore_uri = ApplicationHolder.application.config.docstore

    log.debug("Using docstore ${docstore_uri}");

    def http = new groovyx.net.http.HTTPBuilder(docstore_uri)
    def result = [:]
    //edef result_uuid = null

    http.request(groovyx.net.http.Method.POST) {request ->
      requestContentType = 'multipart/form-data'
      def multipart_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      def uploaded_file_body_part = new org.apache.http.entity.mime.content.FileBody(bagfile);
      multipart_entity.addPart( "upload-file", uploaded_file_body_part)
  
      request.entity = multipart_entity;
  
      response.success = { resp, data ->
        log.debug("Got response ${resp}");
        def tempfile_name = java.util.UUID.randomUUID().toString();
        result.tempfile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempfile_name);
        result.tempfile << data

        // java.util.zip.ZipFile zf = new java.util.zip.ZipFile(result.tempfile);
        // java.util.zip.ZipEntry bag_dir_entry = zf.getEntry('bag_dir');
  
        // InputStream is = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));

        // def result_doc = new groovy.util.XmlSlurper().parse(is);

        // InputStream is2 = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));
        // log.debug("result_doc: ${is2.text} ${result_doc.text()}");
        // result.uuid = result_doc.documents.document.uuid.text()

        // zf.close();
        // FileUtils.deleteQuietly(tempfile);
      }
  
      response.failure = { resp ->
        log.error("Error response from docstore(${docstore_uri}) ${resp}");
      }
    }

    result
  }

  def extractDocId(bagresponsezip) {
    def uuid = null
    try {
      java.util.zip.ZipFile zf = new java.util.zip.ZipFile(bagresponsezip);
      java.util.zip.ZipEntry bag_dir_entry = zf.getEntry('bag_dir');

      InputStream is = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));

      def result_doc = new groovy.util.XmlSlurper().parse(is);

      InputStream is2 = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));
      log.debug("result_doc: ${is2.text} ${result_doc.text()}");
      uuid = result_doc.documents.document.uuid.text()

      zf.close();
    }
    catch ( Exception e ) {
      log.error("problem extracting documnet id from bag response ${e}",e);
    }
    uuid
  }

  def streamResponseDoc(bagresponsezip, outs) {
    def uuid = null
    java.util.zip.ZipFile zf = new java.util.zip.ZipFile(bagresponsezip);
    java.util.zip.ZipEntry bag_dir_entry = zf.getEntry('bag_dir');

    InputStream is = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));

    def result_doc = new groovy.util.XmlSlurper().parse(is);

    InputStream is2 = zf.getInputStream(zf.getEntry('bag_dir/data/response.xml'));
    log.debug("result_doc: ${is2.text} ${result_doc.text()}");
    def targetfile = result_doc.documents.document.documentName.text()
    def targetfile_is = zf.getInputStream(zf.getEntry("bag_dir/data/${targetfile}"))

    org.apache.commons.io.IOUtils.copy(targetfile_is, outs);

    zf.close();
    uuid
  }



  def createRequest(source_file_name, target_file, title) {
    // def writer = new StringWriter()
    def writer = new FileWriter(target_file)
    println("Create ${target_file}");
    def xml = new MarkupBuilder(writer)
    int seq = 1
    xml.request('xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance',
                'xmlns':'http://jisc.kbplus.docstore.com/Request',
                'xsi:schemaLocation':'http://jisc.kbplus.docstore.com/Request http://jisc.kbplus.docstore.com/Request/request.xsd') {
      user('kbplus')
      operation('store')
      requestDocuments {
        ingestDocument(id:seq++,category:'kbplus',type:'kbplus',format:'doc') {
          uuid()
          documentName(source_file_name)
          documentLinkId(java.util.UUID.randomUUID().toString())
          documentTitle(title)
          documentType('kbplusdoc')
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


  def deleteDocs(uuid_list) {
    log.debug("delete uuids: ${uuid_list}");

    final BagFactory bf = new BagFactory();

    // Create a new identifier
    def workdir = "del-req-${java.util.UUID.randomUUID().toString()}"
    File tempdir = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+workdir);
    println("tmpdir :${tempdir}");

    File bag_dir = new File(tempdir, 'bag_dir');

    // tempdir.mkdirs();
    bag_dir.mkdirs();

    // Create request.xml file with a single entry, which is the new uploaded file
    createDeleteRequest("${tempdir}${System.getProperty('file.separator')}bag_dir${System.getProperty('file.separator')}request.xml".toString(), uuid_list)

    // Create bagit structures
    PreBag preBag;
    synchronized (bf) {
      preBag = bf.createPreBag(bag_dir);
    }
    preBag.makeBagInPlace(BagFactory.Version.V0_96, false);

    def zippedbag = zipDirectory(tempdir)

    // Upload
    def result = uploadBag(zippedbag)

    def uuid = extractDocId(result.tempfile);

    FileUtils.deleteQuietly(zippedbag);
    FileUtils.deleteQuietly(results.tempfile);
    FileUtils.deleteQuietly(tempdir);

    uuid
  }

  def createDeleteRequest(target_file, doclist) {
    // def writer = new StringWriter()
    def writer = new FileWriter(target_file)
    log.debug("Create ${target_file}");
    def xml = new MarkupBuilder(writer)
    int seq = 1
    xml.request('xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance',
                'xmlns':'http://jisc.kbplus.docstore.com/Request',
                'xsi:schemaLocation':'http://jisc.kbplus.docstore.com/Request http://jisc.kbplus.docstore.com/Request/request.xsd') {
      user('kbplus')
      operation('delete')
      requestDocuments {
        doclist.each { doc_uuid ->
          ingestDocument(id:seq++,category:'',type:'',format:'') {
            uuid(doc_uuid)
            documentName('')
            // documentTitle('')
            // documentType('')
          }
        }
      }
    }

    writer.flush();
    writer.close();
  }

  def createRetrieveRequest(target_file, doc_uuid) {
    def writer = new FileWriter(target_file)
    log.debug("Create ${target_file}");
    def xml = new MarkupBuilder(writer)
    int seq = 1
    xml.request('xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance',
                'xmlns':'http://jisc.kbplus.docstore.com/Request',
                'xsi:schemaLocation':'http://jisc.kbplus.docstore.com/Request http://jisc.kbplus.docstore.com/Request/request.xsd') {
      user('kbplus')
      operation('retrieve')
      requestDocuments {
        ingestDocument(id:seq++,category:'',type:'',format:'') {
          uuid(doc_uuid)
          documentName('sdsdsd')
          // documentTitle('')
          // documentType('')
        }
      }
    }

    writer.flush();
    writer.close();
  }


}
