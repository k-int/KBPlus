// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

customProperties =[
"org":["journalAccess":["name":"Public Journal Access","class":String.toString(),"note":"Set the required rights for accessing the public Journals page. For example 'Staff,Student,Public' or leave empty/delete for no public access."]
      ]
]
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
            'Definitions' : [
              'processor': ({ List<Map> data ->

                def new_data = []
                data.each { Map item ->
                  switch (item."_name") {
                    case "AgentRelatedAgent" :
                      // Add a new row for each related agent.
                      (0..(item."RelatedAgent"?.size() - 1)).each { int idx ->
                        def entry = [:]

                        // Copy the whole of the data.
                        entry << item

                        // Replace the related agent with a list of just 1.
                        entry."RelatedAgent" = [item["RelatedAgent"][idx]]

                        new_data += entry
                      }
                      break

                    default :
                      // Just add the item.
                      new_data += item
                      break
                  }
                }
                if (new_data.size() > 0) {
                  // Because we want to edit the referenced data we can not create a new list,
                  // we must instead empty the old and repopulate with the new.
                  data.clear()
                  data.addAll(new_data)
                }

                // Return the data.
                data
              }),
              'text' : 'Authorised Users',
              'children': [
                'template' : "_:AgentDefinition[normalize-space(_:AgentLabel/text())='\$value\$']/_:AgentRelatedAgent",
                'values' : [
                  'AuthorizedUser' : ['text': 'Authorized User'],
                ]
              ]
            ],
            'LicenseGrant' : [
              'text' : 'Licence Grants'
            ],
            'UsageTerms' : [
              'processor': ({ List<Map> data ->
                def new_data = []
                def users = data.getAt(0)['User']
                def deepcopy = { orig ->
                  def bos;
                  def oos;
                  def bin;
                  def ois;
                  try{
                   bos = new ByteArrayOutputStream()
                   oos = new ObjectOutputStream(bos)
                   oos.writeObject(orig); oos.flush()
                   bin = new ByteArrayInputStream(bos.toByteArray())
                   ois = new ObjectInputStream(bin)
                   return ois.readObject()
                  }finally{
                    bos?.close()
                    oos?.close()
                    bin?.close()
                    ois?.close()
                  }
                }
                def refresh_data = {
                  if (new_data.size() > 0) {
                  // Because we want to edit the referenced data we can not create a new list,
                  // we must instead empty the old and repopulate with the new.
                  data.clear()
                  data.addAll(new_data)
                  new_data.clear()
                  }
                }
                if(users?.size() > 1){
                  users.each{ item ->
                    def copy = [:]
                    //Copy the data
                    copy << data.getAt(0)
                    //Grab the single user and add him to an array
                    def temp = [item]
                    //Then replace the data User(s) with the single User
                    copy['User'] = temp

                    new_data += copy
                  }
                }

                 refresh_data();
                //Create several rows for comparison points that need to be split
                def replicate_row = {usage,type ->
                    usage?."${type}"?.each{ method ->
                      def copy = [:]
                      copy << usage
                      def temp = [method]
                      copy[type] = temp
                      new_data += copy
                    }
                }

                def replicate_nested_row = {usage,parent,child ->
                    usage."${parent}"?."${child}"?.getAt(0)?.each{ place ->
                      def copy = [:]
                      def entry = [:]
                      copy = deepcopy(usage)
                      entry = place.clone()
                      copy."${parent}"[0]."${child}"= [entry]
                      new_data.addAll(copy)
                    }
                }
                //Need to loop we might have multiple data here, genetrated from above
                data.each{ usage ->
                  def usageType = usage."UsageType"?.getAt(0)?."_content"
                  switch (usageType){
                    case "onixPL:Access":
                      replicate_row(usage,'UsageMethod');
                      break;
                    case "onixPL:Copy":
                      replicate_row(usage,'UsagePurpose');
                      break;
                    case "onixPL:DepositInPerpetuity":
                      replicate_nested_row(usage,'UsageRelatedPlace','RelatedPlace');
                      break;
                    case "onixPL:Include":
                      if(usage.'UsageRelatedResource'?.'UsageResourceRelator'?.'_content'?.contains(['onixPL:TargetResource'])){
                        replicate_nested_row(usage,'UsageRelatedResource','RelatedResource');
                      }
                      break;
                    case "onixPL:MakeAvailable":
                      if(usage.'UsageRelatedAgent'?.'UsageAgentRelator'?.'_content'?.contains(['onixPL:ReceivingAgent'])){
                        replicate_nested_row(usage,'UsageRelatedAgent','RelatedAgent');
                      }
                      break;
                    case "onixPL:SupplyCopy":
                      if(usage.'UsageRelatedAgent'?.'UsageAgentRelator'?.'_content'?.contains(['onixPL:ReceivingAgent'])){
                        replicate_nested_row(usage,'UsageRelatedAgent','RelatedAgent');
                      }
                      break;
                    case "onixPL:Use":
                      replicate_row(usage,'UsagePurpose');
                      break;
                    case "onixPL:UseForDataMining":
                      replicate_row(usage,'UsagePurpose');
                      break;
                    default:
                        new_data += usage
                      break;
                  }

                }
                refresh_data();

                //Return the data.
                data
              }),
              'text' : 'Usage Terms',
              'children' : [
                'template' : "_:Usage[normalize-space(_:UsageType/text())='\$value\$']",
                'values' : [
                  'onixPL:Access' : ['text' :  'Access'],
                  'onixPL:Copy' : ['text' : 'Copy'],
                  'onixPL:DepositInPerpetuity' : ['text' :  'Deposit In Perpetuity'],
                  'onixPL:Include': ['text': 'Include'],
                  'onixPL:MakeAvailable': ['text': 'Make Available'],
                  'onixPL:MakeDigitalCopy' : ['text' :  'Make Digital Copy'],
                  'onixPL:Modify' : ['text' :  'Modify'],
                  'onixPL:PrintCopy' : ['text': 'PrintCopy'],
                  'onixPL:ProvideIntegratedAccess' : ['text' :  'Provide Integrated Access'],
                  'onixPL:ProvideIntegratedIndex' : ['text' :  'Provide Integrated Index'],
                  'onixPL:RemoveObscureOrModify' : ['text' :  'Remove Obscure Or Modify'],
                  'onixPL:Sell' : ['text' :  'Sell'],
                  'onixPL:SupplyCopy' : ['text' : 'Supply Copy'],
                  'onixPL:Use' : ['text': 'Use'],
                  'onixPL:UseForDataMining' : ['text':'Use For Data Mining'],


                ]
              ]
            ],
            'SupplyTerms' : [
              'text' : 'Supply Terms',
              'children' : [
                'template' : "_:SupplyTerm[normalize-space(_:SupplyTermType/text())='\$value\$']",
                'values' : [
                  'onixPL:ChangeOfOwnershipOfLicensedResource' : ['text': 'Change Of Ownership Of Licensed Resource'],
                  'onixPL:ChangesToLicensedContent' : ['text': 'Changes To Licensed Content'],
                  'onixPL:CompletenessOfContent' : ['text': 'Completeness Of Content'],
                  'onixPL:ComplianceWithAccessibilityStandards' : ['text': 'Compliance With Accessibility Standards'],
                  'onixPL:ComplianceWithONIX' : ['text': 'Compliance With ONIX'],
                  'onixPL:ComplianceWithOpenURLStandard' : ['text': 'Compliance With OpenURL Standard'],
                  'onixPL:ComplianceWithProjectTransferCode' : ['text': 'Compliance With Project Transfer Code'],
                  'onixPL:ComplianceWithStandardsAndBestPractices' : ['text': 'Compliance With Standards And Best Practices'],
                  'onixPL:ConcurrencyWithPrintVersion' : ['text': 'Concurrency With Print Version'],
                  'onixPL:ContentDelivery' : ['text': 'Content Delivery'],
                  'onixPL:ContentWarranty' : ['text': 'Content Warranty'],
                  'onixPL:LicenseeOpenAccessContent' : ['text': 'Licensee OpenAccess Content'],
                  'onixPL:MediaWarranty' : ['text': 'Licensee OpenAccess Content'],
                  'onixPL:MetadataSupply' : ['text': 'Metadata Supply'],
                  'onixPL:NetworkAccess' : ['text': 'Network Access'],
                  'onixPL:OpenAccessContent' : ['text': 'OpenAccess Content'],
                  'onixPL:ProductDocumentation' : ['text': 'Product Documentation'],
                  'onixPL:PublicationSchedule' : ['text': 'Publication Schedule'],
                  'onixPL:ServicePerformance' : ['text': 'Service Performance'],
                  'onixPL:ServicePerformanceGuarantee' : ['text': 'Service Performance Guarantee'],
                  'onixPL:StartOfService' : ['text': 'Start Of Service'],
                  'onixPL:UsageStatistics' : ['text': 'Usage Statistics'],
                  'onixPL:UserRegistration' : ['text': 'User Registration'],
                  'onixPL:UserSupport' : ['text': 'UserSupport']
                ]
              ]
            ],
            'ContinuingAccessTerms' : [
              'processor': ({ List<Map> data ->

                def new_data = []
                def deepcopy = { orig ->
                  def bos;
                  def oos;
                  def bin;
                  def ois;
                  try{
                   bos = new ByteArrayOutputStream()
                   oos = new ObjectOutputStream(bos)
                   oos.writeObject(orig); oos.flush()
                   bin = new ByteArrayInputStream(bos.toByteArray())
                   ois = new ObjectInputStream(bin)
                   return ois.readObject()
                  }finally{
                    bos?.close()
                    oos?.close()
                    bin?.close()
                    ois?.close()
                  }
                }
                data.each{access ->
                  access."ContinuingAccessTermRelatedAgent"?."RelatedAgent"?.getAt(0)?.each{ agent ->
                    def copy = [:]
                    def entry = [:]
                    copy = deepcopy(access)
                    entry = agent.clone()
                    copy."ContinuingAccessTermRelatedAgent"."RelatedAgent"[0].clear()
                    copy."ContinuingAccessTermRelatedAgent"."RelatedAgent"[0].addAll(entry)
                    new_data.addAll(copy)
                  }
                }
                if (new_data.size() > 0) {
                  // Because we want to edit the referenced data we can not create a new list,
                  // we must instead empty the old and repopulate with the new.
                  data.clear()
                  data.addAll(new_data)
                }

                data
              }),
              'text' : 'Continuing Access Terms',
              'children' : [
                'template' : "_:ContinuingAccessTerm[normalize-space(_:ContinuingAccessTermType/text())='\$value\$']",
                'values' : [
                  'onixPL:ContinuingAccess' : ['text' :  'Continuing Access' ],
                  'onixPL:ArchiveCopy' : ['text' :  'Archive Copy' ],
                  'onixPL:PostCancellationFileSupply': ['text': 'Post Cancellation File Supply'],
                  'onixPL:PostCancellationOnlineAccess': ['text': 'Post Cancellation Online Access'],
                  'onixPL:NotificationOfDarkArchive': ['text': 'Notification Of Dark Archive'],
                  'onixPL:PreservationInDarkArchive': ['text': 'Preservation In Dark Archive']
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
  'kbplus':[name:'KBPlus (CSV)', xsl:'kbplusimp.xsl', returnFileExtention:'txt', returnMime:'text/plain'],
]

// KBPlus import not available in titlelist because we need sub id and it's possible for multiple IEs to appear
// per title, which isn't valid inside a KB+ package file
titlelistTransforms = [
  'oclc':[name:'OCLC Resolver', xsl:'oclc.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'ss':[name:'Serials Solutions Resolver', xsl:'serialssolutions.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
  'sfx':[name:'SFX Resolver', xsl:'SFX.xslt', returnFileExtention:'txt', returnMime:'text/plain'],
]

packageTransforms = [
  'kbplus':[name:'KBPlus(CSV)', xsl:'kbplusimp.xsl', returnFileExtention:'txt', returnMime:'text/plain'],
  'kbart2':[name:'KBART II', xsl:'kbartii.xsl', returnFileExtention:'tsv', returnMime:'text/tab-separated-values']

]
licenceTransforms = [
  'sub_ie':[name:'Licensed Issue Entitlements (CSV)', xsl:'licenced_titles.xsl', returnFileExtention:'txt', returnMime:'text/plain'],
  'sub_pkg':[name:'Licensed Subscriptions/Packages (CSV)', xsl:'licenced_subscriptions_packages.xsl', returnFileExtention:'txt', returnMime:'text/plain']
]
// log4j configuration
log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  //appenders {
  //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
  //}
  //trace 'org.hibernate.type'
  //debug 'org.hibernate.SQL'

  appenders {
    console name: "stdout", threshold: org.apache.log4j.Level.ALL
  }

  //    // Enable Hibernate SQL logging with param values
  //    trace 'org.hibernate.type'
  // debug 'org.hibernate.SQL'
  off    'grails.plugin.formfields'

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
      'net.sf.ehcache.hibernate',
      'formfields',
      'com.k_int.kbplus.filter',
      'org.codehaus.groovy.grails.plugins.springsecurity'


  debug  'grails.app.controllers',
      'grails.app.service',
      'grails.app.services',
      'grails.app.domain',
      'grails.app.conf',
      'grails.app.jobs',
      'grails.app.conf.BootStrap',
      //'edu.umn.shibboleth.sp',
      'com.k_int'
  // 'org.springframework.security'
  // 'grails.app.tagLib',

  // info   'com.linkedin.grails'
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'com.k_int.kbplus.auth.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'com.k_int.kbplus.auth.UserRole'
grails.plugins.springsecurity.userLookup.usernamePropertyName='username'
grails.plugins.springsecurity.authority.className = 'com.k_int.kbplus.auth.Role'
grails.plugins.springsecurity.securityConfigType = "Annotation"

grails.plugins.springsecurity.providerNames = ['preAuthenticatedAuthenticationProvider',
  'daoAuthenticationProvider' // ,
  //                                               'anonymousAuthenticationProvider',
  //                                               'rememberMeAuthenticationProvider'
]

grails.plugins.springsecurity.controllerAnnotations.staticRules = [
  '/monitoring/**': ['ROLE_ADMIN']
]

auditLog {
  logFullClassName = true

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
  globalDatepickerFormat='yyyy-mm-dd'
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

quartzHeartbeat = 'Never'
// grails.databinding.dateFormats = ['MMddyyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:ss'Z'"]


financialImportTSVLoaderMappings = [
  header:[
    defaultTargetClass:'com.k_int.kbplus.CostItem',

    // Identify the different combinations that can be used to identify domain objects for the current row
    // Names columns in the import sheet - importer will map according to config and do the right thing
    targetObjectIdentificationHeuristics:[
      [
        ref:'subscription',
        cls:'com.k_int.kbplus.Subscription',
        heuristics:[
          [
            type : 'hql',
            hql: 'select o from Subscription as o join o.ids as io where io.identifier.ns.ns = :jcns and io.identifier.value = :orgId',
            values : [ jcns : [type:'static', value:'JC'], orgId: [type:'column', colname:'SubscriptionId'] ]
          ]
        ],
        creation:[
          onMissing:true,
          whenPresent:[ [ type:'ref', refname:'owner'] ],
          properties : [
            // [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'closure', closure : { o, nl, colmap, colname, locatedObjects -> o.setInstitution(locatedObjects['owner']) } ],
            [ type:'val', property:'identifier', colname: 'SubscriptionId'],
            [ type:'val', property:'name', colname: 'ResourceName'],
            [ type:'closure', closure: { o, nl, colmap, colname, locatedObjects -> o.addNamespacedIdentifier('JC',nl[(int)(colmap.get('SubscriptionId'))]); } ]
          ]
        ]
      ],
      [
        ref:'CICategory',
        cls:'com.k_int.kbplus.RefdataValue',
        heuristics:[
          [ type : 'hql',
            hql: 'select o from RefdataValue as o where o.value = :civalue and o.owner.desc = :citype',
            values : [ citype : [type:'static', value:'CostItemCategory'], civalue: [type:'column', colname:'InvoiceType']]
          ]
        ],
        creation:[
          onMissing:false,
        ]
      ],
      [
        ref:'CIElement',
        cls:'com.k_int.kbplus.RefdataValue',
        heuristics:[
          [ type : 'hql',
            hql: 'select o from RefdataValue as o where o.value = :civalue and o.owner.desc = :citype',
            values : [ citype : [type:'static', value:'CostItemElement'], civalue: [type:'static', value:'Content']]
          ]
        ],
        creation:[
          onMissing:false,
        ]
      ],
      [
        ref:'owner',
        cls:'com.k_int.kbplus.Org',
        onOverride:'mustEqual',
        heuristics:[
          [
            type : 'hql',
            hql: 'select o from Org as o join o.ids as io where io.identifier.ns.ns = :jcns and io.identifier.value = :orgId',
            values : [ jcns : [type:'static', value:'JC'], orgId: [type:'column', colname:'InstitutionId'] ]
          ]
        ],
        creation:[
          onMissing:false,
        ]
      ],
      [
        ref:'invoice',
        cls:'com.k_int.kbplus.Invoice',
        heuristics:[
          [ type : 'simpleLookup',
            criteria : [ [ srcType:'col', colname:'InvoiceNumber', domainProperty:'invoiceNumber' ],
                         [ srcType:'ref', refname:'owner', domainProperty:'owner'] ]
          ]
        ],
        creation:[
          onMissing:true,
          whenPresent:[ [ type:'ref', refname:'owner'] ],
          properties : [
            [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'val', property:'invoiceNumber', colname: 'InvoiceNumber' ],
            [ type:'val', property:'startDate', colname: 'InvoicePeriodStart', datatype:'date'],
            [ type:'val', property:'endDate', colname: 'InvoicePeriodEnd', datatype:'date']
          ]
        ]
      ],
      [
        ref:'order',
        cls:'com.k_int.kbplus.Order',
        heuristics:[
          [ type : 'simpleLookup',
            criteria : [ [ srcType:'col', colname:'PoNumber', domainProperty:'orderNumber' ],
                         [ srcType:'ref', refname:'owner', domainProperty:'owner'] ]
          ]
        ],
        creation:[
          onMissing:true,
          whenPresent:[ [ type:'ref', refname:'owner'], [ type:'val', colname:'PoNumber'] ],
          properties : [
            [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'val', property:'orderNumber', colname: 'PoNumber']
          ]
        ]
      ]
    ],
    creationRules : [
      [
        whenPresent:[ [ type:'val', colname:'InvoiceTotalExcVat'],
                      [ type:'ref', refname:'owner', errorOnMissing:true] ],
        ref:'MainCostItem',
        cls:'com.k_int.kbplus.CostItem',
        creation : [
          properties:[
            [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'ref', property:'invoice', refname:'invoice' ],
            [ type:'ref', property:'order', refname:'order' ],
            [ type:'ref', property:'sub', refname:'subscription' ],
            [ type:'val', property:'costInBillingCurrency', colname:'InvoiceTotalExcVat', datatype:'Double'],
            [ type:'ref', property:'costItemCategory', refname:'CICategory'],
            [ type:'ref', property:'costItemElement', refname:'CIElement'],
            [ type:'val', property:'startDate', colname:'InvoicePeriodStart', datatype:'date'],
            [ type:'val', property:'endDate', colname:'InvoicePeriodEnd', datatype:'date'],
            [ type:'val', property:'datePaid', colname:'DatePaid', datatype:'date'],
            [ type:'valueClosure', property:'costDescription', closure: { colmap, values, locatedObjects -> "[Main Cost Item]${values[colmap['ResourceName']]}, ${values[colmap['AgreementName']]}, ${values[colmap['InvoiceNotes']]} "} ]
          ]
        ]
      ],
      [
        ref:'TaxCostItem',
        cls:'com.k_int.kbplus.CostItem',
        whenPresent:[ [ type:'val', colname:'InvoiceVat'],[ type:'ref', refname:'owner'] ],
        creation:[
          properties:[
            [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'ref', property:'invoice', refname:'invoice' ],
            [ type:'ref', property:'order', refname:'order' ],
            [ type:'ref', property:'sub', refname:'subscription' ],
            [ type:'val', property:'costInBillingCurrency', colname:'InvoiceVat', datatype:'Double'],
            [ type:'val', property:'startDate', colname:'InvoicePeriodStart', datatype:'date'],
            [ type:'val', property:'endDate', colname:'InvoicePeriodEnd', datatype:'date'],
            [ type:'val', property:'datePaid', colname:'DatePaid', datatype:'date'],
            [ type:'ref', property:'costItemCategory', refname:'CICategory'],
            [ type:'ref', property:'costItemElement', refname:'CIElement'],
            [ type:'valueClosure', property:'costDescription', closure: { colmap, values, locatedObjects -> "[Tax] ${values[colmap['ResourceName']]}, ${values[colmap['AgreementName']]}, ${values[colmap['InvoiceNotes']]} "} ]
          ]
        ]
      ],
      [
        ref:'InvoiceTransactionCharge',
        cls:'com.k_int.kbplus.CostItem',
        whenPresent:[ [ type:'val', colname:'InvoiceTransactionCharge'],[ type:'ref', refname:'owner'] ],
        creation:[
          properties:[
            [ type:'ref', property:'owner', refname:'owner' ],
            [ type:'ref', property:'invoice', refname:'invoice' ],
            [ type:'ref', property:'order', refname:'order' ],
            [ type:'ref', property:'sub', refname:'subscription' ],
            [ type:'val', property:'costInBillingCurrency', colname:'InvoiceTransactionCharge', datatype:'Double'],
            [ type:'ref', property:'costItemCategory', refname:'CICategory'],
            [ type:'val', property:'startDate', colname:'InvoicePeriodStart', datatype:'date'],
            [ type:'val', property:'endDate', colname:'InvoicePeriodEnd', datatype:'date'],
            [ type:'val', property:'datePaid', colname:'DatePaid', datatype:'date'],
            [ type:'ref', property:'costItemElement', refname:'CIElement'],
            [ type:'valueClosure', property:'costDescription', closure: { colmap, values, locatedObjects -> "[Transaction Charge] ${values[colmap['ResourceName']]}, ${values[colmap['AgreementName']]}, ${values[colmap['InvoiceNotes']]} "} ]
          ]
        ]
      ]
    ]
  ],
  cols: [
    [colname:'InvoiceId', gormMappingPath:'invoice.invoiceNumber', desc:''],
    [colname:'SubscriptionId', desc:'Used to match to an existing KB+ subscription - must contain the KB+ Subscription Reference to match. Subscriptions are matched using references from JC Namespace'],
    [colname:'JC_OrderNumber', desc:''],
    [colname:'InvoiceNumber', desc:'Used to match this line item to an existing KB+ Invoice. Line must first match an organisation via InstitutionId, then this is matched on Invoice Reference. If none found, a new invoice will be created'],
    [colname:'PoNumber', desc:''],
    [colname:'IssuedDate', desc:''],
    [colname:'DueDate', desc:''],
    [colname:'InstitutionName', desc:''],
    [colname:'InstitutionId', desc:'Used to look up an institution based on the JC Institution ID.'],
    [colname:'ISNIId', desc:''],
    [colname:'AccountId', desc:''],
    [colname:'ResourceName', desc:''],
    [colname:'ResourceId', desc:''],
    [colname:'AgreementName', desc:''],
    [colname:'AgreementId', desc:''],
    [colname:'PublisherName', desc:''],
    [colname:'InvoicePeriodStart', desc:''],
    [colname:'InvoicePeriodEnd', desc:''],
    [colname:'Price', desc:''],
    [colname:'AnnualAccessFee', desc:''],
    [colname:'AdditionalFees', desc:''],
    [colname:'SubscriptionTransactionCharge', desc:''],
    [colname:'SubscriptionVAT', desc:''],
    [colname:'DatePaid', desc:''],
    [colname:'InvoiceNotes', desc:''],
    [colname:'InvoiceStatus', desc:''],
    [colname:'Currency', desc:''],
    [colname:'InvoiceTotalExcVat', desc:''],
    [colname:'InvoiceTransactionCharge', desc:''],
    [colname:'InvoiceVat', desc:''],
    [colname:'InvoiceTotal', desc:''],
    [colname:'ItemCount', desc:''],
    [colname:'TotalSubscriptionValue', desc:''],
    [colname:'InvoiceType', desc:'', type:'vocab', mapping:[
      'SubscriptionInvoice':'Price',
    ]]
  ]
];
