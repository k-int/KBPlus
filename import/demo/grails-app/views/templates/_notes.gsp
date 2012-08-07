<div class="well notes">
  <h7>Notes</h7>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${((docctx.owner?.contentType==0) && !(docctx.domain))}">
        <li>
          <g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="content" id="doccontent" class="newipe">${docctx.owner.content}</g:inPlaceEdit>
          <span class="pull-right">
            <g:formatDate format="dd/MM/yyyy" date="${docctx.owner.dateCreated}"/> ${docctx.owner.creator}
          </span>
        </li>
      </g:if>
    </g:each>
  </ul>
  <input type="submit" class="btn btn-primary" value="Add new note" data-toggle="modal" href="#modalCreateNote" />
</div>
