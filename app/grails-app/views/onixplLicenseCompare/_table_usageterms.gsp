<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<g:set var="active_user" value=""/>
<g:each var="row_key,row" in="${data}" status="rowCount">
  <!-- Get the data we are to derive the title cell from -->
  <g:set var="rth" value="${service.getRowHeadingData(row)}" />

  <g:set var="current_user" value="${row_key.substring(1,row_key.indexOf(']'))}"/>
  <g:if test="${active_user != current_user}">
    <tr style="text-align: left;font-size: 150%;">
      <th> ${OnixPLService.getAllValues(rth, 'User', ', ', ' or ')}</th>
    </tr>
    <g:set var="active_user" value="${current_user}"/>
  </g:if>
  <tr>
    <!-- Header -->
    <th class="tr-${ (rowCount + 1) } cell-1" ><span class="cell-inner">
    
      
      ${ OnixPLService.getSingleValue(rth, 'UsageType') }
      the ${ OnixPLService.getSingleValue(rth, 'UsedResource') }
      <g:if test="${rth['UsageMethod']}">
        via ${ OnixPLService.getSingleValue(rth, 'UsageMethod') }
      </g:if>
      <g:if test="${ rth['UsageRelatedPlace']  }" >
        using ${ OnixPLService.getAllValues(rth['UsageRelatedPlace'][0], 'RelatedPlace', ', ', ' or ') }
        as ${ OnixPLService.getSingleValue(rth['UsageRelatedPlace'][0], 'UsagePlaceRelator') }
      </g:if>
    </span></th>
    <g:each var="heading" in="${headings}" status="colCount">
      <g:set var="entry" value="${ row[heading] }" />
      <td class="tr-${ (rowCount + 1) } cell-${ colCount + 2 }">
        <g:if test="${ entry }" >
	        <div class="onix-icons" >
	          <g:if test="${ entry['UsageException'] }" >
	            <span class='exceptions' ><i class='icon-exclamation-sign' title='Exceptions' data-content='${
	              OnixPLService.formatOnixValue(
	                entry['UsageException']['UsageExceptionType']['_content']*.get(0).join(", ")
	              ).encodeAsHTML()
	            }'></i></span>
	          </g:if>
	          <g:if test="${ entry['TextElement'] }" >
	            <g:render template="text" model="${ ["data" : entry['TextElement']] }" /> 
	          </g:if>
	          <g:if test="${ entry['Annotation'] }" >
	            <g:render template="annotation" model="${ ["data" : entry['Annotation']] }" />
	          </g:if>
	        </div>
		      <span class="cell-inner">
		        <g:set var="status" value="${ entry['UsageStatus'][0]['_content'] }" />
		        <span title='${ OnixPLService.getOnixValueAnnotation(status) }' class="onix-status ${ OnixPLService.getClassValue(status) }" ></span>
		      </span>
		    </g:if>
		    <g:else>
          <span class="cell-inner-undefined">
            <span title='Not defined by the license' class="onix-status onix-pl-undefined" ></span>
          </span>
		    </g:else>
      </td>
    </g:each>
  </tr>
</g:each>