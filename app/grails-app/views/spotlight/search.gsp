<g:if test="${filtered}">
	<span class="label label-info">${filtered}</span>
</g:if>
  <ul>
   <g:each in="${hits}" var="hit">
      <li>
      	<g:if test="${!filtered}">
          <g:if test="${hit.type=='com.k_int.kbplus.SitePage'}"><span class="label label-info">Action</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.Org'}"><span class="label label-info">Organisation</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}"><span class="label label-info">Title Instance</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.Package'}"><span class="label label-info">Package</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.Platform'}"><span class="label label-info">Platform</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.Subscription'}"><span class="label label-info">Subscription</span></g:if>
          <g:if test="${hit.type=='com.k_int.kbplus.License'}"><span class="label label-info">License</span></g:if>
       </g:if>

        <g:if test="${hit.type=='com.k_int.kbplus.Org'}">
            <g:link controller="organisations" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.TitleInstance'}">
          <g:link controller="titleDetails" action="show" id="${hit.source.dbId}">${hit.source.title}</g:link> 
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.SitePage'}">
          <g:link controller="${hit.source.controller}" action="${hit.source.action}">${hit.source.alias}</g:link> 
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.Package'}">
          <g:link controller="packageDetails" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.Platform'}">
          <g:link controller="platform" action="show" id="${hit.source.dbId}">${hit.source.name}</g:link>
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.Subscription'}">
          <g:link controller="subscriptionDetails" action="index" id="${hit.source.dbId}">${hit.source.name} (${hit.source.type})</g:link>
        </g:if>
        <g:if test="${hit.type=='com.k_int.kbplus.License'}">
          <g:link controller="licenseDetails" action="index" id="${hit.source.dbId}">${hit.source.name}</g:link>
        </g:if>
      </li>
    </g:each>
  </ul>


