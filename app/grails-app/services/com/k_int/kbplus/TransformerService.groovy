package com.k_int.kbplus

//For Transform
import groovyx.net.http.*
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
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
    log.debug("triggerTransform...${user}, ${filename}, ${tr}, ${content}, ${main_response}...");

    if ( tr ) {
      // def format = tr.accepts_format.value
      // def reqMIME = 'text/xml'
      // if(format == 'json')
      //   reqMIME = 'application/json'
      
      main_response.setHeader("Content-disposition", "attachment; filename=${filename}.${tr.returnFileExtention}")
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
    }
  }

  /**
   * The function first check that the user is allow to access the given {@link com.k_int.kbplus.Transforms}
   * Then it will make a HTTP request to the {@link com.k_int.kbplus.Transforms} URL
   * and pass the given content (XML or JSON), wait for the response and send the returned file
   * to the main {@link #javax.servlet.http.HttpServletResponse HttpServletResponse}.
   * 
   * @param user - the {@link com.k_int.kbplus.auth.User}
   * @param filename - name of the file to be created
   * @param tr_id - the {@link com.k_int.kbplus.Transforms} id
   * @param content - the JSON or XML content depending on what the transform is working with
   * @param main_response - the main {@link #javax.servlet.http.HttpServletResponse HttpServletResponse} 
   * to which the transformation need to be sent to. We want to differentiate this object with the HttpServletResponse created
   * to communicate with the transformer.
   */
  def oldTriggerTransform(user, filename, tr_id, content, main_response) {
    def starttime = exportService.printStart("Transformer service")
    
    // Check that user has access to this transform
    def transform = hasTransformId(user, tr_id)
    if( transform ){
      def tr = transform.transforms
      def format = tr.accepts_format.value
      def reqMIME = 'text/xml'
      if(format == 'json')
        reqMIME = 'application/json'
      
      main_response.setHeader("Content-disposition", "attachment; filename=${filename}.${tr.return_file_extention}")
      main_response.contentType = tr.return_mime //"text/plain"
      main_response.setCharacterEncoding("UTF-8");
      def out = main_response.writer
      
      log.debug("Calling transformer: ${tr.transformer.url}")
      
      // Split URL to get the server and path
      URL url = new URL(tr.transformer.url);
      def server = "${url.getProtocol()}://${url.getAuthority()}"
      def path = "${url.getPath()}"
      
      starttime = exportService.printStart("Connection")
      
      // Create HTTBuilder
      def http = new HTTPBuilder(server)
      // HTTPBuilder has no direct methods to add timeouts. We have to add them to the HttpParams of the underlying HttpClient
      http.getClient().getParams().setParameter("http.connection.timeout", new Integer(30000))
      http.getClient().getParams().setParameter("http.socket.timeout", new Integer(300000))
      
      // Use HTTPBuilder to manage request/response with the Transformer
      http.request( POST ) { request ->
        uri.path = path
        
        // Setting multipart/form coded HTTP entity consisting of multiple body parts.
        MultipartEntity mpe = new MultipartEntity();
        mpe.addPart('path', new StringBody(tr.path_to_stylesheet))
        mpe.addPart(format, new InputStreamBody(new ByteArrayInputStream(content.getBytes("UTF-8")), reqMIME, 'file'))
        request.entity = mpe
        
        // Read response and send it to user outputStream
        response.success = { resp, reader ->
          exportService.printDuration(starttime, "Connection")
          starttime = exportService.printStart("Reading Transformer Output")
          
          log.debug(getResponseHeader(resp))
          
          out << reader
          
          exportService.printDuration(starttime, "Reading Transformer Output")
        }
        
        // handle denied access
        response.'401' = { resp -> log.error('access denied') }
        
        // handle response failure - fill the file sent with an error message
        response.failure = { resp -> out << getResponseHeader(resp) }
      }
      out.close()
    }
    else{
      // fill the file sent with an error message
      out << "Looks like you dont't have access to that transform"
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
