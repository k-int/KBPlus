import com.k_int.kbplus.auth.User


class PreferencesFilters {
   
  def springSecurityService

  def filters = {
    setPrefsFilter(controller:'*', action:'*') {
      before = {

        if ( springSecurityService.principal instanceof String ) {
          log.debug("User is string: ${springSecurityService.principal}");
        }
        else if (springSecurityService.principal?.id != null ) {
          log.debug("Set request.user to ${springSecurityService.principal?.id}");
          request.user = User.get(springSecurityService.principal.id);
          
          // Just set the user preferences equal to those of the current user.
          if ( session.userPereferences == null ) {
            session.userPereferences = request.user.getUserPreferences()
          }
        }

        if ( session.sessionPreferences == null ) {
          session.sessionPreferences = grailsApplication.config.appDefaultPrefs
        }
        else {
        }

      }
    }
  }
}
