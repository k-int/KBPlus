<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<g:set var="vals" value="${ OnixPLService.sortTextElements(data) }" />
<span class='text-icon' ><i class='icon-font' data-content='Click the icon to view the text.' title='Text'></i></span>
<g:set var="last_preceding" />

<div class="textelement" ><ul><g:each var="val" in="${ vals }">
  <g:set var="preceding" value="${ val.get('TextPreceding')?.get(0)?.get('_content')}" />
  <g:set var="display_num" value="${ val.get('DisplayNumber')?.get(0)?.get('_content')}" />
  
	<g:if test="${preceding && preceding != last_preceding}" >
	  <g:if test="${ last_preceding }" >
	    </ul>
	  </g:if>
	  <li><span class="text-preceding" >${ preceding.encodeAsHTML() }</span>
	  <ul><li>
	  <g:if test="${display_num}" >
	    <span class='text-num'>${ display_num.encodeAsHTML() } - </span>
	  </g:if>
	  ${ val.get('Text')?.get(0)?.get('_content')?.encodeAsHTML()}</li>
	</g:if>
	<g:else>
    <g:if test="${ last_preceding }" >
      </ul>
    </g:if>
    <li><g:if test="${display_num}" >
	    <span class='text-num'>${ display_num.encodeAsHTML() } - </span>
	  </g:if>
	  ${ val.get('Text')?.get(0)?.get('_content')?.encodeAsHTML()}</li>
	</g:else>
  <g:set var="last_preceding" value="${ preceding }" />
</g:each></ul></div>