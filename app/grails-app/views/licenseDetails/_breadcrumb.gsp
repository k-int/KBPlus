      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license.licensee}">
          <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} <g:message code="current.licenses" default="Current Licenses"/></g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}"><g:message code="licence.details" default="License Details"/></g:link> </li>
    
        <li class="dropdown pull-right">
          <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>&nbsp;
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
            <li>
              <g:link action="index" params="${params+[format:'json']}">Json Export</g:link>
            </li>
            <li>
              <g:link action="index" params="${params+[format:'xml']}">XML Export(Licence)</g:link>
            </li>
            <g:each in="${transforms}" var="transkey,transval">
              <li><g:link action="index" params="${params+[format:'xml',transformId:transkey]}"> ${transval.name}</g:link></li>
            </g:each>
            <li>
              <g:link action="index" params="${params+[format:'csv']}">CSV Export</g:link>
            </li>
          </ul>
        </li>

        <g:if test="${editable}">
          <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>
        <li class="pull-right"><g:annotatedLabel owner="${license}" property="detailsPageInfo"></g:annotatedLabel>&nbsp;</li>
      </ul>