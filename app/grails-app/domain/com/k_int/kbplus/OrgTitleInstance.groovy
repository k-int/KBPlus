package com.k_int.kbplus

class OrgTitleInstance {

	Boolean isCore

	static belongsTo = [title: TitleInstance, org: Org]

	static mapping = {
		id column:'orgtitle_id'
	    title column:'orgtitle_title'
	    org column:'orgtitle_org'
        version column:'orgtitle_version'
	}
}