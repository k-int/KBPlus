package com.k_int.kbplus

class TitleInstitutionProvider {
  
  static belongsTo = [
                      title: TitleInstance, 
                      institution: Org, 
                      provider: Org]

  static hasMany = [
    coreDates:CoreAssertion
  ]

  static mappedBy = [
    coreDates:'tiinp'
  ]

  static mapping = {
    id column:'tiinp_id'
    title column:'tttnp_title'
    institution column:'tttnp_inst_org_fk'
    provider column:'tttnp_prov_org_fk'
    version column:'title_inst_prov_ver'
  }

  def extendCoreExtent(givenStartDate, givenEndDate) {
    log.debug("extendCoreExtent(${givenStartDate}, ${givenEndDate})");
    // See if we can extend and existing CoreAssertion or create a new one to represent this
    // We soften then edges for extending by a day.
    def startDate = new Date(givenStartDate.getTime()-(1000*60*60*24))
    def endDate = new Date(givenEndDate.getTime()+(1000*60*60*24))

    log.debug("For matching purposes, using ${startDate} and ${endDate}");
    
    def cont = true;

    // Test 1 : Does the given range fall entirely within an existing assertion?
    coreDates.each {
      if ( it.startDate <= givenStartDate && it.endDate >= givenEndDate ) {
        log.debug("date range is subsumed (${it.startDate} <= ${givenStartDate}) && (${it.endDate} >= ${givenEndDate})  ");
        cont = false;
        return;
      }
    }

    if ( cont ) {
      // Not fully enclosed - see if we are extending (Backwards or forewards) any existing 
      coreDates.each {
        // Given range overlaps end date of existing statement
        if ( it.startDate <= startDate && it.endDate >= startDate ) {
          // the start date given falls between the start and end dates of an existing core statement
          // because test 1 did not catch this, the end date must be after the end of this assertion, so we simply extend
          log.debug("Extending end date");
          it.endDate = givenEndDate;
          it.save(flush:true)
          cont=false
          return;
        }
  
        // Given range overlaps start date of existing statement
        if ( it.startDate <= endDate && it.endDate >= endDate ) {
          log.debug("Extending start date");
          it.startDate = givenStartDate;
          it.save(flush:true)
          cont=false
          return;
        }
      }

    }

    if ( cont ) {
      // NO obvious overlaps - create a new range
      log.debug("No obvious overlaps - create new core assertion");
      def new_core_statement = new CoreAssertion(startDate:givenStartDate, endDate:givenEndDate, tiinp:this).save(flush:true)
  
      // See if the new range fully encloses any current assertions
      coreDates.each {
        if ( it.startDate >= givenStartDate && it.endDate <= givenEndDate ) {
          it.delete();
        }
      }
    }

  }


}
