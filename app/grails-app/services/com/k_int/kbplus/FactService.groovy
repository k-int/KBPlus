package com.k_int.kbplus

class FactService {

  static transactional = false;

    def registerFact(fact) {

      if ( ( fact.type == null ) || 
           ( fact.type == '' ) ) 
        return

      try {
        Fact.withTransaction { status ->
          def fact_type_refdata_value = RefdataCategory.lookupOrCreate('FactType',fact.type);

          // Are we updating an existing fact?
          if ( fact.uid != null ) {
            def current_fact = Fact.findByFactTypeAndFactUid(fact_type_refdata_value,fact.uid)
            if ( current_fact == null ) {
              log.debug("Create new fact..");
              current_fact = new Fact(factType:fact_type_refdata_value, 
                                      factFrom:fact.from,
                                      factTo:fact.to,
                                      factValue:fact.value,
                                      factUid:fact.uid,
                                      relatedTitle:fact.title,
                                      supplier:fact.supplier,
                                      inst:fact.inst,
                                      juspio:fact.juspio,
                                      reportingYear:fact.reportingYear,
                                      reportingMonth:fact.reportingMonth)
              if ( current_fact.save(flush:true) ) {
              }
              else {
                log.error("Problem saving fact: ${current_fact.errors}");
              }
            }
            else {
              log.debug("update existing fact ${current_fact.id}");
            }
          }
        }
      }
      catch ( Exception e ) {
        log.error("Problem registering fact",e);
      }
    }


  def generateMonthlyUsageGrid(title_id, org_id, supplier_id) {

    def result=[:]

    if ( title_id != null &&
         org_id != null &&
         supplier_id != null ) {

      def q = "select sum(f.factValue),f.reportingYear,f.reportingMonth,f.factType from Fact as f where f.relatedTitle.id=? and f.supplier.id=? and f.inst.id=? group by f.factType, f.reportingYear, f.reportingMonth order by f.reportingYear,f.reportingMonth,f.factType.value"
      def l1 = Fact.executeQuery(q,[title_id, supplier_id, org_id])

      def y_axis_labels = []
      def x_axis_labels = []

      l1.each { f ->
        def y_label = "${f[1]}-${String.format('%02d',f[2])}"
        def x_label = f[3].value
        if ( ! y_axis_labels.contains(y_label) )
          y_axis_labels.add(y_label)
        if ( ! x_axis_labels.contains(x_label) )
          x_axis_labels.add(x_label)
      }

      x_axis_labels.sort();
      y_axis_labels.sort();

      log.debug("X Labels: ${x_axis_labels}");
      log.debug("Y Labels: ${y_axis_labels}");

      result.usage = new long[y_axis_labels.size()][x_axis_labels.size()]

      l1.each { f ->
        def y_label = "${f[1]}-${String.format('%02d',f[2])}"
        def x_label = f[3].value
        result.usage[y_axis_labels.indexOf(y_label)][x_axis_labels.indexOf(x_label)] += Long.parseLong(f[0])
      }

      result.x_axis_labels = x_axis_labels;
      result.y_axis_labels = y_axis_labels;
    }
    result
  }

  def generateYearlyUsageGrid(title_id, org_id, supplier_id) {

    def result=[:]

    if ( title_id != null &&
         org_id != null &&
         supplier_id != null ) {

      def q = "select sum(f.factValue),f.reportingYear,f.factType from Fact as f where f.relatedTitle.id=? and f.supplier.id=? and f.inst.id=? group by f.factType, f.reportingYear  order by f.reportingYear,f.factType.value"
      def l1 = Fact.executeQuery(q,[title_id, supplier_id, org_id])

      def y_axis_labels = []
      def x_axis_labels = []

      l1.each { f ->
        def y_label = "${f[1]}"
        def x_label = f[2].value
        if ( ! y_axis_labels.contains(y_label) )
          y_axis_labels.add(y_label)
        if ( ! x_axis_labels.contains(x_label) )
          x_axis_labels.add(x_label)
      }

      x_axis_labels.sort();
      y_axis_labels.sort();

      log.debug("X Labels: ${x_axis_labels}");
      log.debug("Y Labels: ${y_axis_labels}");

      result.usage = new long[y_axis_labels.size()][x_axis_labels.size()]

      l1.each { f ->
        def y_label = "${f[1]}"
        def x_label = f[2].value
        result.usage[y_axis_labels.indexOf(y_label)][x_axis_labels.indexOf(x_label)] += Long.parseLong(f[0])
      }

      result.x_axis_labels = x_axis_labels;
      result.y_axis_labels = y_axis_labels;
    }

    result
  }


  /**
   *  Return an array of size n where array[0] = total for year, array[1]=year-1, array[2]=year=2 etc
   *  Array is zero padded for blank years
   */
  def lastNYearsByType(title_id, org_id, supplier_id, report_type, n, year) {

    def result = new String[n+1]

    // def c = new GregorianCalendar()
    // c.setTime(new Date());
    // def current_year = c.get(Calendar.YEAR)

    if ( title_id != null &&
         org_id != null &&
         supplier_id != null ) {

      def q = "select sum(f.factValue),f.reportingYear,f.factType from Fact as f where f.relatedTitle.id=? and f.supplier.id=? and f.inst.id=? and f.factType.value = ? and f.reportingYear >= ? group by f.factType, f.reportingYear  order by f.reportingYear,f.factType.value"
      def l1 = Fact.executeQuery(q,[title_id, supplier_id, org_id, report_type, (long)(year-n)])

      l1.each{ y ->
        if ( y[1] >= (year - n) ) {
          int idx = year - y[1]
          // log.debug("IDX = ${idx} year = ${y[1]} value=${y[0]}");
          result[idx] = y[0].toString()
        }
      }
    }

    // result.each{r->
    //   log.debug(r)
    // }
    result
  }


}
