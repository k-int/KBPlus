<%@ page contentType="text/html;charset=UTF-8"%>
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
			<li>ONIX-PL License Comparison</li>
		</ul>
	</div>

	<div class="container">
		<h1>ONIX-PL Licence Comparison</h1>
	</div>

	<div class="container">
		<div class="row">
			<div class="span8">
				<g:form id="compare" name="compare" action="matrix" method="get">
					<div>
						<label for="license1">License 1:</label>
						<g:select name="license1" class="compare-license" from="${list}"
							optionKey="id" optionValue="title" />
					</div>
					<div>
						<label for="license2">License 2:</label>
						<g:select name="license2" class="compare-license" from="${list}"
							optionKey="id" optionValue="title"
							noSelection="${['all': "All"]}" multiple="true" value="all" />
					</div>
					<div>
						<label for="section">Compare section:</label>
						<g:treeSelect name="sections" id="section" class="compare-section"
							options="${termList}" multiple="true" />
					</div>
					<div id="limit-license-display">
						Only show licenses:<br />
						<g:radio id="same" class="compare-radio" name="match" value="true" />
						&nbsp;&nbsp;<label for="same">The same</label><br />
						<g:radio id="diff" class="compare-radio" name="match"
							value="false" />
						&nbsp;&nbsp;<label for="diff">Different</label><br />
						<g:radio id="all" class="compare-radio" name="match" value=""
							checked="${true}" />
						&nbsp;&nbsp;<label for="all">Show all</label>
					</div>
					<div>
						<label for="max">Number of results per page: </label>
						<g:select name="max" from="${[5, 10, 15]}" class="compare-results" />
					</div>
					<div>
					  <g:submitButton name="Compare" class="btn btn-primary" />
					</div>
				</g:form>
			</div>
		</div>
	</div>
</body>
</html>