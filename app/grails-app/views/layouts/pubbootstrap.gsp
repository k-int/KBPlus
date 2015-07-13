<%@ page import="org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes" %>
<!doctype html>

<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

  <head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="${meta(name: 'app.name')}"/></title>
    <meta name="description" content="">
    <meta name="author" content="">

    <meta name="viewport" content="initial-scale = 1.0">
    <r:require modules="kbplus"/>


    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <g:layoutHead/>
    <r:layoutResources/>

    <tmpl:/layouts/analytics />
  </head>

  <body class="public">
    <g:layoutBody/>
    
    <div id="Footer">
      <div class="navbar navbar-footer">
          <div class="navbar-inner">
              <div class="container">
                  <div>
                      <ul class="footer-sublinks nav">
                          <li><a href="${createLink(uri: '/terms-and-conditions')}">Terms & Conditions</a></li>
                          <li><a href="${createLink(uri: '/privacy-policy')}">Privacy Policy</a></li>
                          <li><a href="${createLink(uri: '/freedom-of-information-policy')}">Freedom of Information Policy</a></li>
                      </ul>
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
  </body>

</html>
