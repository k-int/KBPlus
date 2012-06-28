<div class="well">
  Attached Documents
  <hr/>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <li>
        <g:link controller="doc" id="${docctx.owner.id}">
          ${docctx.owner.id} 
          <g:if test="${docctx.owner.title}"> : <em>${docctx.owner.title}</em><br/></g:if>
          <g:if test="${doccts.owner.filename}">${docctx.owner.filename}</g:if>
        </g:link>
      </li>
    </g:each>
  </ul>
</div>
