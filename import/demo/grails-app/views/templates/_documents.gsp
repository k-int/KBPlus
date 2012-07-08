<div class="well documents">
  <h7>Documents</h7>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${docctx.owner?.contentType==1}">
        <li class="externalLinkIcon">
          <g:link controller="docstore" id="${docctx.owner.uuid}">
            ${docctx.owner.id}:<g:if test="${docctx.owner?.title}"><em>${docctx.owner.title}</em><br/></g:if>
            <g:else>
              <g:if test="${docctx.owner?.filename}">${docctx.owner.filename}</g:if>
              <g:else>Missing title and filename</g:else>
            </g:else>
          </g:link><br/>
          <a href="http://knowplus.edina.ac.uk/oledocstore/document?uuid=${docctx.owner?.uuid}">
            Legacy Docstore Link
          </a>
        </li>
      </g:if>
    </g:each>
  </ul>
  <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />
</div>
