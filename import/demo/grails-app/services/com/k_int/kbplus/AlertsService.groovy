package com.k_int.kbplus

class AlertsService {


  /**
   *  Return a list composed of { object, alert }
   *  for each alert visible to this user.
   */
  def getActiveAlerts(user) {
    def result[] 

    // Firstly.. Find all license notes that this user can see
    
    // List all institutions this user is attached to
    user.affiliations.each { ua ->
      def org = ua.org;
    }
  }
}
