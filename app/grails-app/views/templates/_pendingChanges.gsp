<g:if test="${pendingChanges?.size() > 0}">
  <div class="container alert-warn">
    <h6>This Subscription has pending change notifications</h6>
   <g:if test="${processingpc}">
    <div class="container"><bootstrap:alert class="alert-warning"><g:message code="pendingchange.inprogress"/></bootstrap:alert></div>
  </g:if>
    <g:if test="${editable && !processingpc}">
      <g:link controller="pendingChange" action="acceptAll" id="${model.class.name}:${model.id}" class="btn btn-success"><i class="icon-white icon-ok "></i>Accept All</g:link>
      <g:link controller="pendingChange" action="rejectAll" id="${model.class.name}:${model.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject All</g:link>
    </g:if>
    <br/>&nbsp;<br/>
    <table class="table table-bordered">
      <thead>
        <tr>
          <td>Info</td>
          <td>Action</td>
        </tr>
      </thead>
      <tbody>
        <g:each in="${pendingChanges}" var="pc">
          <tr>
            <td>${pc.desc}</td>
            <td>
              <g:if test="${editable && !processingpc}">
                <g:link controller="pendingChange" action="accept" id="${pc.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept</g:link>
                <g:link controller="pendingChange" action="reject" id="${pc.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject</g:link>
              </g:if>
            </td>
          </tr>
        </g:each>
      </tbody>
    </table>
  </div>
</g:if>