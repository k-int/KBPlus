package com.k_int.kbplus

class PublicLicenceController {
	def index(){
		def result = [:]

        result.max = params.max ? Integer.parseInt(params.max) : 40;
        result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

        def public_flag = RefdataCategory.lookupOrCreate('YN', 'Yes');


        def criteria = License.createCriteria();
        result.licences = criteria.list(max: result.max, offset:result.offset) {
	        isPublic {
	        	idEq(public_flag.id)
	        }
		}
		println result.licences
		result
	}

	def show(){
		def result = [:]

		result.license = License.get(params.id)

		result
	}
}