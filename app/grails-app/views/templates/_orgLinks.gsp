  <table class="table table-bordered licence-properties">
    <thead>
      <tr>
        <td>Organisation Name</td>
        <td>Role</td>
        <td>actions</td>
      </tr>
    </thead>
    <g:each in="${roleLinks}" var="role">
      <tr>
        <td><g:link controller="Organisations" action="info" id="${role.org.id}">${role.org.name}</g:link></td>
        <td>${role.roleType.value}</td>
        <td><a href="#">Delete</a></td>
      </tr>
    </g:each>
  </table>
  <a class="btn" data-toggle="modal" href="#osel_add_modal" >Add Org Link</a>


<div id="osel_add_modal" class="modal hide">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Add Org Link</h3>
  </div>

  <div>
    <table id="org_role_tab" class="table table-bordered">
      <thead>
        <tr id="add_org_head_row">
          <td>Org Name</td>
          <td>Select</td>
        </tr>
      </thead>
    </table>
  </div>

  <div class="modal-footer">
    <a href="#" class="btn" data-dismiss="modal">Close</a>
  </div>

</div>

<script language="JavaScript">
  var oOrTable;

  $(document).ready(function(){

    oOrTable = $('#org_role_tab').dataTable( {
                             "sScrollY": "200px",
                             "sAjaxSource": "<g:createLink controller="ajax" action="refdataSearch"/>/ContentProvider.json",
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
                                     return '<input type="checkbox" name="orgoid" value="'+data+'"/>';
                                   }
                                 } ]
                           } );

    $('#org_role_tab tbody tr').live('click', function () {
      $(this).addClass('row_selected');
      $(this).toggleClass('row_selected');
    });

  });
</script>
