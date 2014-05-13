<r:require modules="onixMatrix" />
  <g:each var="template, table_data" in="${ data }" >
    <div class="onix-matrix-wrapper">
	    <h2>${ table_data.remove("_title") }</h2>
	    <span class="filter-cell" ></span>
			<table class="onix-matrix">
			  <thead>
				  <tr><g:each var="heading" in="${headings}">
				    <th></th>
				    <th>${heading}</th>
				  </g:each></tr>
				</thead>
				<tbody><g:each var="row_key,row" in="${table_data}">
				  <tr>
            <!-- Each cell is rendered using template _table_${ template.toLowerCase() } -->
			      <g:each var="heading" in="${headings}" status="counter">
			        <g:set var="entry" value="${ row[heading] }" />
	            <td><g:if test="${ entry }" >
	              <g:render template="table_${ template.toLowerCase() }" model="${ request.parameterMap + ["data" : entry, "main" : (counter == 0)] }" />
	            </g:if></td>
	          </g:each>
				  </tr>
				</g:each></tbody>
			</table>
    </div>
  </g:each>