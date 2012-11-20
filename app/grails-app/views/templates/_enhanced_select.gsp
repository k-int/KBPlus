<div id="enhanced_select_content_wrapper" class="modal hide">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Select X</h3>
  </div>
  
  <div>
    <table id="escr_tab" class="table table-bordered">
      <thead>
	<tr id="escr_head_row">
        </tr>
      </thead>
    </table>
  </div>
  
  <div class="modal-footer">
    <a href="#" class="btn" data-dismiss="modal">Close</a>
  </div>
  
</div>


<script language="JavaScript">

  var oTable;

  $(document).ready(function(){

    $('#enhanced_select_content_wrapper').on('show', function (e) {
        
    var refdata_profile = $(this).data('modal').options.profile;
        
    // console.log("%o",$(this).data('modal').options.profile);
    $('#escr_head_row').empty();
    $('#escr_head_row').append("<td>Col 1</td>");
    $('#escr_head_row').append("<td>Col 2</td>");
    oTable = $('#escr_tab').dataTable( {
                             "sScrollY": "200px",
                             "sAjaxSource": "<g:createLink controller="ajax" action="refdataSearch"/>/"+refdata_profile+".json",
                             "bServerSide": true,
                             "bProcessing": true,
                             "bDestroy":true,
                             "bSort":false,
                             "sDom": "frtiS",
                             "oScroller": {
                               "loadingIndicator": false
                             },
                             "aoColumnDefs": [ {
                                   "aTargets": [ 1 ],
                                   "mData": "DT_RowId",
                                   "mRender": function ( data, type, full ) {
                                     return '<a href="'+data+'">Select</a>';
                                   }
                                 } ]
                           } );
    });

    /* Click event handler */
    $('#escr_tab tbody tr').live('click', function () {
      $(this).toggleClass('row_selected');
    } );

  });
</script>

