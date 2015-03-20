
<div name="coreAssertionEdit" class="modal hide">

  <div class="modal-header">
    <h6>Core Dates for ${title}</h6>
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
                before="hideModal()" onComplete="showCoreAssertionModal()" update="magicArea">Delete </g:remoteLink></dd>
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
              <div class="input-append date">
                <input class="span2 datepicker-class" size="16" type="text" 
                name="coreStartDate">
                <span class="add-on"><i class="icon-th"></i></span> 
              </div>
        </dd>
        <dd>
            <label class="property-label">Core End:</label> 
              <div class="input-append date">
                <input class="span2 datepicker-class" size="16" type="text" 
                name="coreEndDate">
                <span class="add-on"><i class="icon-th"></i></span> 
              </div>
        </dd>
      </dl>
  </div>
  <div class="modal-footer">
    <input type="submit" value="Extend" class="btn btn-primary btn-small"/>
  </div>
  </g:formRemote>
</div>

