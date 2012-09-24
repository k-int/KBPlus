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
            <h1>Welcome</h1>
        </div>

        <div class="container">
            <div class="row">
                <div class="span8">
                    <p>Welcome to Knowledge Base+, a new shared community service from JISC Collections launching in the autumn that aims to help UK libraries manage their e-resources more efficiently. Over the course of the last year <abbr title="Higher Education Funding Council for England">HEFCE</abbr> has invested significantly in the creation of a shared service knowledge base for UK academic libraries to support the management of e-resources by the academic community – Knowledge Base+ (KB+).</p>
                    <p>This is the phase one release of KB+. At its core is a centrally maintained knowledgebase, where JISC Collections will undertake the verification, normalisation, updating and sharing of data held in KB+.</p>
                    <p>What data will be included?</p>
                    <ul>
                    	<li>Publication Data for all <abbr title="National Electronic Site Licence Initiative">NESLi2</abbr>, <abbr title="Scottish Higher Education Digital Library">SHEDL</abbr> and <abbr title="Wales Higher Education Electronic Library">WHEEL</abbr> agreements all freely available under a <abbr title="Creative Commons Zero">CC0</abbr> licence.</li>
                    	<li>Subscription Information</li>
                    	<li>Licence information</li>
                    </ul>
                    <p>Further information on the content of KB+ can be found in the <a href="about.html">About</a> section.</p>
                    <h2>How can my institution sign up to KB+?</h2>
					<p>A lot of the data that has been created is available under a CC0 licence and available from the <a href="${createLink(uri: '/publicExport')}">Exports</a> page. However, in order to take advantage of institution specific features, institutions will need to sign up for the service. For further information please visit the <a href="${createLink(uri: '/signup')}">Sign Up</a> page.</p>
                </div>
                <div class="span4">
					<div class="well">
						<h2>Login</h2>
						<p><span class="externalLinkIcon"><g:link controller="myInstitutions" action="dashboard">Knowledge Base+ Member Login</g:link></span></p>
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
<p><span class="externalLinkIcon"><a rel="external" href="http://twitter.com/JISCKBPlus">Follow Knowledge Base + on Twitter (@JISCKBPlus)</a></span></p>
</div>
</noscript>
</div>

                </div>
            </div>
        </div>
    </body>
</html>
