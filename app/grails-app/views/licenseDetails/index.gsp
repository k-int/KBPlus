<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license.licensee}">
          <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} Current Licenses</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">License Details</g:link> </li>

        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>

      </ul>
    </div>

    <div class="container">
      <h1>${license.licensee?.name} ${license.type?.value} Licence : <g:xEditable owner="${license}" field="reference" id="reference"/></h1>
      <g:render template="nav" contextPath="." />
    </div>

    <g:if test="${license.pendingChanges?.size() > 0}">
      <div class="container alert-warn">
        <h6>This Licence has pending change notifications</h6>
        <table class="table table-bordered">
          <thead>
            <tr>
              <td>Field</td>
              <td>Has changed to</td>
              <td>Reason</td>
              <td>Actions</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${license.pendingChanges}" var="pc">
              <tr>
                <td style="white-space:nowrap;">${pc.updateProperty}</td>
                <td>${pc.updateValue}</td>
                <td>${pc.updateReason}</td>
                <td>
                  <g:link controller="licenseDetails" action="acceptChange" id="${params.id}" params="${[changeid:pc.id]}" class="btn btn-primary">Accept</g:link>
                  <g:link controller="licenseDetails" action="rejectChange" id="${params.id}" params="${[changeid:pc.id]}" class="btn btn-primary">Reject</g:link>
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
                      <dt><label class="control-label" for="reference">Reference</label></dt>
                      <dd>
                        <g:xEditable owner="${license}" field="reference" id="reference"/>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="noticePeriod">Notice Period</label></dt>
                      <dd>
                        <g:inPlaceEdit domain="${license.class.name}" 
                                       pk="${license.id}" 
                                       field="noticePeriod" 
                                       id="noticePeriod">${license.noticePeriod}</g:inPlaceEdit>
                     </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseUrl">Licence Url</label></dt>
                      <dd>
                        <g:inPlaceEdit domain="${license.class.name}" 
                                       pk="${license.id}" 
                                       field="licenseUrl" 
                                       id="licenseUrl">${license.licenseUrl}</g:inPlaceEdit>

                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licensorRef">Licensor Ref</label></dt>
                      <dd>
                        <g:inPlaceEdit domain="${license.class.name}" 
                                       pk="${license.id}" 
                                       field="licensorRef" 
                                       id="licensorRef">${license.licensorRef}</g:inPlaceEdit>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Licensee Ref</label></dt>
                      <dd>
                        <g:inPlaceEdit domain="${license.class.name}" 
                                       pk="${license.id}" 
                                       field="licenseeRef" 
                                       id="licenseeRef">${license.licenseeRef}</g:inPlaceEdit>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Public?</label></dt>
                      <dd>
                        <g:xEditableRefData owner="${license}" field="isPublic" config='YN'/>
                      </dd>
                  </dl>

                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Org Links</label></dt>
                      <dd>
                        <g:render template="orgLinks" contextPath="../templates" model="${[roleLinks:license?.orgLinks,editmode:editable]}" />
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
                         <g:refdataValue val="${license.concurrentUsers?.value}" domain="License" pk="${license.id}" field="concurrentUsers" cat='Concurrent Access' class="${editable?'cuedit':''}"/>
                         </span>
                         <span id="cucwrap">
                             <span>(</span>
                             <span id="concurrentUserCount" class="intedit" style="padding-top: 5px;">${license.concurrentUserCount}</span>
                             <span>)</span>
                         </span>
                    </td>
                    <td><g:singleValueFieldNote domain="concurrentUsers" value="${license.getNote('concurrentUsers')}" class="${editable?'fieldNote':''}"/></td></tr>
  
                <tr><td>Remote Access</td>
                    <td><g:xEditableRefData owner="${license}" field="remoteAccess" config='YN' /></td>
                    <td><g:xEditableFieldNote owner="${license}" field="remoteAccess" id="remoteAccess" class="${editable?'refdataedit':''}"/></td></tr>  
                <tr><td>Walk In Access</td>
                     <td><g:xEditableRefData owner="${license}" field="walkinAccess" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="walkinAccess" id="walkinAccess"/></td></tr>
                <tr><td>Multi Site Access</td>
                    <td><g:xEditableRefData owner="${license}" field="multisiteAccess" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="multisiteAccess" id="multisiteAccess"/></td></tr>
                <tr><td>Partners Access</td>
                    <td><g:xEditableRefData owner="${license}" field="partnersAccess" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="partnersAccess" id="partnersAccess"/></td></tr>
                <tr><td>Alumni Access</td>
                    <td><g:xEditableRefData owner="${license}" field="alumniAccess" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="alumniAccess" id="alumniAccess"/></td></tr>
                <tr><td>ILL - Inter Library Loans</td>
                    <td><g:xEditableRefData owner="${license}" field="ill" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="ill" id="ill"/></td></tr>
                <tr><td>Include In Coursepacks</td>
                    <td><g:xEditableRefData owner="${license}" field="coursepack" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="coursepack" id="coursepack"/></td></tr>
                <tr><td>Include in VLE</td>
                    <td><g:xEditableRefData owner="${license}" field="vle" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="vle" id="vle"/></td></tr>
                <tr><td>Enterprise Access</td>
                    <td><g:xEditableRefData owner="${license}" field='enterprise' config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="enterprise" id="enterprise"/></td></tr>
                <tr><td>Post Cancellation Access Entitlement</td>
                    <td><g:xEditableRefData owner="${license}" field="pca" config='YN' class="${editable?'refdataedit':''}"/></td>
                    <td><g:xEditableFieldNote owner="${license}" field="pca" id="pca"/></td></tr>
              </tbody>
            </table>
  
              </div>
              <div class="span4">
                <g:render template="documents" contextPath="../templates" model="${[doclist:license.documents, ownobj:license, owntp:'license']}" />
                <g:render template="notes" contextPath="../templates" model="${[doclist:license.documents, ownobj:license, owntp:'license']}" />
              </div>
            </div>
    </div>
    
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:license?.orgLinks,parent:license.class.name+':'+license.id,property:'orgLinks',recip_prop:'lic']}" />

    <script language="JavaScript">
    
       console.log("ed1");
       
      <g:if test="${editable}">
       console.log("ed2");

      $(document).ready(function() {
      
         console.log("ed3f");


         $( "#dialog-form" ).dialog({
           autoOpen: false,
           height: 300,
           width: 350,
           modal: true,
           buttons: {
             Save: function() {
               $( "#upload_new_doc_form" ).submit();
               $( this ).dialog( "close" );
             },
             Cancel: function() {
               $( this ).dialog( "close" );
             }
           },
           close: function() {
             allFields.val( "" ).removeClass( "ui-state-error" );
           }
         });

         console.log("ed3fr");

         $(".announce").click(function(){
           var id = $(this).data('id');
           $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
           $('#modalComments').modal('show');
         });

         $( "#attach-doc" )
             .button()
             .click(function() {
               $( "#dialog-form" ).dialog( "open" );
             });

         $( "#delete-doc" )
             .button()
             .click(function() {
               $( "#delete_doc_form" ).submit();
             });

         url = document.location.href.split('#');
         if(url[1] != undefined) {
           $('[href=#'+url[1]+']').tab('show');
         }


       });
                console.log("ed3fr outer");

      </g:if>
      <g:else>
                console.log("ed3fr else");

        $(document).ready(function() {
          $(".announce").click(function(){
            var id = $(this).data('id');
            $('#modalComments').load('<g:createLink controller="alert" action="commentsFragment" />/'+id);
            $('#modalComments').modal('show');
          });
        }
      </g:else>
                console.log("done");

    </script>

  </body>
</html>
