package com.k_int.kbplus

class FolderItem {

  static belongsTo = [
    folder:UserFolder
  ]

  String referencedOid

  static mapping = {
                 id column:'fi_id'
            version column:'fi_version'
               user column:'fi_owner_id'
      referencedOid column:'fi_ref_oid'
  }

  static constraints = {
  }

  public boolean equals(Object o) {
    if ( o.id == this.id ) {
      return true;
    }
  }
}
