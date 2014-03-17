<g:if test="${ data }" >
  <table class="onix-entry-table">
    <thead>
      <tr>
        <th>Type</th>
        <th>Text</th>
        <th>Notes</th>
      </tr>
    </thead>
    <tbody>
			<g:each var="xpath,result" in="${ data }" >
			  <g:each var="tag_name,entries" in="${ result }" >
			    <g:if test="${ !tag_name.startsWith("_") }" >
			      <tr>
			        <%-- For each of the entries look for the type which contains a value
			          detailing how this affects the license. Also, output the text embedded
			          and any notations --%>
			        <td>
			          <g:each var="entry" in="${ entries[tag_name + 'Type'] }" >
			            <g:render
			             template="/templates/onix/generic_tag_type"
			             model="${ ["data" : entry] }" />
			          </g:each>
			        </td>
			        <td>
			          <g:each var="entry" in="${ entries['licenceText'] }" >
                  <g:render
                   template="/templates/onix/license_text"
                   model="${ ["data" : entry] }" />
                </g:each>
              </td>
			        <td>
			          <g:each var="entry" in="${ entries['Annotation'] }" >
                  <g:render
                   template="/templates/onix/annotation"
                   model="${ ["data" : entry] }" />
                </g:each>
              </td>
				    </tr>
			    </g:if>
			  </g:each>
			</g:each>
	  </tbody>
	</table>
</g:if>
<g:else>
  <p>Undefined</p>
</g:else>