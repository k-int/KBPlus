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
			<li>ONIX-PL <g:message code="licence" default="Licence"/> Comparison</li>
		</ul>
	</div>

	<div class="container">
		<h1>ONIX-PL <g:message code="licence" default="Licence"/> Comparison</h1>
	</div>

	<div class="container">
		<div class="row">
			<div class="span8">
				<g:form id="compare" name="compare" action="matrix" method="post">
					<div>
						<label for="addIdentifierSelect">Search licence for comparison:</label>

		                <input type="hidden" name="selectedIdentifier" id="addIdentifierSelect"/>
		                <button type="button"class="btn btn-success" id="addToList" >Add</button>
					</div>
					
					<label for="selectedLicences">Licences selected for comparison:</label>
					<g:select id="selectedLicences" name="selectedLicences" class="compare-license" from="${[]}" multiple="true" />


					<div>
						<label for="section">Compare section:</label>
						<g:treeSelect name="sections" id="section" class="compare-section"
							options="${termList}" selected="true" multiple="true" />
					</div>

					<div>
					  <g:submitButton name="Compare" class="btn btn-primary" />
					</div>
				</g:form>
			</div>
		</div>
	</div>
	  <r:script language="JavaScript">

	    $(function(){
	      $('#addToList').click(function() {
	      		var option = $("input[name='selectedIdentifier']").val()
	      		var option_name = option.split("||")[0]
	      		var option_id = option.split("||") [1]
	      		var list_option = "<option selected='selected' value='"+option_id+"''>"+option_name+"</option>"
	      		$("#selectedLicences").append(list_option)
			});

	      $("#addIdentifierSelect").select2({
  	        width: '90%',
	        placeholder: "Search for a licence...",
	        minimumInputLength: 1,
	        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
	          url: "<g:createLink controller='ajax' action='lookup'/>",
	          dataType: 'json',
	          data: function (term, page) {
	              return {
	                  q: term, // search term
	                  page_limit: 10,
	                  baseClass:'com.k_int.kbplus.OnixplLicense'
	              };
	          },
	          results: function (data, page) {
	            return {results: data.values};
	          },
	        }
	      });
	    });
      </r:script>
</body>
</html>
