// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils


onix = [
  "codelist" : "ONIX_PublicationsLicense_CodeLists.xsd",
  "comparisonPoints" : [
    'template' : '$value$',
    'values' : [
      '_:PublicationsLicenseExpression' : [
        'text' : 'All',
        'children' : [
          'template' : '_:$value$',
          'values' : [
//            'Definitions/_:*' : [
//              'text' : 'Definitions'
//            ],
            'LicenseGrant' : [
              'text' : 'License Grants'
            ],
            'UsageTerms' : [
              'text' : 'Usage Terms',
              'children' : [
                'template' : "_:Usage[normalize-space(_:UsageType/text())='\$value\$']",
                'values' : [
                  'onixPL:MakeTemporaryDigitalCopy' : ['text' :  'Make Temporary Digital Copy'],
                  'onixPL:ProvideIntegratedAccess' : ['text' :  'Provide Integrated Access'],
                  'onixPL:ProvideIntegratedIndex' : ['text' :  'Provide Integrated Index'],
                  'onixPL:AccessViaSecureAuthentication' : ['text' :  'Access Via Secure Authentication'],
                  'onixPL:MakeDigitalCopy' : ['text' :  'Make Digital Copy'],
                  'onixPL:IncludeInPrintedInstructionalMaterial' : ['text' :  'Include In Printed Instructional Material'],
                  'onixPL:IncludeInDigitalInstructionalMaterial' : ['text' :  'Include In Digital Instructional Material'],
                  'onixPL:IncludeInSpecialNeedsInstructionalMaterial' : ['text' :  'Include In Special Needs Instructional Material'],
                  'onixPL:IncludeInAcademicWork' : ['text' :  'Include In Academic Work'],
                  'onixPL:SupplyCopyByPost' : ['text' :  'Supply Copy By Post'],
                  'onixPL:SupplyCopyByFax' : ['text' :  'Supply Copy By Fax'],
                  'onixPL:SupplyCopyBySecureElectronicTransmission' : ['text' :  'Supply Copy By Secure Electronic Transmission'],
                  'onixPL:UseForPromotingLicensedContent' : ['text' :  'Use for Promoting Licensed Content'],
                  'onixPL:UseForTestingLicensedContent' : ['text' :  'Use for Testing Licensed Content'],
                  'onixPL:UseForTrainingAuthorizedUsers' : ['text' :  'Use for Training Authorized Users'],
                  'onixPL:IncludeMaterialForPresentation' : ['text' :  'Include Material For Presentation'],
                  'onixPL:CopyForTrainingAuthorizedUsers' : ['text' :  'Copy for Training Authorized Users'],
                  'onixPL:DepositInPerpetuity' : ['text' :  'Deposit In Perpetuity'],
                  'onixPL:Sell' : ['text' :  'Sell'],
                  'onixPL:RemoveObscureOrModify' : ['text' :  'Remove Obscure Or Modify'],
                  'onixPL:Modify' : ['text' :  'Modify'],
                  'onixPL:MakeAvailable' : ['text' :  'Make Available'],
                  'onixPL:UseForCommercialUse' : ['text' :  'Use for Commercial Use'],
                  'onixPL:UseForNonAcademicUse' : ['text' :  'Use for Non Academic Use'],
                ]
              ]
            ],
            'SupplyTerms' : [
              'text' : 'Supply Terms',
              'children' : [
                'template' : "_:SupplyTerm[normalize-space(_:SupplyTermType/text())='\$value\$']",
                'values' : [
                  'onixPL:StartOfService' : ['text' :  'Start Of Service' ],
                  'onixPL:ConcurrencyWithPrintVersion' : ['text' :  'Concurrency With Print Version' ],
                  'onixPL:ServicePerformance' : ['text' :  'Service Performance' ],
                  'onixPL:UserSupport' : ['text' :  'User Support' ],
                  'onixPL:ProductDocumentation' : ['text' :  'Product Documentation' ],
                  'onixPL:ComplianceWithOpenURLStandard' : ['text' :  'Compliance With OpenURL Standard' ],
                  'onixPL:ComplianceWithAccessibilityStandards' : ['text' :  'Compliance With Accessibility Standards' ],
                  'onixPL:UsageStatistics' : ['text' :  'Usage Statistics' ],
                  'onixPL:ChangesToLicensedContent' : ['text' :  'Changes To Licensed Content' ],
                  'onixPL:ComplianceWithProjectTransferCode' : ['text' :  'Compliance With Project Transfer Code' ],
                  'onixPL:ArchiveCopy' : ['text' :  'Archive Copy' ]
                ]
              ]
            ],
            'ContinuingAccessTerms' : [
              'text' : 'Continuing Access Terms',
              'children' : [
                'template' : "_:ContinuingAccessTerm[normalize-space(_:ContinuingAccessTermType/text())='\$value\$']",
                'values' : [
                  'onixPL:ContinuingAccess' : ['text' :  'Continuing Access' ],
                  'onixPL:ArchiveCopy' : ['text' :  'Archive Copy' ]
                ]
              ]
            ],
            'PaymentTerms/_:PaymentTerm' : [
              'text' : 'Payment Terms'
            ],
            'GeneralTerms/_:GeneralTerm' : [
              'text' : 'General Terms'
            ]
          ]
        ]
      ]
    ]
  ]
]

grails.config.locations = [ "file:${userHome}/.grails/${appName}-config.groovy"]

System.out.println("conf locations: loc:${grails.config.locations}");

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

grails.project.dependency.resolver="maven"

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

grails.cache.config = {
   cache {
      name 'message'
   }
}

subscriptionTransforms = [
  'oclc':[name:'OCLC Resolver', xsl:'oclc.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'ss':[name:'Serials Solutions Resolver', xsl:'serialssolutions.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'sfx':[name:'SFX Resolver', xsl:'SFX.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'kbplus':[name:'KBPlus Import Format', xsl:'kbplusimp.xsl', returnFileExtention:'txt', returnMime:'text/plain'],
]

// KBPlus import not available in titlelist because we need sub id and it's possible for multiple IEs to appear
// per title, which isn't valid inside a KB+ package file
titlelistTransforms = [
  'oclc':[name:'OCLC Resolver', xsl:'oclc.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'ss':[name:'Serials Solutions Resolver', xsl:'serialssolutions.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'sfx':[name:'SFX Resolver', xsl:'SFX.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
]

packageTransforms = [
  'kbplus':[name:'KBPlus Import Format', xsl:'kbplusimp.xsl', returnFileExtention:'txt', returnMime:'text/plain']
]

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    appenders {
        console name: "stdout", threshold: org.apache.log4j.Level.ALL
    }

//    // Enable Hibernate SQL logging with param values
//    trace 'org.hibernate.type'
//    debug 'org.hibernate.SQL'

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    debug  'grails.app.controllers',
           'grails.app.service',
           'grails.app.services',
           'grails.app.domain',
           // 'grails.app.tagLib',
           'grails.app.conf',
           'grails.app.jobs',
           'grails.app.conf.BootStrap',
           'edu.umn.shibboleth.sp'
        //   'com.k_int'
        // 'org.springframework.security'

    // info   'com.linkedin.grails'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.k_int.kbplus.auth.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.k_int.kbplus.auth.UserRole'
grails.plugins.springsecurity.userLookup.usernamePropertyName='username'
grails.plugins.springsecurity.authority.className = 'com.k_int.kbplus.auth.Role'
grails.plugins.springsecurity.securityConfigType = "Annotation"

grails.plugins.springsecurity.providerNames = ['preAuthenticatedAuthenticationProvider', 
                                               'daoAuthenticationProvider', 
                                               'anonymousAuthenticationProvider', 
                                               'rememberMeAuthenticationProvider' ]

auditLog {
  actorClosure = { request, session ->

    if (request.applicationContext.springSecurityService.principal instanceof java.lang.String){
      return request.applicationContext.springSecurityService.principal
    }

    def username = request.applicationContext.springSecurityService.principal?.username

    if (SpringSecurityUtils.isSwitched()){
      username = SpringSecurityUtils.switchedUserOriginalUsername+" AS "+username
    }

    return username
  }
}

// grails.resources.modules = {
//   overrides {
//     'jquery-theme' {
//       resource id:'theme', url:'/css/path/to/jquery-ui-1.8.17.custom.css'
//     }
//   }
// }

appDefaultPrefs {
  globalDateFormat='yyyy-MM-dd'
  globalDateFormatSQL='%Y-%m-%d'
}

// The following 2 entries make the app use basic auth by default
// grails.plugins.springsecurity.useBasicAuth = true
// grails.plugins.springsecurity.basic.realmName = "KBPlus"


// II : This doesn't work because we are calling registerFilter to install the ediauth filter.. need to find a different solution, which is annoying
// See http://jira.grails.org/browse/GPSPRINGSECURITYCORE-210
// This stanza then says everything should use form apart from /api
// More info: http://stackoverflow.com/questions/7065089/how-to-configure-grails-spring-authentication-scheme-per-url
// grails.plugins.springsecurity.filterChain.chainMap = [
//    '/api/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
//    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
// ]

// Refdata values that need to be added to the database to allow ONIX-PL licences to be compared properly. The code will
// add them to the DB if they don't already exist.
refdatavalues = [ "User" : [ "Authorized User", "ExternalAcademic", "ExternalLibrarian", "ExternalStudent",
        "ExternalTeacher", "ExternalTeacherInCountryOfLicensee", "LibraryUserUnaffiliated", "Licensee",
        "LicenseeAlumnus", "LicenseeAuxiliary", "LicenseeContractor", "LicenseeContractorOrganization",
        "LicenseeContractorStaff", "LicenseeDistanceLearningStudent", "LicenseeExternalStudent", "LicenseeFaculty",
        "LicenseeInternalStudent", "LicenseeLibrary", "LicenseeLibraryStaff", "LicenseeNonFacultyStaff",
        "LicenseeResearcher", "LicenseeRetiredStaff", "LicenseeStaff", "LicenseeStudent", "LoansomeDocUser",
        "OtherTeacherOfAuthorizedUsers", "RegulatoryAuthority", "ResearchSponsor", "ThirdParty", "ThirdPartyLibrary",
        "ThirdPartyNonCommercialLibrary", "ThirdPartyOrganization", "ThirdPartyPerson", "WalkInUser" ],
        "UsedResource" : ["AcademicPaper", "AcademicWork", "AcademicWorkIncludingLicensedContent",
                "AcknowledgmentOfSource", "AuthoredContent", "AuthoredContentPeerReviewedCopy", "AuthorizedUserOwnWork",
                "CatalogOrInformationSystem", "CombinedWorkIncludingLicensedContent", "CompleteArticle", "CompleteBook",
                "CompleteChapter", "CompleteIssue", "CopyrightNotice", "CopyrightNoticesOrDisclaimers",
                "CoursePackElectronic", "CoursePackPrinted", "CourseReserveElectronic", "CourseReservePrinted",
                "DataFromLicensedContent", "DerivedWork", "DigitalInstructionalMaterial",
                "DigitalInstructionalMaterialIncludingLicensedContent",
                "DigitalInstructionalMaterialWithLinkToLicensedContent", "DownloadedLicensedContent",
                "ImagesInLicensedContent", "LicensedContent", "LicensedContentBriefExcerpt", "LicensedContentMetadata",
                "LicensedContentPart", "LicensedContentPartDigital", "LicensedContentPartPrinted", "LicenseeContent",
                "LicenseeWebsite", "LinkToLicensedContent", "MaterialForPresentation", "PersonalPresentationMaterial",
                "PrintedInstructionalMaterial", "SpecialNeedsInstructionalMaterial", "ThirdPartyWebsite",
                "TrainingMaterial", "UserContent", "UserWebsite"]]

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */

// grails.databinding.dateFormats = ['MMddyyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:ss'Z'"]
