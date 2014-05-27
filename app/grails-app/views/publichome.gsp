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
            <g:render template="/templates/loginDiv"/>


            <div class="twitter">
                <g:render template="twitter" contextPath="/templates"/>
            </div>

        </div>
    </div>
</div>
</body>
</html>
