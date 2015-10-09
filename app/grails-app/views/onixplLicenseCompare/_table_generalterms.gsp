<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<g:each var="row_key,row" in="${data}" status="rowCount">
    
      <!-- Get the data we are to derive the title cell from -->
      <g:set var="rth" value="${service.getRowHeadingData(row)}" />
<g:if test="${ OnixPLService.getSingleValue(rth, 'GeneralTermType') }">
  <tr>
    <!-- Header -->
    <th class="tr-${ (rowCount + 1) } cell-1" ><span class="cell-inner">

      <g:if test="${ rth['TermStatus'] }" >
        ${ OnixPLService.getSingleValue(rth, 'TermStatus') }
      </g:if>
      ${ OnixPLService.getSingleValue(rth, 'GeneralTermType') }
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
            <span title='Detailed by license' class="onix-status onix-info" ></span>
          </span>

          <ul><b>
          <g:if test="${entry['GeneralTermRelatedPlace'] }">
            <g:each var="clause" in="${entry['GeneralTermRelatedPlace']}">

             <li> via ${ OnixPLService.getAllValues(clause, 'GeneralTermPlaceRelator',', ') }</li>
            </g:each> 
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