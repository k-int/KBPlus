package com.k_int.kbplus

class Invoice {

  Date dateOfInvoice
  Date dateOfPayment
  Date datePassedToFinance
  String invoiceNumber

  static mapping = {
                      id column:'inv_id'
                 version column:'inv_version'
           dateOfInvoice column:'inv_date_of_invoice'
           dateOfPayment column:'inv_date_of_payment'
     datePassedToFinance column:'inv_date_passed_to_finance'
           invoiceNumber column:'inv_number'
  }

  static constraints = {
          dateOfInvoice(nullable:true, blank:false)
          dateOfPayment(nullable:true, blank:false)
    datePassedToFinance(nullable:true, blank:false)
          invoiceNumber(nullable:true, blank:false)
  }

}
