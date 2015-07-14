package com.k_int.kbplus

class Invoice {

  Date dateOfInvoice
  Date dateOfPayment
  Date datePassedToFinance
  Date startDate
  Date endDate
  String invoiceNumber
  Org owner

  static mapping = {
                      id column:'inv_id'
                 version column:'inv_version'
           dateOfInvoice column:'inv_date_of_invoice'
           dateOfPayment column:'inv_date_of_payment'
     datePassedToFinance column:'inv_date_passed_to_finance'
           invoiceNumber column:'inv_number'
               startDate column:'inv_start_date'
                 endDate column:'inv_end_date'
                   owner column:'inv_owner'
  }

  static constraints = {
          dateOfInvoice(nullable:true, blank:false)
          dateOfPayment(nullable:true, blank:false)
    datePassedToFinance(nullable:true, blank:false)
          invoiceNumber(nullable:false, blank:false)
          startDate(nullable:true, blank:false)
          endDate(nullable:true, blank:false)
                  owner(nullable:false, blank:false)
  }

}
