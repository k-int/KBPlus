package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured
import grails.converters.*

class AjaxController {

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def inPlaceSave() {

    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    log.debug("inPlaceSave ${params}");
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${params.type}")
    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.id) 
      if ( instance ) {
        log.debug("Got instance ${instance}");
        def binding_properties = [ "${params.elementid}":params.value ]
        log.debug("Merge: ${binding_properties}");
        // see http://grails.org/doc/latest/ref/Controllers/bindData.html
        bindData(instance, binding_properties)
        instance.save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << params.value
    outs.flush()
    outs.close()
  }
}
