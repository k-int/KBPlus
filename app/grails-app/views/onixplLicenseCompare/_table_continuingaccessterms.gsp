<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>

<g:each var="row_key,row" in="${data}" status="rowCount">
  <tr>
    <!-- Header -->
    <th class="tr-${ (rowCount + 1) } cell-1" ><span class="cell-inner">
    
      <!-- Get the data we are to derive the title cell from -->
      <g:set var="rth" value="${service.getRowHeadingData(row)}" />

      <g:set var="access_resource" value="${service.getSingleValue(rth['ContinuingAccessTermRelatedResource']?.getAt(0),'RelatedResource') }"/>

      <g:set var="access_provider" value="${service.getSingleValue(rth['ContinuingAccessTermRelatedAgent']?.getAt(0),'RelatedAgent')}"/>

      ${ service.getSingleValue(rth, 'ContinuingAccessTermType') }
   

      <g:if test="${access_resource}">
        of ${access_resource} provided by
      </g:if>
      <g:if test="${access_provider}">
        <g:if test="${!access_resource}">
        for
        </g:if>

        ${access_provider}
      </g:if>




    </span></th>
    <g:each var="heading" in="${headings}" status="colCount">
      <g:set var="entry" value="${ row[heading] }" />
      <td class="tr-${ (rowCount + 1) } cell-${ colCount + 2 }">
        <g:if test="${ entry }" >
          <div class="onix-icons" >
            <g:if test="${ entry['TextElement'] }" >
              <g:render template="text" model="${ ["data" : entry['TextElement']] }" /> 
            </g:if>
            <g:if test="${ entry['Annotation'] }" >
              <g:render template="annotation" model="${ ["data" : entry['Annotation']] }" />
            </g:if>
          </div>
          <span class="cell-inner">
            <g:if test="${ entry['Annotation'] }" >
            <span title='Detailed by license' class="onix-status onix-info" ></span>
            </g:if>
            <g:else>
              <span title='Detailed by license' class="onix-status onix-tick" ></span>

            </g:else>
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