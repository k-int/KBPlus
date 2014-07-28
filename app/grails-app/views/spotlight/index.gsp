
<form class="form-search">
    <input type="text" id="spotlight_text" class="input-medium search-query">
</form>
<div id="spotlight-search-results">
</div>

<script language="JavaScript">
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

        if(event.keyCode == 27){
        	$('.dlpopover').popover('toggle')
        }else{
	        timeoutReference = setTimeout(function() {
	            reloadSpotlightSearchResults();
	        }, 500);       	
        }
    });

</script>
