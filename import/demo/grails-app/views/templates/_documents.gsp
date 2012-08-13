<div class="well documents">
  <h7>Documents</h7>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${docctx.owner?.contentType==1}">
        <li class="externalLinkIcon">
          <g:link controller="docstore" id="${docctx.owner.uuid}">
            ${docctx.owner.id}:<g:if test="${docctx.owner?.title}"><em>${docctx.owner.title}</em></g:if>
            <g:else>
              <g:if test="${docctx.owner?.filename}">${docctx.owner.filename}</g:if>
              <g:else>Missing title and filename</g:else>
            </g:else>
          </g:link>
        </li>
      </g:if>
    </g:each>
  </ul>
  <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />
</div>

<div class="modal hide" id="modalCreateDocument">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Document</h3>
  </div>
  <g:form id="upload_new_doc_form" url="[controller:'docWidget',action:'uploadDocument']" method="post" enctype="multipart/form-data">
    <input type="hidden" name="ownerid" value="${ownobj.id}"/>
    <input type="hidden" name="ownerclass" value="${ownobj.class.name}"/>
    <input type="hidden" name="ownertp" value="${owntp}"/>
    <div class="modal-body">
      <dl>
        <dt>
          <label>Document Name:</label>
        </dt>
        <dd>
          <input type="text" name="upload_title">
        </dd>
      </dl>
      <dl>
        <dt>
          <label>File:</label>
        </dt>
        <dd>
          <input type="file" name="upload_file" />
        </dd>
      </dl>
      <dl>
        <dt>
          <label>Document Type:</label>
        </dt>
        <dd>
          <select name="doctype">
            <option value="License">License</option>
            <option value="General">General</option>
            <option value="General">Addendum</option>
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

