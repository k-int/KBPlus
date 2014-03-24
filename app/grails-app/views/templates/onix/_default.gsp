<g:if test="${ data }" >
  <table class="onix-entry-table">
    <thead>
      <tr>
        <th>Type</th>
        <th>Text</th>
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
				        <td><span class="cell-inner" >
				          <g:each var="tag_n,tag_d" in="${e}" >
				            <g:if test="${ tag_n != "licenseText" && tag_n != "Annotation" }" >
						          <g:each var="entry" in="${ tag_d }" >
						            <g:render
		                     template="/templates/onix/code_annotation"
		                     model="${ entry + ['os' :  os] }" />
							        </g:each>
							      </g:if>
						      </g:each>
				        </span></td>
				        <td><span class="cell-inner" >
				          <g:if test="${ e['licenseText'] }" >
					          <g:each var="entry" in="${ e['licenseText'] }" >
		                  <p class="text-tag"><g:render
		                   template="/templates/onix/license_text"
		                   model="${ entry + ['os' : os] }" /></p>
		                </g:each>
		              </g:if>
				          <g:if test="${ e['Annotation'] }" >
				            <div class="annotation-tag"><ul>
						          <g:each var="entry" in="${ e['Annotation'] }" >
			                  <li><g:render
			                    template="/templates/onix/annotation"
			                    model="${ entry + ['os' : os]}" /></li>
			                </g:each>
		                </ul></div>
		              </g:if>
	              </span></td>
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