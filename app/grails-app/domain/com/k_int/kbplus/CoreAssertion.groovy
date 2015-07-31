package com.k_int.kbplus
import javax.persistence.Transient

class CoreAssertion { 

  Date startDate
  Date endDate

  static belongsTo = [ tiinp : TitleInstitutionProvider ]

  static mapping = {
    id column:'ca_id'
    tiinp column:'ca_owner', index:'ca_owner'
    startDate column:'ca_start_date'
    endDate column:'ca_end_date'
    version column:'ca_ver'
  }

  static constraints = {
    endDate(nullable:true, blank:false)
    startDate(nullable:false, blank:false)
    startDate validator: {val,obj ->
      if(obj.endDate == null) return true;
      val = new java.sql.Timestamp(val.getTime());
      if(val > obj.endDate) return false;
    }
    endDate validator: {val,obj ->
      if ( val != null ) {
        val = new java.sql.Timestamp(val.getTime());
        if(val < obj.startDate) return false;
      }
    }
  }

  @Override
  public String toString(){
    def strFormat = 'yyyy-MM-dd'
    def formatter = new java.text.SimpleDateFormat(strFormat)
    return "${startDate?formatter.format(startDate):''} : ${endDate?formatter.format(endDate):''}"
  }

}
