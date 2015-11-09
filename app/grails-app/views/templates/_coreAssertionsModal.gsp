
<div name="coreAssertionEdit" class="modal hide">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Core Dates for ${tip?.title?.title}</h3>
  </div>

  <div class="modal-body">

    <g:if test="${message}">
      <bootstrap:alert class="alert-info">${message}</bootstrap:alert>
    </g:if>

    <p>Edit existing core dates using the table below. Click the start and end dates to modify them and then the tick to accept your change. Once finished, click the Done button below</p>
    
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
              <g:if test="${editable == 'true' || editable == true}">
                <g:remoteLink url="[controller: 'ajax', action: 'deleteCoreDate', params:[tipID:tipID,title:title,coreDateID:coreDate.id]]" method="get" name="show_core_assertion_modal" 
                before="hideModal()" onComplete="showCoreAssertionModal()" update="magicArea" class="delete-coreDate">Delete </g:remoteLink></dd>
                </g:if>
              </td>
            </tr>
         </g:each>
      </tbody>
    </table>


    <div class="well">
      <h4>Add new core date range</h4>
      <p>Use this form to add new core date ranges. Set the start date and optionally an end date then click apply. If the dates you specify can be merged with a statement
         in the table above they will be, or a new line will be created.</p>
      
      <g:formRemote  name="coreExtendForm" url="[controller: 'ajax', action: 'coreExtend']" before="hideModal()" onComplete="showCoreAssertionModal()" update="magicArea">
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
        <input type="submit" value="Apply" class="btn btn-primary btn-small"/>
      </g:formRemote>
    </div>

  </div>

  <div class="modal-footer">
    <button type="button" data-dismiss="modal">Done</button>
  </div>

</div>

<g:if test="${editable=='true' || editable == true}">
  <script type="text/javascript">
    $('.xEditableValue').editable();
    $(".simpleHiddenRefdata").editable({
      url: function(params) {
        var hidden_field_id = $(this).data('hidden-id');
        $("#"+hidden_field_id).val(params.value);
        // Element has a data-hidden-id which is the hidden form property that should be set to the appropriate value
      }
    });
  </script>
</g:if>


