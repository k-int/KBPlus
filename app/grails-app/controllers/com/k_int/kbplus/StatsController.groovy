package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.plugins.springsecurity.Secured
import grails.converters.*



class StatsController {

  def springSecurityService

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def statsHome() { 
    def result = [:]
    result.instStats = Org.executeQuery('''
select distinct(o), count(u) 
from Org as o, User as u, UserOrg as uo 
where uo.user = u and uo.org = o
group by o
''');
    result
  }
}
