package com.k_int.kbplus

import java.sql.Blob

import org.hibernate.Session

import com.k_int.kbplus.auth.User

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
  String migrated

  static mapping = {
                id column:'doc_id'
           version column:'doc_version'
            status column:'doc_status_rv_fk'
              type column:'doc_type_rv_fk'
             alert column:'doc_alert_fk'
       contentType column:'doc_content_type'
              uuid column:'doc_docstore_uuid', index:'doc_uuid_idx'
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
    migrated(nullable:true, blank:false, maxSize:1)
  }

  def setBlobData(InputStream is, long length) {
    Session hib_ses = sessionFactory.getCurrentSession()
    blobContent = hib_ses.getLobHelper().createBlob(is, length)
  }
    
  def getBlobData() {
    return blobContent?.binaryStream
  }


  Long getBlobSize() {
    return blobContent?.length() ?: 0
  }
    
  def render(def response) {
    response.setContentType(mimeType)
    response.addHeader("content-disposition", "attachment; filename=\"${filename}\"")
    response.outputStream << getBlobData()
  }
    
  static fromUpload(def file) {
    if(!file) return new Doc()
        
    def filename = file.originalFilename
    def slashIndex = Math.max(filename.lastIndexOf("/"),filename.lastIndexOf("\\"))
    if(slashIndex > -1) filename = filename.substring(slashIndex + 1)
        
    def doc = new Doc(filename: filename)
    doc.setBlobData(file.inputStream, file.size)
    return doc
  }
}
