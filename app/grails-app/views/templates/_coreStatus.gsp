<g:set var="tip" value="${issueEntitlement.getTIP()}" />
<g:if test="${tip}">
  <g:set var="dateFormatter" value="${new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)}"/>
  <g:set var="date" value="${date ? dateFormatter.parse(date) : null}"/>
  <g:set var="status" value="${tip.coreStatus(date)}"/>
  <g:set var="date_text" value="${date ?: (status?'Now':'Never')}"/>
  <g:remoteLink url="[controller: 'ajax', action: 'getTipCoreDates', params:[tipID:tip.id,title:issueEntitlement.tipp?.title?.title]]"
                method="get" name="show_core_assertion_modal" onComplete="showCoreAssertionModal()" class="editable-click"
                update="magicArea">${ status ? "True(${date_text})" : "False(${date_text})" }</g:remoteLink>
</g:if>
<g:else>
  Content Provider missing.  Add one as Org Link of the Package.
</g:else>