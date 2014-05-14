<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<span class='main-annotation'><i class='icon-edit' title='Annotations'
data-content='<g:each var="an" in="${ data }" status="counter">
  <p><strong>${ OnixPLService.formatOnixValue(an['AnnotationType'][0]['_content']).encodeAsHTML() }</strong> -
    ${ an['AnnotationText'][0]['_content'].encodeAsHTML() }</p>
</g:each>'></i></span>