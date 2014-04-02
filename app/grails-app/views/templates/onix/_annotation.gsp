<g:if test="${ AnnotationType }" >
  ${ AnnotationType[0]?."_content"?.replaceAll("onixPL\\:", "") } -
</g:if>
${ AnnotationText[0]?."_content" }