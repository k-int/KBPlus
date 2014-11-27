package com.k_int.kbplus

class TitleInstitutionProvider {

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

  def extendCoreExtent(startDate, endDate) {
    // See if we can extend and existing CoreAssertion or create a new one
    // to represent this
  }


}
