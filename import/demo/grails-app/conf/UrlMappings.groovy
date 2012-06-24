class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

                "/myInstitutions/$shortcode/$action"(controller:'myInstitutions')
                "/myInstitutions/$shortcode/$action/$id"(controller:'myInstitutions')

		"/"(controller:"home")
		"500"(view:'/error')
	}
}
