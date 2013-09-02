<!doctype html>
<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Knowledge Base+</title>
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
                            <li>
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
            <div class="row">
                <div class="span8">
                    <markdown:renderHtml><g:dbContent key="kbplus.welcome.text"/></markdown:renderHtml>
                </div>
                <div class="span4">
					<div class="well">
						<h2>Login</h2>
						<p><span class="external-link"><g:link controller="myInstitutions" action="dashboard">Knowledge Base+ Member Login</g:link></span></p>
					</div>                                   
<div class="twitter">
<script charset="utf-8" src="http://widgets.twimg.com/j/2/widget.js"></script>
<script>
new TWTR.Widget({
  version: 2,
  type: 'profile',
  rpp: 4,
  interval: 30000,
  width: 'auto',
  height: 300,
  theme: {
    shell: {
      background: '#990000',
      color: '#ffffff'
    },
    tweets: {
      background: '#ffffff',
      color: '#222222',
      links: '#005580'
    }
  },
  features: {
    scrollbar: false,
    loop: false,
    live: false,
    behavior: 'all'
  }
}).render().setUser('JISCKBPlus').start();
</script>
<noscript>
<div class="well">
<h2>Twitter</h2>
<p><span class="external-link"><a rel="external" href="http://twitter.com/JISCKBPlus">Follow Knowledge Base + on Twitter (@JISCKBPlus)</a></span></p>
</div>
</noscript>
</div>

                </div>
            </div>
        </div>
    </body>
</html>
