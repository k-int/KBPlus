package com.k_int.kbplus
import com.k_int.kbplus.auth.*;

class PublicController {

    def springSecurityService

	def journalLicences(){
		log.debug("journalLicences :: ${params}")
		def result = [:]

		if(params.journal && params.org){
			if(springSecurityService.principal != "anonymousUser"){
			    result.user = User.get(springSecurityService.principal.id)
			}else{
				result.user = null
			}
			def ti = null
			def org = null
			if(params.journal.contains(":")){
				ti = TitleInstance.lookupByIdentifierString(params.journal)
			}else{
				ti = TitleInstance.findAllByTitleIlike("${params.journal}%")
			}
			if(params.org.isLong()){
				org = Org.get(params.org.toLong())
			}else{
				org = Org.findByNameIlike("${params.org}%")
			}
			log.debug("${ti} and ${org}")
			if(ti && org){
				def access_prop =  grails.util.Holders.config.customProperties.org.journalAccess
				def org_access = org.customProperties.find{it.type.name == access_prop.name}
				if(checkUserAccessToOrg(result.user,org,org_access)){
					def ies = retrieveIssueEntitlements(ti,org,result)
					log.debug("Retrieved ies: ${ies}")
					if(ies) generateIELicenceMap(ies,result);
				}else{
					flash.error = "${org.name} does not provide public access to this service."
				}
			}
		}
		result.journal = params.journal
		result.org = params.org

		result
	}

	def checkUserAccessToOrg(user,org,org_access){
		def org_access_rights = org_access.getValue()?org_access.getValue().split(",") : []
		if(org_access_rights.contains("public")) return true;
		if(org_access_rights == []){
			//When no rights specified, users affiliated with the org should have access
			if(com.k_int.kbplus.auth.UserOrg.findAllByUserAndOrg(user,org)) return true;
		}
		if(user){
			def userRole = com.k_int.kbplus.auth.UserOrg.findAllByUserAndOrg(user,org)
			userRole.each{
				if(org_access_rights.contains(it.formalRole.authority) || org_access_rights.contains(it.formalRole.roleType)) return true;
			}
		}
		return false
	}

	def generateIELicenceMap(ies,result){
		log.debug("generateIELicenceMap")
		def comparisonMap = [:]
		def licIEMap = new TreeMap()
		//See if we got IEs under the same licence, and list them together
		ies.each{ ie->
			def lic = ie.subscription.owner
			if(licIEMap.containsKey(lic)){
				licIEMap.get(lic).add(ie)
			}else{
				licIEMap.put(lic,[ie])
			}
		}
		licIEMap.each{
			def lic = it.getKey()
	        lic.customProperties.each{prop ->
	          def point = [:]
	          if(prop.getValue()|| prop.getNote()){
	            point.put(lic.reference,prop)
	            if(comparisonMap.containsKey(prop.type.name)){
	              comparisonMap[prop.type.name].putAll(point)
	            }else{
	              comparisonMap.put(prop.type.name,point)
	            }
	          }
	        }
		}
		result.licIEMap = licIEMap
		result.comparisonMap = comparisonMap
		log.debug("Processed: "+result)
	}

	def retrieveIssueEntitlements(ti,org,result){
		log.debug("retrieveIssueEntitlements")
		def issueEntitlements = []
		def deleted_ie = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
		def today = new Date()

		String ie_query = "select ie from IssueEntitlement as ie join ie.subscription as sub where ie.tipp.title=(:journal) and exists ( select orgs from sub.orgRelations orgs where orgs.org = (:org) AND orgs.roleType.value = 'Subscriber' ) and ie.status != (:deleted_ie) and ie.subscription.owner is not null"
		ti.each{
			def queryParams = [org:org,journal:it,deleted_ie:deleted_ie]
			def query_results = IssueEntitlement.executeQuery(ie_query,queryParams)
			//Check that items are current based on dates
			query_results.each{ ie ->
				def current_ie = ie.accessEndDate > today || ie.accessEndDate == null
				def current_sub = ie.subscription.endDate > today || ie.subscription.endDate == null
				def current_licence = ie.subscription.owner.endDate > today || ie.subscription.owner.endDate == null
				if(current_ie && current_sub && current_licence){
					issueEntitlements.add(ie)
				}else{
					log.debug("${ie} is not current")
				}
			}
		}
		return issueEntitlements
	} 
}