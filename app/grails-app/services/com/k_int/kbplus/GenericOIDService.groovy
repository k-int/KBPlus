package com.k_int.kbplus

class GenericOIDService {

  def grailsApplication

  def resolveOID(oid) {

    def result = null;

    if ( oid != null ) {
      def oid_components = oid.toString().split(':');
  
      def domain_class=null;
  
      if ( oid_components[0].startsWith("com.k_int.kbplus") )
        domain_class = grailsApplication.getArtefact('Domain',oid_components[0])
      else
        domain_class = grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${oid_components[0]}")
  
  
      if ( domain_class ) {
        result = domain_class.getClazz().get(oid_components[1])
        // log.debug("oid ${oid} resolved to ${result}")
      }
      else {
        log.error("resolve OID failed to identify a domain class. Input was ${oid_components}");
      }
    }
    result
  }

}
