<div class="well">
Attached Documents
<hr/>
<ul>
<g:each in="${doclist}" var="docctx">
  <li><g:link controller="doc" id="${docctx.owner.id}">${docctx.id}</g:link></li>
</g:each>
</ul>
</div>
