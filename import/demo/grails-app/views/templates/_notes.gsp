<div class="well notes">
  <h7>Notes</h7>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${((docctx.owner?.contentType==0) && !(docctx.domain))}">
        <li>
          <g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="content" id="doccontent" class="newipe">${docctx.owner.content}</g:inPlaceEdit><br/>
            <i>Note created <g:formatDate format="dd/MM/yyyy" date="${docctx.owner.dateCreated}"/>
            <g:if test="${docctx.alert}">
              shared by ${docctx.alert.createdBy.displayName}
              <g:if test="${docctx.alert.sharingLevel==1}">With JC</g:if>
              <g:if test="${docctx.alert.sharingLevel==2}">With Community</g:if>
            </g:if>
            <g:else>(Not shared)</g:else></i>
        </li>
      </g:if>
    </g:each>
  </ul>
  <input type="submit" class="btn btn-primary" value="Add new note" data-toggle="modal" href="#modalCreateNote" />
</div>
