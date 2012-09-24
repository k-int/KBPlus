<!doctype html>
<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Sign Up | Knowledge Base+</title>
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
            <h1>How can institutions get involved?</h1>
        </div>

        <div class="container">
            <div class="row">
                <div class="span8">

<p>The service will launch with the nine universities which have been testing KB+ to date as the core users.</p> 
<p>We will then be opening up the service to other libraries across the UK. We want to manage the joining process for KB+ quite carefully. Whilst there is a lot of data that will be instantly useable and valuable to libraries available from the <a href="${createLink(uri: '/publicExport')}">exports page</a>, such as all of the publication and licence information, it will take us longer to add in local institutional information and we want to make sure that we are able to offer support and training to library staff as they start to use and engage with KB+. This means that new members will be added incrementally over the course of the first year.</p>
<p>If you are interested in signing up to KB+, please contact <a href="mailto:help@jisc-collections.ac.uk">help@jisc-collections.ac.uk</a> and we'll discuss it with you directly.</p>
<p>As with JUSP, KB+ will have a participation agreement covering the use of the service, which will be made available shortly.</p>

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
<p><span class="externalLinkIcon"><a rel="external" href="http://twitter.com/JISCKBPlus">Follow Knowledge Base + on Twitter (@JISCKBPlus)</a></span></p>
</div>
</noscript>
					</div>
                </div>
            </div>
        </div>

</html>
