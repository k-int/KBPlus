
<div name="coreAssertionEdit" class="modal hide">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>

    <h3>Core Dates for ${title}</h3>
  </div>
  <g:formRemote  name="coreExtendForm" url="[controller: 'ajax', action: 'coreExtend']" before="hideModal()" onComplete="showCoreAssertionModal()" update="magicArea">
  <div class="modal-body">
    <table class="table table-bordered">
      <thead>
        <th>Core Start Date</th>
        <th>Core End Date</th>
        <th>Action</th>
      </thead>
      <tbody>
         <g:each in="${coreDates}" var="coreDate">
            <tr>
              <td>
                <g:xEditable owner="${coreDate}" type="date" field="startDate" /> 
              </td>
              <td>
                <g:xEditable owner="${coreDate}" type="date" field="endDate" /> 
              </td>
              <td>
                <g:remoteLink url="[controller: 'ajax', action: 'deleteCoreDate', params:[tipID:tipID,title:title,coreDateID:coreDate.id]]" method="get" name="show_core_assertion_modal" 
                before="hideModal()" onComplete="showCoreAssertionModal()" update="magicArea" class="delete-coreDate">Delete </g:remoteLink></dd>
              </td>
            </tr>
         </g:each>
      </tbody>
    </table>
      <input type="hidden" name="tipID" value="${tipID}"/>
      <input type="hidden" name="title" value="${title}"/>

       <dl>
        <dt><label class="control-label">Extend Core Dates:</label></dt>
        <dd>
            <label class="property-label">Core Start:</label> 
            <g:simpleHiddenValue  id="coreStartDate" name="coreStartDate" type="date"/>
        </dd>
        <dd>
            <label class="property-label">Core End:</label> 
              <g:simpleHiddenValue id="coreEndDate" name="coreEndDate" type="date"/>
        </dd>
      </dl>
  </div>
  <div class="modal-footer">
    <input type="submit" value="Apply" class="btn btn-primary btn-small"/>
  </div>
  </g:formRemote>
</div>

<script type="text/javascript">
   $('.xEditableValue').editable();
   $(".simpleHiddenRefdata").editable({
     url: function(params) {
       var hidden_field_id = $(this).data('hidden-id');
       $("#"+hidden_field_id).val(params.value);
       // Element has a data-hidden-id which is the hidden form property that should be set to the appropriate value
     }
   });
</script>>


