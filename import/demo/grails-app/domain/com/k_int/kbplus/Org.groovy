package com.k_int.kbplus

class Org {

  String name
  String impId
  String address
  String ipRange
  String sector

  static mappedBy = [ids: 'org', 
                     outgoingCombos: 'fromOrg', 
                     incomingCombos:'toOrg',
                     packages:'contentProvider',
                     links: 'org' ]

  static hasMany = [ids: IdentifierOccurrence, 
                    outgoingCombos: Combo,  
                    incomingCombos:Combo,
                    packages: Package,
                    links: OrgRole]

  static mapping = {
         id column:'org_id'
    version column:'org_version'
      impId column:'org_imp_id', index:'org_imp_id_idx'
       name column:'org_name', index:'org_name_idx'
    address column:'org_address'
    ipRange column:'org_ip_range'
  }

  static constraints = {
    address(nullable:true, blank:true,maxSize:256);
    ipRange(nullable:true, blank:true, maxSize:256);
    sector(nullable:true, blank:true, maxSize:128);
  }
}
