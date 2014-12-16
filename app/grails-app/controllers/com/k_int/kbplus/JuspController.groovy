package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured
import grails.converters.*
import com.k_int.custprops.PropertyDefinition
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil
import java.text.SimpleDateFormat


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
      html {
        result
      }
      json {
        def json = result as JSON
        response.contentType = 'application/json'
        render json
      }
    }
   }

 /**
  * Expecting jusp_ti and jusp_inst, will return a JSON map of TitleInstitutionProvider, with TIP as key, and
  * coreDates as values.
  **/
  def titleInstitutionProvider() {
    log.debug("JuspController::titleInstitutionProvider ${params}");
    def result = [:]
    if(params.jusp_ti && params.jusp_inst) {
      def tiInstProv = getTIP(params.jusp_ti,params.jusp_inst, params.jusp_prov)
      if(tiInstProv){
        //Go through all the result TIP, add them as keys and their coreAssertion dates as values.
        result.listOfTip = [:]
        tiInstProv.each{ tip ->   
          def coreDatesList = []
          tip.coreDates.each{ coreDate ->
            coreDatesList.push(coreDate)
          }
          result.listOfTip.put(tip, coreDatesList)
        }
      }else{
        result.error = "No TitleInstitutionProvider found for the given parameters"
      }  
    }
    withFormat{
      html {
        result
      }
      json {       
        def json = result as JSON        
        response.contentType = "application/json"
        render json
      }
    }
    result  
 }

 def titleInstProvCoreStatus(){
    log.debug("JuspController::titleInstProvCoreStatus ${params}");
    def result = [:]
    if(params.jusp_ti && params.jusp_inst ) {
      def tiInstProv = getTIP(params.jusp_ti,params.jusp_inst, params.jusp_prov)
      tiInstProv = tiInstProv[0]//FIXME: Should we care for lists?
      if(tiInstProv){
        def lookupDate = null
        if(params.lookupDate){
          def dateFormatter = new java.text.SimpleDateFormat('yyyy-MM-dd')
          lookupDate = dateFormatter.parse(params.lookupDate)
        }
        result.coreStatus = tiInstProv.coreStatus(lookupDate)
        result.status = "OK"
      }
    }else{
      result.error = "Please provide all the required parameters, jusp_ti, jusp_inst."
      result.status = "ERROR"
    }
    def json = result as JSON        
    response.contentType = "application/json"
    render json
 }
 
 def addCoreAssertionDates(){
    log.debug("JuspController::addCoreAssertionDates ${params}");
    def result = [:]
    if(params.jusp_ti && params.jusp_inst && params.core_start && params.core_end ) {
      def tiInstProv = getTIP(params.jusp_ti,params.jusp_inst, params.jusp_prov)
      tiInstProv = tiInstProv[0]//FIXME: Should we care for lists?
      if(tiInstProv && params.core_start){
        def dateFormatter = new java.text.SimpleDateFormat('yyyy-MM-dd')
        def coreStart = dateFormatter.parse(params.core_start)
        def coreEnd = params.core_end ? dateFormatter.parse(params.core_end) : null
        tiInstProv.extendCoreExtent(coreStart, coreEnd)
        tiInstProv.save()
        result.coreStatus = tiInstProv.coreStatus(null)
        result.status = "OK"
      }
    }else{
      result.error = "Please provide all the required parameters, jusp_ti, jusp_inst, core_start, core_end."
      result.status = "ERROR"
    }
    def json = result as JSON        
    response.contentType = "application/json"
    render json
 }

 def changeIsCoreStatus(){
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
    redirect (action:"titleInfo", params:[jusp_ti:"${params.jusp_ti}",jusp_inst:"${params.jusp_inst}"])
  }

 def getTIP(jusp_ti, jusp_inst, jusp_prov){
    def titlehql = "select ti from TitleInstance ti where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti = ti and id.ns = ns and io.identifier = id and ns.ns = 'jusp' and id.value like ? )"
    def insthql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )"
    def title = TitleInstance.executeQuery(titlehql,[jusp_ti])[0]
    def insti = Org.executeQuery(insthql, [jusp_inst])[0]
    def prov = null
    if(jusp_prov) {
      prov = Org.executeQuery(insthql, [jusp_prov])[0]
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
    }
    return tiInstProv
 }
 
}
