<!doctype html>
<html>
<head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Knowledge Base+</title>
</head>

<body class="public">
<g:render template="public_navbar" contextPath="/templates" model="['active': 'home']"/>


<div class="container">
    <div class="row">
        <div class="span8">
            <markdown:renderHtml><g:dbContent key="kbplus.welcome.text"/></markdown:renderHtml>
        </div>

        <div class="span4">
            <div class="well">
                <h2>Login</h2>

                <p><span class="external-link"><g:link controller="home"
                                                       action="index">Knowledge Base+ Member Login</g:link></span></p>
            </div>

            <div class="twitter">
                <g:render template="twitter" contextPath="/templates"/>
            </div>

        </div>
    </div>
</div>
</body>
</html>
