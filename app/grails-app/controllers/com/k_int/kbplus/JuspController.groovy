package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured
import grails.converters.*
import com.k_int.custprops.PropertyDefinition
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

class JuspController {

  def index() {
    log.debug("JuspController");
  }
  /**
  * Expecting jusp_ti and jusp_org
  **/
  def titleInfo() {
  	def result = [:]
    log.debug("JuspController::TitleInfo ${params}");
    // Pass in a Jusp Title Identifier and a Jusp Institition Identifier and a Jusp Content Provider
    // to get back a form which will allow the user to edit the Core status of the identified tipp as a Jusp Title
    if(params.jusp_ti && params.jusp_org) {
	    def titlehql = "select ti from TitleInstance ti where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti = ti and id.ns = ns and io.identifier = id and ns.ns = 'jusp' and id.value like ? )"
	    def orghql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )"
  		def title = TitleInstance.executeQuery(titlehql,[params.jusp_ti])[0]
      def org = Org.executeQuery(orghql, [params.jusp_org])[0]
      
      log.debug("TITLES FOUND ${title}")
      //title = title [0]
      def orgtitle
  		if(title && org) {
        orgtitle = OrgTitleInstance.executeQuery("select orgti from OrgTitleInstance orgti where orgti.title = ? and orgti.org = ?",[title,org])[0] 
      }else{
        def wrongIDs = " "
        if (!title) wrongIDs += "JUSP title: ${params.jusp_ti} "
        if(!org) wrongIDs += "JUSP organization: ${params.jusp_org}"
        result.wrongIDs = wrongIDs
      }
  		if(orgtitle == null){
  			orgtitle = new OrgTitleInstance(title:title,org:org,isCore:false).save()
        log.debug("NEW ORG TITLE")
  		}
      log.debug ("ORG TITLE ID ${orgtitle?.id}")
  		result.orgtitle = orgtitle
    }
    result
   }
   
   def changeStatus(){
      log.debug("JuspController::ChangeStatus ${params}");

    	if(params.orgtiID){
    		def orgTitleInstance = OrgTitleInstance.get(params.orgtiID.toLong())
    		def core = params.core == "Yes" ? true : false
    		if(orgTitleInstance.isCore != core){
    			orgTitleInstance.isCore = core
    			orgTitleInstance.save()
    			log.debug{"Submited change"}
    		}
    	}
      redirect (action:"titleInfo", params:[jusp_ti:"${params.jusp_ti}",jusp_org:"${params.jusp_org}"])
    }

}
