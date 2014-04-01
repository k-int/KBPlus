<g:each var="text" in="${ TextElement }" >
  <g:if test="${ text['TextPreceding'] }" >
    <g:each var="tp" in="${ text['TextPreceding'] }" >
      <span class="textpreceding">${ tp['_content'] }</span>
    </g:each>
  </g:if>
  <g:if test="${ text['Text'] }" >
    <g:each var="tp" in="${ text['Text'] }" >
      <span class="text">${ tp['_content'] }</span>
    </g:each>
  </g:if>
</g:each>