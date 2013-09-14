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
where uo.user = u 
and uo.org = o
and ( uo.status = 1 or uo.status = 3 )
group by o
''');

    result.soStats = Subscription.executeQuery('''
select distinct(o), count(s)
from Org as o, Subscription as s, OrgRole as orl
where orl.org = o 
and orl.sub = s 
and orl.roleType.value = 'Subscriber'
group by o
''');


    result.lStats = Subscription.executeQuery('''
select distinct(o), count(l)
from Org as o, License as l, OrgRole as orl
where orl.org = o 
and orl.lic = l 
and orl.roleType.value = 'Licensee'
group by o
''');

    result
  }
}
