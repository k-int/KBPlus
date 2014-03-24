<%@ page import="grails.util.GrailsNameUtils"%>
<span class="onix-code">${ GrailsNameUtils.getNaturalName(_content?.replaceAll("onixPL\\:", "") ?: "") }</span>
<g:if test="${ _content?.startsWith("onixPL:") }" >
  <span class="onix-code-annotation">${ os.onixPLHelperService.lookupCodeValueAnnotation (_content)?.trim() }</span>
</g:if>