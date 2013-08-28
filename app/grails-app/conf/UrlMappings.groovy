class UrlMappings {

  static mappings = {
    "/$controller/$action?/$id?"{
      constraints {
        // apply constraints here
      }
    }

    "/lic/$action?/$id?"(controller:'license')

    "/myInstitutions/$shortcode/$action"(controller:'myInstitutions')
    "/myInstitutions/$shortcode/$action/$id"(controller:'myInstitutions')
    "/myInstitutions/$shortcode/dashboard"(controller:'myInstitutions', action:'instdash')

    // "/"(controller:"home")
    "/"(view:"/publichome")

    "/about"(view:"/about")
    "/terms-and-conditions"(view:"/terms-and-conditions")
    "/privacy-policy"(view:"/privacy-policy")
    "/freedom-of-information-policy"(view:"/freedom-of-information-policy")
    "/contact-us"(view:"/contact-us")
    "/publichome"(view:"/publichome")
    "/signup"(view:"/signup")

    "500"(view:'/error')
    "401"(view:'/forbidden')
    "403"(view:'/error')
 
  }
}
