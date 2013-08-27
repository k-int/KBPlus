package com.k_int.kbplus

import java.util.Date;

class Transformer {
		
	String name
	String url
	
    static mapping = {
		table 'transformer'
		id column:'tfmr_id', generator: 'increment'
		name column:'tfmr_name'
		url column:'tfmr_url'
    }
	
	static constraints = {
		//id(nullable:false, unique: true, blank:false)
		name(nullable:false, blank:false)
		url(nullable:false, unique: true, blank:false)
	}
	
	static hasMany = [ transforms : Transforms ]
}
