package com.k_int.kbplus

import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.User
import com.k_int.kbplus.auth.UserOrg

import grails.converters.JSON

class LicenceCompareController {
  
    static String INSTITUTIONAL_LICENSES_QUERY = " from License as l where exists ( select ol from OrgRole as ol where ol.lic = l AND ol.org = ? and ol.roleType = ? ) AND l.status.value != 'Deleted'"
    def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
        def result = [:]
        result.user = User.get(springSecurityService.principal.id)
        result.institution = Org.findByShortcode(params.shortcode)
        if (!checkUserIsMember(result.user, result.institution)) {
            flash.error = "You do not have permission to view ${result.institution.name}. Please request access on the profile page";
            response.sendError(401)
            return;
        }

        def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');
        result.isPublic = RefdataCategory.lookupOrCreate('YN', 'Yes');
        result.licensee_role  =licensee_role.id
        result
  }
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def compare(){
    log.debug("compare ${params}")
    def result = [:]
    result.institution = Org.get(params.institution)

    def licences = params.list("selectedLicences").collect{
      License.get(it.toLong())
    }
    log.debug(licences)
    def comparisonMap = new TreeMap()
    licences.each{ lic ->
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
    result.map = comparisonMap
    result.licences = licences
  	return result

  }

  def checkUserIsMember(user, org) {
    def result = false;
    // def uo = UserOrg.findByUserAndOrg(user,org)
    def uoq = UserOrg.where {
        (user == user && org == org && (status == 1 || status == 3))
    }

    if (uoq.count() > 0)
        result = true;

    result
}
}