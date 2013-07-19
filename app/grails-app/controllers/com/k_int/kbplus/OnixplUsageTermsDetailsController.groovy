package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured

class OnixplUsageTermsDetailsController {

    def springSecurityService
    def ESWrapperService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        def user = User.get(springSecurityService.principal.id)
        def onixplUsageTerm = OnixplUsageTerm.get(params.id)
//        if ( !onixplUsageTerm.hasPerm("view",user) ) {
//            log.debug("return 401....");
//            response.sendError(401);
//            return
//        }
        [onixplUsageTerm: onixplUsageTerm, user: user]
    }
}
