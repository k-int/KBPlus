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

  def last_forum_check = 0;
  def cached_forum_activity = null

  // see http://developer.zendesk.com/documentation/rest_api/forums.html#create-forum

  def doSync() {

    if ( ApplicationHolder.application.config.ZenDeskBaseURL == null || 
         ApplicationHolder.application.config.ZenDeskBaseURL == '' ||
         ApplicationHolder.application.config.kbplusSystemId == null ||
         ApplicationHolder.application.config.kbplusSystemId == '' )
      return;

    // Select all public packages where there is currently no forumId
    def http = new RESTClient(ApplicationHolder.application.config.ZenDeskBaseURL)

    log.debug("Add zendesk creds: ${ApplicationHolder.application.config.ZenDeskLoginEmail}:${ApplicationHolder.application.config.ZenDeskLoginPass}");

    http.client.addRequestInterceptor( new HttpRequestInterceptor() {
      void process(HttpRequest httpRequest, HttpContext httpContext) {
        String auth = "${ApplicationHolder.application.config.ZenDeskLoginEmail}:${ApplicationHolder.application.config.ZenDeskLoginPass}"
        String enc_auth = auth.bytes.encodeBase64().toString()
        httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
      }
    })


    // If we re-import a live database the odds are that the package-forum links will be incorrect.
    // This function reconnects packages in the connected zendesk system to any packages where pkg.forumId is null
    reconnectOrphanedZendeskForums(http);

    def current_categories = getCategories(http);

    // Create forums for any packages we don't have yet.
    Package.findAllByForumId(null).each { pkg ->
      // Check that there is a category for the content provider, if not, create
      def cp = pkg.getContentProvider()
      def cp_category_id = null
      if ( cp != null ) {
        if ( cp.categoryId == null ) {
          cp.categoryId = lookupOrCreateZenDeskCategory(http,"${cp.name} ( ${ApplicationHolder.application.config.kbplusSystemId} )", current_categories);
          cp.save(flush:true);
        }
        pkg.forumId = createForum(http,pkg,cp.categoryId)
        pkg.save(flush:true);
      }
      // Create forum in category
    }
  }

  def reconnectOrphanedZendeskForums(http) {
    log.debug("reconnectOrphanedZendeskForums starting...");

    http.get( path : '/api/v2/forums.json', requestContentType : ContentType.JSON) { resp, json ->
      if ( json ) {
        json.forums.each { f ->
          try {
            log.debug("Checking ${f.name} (${f.id})");
            if ( f.name ==~ /(.*)\(Package (\d+) from (.*)(\)$)/ ) {
              def pkg_info = f.name =~ /(.*)\(Package (\d+) from (.*)(\)$)/
              def package_name = pkg_info[0][1]
              def package_id = pkg_info[0][2]
              def system_id = pkg_info[0][3]
  
              // Only hook up forums if they correspond to our local system identifier
              if ( system_id == ApplicationHolder.application.config.kbplusSystemId ) {
                // Lookup package with package_id
                def pkg = Package.get(Long.parseLong(package_id))
                if ( pkg != null ) {
                  if ( pkg.forumId == null ) {
                    log.debug("Update package ${pkg.id} - link to forum ${f.id}");
                    pkg.forumId = f.id
                    pkg.save()
                  }
                }
              }
            }
          }
          catch ( Exception e ) {
            log.error("Problem reconnecting orphaned forums",e);
          }
        }
      }
    }
    log.debug("reconnectOrphanedZendeskForums completed");
  }

  def createForum(http,pkg,categoryId) {
    def result = null
    // curl https://{subdomain}.zendesk.com/api/v2/forums.json \
    //   -H "Content-Type: application/json" -X POST \
    //   -d '{"forum": {"name": "My Forum", "forum_type": "articles", "access": "logged-in users", "category_id":"xx"  }}' \
    //   -v -u {email_address}:{password}
    def forum_name = pkg.name+" (Package ${pkg.id} from ${ApplicationHolder.application.config.kbplusSystemId})".toString()
    def forum_desc = 'Questions and discussions relating to package :'+pkg.name.toString()

    log.debug("Create forum: ${forum_name}, ${forum_desc}, ${categoryId}");

    http.post( path : '/api/v2/forums.json', 
               requestContentType : ContentType.JSON, 
               body : [ 'forum' : [ 'name' : forum_name,
                                    'forum_type': 'articles', // 'questions', 
                                    'access': 'everybody', // 'logged-in users'
                                    'category_id' : "${categoryId}".toString(),
                                    'description' : forum_desc//,
                                    // 'tags' : [ 'kbpluspkg' , "pkg:${pkg.id}".toString(), ApplicationHolder.application.config.kbplusSystemId.toString()  ]  
                                  ] 
                      ]) { resp, json ->
      log.debug("Create forum Result: ${resp.status}, ${json}");
      result = json.forum.id
    }
    result
  }

  def lookupOrCreateZenDeskCategory(http,catname,current_categories) {
    log.debug("lookupOrCreateZenDeskCategory(${catname})");
    def result = null

    def current_category = null
    if ( current_categories != null ) {
      current_category = current_categories.categories.find { c -> c.name == catname }
    }

    if ( current_category == null ) {
      log.debug("Not found, create...");

      try {
        http.post( path : '/api/v2/categories.json', 
                   requestContentType : ContentType.JSON, 
                   body : [ 'category' : [ 'name' : catname.toString() ] ]) { resp, json ->
          log.debug("Result: ${resp.status}, ${json}");
          result = json.category.id
        }
      }
      catch ( Exception e ) {
        log.error("Problem creating category: ${catname.toString()}",e)
      }

    }
    else {
      log.debug("Found: ${current_category}");
      result = current_category.id
    }

    result
  }

  def getCategories(http) {
    def result = null

    // GET /api/v2/categories.json
    try {
      http.get(path:'/api/v2/categories.json') { resp, data ->
        result = data
      }
    }
    catch ( Exception e ) {
      log.error("Problem fetching categories.. think this can happen if there aren't any cats yet",e);
    }

    result
  }

  // Latest activity:
  // http://developer.zendesk.com/documentation/rest_api/activity_stream.html


  def getLatestForumActivity() {
    // https://ostephens.zendesk.com/api/v2/search.json?query=type:topic
    def now = System.currentTimeMillis();
    def intervalms = 1000 * 60 * 5 // Re-fetch forum activity every 5 minutes
    if ( now - last_forum_check > intervalms ) {
      try {
        def http = new RESTClient(ApplicationHolder.application.config.ZenDeskBaseURL)

        http.client.addRequestInterceptor( new HttpRequestInterceptor() {
          void process(HttpRequest httpRequest, HttpContext httpContext) {
            String auth = "${ApplicationHolder.application.config.ZenDeskLoginEmail}:${ApplicationHolder.application.config.ZenDeskLoginPass}"
            String enc_auth = auth.bytes.encodeBase64().toString()
            httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
          }
        })

        http.get(path:'/api/v2/search.json',
                 query:[query:'type:topic', sort_by:'updated_at', sort_order:'desc']) { resp, data ->
          cached_forum_activity = data
        }
      }
      catch ( Exception e ) {
        log.error("Problem collecting feed activity",e)
      }
      finally {
        last_forum_check = now
      }
    }

    cached_forum_activity
  }

  def postTopicCommentInForum(text, forumId, topicName, topicBody) {
    log.debug("postTopicCommentInForum ${forumId}");
    try {
        def http = new RESTClient(ApplicationHolder.application.config.ZenDeskBaseURL)

        http.client.addRequestInterceptor( new HttpRequestInterceptor() {
          void process(HttpRequest httpRequest, HttpContext httpContext) {
            String auth = "${ApplicationHolder.application.config.ZenDeskLoginEmail}:${ApplicationHolder.application.config.ZenDeskLoginPass}"
            String enc_auth = auth.bytes.encodeBase64().toString()
            httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
          }
        })

        def topic_id = lookupOrCreateTopicInForum(forumId, topicName, topicBody, http);

        log.debug("Posting in topic ${topic_id}");

        http.post(path:"/api/v2/topics/${topic_id}/comments.json",
                    requestContentType : ContentType.JSON,
                    body : [ 'topic_comment' : [ 'body' : text ] ]) { resp, json ->
        }
    }
    catch ( Exception e ) {
      log.error("Problem activity",e)
    }
    finally {
    }
  }

  def lookupOrCreateTopicInForum(forumId, topicName, topicBody, endpoint) {
    log.debug("lookupOrCreateTopicInForum(${forumId},${topicName}...)");

    def result = null
    def currentForumTopics = endpoint.get(path:"/api/v2/forums/${forumId}/topics.json") { resp, data ->

      log.debug("Consider existing topics : ${data}");

      data.topics.each { topic ->
        log.debug("Consider existing topic: ${topic}");
        if ( topic.title == topicName ) 
          result = topic.id
      }
    }
 
    if ( result == null ) {
      log.debug("Create new topic with name ${topicName}");
      // Not able to locate topic with topicName in the identified forum.. Create it
      endpoint.post(path:"/api/v2/topics.json",
                    requestContentType : ContentType.JSON,
                    body : [ 'topic' : [ 'forum_id' : forumId,
                                         'title': topicName,
                                         'body' : topicBody
                                       ]
                      ]) { resp, json ->
        result = json.topic.id
      }
    }

    log.debug("Result of lookupOrCreateTopicInForum is ${result}");
    result
  }
}
