package com.k_int.kbplus

//For Transform
import groovyx.net.http.*
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class TransformerService {
	
	def exportService
	
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
    def triggerTransform(user, filename, tr_id, content, main_response) {
		def starttime = exportService.printStart("Transformer service")
		
		def transform = hasTransformId(user, tr_id)
		if( transform ){
			def tr = transform[0].transforms
			def format = tr.accepts_format.value
			def reqMIME = 'text/xml'
			if(format == 'json')
				reqMIME = 'application/json'
			
			
			main_response.setHeader("Content-disposition", "attachment; filename=${filename}.${tr.return_file_extention}")
			main_response.contentType = tr.return_mime //"text/plain"
			def out = main_response.outputStream
			
			log.debug("Calling transformer: ${tr.transformer.url}")
			
			URL url = new URL(tr.transformer.url);
			def server = "${url.getProtocol()}://${url.getAuthority()}"
			def path = "${url.getPath()}"
			
			def http = new HTTPBuilder(server)
			def msg = ""
			
			def is_file = new InputStreamBody(new ByteArrayInputStream(content.getBytes()), reqMIME, 'file')
			
			starttime = exportService.printStart("Connection")
			
			//HTTPBuilder has no direct methods to add timeouts. We have to add them to the HttpParams of the underlying HttpClient
			http.getClient().getParams().setParameter("http.connection.timeout", new Integer(30000))
			http.getClient().getParams().setParameter("http.socket.timeout", new Integer(300000))
			
			http.request( POST ) { request ->
				uri.path = path
				
				MultipartEntity mpe = new MultipartEntity();
				mpe.addPart('path', new StringBody(tr.path_to_stylesheet))
				mpe.addPart(format, is_file)
				request.entity = mpe
				request.getParams().setParameter("http.connection.timeout", new Integer(30000))
				request.getParams().setParameter("http.socket.timeout", new Integer(300000))
				
				response.'401' = { resp ->
					log.error('access denied')
				}
				
				response.success = { resp, reader ->
					exportService.printDuration(starttime, "Connection")
					starttime = exportService.printStart("Reading Transformer Output")
					
					assert resp.statusLine.statusCode == 200
					msg += "\nresponse status: ${resp.statusLine}\n"
					msg += "Headers:\n"
					resp.headers.each {
						msg += "${it.name} : ${it.value}\n"
					}
					log.debug(msg)
					out << reader
					
					exportService.printDuration(starttime, "Reading Transformer Output")
				}
				
				response.failure = { resp ->
					msg += "\nresponse failed: ${resp.statusLine}\n"
					msg += "Headers:\n"
					resp.headers.each {
						msg += "${it.name} : ${it.value}\n"
					}
					log.error(msg)
					out << msg
				}
			}
			out.close()
		}
		else{
			out << "Looks like you dont't have access to that transform"
		}
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
			transform
		}else{
			null
		}
	}
}
