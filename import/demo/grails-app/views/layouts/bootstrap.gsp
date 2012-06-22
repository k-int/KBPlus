<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<!doctype html>
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

   <style>

   </style>

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

    <nav class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          
          <sec:ifLoggedIn>
            <a class="brand" href="${createLink(uri: '/')}">KB+ Data Import Explorer</a>
          </sec:ifLoggedIn>

          <div class="nav-collapse">
            <ul class="nav">              
              <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown"> Data Explorer <b class="caret"></b> </a>
                <ul class="dropdown-menu">
                  <li<%= request.forwardURI == "${createLink(uri: '/')}" ? ' class="active"' : '' %>><a href="${createLink(uri: '/')}">Home</a></li>
                  <li <%='package'== controllerName ? ' class="active"' : '' %>><g:link controller="package">Package</g:link></li>
                  <li <%='org'== controllerName ? ' class="active"' : '' %>><g:link controller="org">Organisations</g:link></li>
                  <li <%='platform'== controllerName ? ' class="active"' : '' %>><g:link controller="platform">Platform</g:link></li>
                  <li <%='titleInstance'== controllerName ? ' class="active"' : '' %>><g:link controller="titleInstance">Title Instance</g:link></li>
                  <li <%='titleInstancePackagePlatform'== controllerName ? ' class="active"' : '' %>><g:link controller="titleInstancePackagePlatform">Title Instance Package Platform</g:link></li>
                  <li <%='subscription'== controllerName ? ' class="active"' : '' %>><g:link controller="subscription">Subscriptions</g:link></li>
                  <li <%='license'== controllerName ? ' class="active"' : '' %>><g:link controller="license">Licenses</g:link></li>
                </ul>
              </li>

              <g:if test="${user}">
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown"> Forms and Actions <b class="caret"></b> </a>
                  <ul class="dropdown-menu">

                    <li <%= ( ( 'myInstitutions'== controllerName ) && ( 'manageAffiliations'==actionName ) ) ? ' class="active"' : '' %>>
                       <g:link controller="myInstitutions" action="manageAffiliations">Manage Affiliations</g:link></li>

                     <li><hr/></li>
                     <g:if test="${user.affiliations && user.affiliations.size() > 0}">
                       <li>Manage Licenses For...</li>
                       <g:each in="${user.affiliations}" var="ua">
                         <li>
                           <g:link controller="myInstitutions" 
                                   action="manageLicenses" 
                                   params="${[shortcode:ua.org.shortcode]}" 
                                   class="btn btn-primary btn-small"></i>${ua.org.name}</g:link>
                         </li>
                       </g:each>
                     </g:if>
                     <g:else>
                       <li>Please use the manage affiliations option to request access
                                to your home institution</li>
                     </g:else>
                  </ul>
                </li>
              </g:if>

              <sec:ifAnyGranted roles="ROLE_ADMIN">
                 <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown"> Admin Actions <b class="caret"></b> </a>
                  <ul class="dropdown-menu">
                    <li <%= ( ( 'admin'== controllerName ) && ( 'manageAffiliationRequests'==actionName ) ) ? ' class="active"' : '' %>>
                       <g:link controller="admin" action="manageAffiliationRequests">Manage Affiliation Requests</g:link></li>
                  </ul>
                </li>

              </sec:ifAnyGranted>
            </ul>
          </div>
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <g:layoutBody/>

      <hr>

      <footer>
        <div style="float:right; text-align:right;"><g:meta name="app.buildProfile"/> / <g:meta name="app.version"/> / build <g:meta name="app.buildNumber"/> on <g:meta name="app.buildDate"/><br/><a href="https://github.com/k-int/KBPlus">Source</a></div>
        <div<
        &copy; <a href="http://www.k-int.com">Knowledge Integration Ltd</a> 2012<br/>
        <sec:ifLoggedIn>Logged In</sec:ifLoggedIn>
        <sec:ifNotLoggedIn>Not Logged In</sec:ifNotLoggedIn>
        </div>
      </footer>
    </div>

    <r:layoutResources/>

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

  </body>
</html>
