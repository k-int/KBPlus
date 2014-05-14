
var scrolling = false;

$(document).ready(function() {

  // Create the table.
  $('.onix-matrix').each(function(){
    var table = $(this).dataTable( {
      // "bJQueryUI": true,
      "sScrollX"        : "100%",
      "sScrollY"        : ($(window).height() - 150),
      "bScrollCollapse" : true,
      "bPaginate"       : false,
      "bLengthChange"   : false,
      "bInfo"           : false,
      "bFilter"         : false,
      "bSort"           : false,
    } );
    
    // Fix the first column.
    new $.fn.dataTable.FixedColumns( table, {
      "iLeftColumns": 2
    });

    // Column filter.
//    var colVis = new $.fn.dataTable.ColVis( table.fnSettings(), {
//      "buttonText"  : "Show / Hide License(s)",
//      "aiExclude" : [0,1]
//    });
//    var button = $(colVis.button());
//
//    // Append the button.
//    $(this).siblings(".filter-cell").append(button);
  });
  
  // Now attach an event listener to enforce at least one license selected.
  $("body").on("change", function (e){
    // ColVis_collection
    var inputs = $('ul.ColVis_collection input[type="checkbox"]');
    if ($(e.target).is(inputs)) {
      var checked = $('ul.ColVis_collection input[type="checkbox"]:checked');
      if (checked.length == 1) {
        // Disable the button.
        checked.prop("disabled", true);
      } else {
        inputs.prop("disabled", false);
      }
    }
  });
  
  // Get all the scrollable tables created and sync up the scroll positions to ensure that the same license
  // is displayed in the variouse areas.
  var scr_tabs = $('.dataTables_scrollBody');
  scr_tabs.on("scroll", function (e){
    
    // Test whether another element is already scrolling.
    if (scrolling != true) {
    
      // Set the scrolling flag.
      scrolling = true;
      
      // Get this scroll area.
      var area = $(this);
        
      // Go through all other scrollable areas and adjust to match this one.
      for (var i=0; i<scr_tabs.length; i++) {
        var me = scr_tabs.get(i);
        if (me != area.get(0)) {
          // Set the left scroll equal to the scrolled one.
          $(me).scrollLeft(area.scrollLeft());
        }
      }
      
      // Scrolling finished.
      scrolling = false;
    }
  });
  
  // Tooltips.
  $('.onix-code, .onix-status').tooltip(
      {placement: 'bottom', trigger:'hover', html: true, container: 'body'}
  );
  $('.onix-icons span i').popover(
    {placement: 'left', trigger:'hover', html: true, container: 'body'}
  );
  
  // Modal.
  var modal = $('#onix-modal');
  $('.text-icon, .main-annotation').each(function(){
    
    // Me.
    var me = $(this);
    
    // Get the adjacent text element.    
    var textelement = me.next('.textelement');
    
    // Bind the on click.
    me.click (function() {
      
      $('.modal-title', modal).text(
        me.is(".main-annotation") ? "Annotations" : "Text Content"
      );
      
      $('.modal-body', modal).html(
        textelement.html()
      );
      
      // Show the modal.
      modal.modal();
    });
  });
});