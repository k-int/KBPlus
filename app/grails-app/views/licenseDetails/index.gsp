<!doctype html>
<r:require module="annotations" />

<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
     <g:javascript src="custom_properties.js"/>
    <title>KB+ Licence</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license.licensee}">
          <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} Current Licences</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">Licence Details</g:link> </li>
    
        <li class="dropdown pull-right">
          <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>&nbsp;
          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
            <li>
              <% def ps_json = [:]; ps_json.putAll(params); ps_json.format = 'json'; %>
              <g:link action="index" params="${ps_json}">Json Export</g:link>
            </li>
            <li>
                <% def ps_xml = [:]; ps_xml.putAll(params); ps_xml.format = 'xml'; %>
              <g:link action="index" params="${ps_xml}">XML Export</g:link>
            </li>
          </ul>
        </li>

        <g:if test="${editable}">
          <li class="pull-right"><span class="badge badge-warning">Editable</span>&nbsp;</li>
        </g:if>
        <li class="pull-right"><g:annotatedLabel owner="${license}" property="detailsPageInfo"></g:annotatedLabel>&nbsp;</li>
      </ul>
    </div>

    <div class="container">
      <h1>${license.licensee?.name} ${license.type?.value} Licence : <g:xEditable owner="${license}" field="reference" id="reference"/></h1>
      <g:render template="nav" />
    </div>

    <g:if test="${pendingChanges?.size() > 0}">
      <div class="container alert-warn">
        <h6>This licence has pending change notifications</h6>
        <g:if test="${editable}">
          <g:link controller="pendingChange" action="acceptAll" id="com.k_int.kbplus.License:${license.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept All</g:link>
          <g:link controller="pendingChange" action="rejectAll" id="com.k_int.kbplus.License:${license.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject All</g:link>
        </g:if>
        <br/>&nbsp;<br/>
        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Info</td>
              <td>Action</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${pendingChanges}" var="pc">
              <tr>
                <td>${pc.desc}</td>
                <td>
                  <g:if test="${editable}">
                    <g:link controller="pendingChange" action="accept" id="${pc.id}" class="btn btn-success"><i class="icon-white icon-ok"></i>Accept</g:link>
                    <g:link controller="pendingChange" action="reject" id="${pc.id}" class="btn btn-danger"><i class="icon-white icon-remove"></i>Reject</g:link>
                  </g:if>
                </td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </g:if>

    <div class="container">
            <div class="row">
              <div class="span8">
  
                <h6>Information</h6>

                <div class="inline-lists">

                <g:if test="${flash.message}">
                  <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
                </g:if>
  
                <g:hasErrors bean="${titleInstanceInstance}">
                  <bootstrap:alert class="alert-error">
                  <ul>
                    <g:eachError bean="${titleInstanceInstance}" var="error">
                      <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                  </ul>
                  </bootstrap:alert>
                </g:hasErrors>
  
  
                  <dl>
                      <dt><label class="control-label" for="subscriptions">Linked Subscriptions</label></dt>
                      <dd>
                        <g:if test="${license.subscriptions && ( license.subscriptions.size() > 0 )}">
                          <g:each in="${license.subscriptions}" var="sub">
                            <g:link controller="subscriptionDetails" action="index" id="${sub.id}">${sub.id} (${sub.name})</g:link><br/>
                          </g:each>
                        </g:if>
                        <g:else>No currently linked subscriptions.</g:else>
                      </dd>
                  </dl>
                
                  <dl>
                      <dt><label class="control-label" for="${license.pkgs}">Linked Packages</label></dt>
                      <dd>
                        <g:if test="${license.pkgs && ( license.pkgs.size() > 0 )}">
                          <g:each in="${license.pkgs}" var="pkg">
                            <g:link controller="packageDetails" action="index" id="${pkg.id}">${pkg.id} (${pkg.name})</g:link><br/>
                          </g:each>
                        </g:if>
                        <g:else>No currently linked packages.</g:else>
                      </dd>
                  </dl>
                
      
                  <dl>
                      <dt><label class="control-label" for="reference">Reference</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="reference" id="reference"/>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="reference">Status</label></dt>
                      <dd>
                        <g:xEditableRefData owner="${license}" field="status" config='License Status'/>
                      </dd>
                  </dl>
      
      
                  <dl>
                      <dt><label class="control-label" for="noticePeriod">Notice Period</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="noticePeriod" id="noticePeriod"/>
                     </dd>
                  </dl>

                  <sec:ifAnyGranted roles="ROLE_ADMIN,KBPLUS_EDITOR">
                    <dl>
                        <dt><label class="control-label">ONIX-PL Licence</label></dt>
                        <dd>
                            <g:if test="${license.onixplLicense}">
                                <g:link controller="onixplLicenseDetails" action="index" id="${license.onixplLicense?.id}">${license.onixplLicense.title}</g:link>
                                <g:if test="${editable}">
                                    <g:link class="btn btn-warning" controller="licenseDetails" action="unlinkLicense" params="[license_id: license.id, opl_id: onixplLicense.id]">Unlink</g:link>
                                </g:if>
                            </g:if>
                            <g:else>
                                <g:link class="btn btn-warning" controller='licenseImport' action='doImport' params='[license_id: license.id]'>Import an ONIX-PL licence</g:link>
                            </g:else>
                        </dd>
                    </dl>
                  </sec:ifAnyGranted>
      
                  <dl>
                      <dt><label class="control-label" for="licenseUrl">Licence Url</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="licenseUrl" id="licenseUrl"/>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licensorRef">Licensor Ref</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="licensorRef" id="licensorRef"/>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Licensee Ref</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="licenseeRef" id="licenseeRef"/>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="isPublic">Public?</label></dt>
                      <dd>
                        <g:xEditableRefData owner="${license}" field="isPublic" config='YN'/>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="isPublic">Start Date</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" type="date" field="startDate" />
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="isPublic">End Date</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" type="date" field="endDate" />
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="licenseCategory">Licence Category</label></dt>
                      <dd>
                        <g:xEditableRefData owner="${license}" field="licenseCategory" config='LicenseCategory'/>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Org Links</label></dt>
                      <dd>
                        <g:render template="orgLinks" contextPath="../templates" model="${[roleLinks:license?.orgLinks,editmode:editable]}" />
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Incoming Licence Links</label></dt>
                      <dd>
                        <ul>
                          <g:each in="${license?.incomingLinks}" var="il">
                            <li><g:link controller="licenseDetails" action="index" id="${il.fromLic.id}">${il.fromLic.reference} (${il.type?.value})</g:link></li>
                          </g:each>
                        </ul>
                      </dd>
                  </dl>

                  <div class="clearfix"></div>
                  </div>

            <h6>Licence Properties</h6>

            <table class="table table-bordered licence-properties">
              <thead>
                <tr>
                  <th>Property</th>
                  <th>Status</th>
                  <th>Notes</th>
                </tr>
              </thead>
              <tbody>
                <tr><td>Concurrent Access</td>
                    <td>
                         <span>
                         <g:xEditableRefData owner="${license}" field="concurrentUsers" config='ConcurrentAccess'/>
                         </span>
                         <span id="cucwrap">
                             <span>(</span>
                               <g:xEditable owner="${license}" field="concurrentUserCount" id='concurrentUserCount'/>
                             <span>)</span>
                         </span>
                    </td>
                    <td>
                      <g:xEditableFieldNote owner="${license}" field="concurrentUsers" id="concurrentUsers"/></td>
                </tr>
  
                <tr><td>Remote Access</td>
                    <td><g:xEditableRefData owner="${license}" field="remoteAccess" config='YNO' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="remoteAccess" id="remoteAccess"/></td></tr>  
                <tr><td>Walk In Access</td>
                     <td><g:xEditableRefData owner="${license}" field="walkinAccess" config='YNO' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="walkinAccess" id="walkinAccess"/></td></tr>
                <tr><td>Multi Site Access</td>
                    <td><g:xEditableRefData owner="${license}" field="multisiteAccess" config='YNO'/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="multisiteAccess" id="multisiteAccess"/></td></tr>
                <tr><td>Partners Access</td>
                    <td><g:xEditableRefData owner="${license}" field="partnersAccess" config='YNO'/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="partnersAccess" id="partnersAccess"/></td></tr>
                <tr><td>Alumni Access</td>
                    <td><g:xEditableRefData owner="${license}" field="alumniAccess" config='YNO'/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="alumniAccess" id="alumniAccess"/></td></tr>
                <tr><td>ILL - Inter Library Loans</td>
                    <td><g:xEditableRefData owner="${license}" field="ill" config='YN' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="ill" id="ill"/></td></tr>
                <tr><td>Include In Coursepacks</td>
                    <td><g:xEditableRefData owner="${license}" field="coursepack" config='YNO' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="coursepack" id="coursepack"/></td></tr>
                <tr><td>Include in VLE</td>
                    <td><g:xEditableRefData owner="${license}" field="vle" config='YNO' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="vle" id="vle"/></td></tr>
                <tr><td>Enterprise Access</td>
                    <td><g:xEditableRefData owner="${license}" field='enterprise' config='YNO' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="enterprise" id="enterprise"/></td></tr>
                <tr><td>Post Cancellation Access Entitlement</td>
                    <td><g:xEditableRefData owner="${license}" field="pca" config='YNO'/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="pca" id="pca"/></td></tr>
              </tbody>
            </table>

            <br/>
              <div id="custom_props_div">
                  <g:render template="/templates/custom_props" model="${[ ownobj:license ]}"/>
              </div>
              </div>
              <div class="span4">
                <g:render template="/templates/documents" model="${[ ownobj:license, owntp:'license']}" />
                <g:render template="/templates/notes"  model="${[ ownobj:license, owntp:'license']}" />
              </div>
            </div>
    </div>
    
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:license?.orgLinks,parent:license.class.name+':'+license.id,property:'orgLinks',recip_prop:'lic']}" />

    <r:script language="JavaScript">
    
      <g:if test="${editable}">
      </g:if>
      <g:else>
        $(document).ready(function() {
          $(".announce").click(function(){
            var id = $(this).data('id');
            $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
            $('#modalComments').modal('show');
          });
        }
      </g:else>
     window.onload = function() {
     runCustomPropsJS("<g:createLink controller='ajax' action='lookup'/>");
    }
    </r:script>

  </body>
</html>
