<!doctype html>
<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>No Host Platform URL | Knowledge Base+</title>
  </head>

    <body class="public">
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner navbar-public">
                <div class="container">
                    <img class="brand" alt="Knowledge Base + logo" src="images/kb_large_icon.png" />
                    <div class="nav-collapse">               
                    <ul class="nav">
                            <li>
                                <a href="${createLink(uri: '/')}"> Home </a>
                            </li>
                            <li class="active">
                                <a href="${createLink(uri: '/about')}"> About KB+ </a>
                            </li>
                            <li>
                                <a href="${createLink(uri: '/signup')}"> Sign Up </a>
                            </li>
                            <li>
                                <a href="${createLink(uri: '/publicExport')}"> Exports </a>
                            </li>
                            <li class="last">
                                <a href="${createLink(uri: '/contact-us')}"> Contact Us </a>
                            </li>
                        </ul>            
                    </div>                     
                </div>           
            </div>
        </div>

        <div class="navbar-push-public"></div>

        <div class="container">
            <h1>No Host Platform URL</h1>
        </div>

        <div class="container">
            <div class="row">
                <div class="span8">
                  <markdown:renderHtml><g:dbContent key="kbplus.noHostPlatformURL"/></markdown:renderHtml>
                </div>
            </div>
        </div>

</html>
