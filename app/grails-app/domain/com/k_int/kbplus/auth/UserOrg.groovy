package com.k_int.kbplus.auth

import com.k_int.kbplus.Org
import javax.persistence.Transient
import org.apache.commons.lang.builder.HashCodeBuilder


class UserOrg  implements Comparable {

  int hashCode() {
    def builder = new HashCodeBuilder()
    if (user) builder.append(user.id)
    if (org) builder.append(org.id)
    if (formalRole) builder.append(formalRole.id)
    builder.toHashCode()
  }

  Long dateRequested
  Long dateActioned
  int status  // 0=Pending, 1=Approved, 2=Rejected, 3=AutoApproved
  String role = "deprecated"

  com.k_int.kbplus.Org org
  com.k_int.kbplus.auth.User user
  com.k_int.kbplus.auth.Role formalRole

  static constraints = {
    dateActioned(nullable:true)
    dateRequested(nullable:true)
    formalRole(nullable:true)
    role(nullable:true)
  }

  @Transient
  def getSortString() {
    return org?.name+' '+formalRole?.authority
  }

  @Transient
  int compareTo(obj) {
    sortString.compareTo(obj?.sortString)
  }

}

