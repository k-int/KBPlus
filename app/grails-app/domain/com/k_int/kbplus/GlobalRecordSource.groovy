package com.k_int.kbplus

import javax.persistence.Transient
 

class GlobalRecordSource {

  String identifier
  String name
  String type
  Date haveUpTo
  String uri
  String listPrefix
  String fullPrefix
  String principal
  String credentials
  Long rectype
  Boolean active

  static mapping = {
                   id column:'grs_id'
              version column:'grs_version'
           identifier column:'grs_identifier'
                 name column:'grs_name'
             haveUpTo column:'grs_have_up_to'
                  uri column:'grs_uri'
           fullPrefix column:'grs_full_prefix'
           listPrefix column:'grs_list_prefix'
                 type column:'grs_type'
            principal column:'grs_principal'
          credentials column:'grs_creds'
              rectype column:'grs_rectype'
               active column:'grs_active'
  }

  static constraints = {
     identifier(nullable:true, blank:false)
           name(nullable:true, blank:false)
       haveUpTo(nullable:true, blank:false)
            uri(nullable:true, blank:false)
           type(nullable:true, blank:false)
     fullPrefix(nullable:true, blank:false)
     listPrefix(nullable:true, blank:false)
      principal(nullable:true, blank:false)
    credentials(nullable:true, blank:false)
         active(nullable:true, blank:false)
  }

  @Transient
  def getBaseUrl() {
    // For now, assume type=gokb - and trim off the oai/packages
    def result = uri.replaceAll('oai.*','');
    result
  }

  @Transient
  def getNumberLocalPackages() {
    GlobalRecordSource.executeQuery("select count(*) from GlobalRecordTracker grt where grt.owner.source = ?",[this]);
  }

  @Transient
  static def removeSource(source_id) {
    GlobalRecordSource.executeUpdate("delete GlobalRecordInfo gri where gri.source.id = ?",[source_id])
    GlobalRecordSource.executeUpdate("delete GlobalRecordSource grs where grs.id = ?",[source_id])
  }
  
}
