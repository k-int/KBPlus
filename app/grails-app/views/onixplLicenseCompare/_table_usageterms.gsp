<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<table class="onix-entry-table">
  <thead>
    <tr>
      <th width="20%">User</th>
      <th width="20%">Used Resource</th>
      <th width="20%">Usage Status</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td width="20%"><g:if test="${ entry['User'] }" >
        <g:each var="val" in="${ entry['User'] }" >
	        <div
	          class="onix-code"
	          title="${ OnixPLService.treatTextForDisplay (val?.get('_content'))?.encodeAsHTML() }" >
	             ${ OnixPLService.formatOnixValue (val?.get('_content')) }</div>
	      </g:each>
      </g:if></td>
      <td width="20%"></td>
      <td width="20%"></td>
    </tr>
  </tbody>
</table>