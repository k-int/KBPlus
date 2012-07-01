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

class DocstoreService {

  def uploadStream(source_stream, original_filename, title) {

    final BagFactory bf = new BagFactory();

    // Create a new identifier
    def workdir = java.util.UUID.randomUUID().toString();
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

    def result = zipDirectory(tempdir)

    FileUtils.deleteQuietly(tempdir);

    // Upload
    // uploadBag(result, filelist);

    FileUtils.deleteQuietly(result);
  }


  def uploadBag(bagfile,filelist) {
    println("uploading bagfile ${bagfile}");
    def http = new groovyx.net.http.HTTPBuilder('http://knowplus.edina.ac.uk/oledocstore/KBPlusServlet')
    def result_uuid = null

    http.request(groovyx.net.http.Method.POST) {request ->
      requestContentType = 'multipart/form-data'
      def multipart_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
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
        result_uuid = result_doc.documents.document.uuid.text()
      }
  
      response.failure = { resp ->
        println("Error response ${resp}");
      }
    }

    result_uuid
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

}
