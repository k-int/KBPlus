<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
ds
${data}
%{-- <g:each var="row_key,row" in="${data}" status="rowCount">
  <tr>
    <!-- Header -->
    <th class="tr-${ (rowCount + 1) } cell-1" ><span class="cell-inner">
    
      <!-- Get the data we are to derive the title cell from -->
      <g:set var="rth" value="${service.getRowHeadingData(row)}" />
     
    </span></th>
    <g:each var="heading" in="${headings}" status="colCount">
      <g:set var="entry" value="${ row[heading] }" />
      <td class="tr-${ (rowCount + 1) } cell-${ colCount + 2 }">
          ${entry}
      </td>
    </g:each>
  </tr>
</g:each> --}%