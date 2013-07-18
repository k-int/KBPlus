package com.k_int.kbplus

class Transforms {
	
	static belongsTo = [
		transformer: Transformer // make sure that transforms can't be created without allocated transformer
	]
	
	String name
	String path_to_stylesheet
		
	RefdataValue accepts_type // json, xml, url
	RefdataValue accepts_format // subscription, licence
	
	static mapping = {
		table 'transforms'
	    id column: 'tr_id', generator: 'increment'
		name column: 'tr_name'
		accepts_type column: 'tr_accepts_type_rv_fk'
		accepts_format column: 'tr_accepts_format_rv_fk'
		transformer column: 'tr_transformer_fk' , index:'tr_transformer_id_idxfk'
		path_to_stylesheet column: 'tr_path_to_stylesheet'
	}
	
    static constraints = {
		id(nullable:false, unique: true, blank:false)
		name(nullable:false, blank:false)
		accepts_type(nullable:false, blank:false)
		accepts_format(nullable:false, blank:false)
		path_to_stylesheet(nullable:true, blank:true)
		transformer(nullable:false, blank:false)
	}
}
