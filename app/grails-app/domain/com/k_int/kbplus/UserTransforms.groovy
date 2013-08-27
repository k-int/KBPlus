package com.k_int.kbplus

import com.k_int.kbplus.auth.*;

class UserTransforms {
	
	User user
	Transforms transforms
	
	
	static mapping = {
		table: 'user_transforms'
		user column:'ut_user_fk', index:'ut_user_id_idxfk_2'
		transforms column:'ut_transforms_fk', index:'ut_transforms_id__idxfk'
	}
	
	static constraints = {
		id composite: ['user', 'transforms']
		user(nullable:true, blank:false)
		transforms(nullable:true, blank:false)
	}
}
