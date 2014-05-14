<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<span class='main-annotation' ><i class='icon-edit' data-content='Click the icon to view the annotations.' title='Annotations'></i></span>
<g:set var="last_preceding" />

<div class="textelement" ><ul><g:each var="val" in="${ data }">
  <g:set var="preceding" value="${ OnixPLService.formatOnixValue( val.get('AnnotationType')?.get(0)?.get('_content') ) }" />
  
  <g:if test="${preceding && preceding != last_preceding}" >
    <g:if test="${ last_preceding }" >
      </ul>
    </g:if>
    <li><span class="text-preceding" >${ preceding.encodeAsHTML() }</span>
    <ul><li>
    ${ val.get('AnnotationText')?.get(0)?.get('_content')?.encodeAsHTML()}</li>
  </g:if>
  <g:else>
    <g:if test="${ last_preceding }" >
      </ul>
    </g:if>
    <li>
    ${ val.get('AnnotationText')?.get(0)?.get('_content')?.encodeAsHTML()}</li>
  </g:else>
  <g:set var="last_preceding" value="${ preceding }" />
</g:each></ul></div>