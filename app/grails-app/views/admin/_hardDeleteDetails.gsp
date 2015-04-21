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
              	<g:if test="${conflict_item.details instanceof List}">
	              	<ul>
	              	<g:each in="${conflict_item.details}" var="detail_item">
				      	<li> ${detail_item}</li>
	              	</g:each>
	              	</ul>
              	</g:if>
              	<g:else>
	                 ${conflict_item.details}
              	</g:else>
              </td>
              <td>
				 ${conflict_item.action}
              </td>
            </tr>
         </g:each>
      </tbody>
    </table>

  </div>
  <div class="modal-footer">
    <input type="submit" value="Apply" class="btn btn-primary btn-small"/>
  </div>
</div>  