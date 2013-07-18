package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured

class OnixplLicenseTextDetailsController {

    def springSecurityService
    def ESWrapperService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        def user = User.get(springSecurityService.principal.id)
        def onixplLicenseText = OnixplLicenseText.get(params.id)
        if ( ! onixplLicenseText.hasPerm("view",user) ) {
            log.debug("return 401....");
            response.sendError(401);
            return
        }
        def editable = onixplLicenseText.hasPerm("edit", user)
        [onixplLicenseText: onixplLicenseText, user: user, editable: editable]
    }
}
