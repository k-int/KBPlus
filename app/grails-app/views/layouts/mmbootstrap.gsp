<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<!doctype html>

<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

<html lang="en">
  <head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
    <meta name="description" content="">
    <meta name="author" content="">

    <meta name="viewport" content="initial-scale = 1.0">

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <r:require modules="scaffolding"/>

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <g:layoutHead/>
    <r:layoutResources/>

    <script type="text/javascript">
      var _gaq = _gaq || [];
      _gaq.push(['_setAccount', '${grailsApplication.config.kbplus.analytics.code}']);
      _gaq.push(['_trackPageview']);
      (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
    </script>
  </head>

  <body>

    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <g:link controller="myInstitutions" action="dashboard" class="brand">KB+</g:link>
          <div class="nav-collapse">
            <ul class="nav">
              <sec:ifLoggedIn>

                <sec:ifAnyGranted roles="ROLE_ADMIN">
                  <li class="dropdown">
                    <a href="#" class="dropdown-toggle explorer-link" data-toggle="dropdown"> Data Explorer <b class="caret"></b> </a>
                    <ul class="dropdown-menu" style="max-width:none;">
                      <li<%= request.forwardURI == "${createLink(uri: '/home')}" ? ' class="active"' : '' %>><a href="${createLink(uri: '/home')}">Search</a></li>
                      <li <%='package'== controllerName ? ' class="active"' : '' %>><g:link controller="package">Package</g:link></li>
                      <li <%='org'== controllerName ? ' class="active"' : '' %>><g:link controller="org">Organisations</g:link></li>
                      <li <%='platform'== controllerName ? ' class="active"' : '' %>><g:link controller="platform">Platform</g:link></li>
                      <li <%='titleInstance'== controllerName ? ' class="active"' : '' %>><g:link controller="titleInstance">Title Instance</g:link></li>
                      <li <%='titleInstancePackagePlatform'== controllerName ? ' class="active"' : '' %>><g:link controller="titleInstancePackagePlatform">Title Instance Package Platform</g:link></li>
                      <li <%='subscription'== controllerName ? ' class="active"' : '' %>><g:link controller="subscription">Subscriptions</g:link></li>
                      <li <%='license'== controllerName ? ' class="active"' : '' %>><g:link controller="license">Licences</g:link></li>
                    </ul>
                  </li>
                </sec:ifAnyGranted>
  
                <g:if test="${user}">
                  <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"> Manage <b class="caret"></b> </a>
                    <ul class="dropdown-menu" style="max-width:none;">
  
  
                       <g:set var="usaf" value="${user.authorizedAffiliations}" />
                       <g:if test="${usaf && usaf.size() > 0}">
                         <g:each in="${usaf}" var="ua">
                           <li>
                             <g:link controller="myInstitutions" 
                                     action="currentLicenses" 
                                     params="${[shortcode:ua.org.shortcode]}">${ua.org.name} - Licences</g:link>
                           </li>
                           <li>
                             <g:link controller="myInstitutions" 
                                     action="currentSubscriptions" 
                                     params="${[shortcode:ua.org.shortcode]}">${ua.org.name} - Subscriptions</g:link>
                           </li>
                         </g:each>
                       </g:if>
                       <g:else>
                         <li>Please request institutional affiliations via your <g:link controller="profile" action="index">Profile Page</g:link></li>
                       </g:else>
                       <li class="divider"></li>
                       <li><g:link controller="myInstitutions" action="dashboard">Dashboard</g:link></li>
                       <li><g:link controller="myInstitutions" action="index">Alerts</g:link></li>
                       <li><a href="/reports">Reports</a></li>
                       <li><a href="https://knowledgebaseplus.wordpress.com/kb-support/">Help</a></li>
                    </ul>
                  </li>
                </g:if>
  
                <sec:ifAnyGranted roles="ROLE_ADMIN">
                   <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"> Admin Actions <b class="caret"></b> </a>
                    <ul class="dropdown-menu">
                      <li <%= ( ( 'admin'== controllerName ) && ( 'manageAffiliationRequests'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="manageAffiliationRequests">Manage Affiliation Requests</g:link></li>
                      <li class="divider"></li>
                      <li <%= ( ( 'admin'== controllerName ) && ( 'reconcile'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="reconcile">Manage Data Reconciliation</g:link>
                      </li>
                      <li <%= ( ( 'startFTIndex'== controllerName ) && ( 'index'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="startFTIndex" action="index">Start FT Index Update</g:link>
                      </li>
                      <li <%= ( ( 'admin'== controllerName ) && ( 'showAffiliations'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="showAffiliations">Show Affiliations</g:link>
                      </li>
                      <li <%= ( ( 'admin'== controllerName ) && ( 'allNotes'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="allNotes">All Notes</g:link>
                      </li>
                      <li <%= ( ( 'admin'== controllerName ) && ( 'dataCleanse'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="dataCleanse">Run Data Cleaning (Nominal Platforms)</g:link>
                      </li>
                      <li <%= ( ( 'admin'== controllerName ) && ( 'titleAugment'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="admin" action="titleAugment">Run Data Cleaning (Title Augment)</g:link>
                      </li>
                    </ul>
                  </li>
  
                </sec:ifAnyGranted>
              </sec:ifLoggedIn>
            </ul>
            <ul class="nav pull-right">
              <sec:ifLoggedIn>
                <g:if test="${user}">
                  <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">${user.displayName} <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                      <li <%= ( ( 'profile'== controllerName ) && ( 'index'==actionName ) ) ? ' class="active"' : '' %>>
                         <g:link controller="profile" action="index">Profile</g:link></li>
                      <li><g:link controller="logout">Logout</g:link></li>
  
                    </ul>
                  </li>
                </g:if>
              </sec:ifLoggedIn>
              <sec:ifNotLoggedIn>
                <li><g:link controller="myInstitutions" action="dashboard">Login</g:link></li>
              </sec:ifNotLoggedIn>
            </ul>
          </div>
        </div>
      </div>
    </div>

   <div class="navbar-push"></div>

       
  <g:layoutBody/>
      
  <div id="Footer">
      <div class="navbar navbar-footer">
          <div class="navbar-inner">
              <div class="container">
                  <div>
                      <ul class="footer-sublinks nav">
                          <!--<li><a href="${createLink(uri: '/terms-and-conditions')}">Terms & Conditions</a></li>-->
                          <li><a href="${createLink(uri: '/privacy-policy')}">Privacy Policy</a></li>
                          <li><a href="${createLink(uri: '/freedom-of-information-policy"')}">Freedom of Information Policy</a></li>
                      </ul>
                  </div>
                      
                  <div class="pull-right">
                      <div class="nav-collapse">
                          <ul class="nav">
                              <li class="dropdown">
                                  <a href="#"
                                     class="dropdown-toggle"
                                     data-toggle="dropdown">
                                      Tools
                                      <b class="caret"></b>
                                  </a>
                                  <ul class="dropdown-menu">
                                      <li><a href="http://www.kbplus.ac.uk/kbplus/myInstitutions/index">KB+</a></li>
                                      <li><a href="http://test.kbplus.ac.uk/kbplus/myInstitutions/index">KB+ Sandpit</a></li>
                                  </ul>
                              </li>
                          </ul>
                      </div>
                  </div>
              </div>
          </div>
      </div>
                
      <div class="clearfix"></div>
          
      <div class="footer-links container">
          <div class="row">
              <div class="pull-left">
                  <a href="http://www.jisc-collections.ac.uk/"><div class="sprite sprite-jisc_collections_logo">JISC Collections</div></a>
              </div>
              <div class="pull-right">
                  <a href="http://www.kbplus.ac.uk"><div class="sprite sprite-kbplus_logo">Knowledge Base Plus</div></a>
              </div>
          </div>
      </div>
  </div>

    <r:layoutResources/>
    
    <div id="SupportTab">
            <a href="mailto:kbplus@jisc-collections.ac.uk?subject=KBPlus%20Support%20Query"><i class="icon-question-sign icon-white"></i>Request Support</a>
        </div>

    <!--
    <script type="text/javascript" src="//assets.zendesk.com/external/zenbox/v2.4/zenbox.js"></script>
    <style type="text/css" media="screen, projection">
      @import url(//assets.zendesk.com/external/zenbox/v2.4/zenbox.css);
    </style>
    <script type="text/javascript">
      if (typeof(Zenbox) !== "undefined") {
        Zenbox.init({
          dropboxID:   "20059881",
          url:         "https://kbplus.zendesk.com",
          tabID:       "feedback",
          tabColor:    "green",
          tabPosition: "Right"
        });
      }
    </script>
    -->

  </body>
</html>
