<div class="well">
  Attached Documents
  <hr/>
  <ul>
    <g:each in="${doclist}" var="docctx">
      <li>
        <g:link controller="doc" id="${docctx.owner.id}">
          <g:if test="${docctx.owner.title}">${docctx.owner.title}</g:if>
          <g:else>Unknown Title Doc #${docctx.id}</g:else>
        </g:link>
      </li>
    </g:each>
  </ul>
</div>
