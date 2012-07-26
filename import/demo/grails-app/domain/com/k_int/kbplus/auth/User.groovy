package com.k_int.kbplus.auth

import javax.persistence.Transient

class User {

  transient springSecurityService

  String username
  String display
  String password
  String instname
  String instcode
  String email
  String shibbScope
  boolean enabled
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  SortedSet affiliations

  static hasMany = [ affiliations: com.k_int.kbplus.auth.UserOrg ]
  static mappedBy = [ affiliations: 'user' ]

  static constraints = {
    username blank: false, unique: true
    password blank: false
    instname blank: true, nullable: true
    display blank: true, nullable: true
    instcode blank: true, nullable: true
    email blank: true, nullable: true
    shibbScope blank: true, nullable: true
  }

  static mapping = {
    password column: '`password`'
  }

  Set<Role> getAuthorities() {
    UserRole.findAllByUser(this).collect { it.role } as Set
  }

  def beforeInsert() {
    encodePassword()
  }

  def beforeUpdate() {
    if (isDirty('password')) {
      encodePassword()
    }
  }

  @Transient
  def getDisplayName() {
    def result = null;
    if ( display ) {
      result = display
    }
    else {
      result = username
    }
    result
  }

  protected void encodePassword() {
    password = springSecurityService.encodePassword(password)
  }

  @Transient
  def getAuthorizedAffiliations() {
    affiliations.findAll { (it.status == 1) || (it.status==3) }
  }
}
