<r:require modules="onixMatrix" />
 <g:each var="template, table_data" in="${ data }" >
   <div class="onix-matrix-wrapper">
    <h2>${ table_data.remove("_title") }</h2>
    <span class="filter-cell" ></span>
		<table class="onix-matrix">
		  <g:render template="table_${ template.toLowerCase() }" model="${ [] + request.parameterMap + ["data" : table_data] }" />
		</table>
   </div>
 </g:each>