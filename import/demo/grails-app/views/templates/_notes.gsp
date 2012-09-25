<div class="well notes">
  <h7>Notes</h7>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${((docctx.owner?.contentType==0) && !(docctx.domain) && (docctx.status?.value!='Deleted') )}">
        <li>
          <g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="content" id="doccontent" class="newipe">${docctx.owner.content}</g:inPlaceEdit><br/>
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
  <input type="submit" class="btn btn-primary" value="Add new note" data-toggle="modal" href="#modalCreateNote" />
</div>

<!-- Lightbox modal for creating a note taken from licenceNotes.html -->
<div class="modal hide" id="modalCreateNote">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Note</h3>
  </div>
  <g:form id="create_note" url="[controller:'docWidget',action:'createNote']" method="post">
    <input type="hidden" name="ownerid" value="${ownobj.id}"/>
    <input type="hidden" name="ownerclass" value="${ownobj.class.name}"/>
    <input type="hidden" name="ownertp" value="${owntp}"/>
    <div class="modal-body">
      <dl>
        <dt>
          <label>Note:</label>
        </dt>
        <dd>
          <textarea name="licenceNote"></textarea>
        </dd>
      </dl>
      <dl>
        <dt>
          <label>Shared:</label>
        </dt>
        <dd>
          <select name="licenceNoteShared">
            <option value="0">Not Shared</option>
            <!-- <option value="1">JISC Collections</option> -->
            <option value="2">Community</option>
          </select>
        </dd>
      </dl>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
      <input type="submit" class="btn btn-primary" value="Save Changes">
    </div>
  </g:form>
</div>

<!-- Lightbox modal for creating a note taken from licenceNotes.html -->
<div class="modal hide fade" id="modalComments">
</div>

