package com.k_int.kbplus
 
import com.k_int.kbplus.auth.User


class SitePage {
	String alias
	String action
	String controller
	String rectype = "action"
	
	static constraints = {
      alias(nullable: false, blank: false, unique:true)
    
    }

	def getLink(){
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

		[linktext:alias, url:g.createLink(controller:controller,action:action)]
	}
}
