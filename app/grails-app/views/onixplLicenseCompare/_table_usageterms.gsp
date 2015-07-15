<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<g:set var="active_user" value=""/>

<g:each var="row_key,row" in="${data}" status="rowCount">
  <!-- Get the data we are to derive the title cell from -->
  <g:set var="rth" value="${service.getRowHeadingData(row)}" />
  <g:if test="${ OnixPLService.getSingleValue(rth, 'UsageType')}">  
  <g:set var="hasPlaceOfReceivingAgent" value="${rth.'UsageRelatedPlace'?.'UsagePlaceRelator'?.'_content'?.contains(['onixPL:PlaceOfReceivingAgent'])}"/>

  <g:set var="current_user" value="${row_key.substring(1,row_key.indexOf(']'))}"/>
  <g:if test="${active_user != current_user}">
    <tr class="cell-1" style="text-align: left;font-size: 150%;">
      <th><span class="cell-inner"> ${OnixPLService.getAllValues(rth, 'User', ', ', ' or ')}</span></th>
   
      <!-- This is needed or the annotations break -->
    <g:each in="${headings}"> <td></td>  </g:each>
    </tr>
    <g:set var="active_user" value="${current_user}"/>
  </g:if>
  <tr>
    <!-- Header -->
    <th class="tr-${ (rowCount + 1) } cell-1" ><span class="cell-inner">
    
      
      ${ OnixPLService.getSingleValue(rth, 'UsageType') }
      the ${ OnixPLService.getAllValues(rth, 'UsedResource',',') }

      <g:if test="${rth['UsagePurpose']}">
        for ${ OnixPLService.getSingleValue(rth, 'UsagePurpose') }
      </g:if>
      <g:if test="${rth['UsageRelatedResource']}">
        in ${OnixPLService.getSingleValue(rth['UsageRelatedResource'][0], 'RelatedResource')}
      </g:if>
      <g:if test="${ rth['UsageRelatedPlace'] && !hasPlaceOfReceivingAgent }" >
        using ${ OnixPLService.getAllValues(rth['UsageRelatedPlace'][0], 'RelatedPlace', ', ', ' or ') }
        as ${ OnixPLService.getSingleValue(rth['UsageRelatedPlace'][0], 'UsagePlaceRelator') }
      </g:if>

      <g:if test="${rth['UsageRelatedAgent']}">
        to ${ OnixPLService.getSingleValue(rth['UsageRelatedAgent'][0], 'RelatedAgent') } 
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
		        <g:set var="status" value="${ entry?.'UsageStatus'?.getAt(0)?.'_content' }" />
		        <span title='${ OnixPLService.getOnixValueAnnotation(status) }' class="onix-status ${ OnixPLService.getClassValue(status) }" ></span>
		      </span>

          %{-- List all the extra matrix details. Should create css class to use less space --}%
          <ul><b>
          <g:if test="${entry['UsageMethod'] }">
           <li> via ${ OnixPLService.getAllValues(entry, 'UsageMethod',', ') }</li>
          </g:if>
          <g:if test="${hasPlaceOfReceivingAgent}">
          <li>  In ${OnixPLService.getSingleValue(rth['UsageRelatedPlace'][0],'RelatedPlace')}</li>
          </g:if>
          <g:if test="${rth['UsageQuantity']}">
           <li> ${OnixPLService.getUsageQuantity(rth['UsageQuantity'][0])} </li>
          </g:if>
          
          <g:if test= "${rth['UsageCondition']}">
             <li>${OnixPLService.getSingleValue(rth,'UsageCondition')}</li>
          </g:if>
          <g:if test="${rth['UsageRelatedResource'] && rth.'UsageRelatedResource'?.'UsageResourceRelator'?.'_content' != [['onixPL:TargetResource']]}">
            
            <g:each var="clause" in="${rth['UsageRelatedResource']}">
              <g:if test="${clause.'UsageResourceRelator'.'_content' != ['onixPL:TargetResource']}">
               <li> ${OnixPLService.getSingleValue(clause,'UsageResourceRelator')}
 ${OnixPLService.getAllValues(clause, 'RelatedResource', ', ', ' or ')}</li>
              </g:if>
            </g:each>          
          </g:if>
          <g:if test="${rth.'UsageType'?.getAt(0)?.'_content'?.getAt(0) == 'onixPL:SupplyCopy'}">

            <g:set var="hasVal" value="${OnixPLService.getAllValues(rth, 'UsageMethod', ', ', ' or ')}"/>
            <g:if test="${hasVal}">  <li>${hasVal}</li></g:if>
          </g:if>
        </b></ul>
		    </g:if>
        <g:else>
          <span class="cell-inner-undefined">
            <span title='Not defined by the license' class="onix-status onix-pl-undefined" ></span>
          </span>
		    </g:else>
      </td>
    </g:each>
  </tr>
    </g:if>

</g:each>