<!doctype html>
<html>
<head>
    <meta name="layout" content="pubbootstrap"/>
    <title>About | Knowledge Base+</title>
</head>

<body class="public">
<g:render template="public_navbar" contextPath="/templates" model="['active': ' ']"/>

<div class="container">

<g:form action="journalLicences" method="post">
<div class="well form-horizontal">
	    Search Journal: <input placeholder="Title or Identifier" name="journal" value="${journal}"/>
	    Instituion: <input placeholder="Shortcode or KB+ ID" name="org" value="${org}"/>
	  
	    <button type="submit" name="search" value="yes">Search</button>
	  </div>
</div>
</g:form>
</body>
</html>