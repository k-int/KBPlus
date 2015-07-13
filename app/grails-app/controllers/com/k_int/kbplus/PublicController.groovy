package com.k_int.kbplus

class PublicController {
	def journalLicences(){
		log.debug("journalLicences :: ${params}")
		def result = [:]

		if(params.journal && params.org){
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
				def ies = generateCompareMap(ti,org,result)
				log.debug("Retrieved ies: ${ies}")
				if(ies) generateIELicenceMap(ies,result)
			}
		}
		result.journal = params.journal
		result.org = params.org

		result
	}
	def generateIELicenceMap(ies,result){
		log.debug("generateIELicenceMap")
		def ieMap = [:]

		ies.each{ie->
			ieMap.put("")
		}
	}

	def retrieveIssueEntitlements(ti,org,result){
		log.debug("generateCompareMap")
		def issueEntitlements = []
		def deleted_ie = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
		def today = new Date()

		String ie_query = "select ie from IssueEntitlement as ie join ie.subscription as sub where ie.tipp.title=(:journal) and exists ( select orgs from sub.orgRelations orgs where orgs.org = (:org) AND orgs.roleType.value = 'Subscriber' ) and ie.status != (:deleted_ie) and ie.subscription.owner is not null"
		ti.each{
			def queryParams = [org:org,journal:it,deleted_ie:deleted_ie]
			def query_results = IssueEntitlement.executeQuery(ie_query,queryParams)
			//Check that items are current based on dates
			query_results.each{ ie ->
				def current_ie = ie.accessEndDate > today
				def current_sub = ie.subscription.endDate > today
				def current_licence = ie.subscription.owner.endDate > today
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