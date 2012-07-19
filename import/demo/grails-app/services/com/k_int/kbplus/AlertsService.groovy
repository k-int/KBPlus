package com.k_int.kbplus

class AlertsService {


  /**
   *  Return a list composed of { object, alert }
   *  for each alert visible to this user.
   */
  def getActiveAlerts(user) {
    def result = [] 

    // Firstly.. Find all license notes that this user can see
    
    // List all institutions this user is attached to
    user.affiliations.each { ua ->
      def org = ua.org;

      // For each institution, iterate through all licenses attached to that institution
      def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role','Licensee');
      OrgRole.findAllByOrgAndRoleType(result.institution, licensee_role).each { or ->
        if ( or.lic?.status?.value!='Deleted' ) {
          // license under question is in or.lic - See if there are any notes attached to that license that this user can see
        }
      }



    }
  }
}
