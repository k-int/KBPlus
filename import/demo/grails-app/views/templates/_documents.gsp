<div class="well">
  Attached Documents
  <hr/>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <g:if test="${docctx.owner?.contentType==1}">
        <li>
          <a href="http://knowplus.edina.ac.uk/oledocstore/document?uuid=${docctx.owner?.uuid}">
            ${docctx.owner.id} 
            <g:if test="${docctx.owner?.title}"> : <em>${docctx.owner.title}</em><br/></g:if>
            <g:else>
              <g:if test="${docctx.owner?.filename}">${docctx.owner.filename}</g:if>
              <g:else>Missing title and filename</g:else>
            </g:else>
          </a>
        </li>
      </g:if>
    </g:each>
  </ul>
</div>
