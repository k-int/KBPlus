// KBPlusApp.gsp.js
//
$(document).ready(function() {

  $.fn.editable.defaults.mode = 'inline';
  $.fn.editable.defaults.emptytext = 'Edit';

  $('.xEditableValue').editable();
  $(".xEditableManyToOne").editable();
  $(".simpleHiddenRefdata").editable({
    url: function(params) {
      var hidden_field_id = $(this).data('hidden-id');
      $("#"+hidden_field_id).val(params.value);
      // Element has a data-hidden-id which is the hidden form property that should be set to the appropriate value
    }
  });
  
  $(".simpleReferenceTypedown").select2({
    placeholder: "Search for...",
    minimumInputLength: 1,
    ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
      url: "<g:createLink controller='ajax' action='lookup'/>",
      dataType: 'json',
      data: function (term, page) {
          return {
              format:'json',
              q: term,
              baseClass:$(this).data('domain')
          };
      },
      results: function (data, page) {
        return {results: data.values};
      }
    }
  });

  $('.dlpopover').popover({html:true,
                           placement:'bottom',
                           title:'search', 
                           trigger:'click', 
template: '<div class="popover" style="width: 400px;"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"></div></div></div>',
                           'max-width':400, 
                           content:function() {return getContent()}});
});

function getContent() {
  return $('#spotlight_popover_content_wrapper').html();
  // var result=""
  // jQuery.ajax({
  //  url:"<g:createLink controller='spotlight' action='index' />",
  //  success: function(r) {
  //             result=r;
  //           },
  //  async:   false
  // });          
  // return result;
}

function reloadSpotlightSearchResults() {
  console.log("reload...");
  $('#spotlight-search-results').load("<g:createLink controller='spotlight' action='search' />");
}
