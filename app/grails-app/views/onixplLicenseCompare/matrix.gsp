<%@ page
	import="com.k_int.kbplus.OnixplLicenseCompareController; com.k_int.kbplus.OnixplLicense; com.k_int.kbplus.OnixplUsageTerm; com.k_int.kbplus.RefdataCategory"
	contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
<meta name="layout" content="mmbootstrap" />
<title>KB+</title>
</head>
<body>
	<div class="container">
		<ul class="breadcrumb">
			<li><g:link controller="home" action="index">Home</g:link> <span
				class="divider">/</span></li>
			<li>ONIX-PL <g:message code="licence" default="Licence"/> Comparison</li>
		</ul>
	</div>

	<div class="container">
		<h1>ONIX-PL <g:message code="licence" default="Licence"/> Comparison</h1>
	</div>
	<div class="container">
		<g:if test="${flash.message}">
			<bootstrap:alert class="alert-info">
				${flash.message}
			</bootstrap:alert>
		</g:if>
		<g:render template="tables" model="${request.parameterMap}" />
	</div>
</body>
</html>
