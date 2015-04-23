<div id="pkg_details_modal" class="modal hide">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h6>Hard Delete: ${pkg}</h6>
  </div>
  <div class="modal-body">
    <table class="table table-bordered">
      <thead>
        <th>Item</th>
        <th>Details</th>
        <th>Action</th>
      </thead>
      <tbody>
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
                  ${conflict_item.action.actionRequired}:: ${conflict_item.action.text}				
              </td>
            </tr>
         </g:each>
      </tbody>
    </table>

  </div>
  <div class="modal-footer">
    <g:link action="performPackageDelete" id="${pkg.id}"class="btn btn-primary btn-small" controller="admin">Confirm Delete</g:link>
  </div>
</div>  
