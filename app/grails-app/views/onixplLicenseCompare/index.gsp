<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<!doctype html>
<html>
<head>
<meta name="layout" content="mmbootstrap" />
<title>KB+</title>
<r:script type="text/javascript">
  (function ($) {
    $(document).ready(function(){
      var disableOption = function (dd1, dd2) {
  
        // current selected val.
        var val = $("option:selected", dd1).attr("value");
  
        // Go through each option in 2 and ensure all are enabled,
        // appart from a matching value.
        $("option", dd2).each(function(){
          var opt = $(this);
          if (opt.attr("value") == val) {
            // Hide this.
            opt.hide();
          } else {
            opt.show();
          }
        });
      }
  
      // Default.
      var main = $('#license1');
      var secondary = $('#license2');
      disableOption (main, secondary);
  
      // Now add the onchange.
      main.on("change", function() {
        disableOption (this, secondary)
      });
    });
  })(jQuery);
</r:script>
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
						<g:select id="license1" name="license1" class="compare-license" from="${list}"
							optionKey="id" optionValue="title" />
					</div>
					<div>
						<label for="license2">License 2:</label>
						<g:select id="license2" name="license2" class="compare-license" from="${list}"
							optionKey="id" optionValue="title"
							noSelection="${['all': "All"]}" multiple="true" value="all" />
					</div>
					<div>
						<label for="section">Compare section:</label>
						<g:treeSelect name="sections" id="section" class="compare-section"
							options="${termList}" multiple="true" />
					</div>
<%--					<div id="limit-license-display">--%>
<%--						Only show licenses:<br />--%>
<%--						<g:radio id="same" class="compare-radio" name="match" value="${OnixPLService.COMPARE_RETURN_SAME}" />--%>
<%--						&nbsp;&nbsp;<label for="same">The same</label><br />--%>
<%--						<g:radio id="diff" class="compare-radio" name="match"--%>
<%--							value="${OnixPLService.COMPARE_RETURN_DIFFERENT}" />--%>
<%--						&nbsp;&nbsp;<label for="diff">Different</label><br />--%>
<%--						<g:radio id="all" class="compare-radio" name="match" value="${OnixPLService.COMPARE_RETURN_ALL}"--%>
<%--							checked="${true}" />--%>
<%--						&nbsp;&nbsp;<label for="all">Show all</label>--%>
<%--					</div>--%>
					<div>
					  <g:submitButton name="Compare" class="btn btn-primary" />
					</div>
				</g:form>
			</div>
		</div>
	</div>
</body>
</html>