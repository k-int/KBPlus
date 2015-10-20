<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Not Found</title>
</head>

<body>
<div class="container">
    <b>404</b> That's an error.

    <g:if test="${flash.error}">
      <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>
    <g:else>
	    <p>The requested URL was not found. That's all we know.</p>
    </g:else>
</div>
</body>
</html>
