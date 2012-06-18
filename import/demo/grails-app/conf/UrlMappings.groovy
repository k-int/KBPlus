class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

                "/myInstitutions/$shortcode/licenses/"(controller:'myInstitutions',action:'manageLicenses')

		"/"(controller:"home")
		"500"(view:'/error')
	}
}
