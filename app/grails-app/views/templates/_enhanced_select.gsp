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
        
    var escr_refdata_profile = $(this).data('modal').options.profile;
    var escr_owner = $(this).data('modal').options.owner;
        
    // console.log("%o",$(this).data('modal').options.profile);
    $('#escr_head_row').empty();
    $('#escr_head_row').append("<td>Col 1</td>");
    $('#escr_head_row').append("<td>Col 2</td>");
    // var baseurl = '<g:createLink controller="ajax" action="genericSetRel"/>?elementid='+owner;

    oTable = $('#escr_tab').dataTable( {
                             "sScrollY": "200px",
                             "sAjaxSource": "<g:createLink controller="ajax" action="refdataSearch"/>/"+escr_refdata_profile+".json",
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
                                     var cl = "javascript:enhancedSelectRefOption('"+escr_owner+"','"+data+"');"
                                     return '<a href="'+cl+'">Select</a>';
                                   }
                                 } ]
                           } );
    });
  });

  function enhancedSelectRefOption(owner,data) {
    var baseurl = '<g:createLink controller="ajax" action="genericSetRel"/>?elementid='+owner;
    $.ajax({
      url: baseurl+'&value='+data,
      dataType: 'text',
      success: function(data) {
        console.log("%o",data);
        window.location.reload()
      },
      error: function(hdr,status,errorThrown) {  
        console.log("Problem processing %o, %s, %s",hdr,status,errorThrown);
      }
    });
  }

</script>

