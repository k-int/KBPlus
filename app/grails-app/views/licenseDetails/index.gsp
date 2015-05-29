<!doctype html>
<r:require module="annotations" />

<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
     <g:javascript src="custom_properties.js"/>
    <title>KB+ <g:message code="licence" default="Licence"/></title>
  </head>

  <body>

    <div class="container">
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
              <g:link action="index" params="${params+[format:'xml']}">XML Export</g:link>
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
 <h6>${message(code:'licence.properties')}</h6>
              <div id="custom_props_div" class="span12">
                  <g:render template="/templates/custom_props" model="${[ ownobj:license ]}"/>
              </div>
            <br/>
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
                            <g:link controller="packageDetails" action="show" id="${pkg.id}">${pkg.id} (${pkg.name})</g:link><br/>
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
                      <dt><label class="control-label" for="licenseUrl"><g:message code="licence" default="Licence"/> Url</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="licenseUrl" id="licenseUrl"/>
                        <g:if test="${license.licenseUrl}"><a href="${license.licenseUrl}">License Link</a></g:if>
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
                            <li><g:link controller="licenseDetails" action="index" id="${il.fromLic.id}">${il.fromLic.reference} (${il.type?.value})</g:link> - 
                            Child: <g:xEditableRefData owner="${il}" field="isSlaved" config='YN'/>

                            </li>
                          </g:each>
              
                        </ul>
                      </dd>
                  </dl>

                  <div class="clearfix"></div
>              </div>
              </div>
              <div class="span4">
                <div class="well">
                <label>  <h5>Licence Actions</h5> </label>
                  <g:if test="${editable}">
                 
                  <label>Copy licence for:</label>
                  <g:select from="${user.authorizedOrgs}" optionValue="name" optionKey="shortcode" id="orgShortcode" name="orgShortcode"/>
                                 <br/>
                   <g:link name="copyLicenceBtn" controller="myInstitutions" action="actionLicenses" params="${[shortcode:'replaceme',baselicense:license.id,'copy-licence':'Y']}" onclick="return changeLink(this,'Are you sure you want to copy this licence?')" class="btn btn-success">Copy</g:link>
               
%{--           
          leave this out for now.. it is a bit confusing.
          <g:link name="deletLicenceBtn" controller="myInstitutions" action="actionLicenses" onclick="return changeLink(this,'Are you sure you want to delete ${license.reference?:'** No licence reference ** '}?')" params="${[baselicense:license.id,'delete-licence':'Y',shortcode:'replaceme']}" class="btn btn-danger">Delete</g:link> --}%
                  </g:if>
                  <g:else>
                    Actions available to editors only
                  </g:else>
                 </div>
                <g:render template="/templates/documents" model="${[ ownobj:license, owntp:'license']}" />
                <g:render template="/templates/notes"  model="${[ ownobj:license, owntp:'license']}" />
              </div>
            </div>
    </div>
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[linkType:license?.class?.name,roleLinks:license?.orgLinks,parent:license.class.name+':'+license.id,property:'orgLinks',recip_prop:'lic']}" />

    <r:script language="JavaScript">
      function changeLink(elem,msg){
        var selectedOrg = $('#orgShortcode').val();
        var edited_link =  $("a[name="+elem.name+"]").attr("href",function(i,val){
          return val.replace("replaceme",selectedOrg)
        });

       return confirm(msg);
      }

      <g:if test="${editable}">
      </g:if>
      <g:else>
        $(document).ready(function() {
          $(".announce").click(function(){
            var id = $(this).data('id');
            $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
            $('#modalComments').modal('show');
          });
        });
      </g:else>
     window.onload = function() {
     runCustomPropsJS("<g:createLink controller='ajax' action='lookup'/>");
    };
    </r:script>

  </body>
</html>
