package com.k_int.kbplus.auth

class User {

	transient springSecurityService

	String username
	String password
        String instname
        String instcode
        String email
        String shibbScope
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static constraints = {
		username blank: false, unique: true
		password blank: false
                instname blank: true, nullable: true
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

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
