<g:each in="${resultCategories}" var="rc">
  ${rc.name}
  <hr/>
  <ul>
    <g:each in="${rc.results}" var="sr">
      <li><a href="${sr.url}">${sr.linktext}</a></li>
    </g:each>
  </ul>
</g:each>
