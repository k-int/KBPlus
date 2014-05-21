<div class="well documents">
  <h5>Documents</h5>
  <ul>
    <g:each in="${ownobj.documents}" var="docctx">
      <g:if test="${(( (docctx.owner?.contentType==1) || ( docctx.owner?.contentType==3) ) && ( docctx.status?.value!='Deleted'))}">
        <li class="external-link">
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
  <g:if test="${editable}">
    <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />
  </g:if>
</div>
<g:render template="/templates/addDocument"  />


