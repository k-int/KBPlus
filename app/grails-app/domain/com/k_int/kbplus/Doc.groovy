package com.k_int.kbplus

import com.k_int.kbplus.auth.User;
import java.sql.Blob;
import org.hibernate.Hibernate;

class Doc {

  static transients = [ 'blobSize', 'blobData', 'sessionFactory' ]
  private static final MAX_SIZE = 1073741824 // 4GB 
  def sessionFactory

  RefdataValue status
  RefdataValue type
  Alert alert

  String title
  String filename
  String creator
  String mimeType
  Integer contentType=0 // 0=String, 1=docstore, 2=update notification, 3=blob
  String content 
  Blob blobContent 
  String uuid 
  Date dateCreated
  Date lastUpdated
  User user

  static mapping = {
                id column:'doc_id'
           version column:'doc_version'
            status column:'doc_status_rv_fk'
              type column:'doc_type_rv_fk'
             alert column:'doc_alert_fk'
       contentType column:'doc_content_type'
              uuid column:'doc_docstore_uuid'
             title column:'doc_title'
           creator column:'doc_creator'
          filename column:'doc_filename'
           content column:'doc_content', type:'text'
       blobContent column:'doc_blob_content'
          mimeType column:'doc_mimeType'
              user column:'doc_user_fk'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    alert(nullable:true, blank:false)
    content(nullable:true, blank:false)
    blobContent(nullable:true, blank:false, maxSize:MAX_SIZE)
    uuid(nullable:true, blank:false)
    contentType(nullable:true, blank:false)
    title(nullable:true, blank:false)
    creator(nullable:true, blank:true)
    filename(nullable:true, blank:false)
    mimeType(nullable:true, blank:false)
    user(nullable:true, blank:false)
  }

  def setBlobData(InputStream is, long length) {
    def session = sessionFactory.getCurrentSession() 
    binaryData = Hibernate.createBlob(is, (int)length)
  }
    
  def getBlobData() {
    return binaryData?.binaryStream
  }


  Long getBlobSize() {
    return binaryData?.length() ?: 0
  }
    
  def render(def response) {
    response.contentType = "application/octet-stream"
    response.outputStream << data
  }
    
  static fromUpload(def file) {
    if(!file) return new Doc()
        
    def origFileName = file.originalFilename
    def slashIndex = Math.max(origFileName.lastIndexOf("/"),origFileName.lastIndexOf("\\"))
    if(slashIndex > -1) origFileName = origFileName.substring(slashIndex + 1)
        
    def doc = new Doc(name: origFileName)
    doc.setBlobData(file.inputStream, file.size)
    return doc
  }
}
