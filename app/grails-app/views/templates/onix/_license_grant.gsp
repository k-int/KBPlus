<g:if test="${ data }" >
  <table class="onix-entry-table">
    <thead>
      <tr>
        <th>Type</th>
        <th>Purpose</th>
        <th>Text</th>
      </tr>
    </thead>
    <tbody>
			<g:each var="xpath,result" in="${ data }" >
		    <tr>
		      <td><span class="cell-inner" ><g:render
		        template="/templates/onix/code_annotation"
		        model="${ result?."LicenseGrantType"?.getAt(0) + ['os' : os] }" /></span></td>
          <td><span class="cell-inner" ><g:render
            template="/templates/onix/code_annotation"
            model="${ result?."LicenseGrantPurpose"?.getAt(0) + ['os' : os] }" /></span></td>
          <td><span class="cell-inner" >
            <g:each var="t" in="${ result?."licenseText" }" >
              <g:render
                template="/templates/onix/license_text"
                model="${ t  + ['os' : os] }" />
            </g:each>
            <g:if test="${ result['Annotation'] }" >
              <div class="annotation-tag"><ul>
                <g:each var="entry" in="${ result['Annotation'] }" >
                  <li><g:render
                    template="/templates/onix/annotation"
                    model="${ entry + ['os' : os]}" /></li>
                </g:each>
              </ul></div>
            </g:if>
          </span></td>
        </tr>
      </g:each>
	  </tbody>
	</table>
</g:if>
<g:else>
  <p>Undefined</p>
</g:else>