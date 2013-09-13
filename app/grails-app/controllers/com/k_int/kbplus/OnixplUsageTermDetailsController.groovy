package com.k_int.kbplus

import grails.plugins.springsecurity.Secured

class OnixplUsageTermDetailsController {

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        def ut = OnixplUsageTerm.get(params.id);

        [usageTerm: ut]
    }
}
