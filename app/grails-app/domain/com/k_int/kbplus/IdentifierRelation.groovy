package com.k_int.kbplus

class IdentifierRelation {

  Identifier fromIdentifier
  Identifier toIdentifier
  RefdataValue relation
  
  static mapping = {
    id column:'ir_id'
    fromIdentifier column:'ir_from_id_fk'
    toIdentifier column:'ir_to_id_fk'
    relation column:'ir_rel_rdv_id_fk'
  }

  static constraints = {
    fromIdentifier(nullable:false, blank:false);
    toIdentifier(nullable:false, blank:false);
    relation(nullable:true, blank:false);
  }

}
