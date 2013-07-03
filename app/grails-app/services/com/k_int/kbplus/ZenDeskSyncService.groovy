package com.k_int.kbplus

import org.codehaus.groovy.grails.commons.ApplicationHolder

class ZenDeskSyncService {

  // see http://developer.zendesk.com/documentation/rest_api/forums.html#create-forum

  def doSync() {
    // Select all public packages where there is currently no forumId
    if ( ApplicationHolder.application.config.kbplusSystemId != null ) {
      Package.findAllByForumId(null).each { pkg ->
        // Check that there is a category for the content provider, if not, create
        def cp = pkg.getContentProvider()
        def cp_category_id = null
        if ( cp != null ) {
          cp_category_id = lookupOrCreateZenDeskCategory("${ApplicationHolder.application.config.kbplusSystemId} - ${cp.name}");
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

  def lookupOrCreateZenDeskCategory(catname) {
    log.debug("lookupOrCreateZenDeskCategory(${catname})");
    def result = null
    result
  }
}
