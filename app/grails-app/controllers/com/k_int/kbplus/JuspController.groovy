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
  * Expecting jusp_ti and jusp_inst
  **/
  def titleInfo() {
    def result = [:]
    log.debug("JuspController::TitleInfo ${params}");
    if(params.jusp_ti && params.jusp_inst) {
      def titlehql = "select ti from TitleInstance ti where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti = ti and id.ns = ns and io.identifier = id and ns.ns = 'jusp' and id.value like ? )"
      def orghql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )"
      def title = TitleInstance.executeQuery(titlehql,[params.jusp_ti])[0]
      def org = Org.executeQuery(orghql, [params.jusp_inst])[0]
      
      log.debug("TITLES FOUND ${title}")

      def orgtitle
      if(title && org) {
        orgtitle = OrgTitleInstance.executeQuery("select orgti from OrgTitleInstance orgti where orgti.title = ? and orgti.org = ?",[title,org])[0] 
      }else{
        def wrongIDs = " "
        if (!title) wrongIDs += "JUSP title: ${params.jusp_ti} "
        if(!org) wrongIDs += "JUSP organization: ${params.jusp_inst}"
        result.wrongIDs = wrongIDs
      }
      if(orgtitle == null){
        orgtitle = new OrgTitleInstance(title:title,org:org,isCore:false).save()
        log.debug("NEW ORG TITLE")
      }
      log.debug ("ORG TITLE ID ${orgtitle?.id}")
      result.orgtitle = orgtitle
    }
    withFormat {
      json {
        def json = result as JSON
        response.contentType = 'application/json'
        render json
      }
      html {
        result
      }
    }
   }

 /**
  * Expecting jusp_ti and jusp_inst, will return a JSON map of TitleInstitutionProvider, with TIP as key, and
  * coreDates as values.
  **/
  def titleInstitutionProvider() {
    def result = [:]
    log.debug("JuspController::titleInstitutionProvider ${params}");
    if(params.jusp_ti && params.jusp_inst) {
      def titlehql = "select ti from TitleInstance ti where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti = ti and id.ns = ns and io.identifier = id and ns.ns = 'jusp' and id.value like ? )"
      def insthql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )"
      def title = TitleInstance.executeQuery(titlehql,[params.jusp_ti])[0]
      def insti = Org.executeQuery(insthql, [params.jusp_inst])[0]
      def prov = null
      if(params.jusp_prov) {
        prov = Org.get(params.jusp_prov)
      } 
      log.debug("TITLES FOUND ${title}")

      def tiInstProv
      if(title && insti) {
        def query_str = "select tip from TitleInstitutionProvider tip where tip.title = ? and tip.institution = ?"
        if(prov){
          tiInstProv = TitleInstitutionProvider.executeQuery("${query_str} and tip.provider = ?",[title,insti,prov])
        }else{
          tiInstProv = TitleInstitutionProvider.executeQuery(query_str,[title,insti])
          }
      }else{
        def wrongIDs = " "
        if (!title) wrongIDs += "JUSP title: ${params.jusp_ti} "
        if(!insti) wrongIDs += "JUSP organization: ${params.jusp_inst}"
        result.wrongIDs = wrongIDs
      }
      //Go through all the result TIP, add them as keys and their coreAssertion dates as values.
      result.listOfTip = [:]
      tiInstProv.each{ tip ->   
        def coreDatesList = []
        tip.coreDates.each{ coreDate ->
          coreDatesList.push(coreDate)
        }
        result.listOfTip.put(tip, coreDatesList)
      } 
    }

    withFormat{
      json {       
        def json = result as JSON        
        response.contentType = "application/json"
        render json
      }
      html result
    }  
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
