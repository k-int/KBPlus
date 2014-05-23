<div class="well notes">
  <h5>Notes</h5>
  <ul>
    <g:each in="${ownobj.documents}" var="docctx">
      <g:if test="${((docctx.owner?.contentType==0) && !(docctx.domain) && (docctx.status?.value!='Deleted') )}">
        <li>
          <g:xEditable owner="${docctx.owner}" field="content"/><br/>
          <i>Note created <g:formatDate format="dd/MM/yyyy" date="${docctx.owner.dateCreated}"/>
          <g:if test="${docctx.alert}">
            shared by ${docctx.alert.createdBy.displayName}
            <g:if test="${docctx.alert.sharingLevel==1}">With JC</g:if>
            <g:if test="${docctx.alert.sharingLevel==2}">With Community</g:if>
            <div class="comments"><a href="#modalComments" class="announce" data-id="${docctx.alert.id}">${docctx.alert?.comments != null ? docctx.alert?.comments?.size() : 0} Comment(s)</a></div>
          </g:if>
          <g:else>(Not shared)</g:else></i>
        </li>
      </g:if>
    </g:each>
  </ul>
  <g:if test="${editable}">
    <input type="submit" class="btn btn-primary" value="Add new note" data-toggle="modal" href="#modalCreateNote" />
  </g:if>
</div>

<g:render template="/templates/addNote" />

<div class="modal hide fade" id="modalComments">
</div>

