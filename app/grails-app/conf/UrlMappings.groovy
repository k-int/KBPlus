class UrlMappings {

  static mappings = {
    "/$controller/$action?/$id?"{
      constraints {
        // apply constraints here
      }
    }

    "/myInstitutions/$shortcode/$action"(controller:'myInstitutions')
    "/myInstitutions/$shortcode/$action/$id"(controller:'myInstitutions')

    // "/"(controller:"home")
    "/"(view:"/publichome")
    "500"(view:'/error')
 
    "/about"(view:"/about")
    "/terms-and-conditions"(view:"/terms-and-conditions")
    "/privacy-policy"(view:"/privacy-policy")
    "/freedom-of-information-policy"(view:"/freedom-of-information-policy")
    "/contact-us"(view:"/contact-us")
    "/publichome"(view:"/publichome")
    "/signup"(view:"/signup")
  }
}
