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

  <g:form id="create_org_role_link" url="[controller:'ajax',action:'addOrgRole']" method="post">
    <div class="modal-body">
      <dl>
        <dt><label class="control-label">Orgs</label></dt>
        <dd>
          <table id="org_role_tab" class="table table-bordered">
            <thead>
              <tr id="add_org_head_row">
              </tr>
            </thead>
          </table>
        </dd>
      </dl>

      <dl>         
        <dt><label class="control-label">Role</label></dt>
        <dd>    
          <g:select name="orgRole" 
                    noSelection="${['':'Select One...']}" 
                    from="${com.k_int.kbplus.RefdataValue.findAllByOwner(com.k_int.kbplus.RefdataCategory.get(2))}" 
                    optionKey="id" 
                    optionValue="value"/>
        </dd>
      </dl>

    </div>

    <div class="modal-footer">
      <input id="org_role_add_btn" type="submit" class="btn btn-primary" value="Add">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
    </div>
  </g:form>

</div>

<script language="JavaScript">
  var oOrTable;

  $(document).ready(function(){

    $('#add_org_head_row').empty();
    $('#add_org_head_row').append("<td>Org Name</td>");
    $('#add_org_head_row').append("<td>Select</td>");

    oOrTable = $('#org_role_tab').dataTable( {
                             'bAutoWidth': true,
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

    oOrTable.fnAdjustColumnSizing();

  });
</script>
