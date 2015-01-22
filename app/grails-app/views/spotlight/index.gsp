
<form class="form-search">
    <input type="text" id="spotlight_text" class="input-medium search-query">
 	<i onclick="showHelp()"class="icon-question-sign"></i>
</form>
<div id="spotlight-search-results">
</div>


%{-- Problem with layers. Probably caused because of spotlight popup 
	<div class="modal hide" id="spotlight_help">
	<div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Spotlight help</h3>
    </div>

    <div class="modal-body">
    	instructions here
	</div>
</div> --}%

<script language="JavaScript">
    function showHelp() {
    	var helpStr = "Search through TitleInstances (\$t), Organisations(\$o), Subscriptions(\$s)\n"+
    	 "Packages(\$pa/ p), Platforms(\$pl), Licences (\$l), and Actions(\$a). \nUse \$ and category shortcut to filter results,"+
    	"\nSearching \$a Pages will take you to actions management screen."
    	alert(helpStr)
    }
	function reloadSpotlightSearchResults() {
	  
	  var q =  $("#spotlight_text").val();
	  if(q.length > 0 ){
		  q= encodeURIComponent(q);
		  $('#spotlight-search-results').load("<g:createLink controller='spotlight' action='search'/>"+"?query="+q);
	  }
	}
	var timeoutReference;

	//make sure we dont send too many requests. Limit to 1 per 500ms
	$('#spotlight_text').keyup(function(event) {
        var _this = $(this); 
    	if (timeoutReference) clearTimeout(timeoutReference);

        if(event.keyCode == 27){ //esc
        	$('.dlpopover').popover('toggle')
        }else{
	        timeoutReference = setTimeout(function() {
	            reloadSpotlightSearchResults();
	        }, 500);       	
        }
    });
	
	$("#spotlight_text").focus()

</script>
