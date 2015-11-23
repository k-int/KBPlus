package com.k_int.kbplus

//For Transform
import groovyx.net.http.*
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class TransformerService {
  
  def exportService
  def grailsApplication
  
  /**
   *
   * A replacement implementation of trigger transform (As below) Which uses Xalan instead of calling out to an external
   * transform service using http.
   *
   * @param user - the {@link com.k_int.kbplus.auth.User}
   * @param filename - name of the file to be created
   * @param tr - Transformer config row from Config.groovy
   * @param content - the JSON or XML content depending on what the transform is working with
   * @param main_response - the main {@link #javax.servlet.http.HttpServletResponse HttpServletResponse} 
   */
  def triggerTransform(user, filename, tr, content, main_response) {
    log.debug("triggerTransform...${user}, ${filename}, ${tr}...");

    if ( tr ) {
      try{
        // def format = tr.accepts_format.value
        // def reqMIME = 'text/xml'
        // if(format == 'json')
        //   reqMIME = 'application/json'
        
        main_response.setHeader("Content-disposition", "attachment; filename=\"${filename}.${tr.returnFileExtention}\"")
        main_response.contentType = tr.returnMime //"text/plain"
        main_response.setCharacterEncoding("UTF-8");
        def out = main_response.writer
        // def xsl_file = tr.path_to_stylesheet.substring(tr.path_to_stylesheet.lastIndexOf('/'), tr.path_to_stylesheet.length())
        // log.debug("Lookup XSL: ${xsl_file}");
        def xslt = grailsApplication.mainContext.getResource('/WEB-INF/resources/'+tr.xsl).inputStream

        if ( xslt != null ) {
          // Run transform against document and store output in license.summaryStatement
          def factory = TransformerFactory.newInstance()
          def transformer = factory.newTransformer(new StreamSource(xslt))
          transformer.transform(new StreamSource(new StringReader(content)), new StreamResult(out))
        }
        else {
          log.error("Unable to get handle to ${filename} XSL");
        }
      }catch(Exception e){
        log.error("Error while triggering transform",e)
      }
    }
  }

  
  /**
   * Simple function to display the response header for debug purpose.
   * 
   * @param resp - {@link #javax.servlet.http.HttpServletResponse HttpServletResponse} from transform
   * @return a string representation of the header
   */
  def getResponseHeader(resp){
    def msg = "\nresponse failed: ${resp.statusLine}\n"
    msg += "Headers:\n"
    resp.headers.each { msg += "${it.name} : ${it.value}\n" }
    
    return msg
  }
  
  /**
   * Check if the user has access to the {@link com.k_int.kbplus.Transforms}
   * 
   * @param user - the {@link com.k_int.kbplus.auth.User}
   * @param tr_id - the {@link com.k_int.kbplus.Transforms} id
   * @return the {@link com.k_int.kbplus.Transforms} object or null if the user doesn't have access
   */
  def hasTransformId(user, tr_id){
    def transform = UserTransforms.findAllByUser(user).findAll{ it.transforms.id == Long.valueOf(tr_id) }
    if(transform.size() == 1){
      transform[0]
    }else{
      null
    }
  }
}
