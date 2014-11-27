package com.k_int.kbplus

class TitleInstitutionProvider {

  Boolean isCore

  static belongsTo = [
                      title: TitleInstance, 
                      institution: Org, 
                      provider: Org]

  static mapping = {
    id column:'tiinp_id'
    title column:'tttnp_title'
    institution column:'tttnp_inst_org_fk'
    provider column:'tttnp_prov_org_fk'
    version column:'title_inst_prov_ver'
  }
}
