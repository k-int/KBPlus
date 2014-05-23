<r:require modules="onixMatrix" />
<%@ page import="com.k_int.kbplus.onixpl.OnixPLService" %>
<g:each var="template, table_data" in="${ data }" >
  <div class="onix-matrix-wrapper">
    <g:set var="table_name" value="${ table_data.remove("_title") }" />
    <h2 id="table-${ OnixPLService.getClassValue(table_name) }">${ table_name }</h2>
    <span class="filter-cell" ></span>
		<table class="onix-matrix">
		  <thead>
			  <tr>
			    <th class="cell-1"></th>
			    <g:each var="heading" in="${headings}" status="counter">
			    <th class="cell-${ (counter + 2) }"><span class="cell-inner" >${heading}</span></th>
			  </g:each>
			</tr>
		</thead>
		<tbody>
		  <g:render template="table_${ template.toLowerCase() }" model="${ request.parameterMap + ["data" : table_data] }" />
			</tbody>
		</table>
  </div>
</g:each>
<div id="onix-modal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
  </div>
  </div>
</div>