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
        if(orgtitle == null){
          orgtitle = new OrgTitleInstance(title:title,org:org,isCore:false).save()
          log.debug("NEW ORG TITLE")
        }
        log.debug ("ORG TITLE ID ${orgtitle?.id}")
        result.data = orgtitle
        result.status= "ok"
      }else{
        def wrongIDs = " "
        if (!title) wrongIDs += "JUSP title: ${params.jusp_ti} "
        if(!org) wrongIDs += "JUSP organization: ${params.jusp_inst}"
        result.data = wrongIDs
        result.status= "error"
      }
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
        result.data = [:]
        tiInstProv.each{ tip ->   
          def coreDatesList = []
          tip.coreDates.each{ coreDate ->
            coreDatesList.push(coreDate)
          }
          result.data.put(tip, coreDatesList)
        }
        result.status="ok"
        result.count=tiInstProv.size()
      }else{
        result.data = "No TitleInstitutionProvider found for the given parameters"
        result.status="error"
        result.count=0
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
 /**
 * Return all titles for a given institution, with core status true for the given date range
 * @param jusp_inst: The jusp institution ID
 * @param core_start: The earliest core start date for target titles
 * @param core_end: The latest core end date for target titles
 * @return jIdList: A list of jusp title identifiers.
 */
 def coreTitles(){
    log.debug("JuspController::coreTitles")
    def result = [:]
    if(params.jusp_inst && params.core_start && params.core_end){
        def dateFormatter = new java.text.SimpleDateFormat('yyyy-MM-dd')
        def coreStart = dateFormatter.parse(params.core_start)
        def coreEnd = dateFormatter.parse(params.core_end)

        def insthql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )" 
        def insti = Org.executeQuery(insthql, [params.jusp_inst])[0]

        if(insti){
          //First we find all the titles within the given date range
          def titlesHQL = "select tip.title.id from TitleInstitutionProvider tip join tip.coreDates as coreDates where tip.institution=:insti and coreDates.startDate <= :coreStart and coreDates.endDate >= :coreEnd"
          def titleIdentifiers = TitleInstitutionProvider.executeQuery(titlesHQL,[insti:insti,coreStart:coreStart,coreEnd:coreEnd])
          //Then we select the jusp identifiers for those titles
          def jusp_ti_hql = "select id.value from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti.id in (:idents) and id.ns = ns and io.identifier = id and ns.ns = 'jusp'"
          result.data = IdentifierOccurrence.executeQuery(jusp_ti_hql,[idents:titleIdentifiers])
          result.status= "ok"
          result.count=result.data.size()
        }
    }else{
      result.data = "Not all parameters provided (jusp_inst, core_start, core_end). Date format yyyy-MM-dd"
      result.status = "error"
      result.count = "0"
    }
    def json = result as JSON
    response.contentType = "application/json"
    render json

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
        result.data = tiInstProv.coreStatus(lookupDate)
        result.status = "ok"
      }
    }else{
      result.data = "Please provide all the required parameters, jusp_ti, jusp_inst."
      result.status = "error"
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
        result.data = tiInstProv.coreStatus(null)
        result.status = "ok"
      }
    }else{
      result.data = "Please provide all the required parameters, jusp_ti, jusp_inst, core_start, core_end."
      result.status = "error"
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
    def insthql = "select org from Org org where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.org = org and id.ns = ns and io.identifier = id and ns.ns = 'jusplogin' and id.value like ? )" 
    def insti = Org.executeQuery(insthql, [jusp_inst])[0]
    
    def title = null;
    if(jusp_ti){  
      def titlehql = "select ti from TitleInstance ti where exists ( select io from IdentifierOccurrence io, Identifier id, IdentifierNamespace ns where io.ti = ti and id.ns = ns and io.identifier = id and ns.ns = 'jusp' and id.value like ? )"
      title = TitleInstance.executeQuery(titlehql,[jusp_ti])[0]
    }

    def prov = null
    if(jusp_prov) {
      prov = Org.executeQuery(insthql, [jusp_prov])[0]
    } 
    log.debug("TITLES FOUND ${title}")

    def tiInstProv = null
    if(insti){
      def query_params = [insti]
      def query_str = "select tip from TitleInstitutionProvider tip where tip.institution=?"
      if(title){
        query_str += " and tip.title=?"
        query_params += title
      }  
      if(prov){
        query_str += " and tip.provider=?"
        query_params += prov
      }    

      tiInstProv = TitleInstitutionProvider.executeQuery(query_str,query_params)
    }
    
    return tiInstProv
 }

}
