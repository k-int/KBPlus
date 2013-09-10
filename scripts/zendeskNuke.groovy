#!/usr/bin/groovy

// @GrabResolver(name='es', root='https://oss.sonatype.org/content/repositories/releases')
@Grapes([
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2')
])


import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import org.apache.http.*
import org.apache.http.protocol.*


def zendesk_login_email = ''
def zendesk_login_pass = ''
def zendesk_base_url = ''


// Select all public packages where there is currently no forumId
def http = new RESTClient(zendesk_base_url)

http.client.addRequestInterceptor( new HttpRequestInterceptor() {
  void process(HttpRequest httpRequest, HttpContext httpContext) {
    String auth = "${zendesk_login_email}:${zendesk_login_pass}"
    String enc_auth = auth.bytes.encodeBase64().toString()
    httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
  }
})

// Ger all categories
try {
  http.get(path:'/api/v2/categories.json') { resp, data ->
    result = data
    result.categories.each { cat ->
      http.delete(path:"/api/v2/categories/${cat.id}.json") { resp ->
        println(resp);
      }
    }
  }
}
catch ( Exception e ) {
  e.printStackTrace();
}

