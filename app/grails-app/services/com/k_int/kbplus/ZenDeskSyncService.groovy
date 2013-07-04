package com.k_int.kbplus

import org.codehaus.groovy.grails.commons.ApplicationHolder
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.JSON
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import org.apache.http.*
import org.apache.http.protocol.*

class ZenDeskSyncService {

  // see http://developer.zendesk.com/documentation/rest_api/forums.html#create-forum

  def doSync() {

    // Select all public packages where there is currently no forumId
    def http = new RESTClient(ApplicationHolder.application.config.ZenDeskBaseURL)

    http.client.addRequestInterceptor( new HttpRequestInterceptor() {
      void process(HttpRequest httpRequest, HttpContext httpContext) {
        String auth = "${ApplicationHolder.application.config.ZenDeskLoginEmail}:${ApplicationHolder.application.config.ZenDeskLoginPass}"
        String enc_auth = auth.bytes.encodeBase64().toString()
        httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
      }
    })


    if ( ApplicationHolder.application.config.kbplusSystemId != null ) {
      Package.findAllByForumId(null).each { pkg ->
        // Check that there is a category for the content provider, if not, create
        def cp = pkg.getContentProvider()
        def cp_category_id = null
        if ( cp != null ) {
          if ( cp.categoryId == null ) {
            cp.categoryId = lookupOrCreateZenDeskCategory(http,"${cp.name} ( ${ApplicationHolder.application.config.kbplusSystemId} )");
            cp.save(flush:true);
          }
        }
        // Create forum in category
      }
    }
    else {
      log.error("KBPlus ZenDesk sync cannot run - You MUST set a KBPlus System ID");
    }
  }


  def createForum() {
    // curl https://{subdomain}.zendesk.com/api/v2/forums.json \
    //   -H "Content-Type: application/json" -X POST \
    //   -d '{"forum": {"name": "My Forum", "forum_type": "articles", "access": "logged-in users", "category_id":"xx"  }}' \
    //   -v -u {email_address}:{password}

  }

  def lookupOrCreateZenDeskCategory(http,catname) {
    log.debug("lookupOrCreateZenDeskCategory(${catname})");
    def result = null

    def current_categories = getCategories(http);
    def current_category = current_categories.categories.find { c -> c.name == catname }
    if ( current_category == null ) {
      log.debug("Not found, create...");

      // curl https://{subdomain}.zendesk.com/api/v2/categories.json \
      //   -H "Content-Type: application/json" -d '{"category": {"name": "My Category"}}' \
      //   -v -u {email_address}:{password} -X POST

      // http://groovy.codehaus.org/modules/http-builder/apidocs/groovyx/net/http/EncoderRegistry.html#encodeJSONjava.lang.Object
      // def create_request = { category = { name = catname} }
      // def create_request = [ category : [ name : catname] ]
      // def create_request = category { name = catname }
      http.post( path : '/api/v2/categories.json', 
                 requestContentType : ContentType.JSON, 
                 body : [ 'category' : [ 'name' : catname.toString() ] ]) { resp, json ->
        log.debug("Result: ${resp}, ${json}");
        // Result: groovyx.net.http.HttpResponseDecorator@48691d94, [category:[url:https://kbplus.zendesk.com/api/v2/categories/20104091.json, id:20104091, name:NRC Research Press ( IanHubbleDev ), description:null, position:9999, created_at:2013-07-04T08:36:21Z, updated_at:2013-07-04T08:36:21Z]]

      }

    }
    else {
      log.debug("Found: ${current_category}");
    }

    result
  }

  def getCategories(http) {
    def result = null

    // GET /api/v2/categories.json
    http.get(path:'/api/v2/categories.json') { resp, data ->
      result = data
    }

    result
  }
}
