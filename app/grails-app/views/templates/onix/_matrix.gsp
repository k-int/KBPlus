<r:require modules="onixMatrix" />
  <g:each var="row" in="${ rows }" >
    <div class="onix-matrix-wrapper">
	    <h2>${ row.title }</h2>
	    <span class="filter-cell" ></span>
			<table class="onix-matrix">
			  <thead>
			    <tr>
			      <g:each var="title" in="${ header.titles }">
			        <th><span class="cell-inner" >${ title }</span></th>
			      </g:each>
			    </tr>
			  </thead>
			  <tbody>
		      <tr>
		        <g:each var="licence_name,license_data" in="${ data }" >
		          <td><span class="cell-inner" >
		            <g:render template="${ row.template }" model="${ ['data' : license_data[row['key']]] }" />
		          </span></td>
		        </g:each>
		      </tr>
			  </tbody>
			</table>
    </div>
  </g:each>