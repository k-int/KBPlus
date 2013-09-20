package com.k_int.kbplus


import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;

class ProfileController {

  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    def result = [:]
    result.user = User.get(springSecurityService.principal.id)
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def processJoinRequest() {
    log.debug("processJoinRequest org with id ${params.org} role ${params.formalRole}");
    def user = User.get(springSecurityService.principal.id)
    def org = com.k_int.kbplus.Org.get(params.org)
    def formal_role = com.k_int.kbplus.auth.Role.get(params.formalRole)


    if ( ( org != null ) && ( formal_role != null ) ) {
      def existingRel = UserOrg.find( { org==org && user==user && formalRole==formal_role } )
      if ( existingRel ) {
        log.debug("existing rel");
        flash.error="You already have a relation with the requested organisation."
      }
      else {
        log.debug("Create new user_org entry....");
        def p = new UserOrg(dateRequested:System.currentTimeMillis(),
                            status:0,
                            org:org,
                            user:user,
                            formalRole:formal_role)
        p.save(flush:true)
      }
    }
    else {
      log.error("Unable to locate org or role");
    }

    redirect(action: "index")
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def updateProfile() {
    def user = User.get(springSecurityService.principal.id)
    user.display = params.userDispName
    user.email = params.email

    flash.message="Profile Updated"

    if ( params.defaultPageSize != null ) {
      try {
        long l = Long.parseLong(params.defaultPageSize);
        if ( ( l >= 10 ) && ( l <= 100 ) ) {
          user.defaultPageSize = new Long(l);
        }
        else {
          flash.message="Default page size must be between 10 and 100";
        }
      }
      catch ( Exception e ) {
      }
    }

    if ( params.defaultDash != user.defaultDash?.id ) {
      if ( params.defaultDash == '' ) {
        user.defaultDash = null
      }
      else {
        user.defaultDash = Org.get(params.defaultDash);
      }
    }

    user.save();


    redirect(action: "index")
  }
  
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def addTransforms() {
	  def user = User.get(springSecurityService.principal.id)
	  def transforms = Transforms.findById(params.transformId)
	  
	  //Check if has already transforms
	  if(user && transforms){
		  def userTransforms = UserTransforms.findAllByUser(user)
		  if(userTransforms.find { it.transforms ==  transforms}){
			  flash.error="You already have added this transform."
		  }else{
			  new UserTransforms(
				  user: user,
				  transforms: transforms).save(failOnError: true)
			  flash.message="Transformation added"
		  }
	  }else{  
	  	log.error("Unable to locate transforms");
	  	flash.error="Error we could not add this transformation"
	  }
	  
	  redirect(action: "index")
  }
  
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def removeTransforms() {
	  def user = User.get(springSecurityService.principal.id)
	  def transforms = Transforms.findById(params.transformId)
	  
	  //Check if has already transforms
	  if(user && transforms){
		  def userTransforms = UserTransforms.findAllByUser(user)
		  def transform = userTransforms.find { it.transforms ==  transforms}
		  if(transform){
			  transform.delete(failOnError: true, flush: true)
			  flash.message="Transformation removed from your list."
		  }else{
			  flash.error="This transformation is not in your list."
		  }
	  }else{
		  log.error("Unable to locate transforms");
		  flash.error="Error we could not remove this transformation"
	  }
	  
	  redirect(action: "index")
  }
}
