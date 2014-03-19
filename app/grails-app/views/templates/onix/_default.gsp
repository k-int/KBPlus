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
            <g:each var="e" in="${ entries }" >
				      <tr>
				        <%-- For each of the entries look for the type which contains a value
				          detailing how this affects the license. Also, output the text embedded
				          and any notations --%>
				        <td>
				          <g:each var="entry" in="${ e[tag_name + 'Type'] }" >
				            <g:render
                     template="/templates/onix/generic_tag_type"
                     model="${ entry }" />
				          </g:each>
				        </td>
				        <td>
				          <g:if test="${ e['licenseText'] }" >
				            <ul>
						          <g:each var="entry" in="${ e['licenseText'] }" >
			                  <li><g:render
			                   template="/templates/onix/license_text"
			                   model="${ entry }" /></li>
			                </g:each>
			              </ul>
		              </g:if>
	              </td>
				        <td>
				          <g:if test="${ e['Annotation'] }" >
				            <ul>
						          <g:each var="entry" in="${ e['Annotation'] }" >
			                  <li><g:render
			                    template="/templates/onix/annotation"
			                    model="${ entry }" /></li>
			                </g:each>
		                </ul>
		              </g:if>
	              </td>
					    </tr>
            </g:each>
				  </g:if>
			  </g:each>
			</g:each>
	  </tbody>
	</table>
</g:if>
<g:else>
  <p>Undefined</p>
</g:else>