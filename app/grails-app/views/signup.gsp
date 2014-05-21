<!doctype html>
<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Sign Up | Knowledge Base+</title>
  </head>

    <body class="public">
    <g:render template="public_navbar" contextPath="/templates" model="['active':'signup']"/>

        <div class="container">
            <h1>How can institutions get involved?</h1>
        </div>

        <div class="container">
            <div class="row">
                <div class="span8">
                   <markdown:renderHtml><g:dbContent key="kbplus.signup.text"/></markdown:renderHtml>
                </div>
                <div class="span4">
					<div class="well">
						<h2>Login</h2>
						<p><span class="external-link"><g:link controller="myInstitutions" action="dashboard">Knowledge Base+ Member Login</g:link></span></p>
					</div>
                    <div class="twitter">
                        <g:render template="twitter" contextPath="/templates"/>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
