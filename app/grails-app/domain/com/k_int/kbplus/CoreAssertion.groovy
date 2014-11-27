package com.k_int.kbplus

class CoreAssertion { 

  Date startDate
  Date endDate

  static belongsTo = [ tiinp : TitleInstitutionProvider ]

  static mapping = {
    id column:'ca_id'
    tiinp column:'ca_owner'
    startDate column:'ca_start_date'
    endDate column:'ca_end_date'
    version column:'ca_ver'
  }

}
