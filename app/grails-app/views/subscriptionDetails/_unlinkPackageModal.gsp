<div id="unlinkPackageModal" class="modal hide">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h6>Unlink Package: ${pkg}</h6>
  </div>
  <div class="modal-body">
    <p> No user actions required for this process.</p>
    <table class="table table-bordered">
      <thead>
        <th>Item</th>
        <th>Details</th>
        <th>Action</th>
      </thead>
      <tbody>
      <g:set var="actions_needed" value="false"/>

         <g:each in="${conflicts_list}" var="conflict_item">
            <tr>
              <td>
                ${conflict_item.name}
              </td>
              <td>
                  <ul>
                  <g:each in="${conflict_item.details}" var="detail_item">
                <li> 
                <g:if test="${detail_item.link}">
                  <a href="${detail_item.link}">${detail_item.text}</a>
                </g:if>
                <g:else>
                  ${detail_item.text}
                </g:else>
                </li>
                  </g:each>
                  </ul>
              </td>
              <td>
              %{-- Add some CSS based on actionRequired to show green/red status --}%
              <g:if test="${conflict_item.action.actionRequired}">
                  <i style="color:red" class="fa fa-times-circle"></i>
                  <g:set var="actions_needed" value="true"/>

              </g:if>
              <g:else>
                <i style="color:green" class="fa fa-check-circle"></i>
              </g:else>
                 ${conflict_item.action.text}       
              </td>
            </tr>
         </g:each>
      </tbody>
    </table>
  </div>
  <div class="modal-footer">
    <g:form action="unlinkPackage" onsubmit="return confirm('Deletion of IEs is not reversable. Are you sure?')" method="POST">
      <input type="hidden" name="package" value="${pkg.id}"/>
      <input type="hidden" name="subscription" value="${subscription.id}"/>
      <input type="hidden" name="confirmed" value="Y"/>
      <button type="submit"class="btn btn-danger btn-small">Confirm Delete</button>
    </g:form>
  </div>
</div>  
