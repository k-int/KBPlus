<!doctype html>
<html>
<head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Contact Us | Knowledge Base+</title>
</head>

<body class="public">
<g:render template="public_navbar" contextPath="/templates" model="['active': 'contact']"/>

<div class="container">
    <h1>Contact Us</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span8 contact-wells">
            <markdown:renderHtml><g:dbContent key="kbplus.contact.text"/></markdown:renderHtml>
        </div>

        <div class="span4">
            <div class="well">
                <h2>Login</h2>

                <p><span class="external-link"><g:link controller="myInstitutions"
                                                       action="dashboard">Knowledge Base+ Member Login</g:link></span>
                </p>
            </div>

            <div class="twitter">
                <g:render template="twitter" contextPath="/templates"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
