<div class="well">
  Attached Documents
  <hr/>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <li>
        <g:if test="${docctx.owner?.contentType==1}">
          <a href="http://knowplus.edina.ac.uk/oledocstore/document?uuid=${docctx.owner?.uuid}">
            ${docctx.owner.id} 
            <g:if test="${docctx.owner?.title}"> : <em>${docctx.owner.title}</em><br/></g:if>
            <g:if test="${docctx.owner?.filename}">${docctx.owner.filename}</g:if>
          </a>
        </g:if>
        <g:else>
          <g:link controller="doc" id="${docctx.owner?.id}">
            ${docctx.owner.id} 
            <g:if test="${docctx.owner?.title}"> : <em>${docctx.owner.title}</em><br/></g:if>
            <g:if test="${docctx.owner?.filename}">${docctx.owner.filename}</g:if>
          </g:link>
        </g:else>
      </li>
    </g:each>
  </ul>
</div>
