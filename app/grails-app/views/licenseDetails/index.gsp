<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
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

                <div class="licence-info">

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
                        <g:xEditableRefData owner="${license}" field="isPublic" config='YN'/>...
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
                    <td><g:refdataValue val="${license.remoteAccess?.value}" domain="License" pk="${license.id}" field="remoteAccess" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="remoteAccess" value="${license.getNote('remoteAccess')}" class="${editable?'fieldNote':''}"/></td></tr>
  
                <tr><td>Walk In Access</td>
                    <td><g:refdataValue val="${license.walkinAccess?.value}" domain="License" pk="${license.id}" field="walkinAccess" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="walkinAccess" value="${license.getNote('walkinAccess')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Multi Site Access</td>
                    <td><g:refdataValue val="${license.multisiteAccess?.value}" domain="License" pk="${license.id}" field="multisiteAccess" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="multisiteAccess" value="${license.getNote('multisiteAccess')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Partners Access</td>
                    <td><g:refdataValue val="${license.partnersAccess?.value}" domain="License" pk="${license.id}" field="partnersAccess" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="partnersAccess" value="${license.getNote('partnersAccess')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Alumni Access</td>
                    <td><g:refdataValue val="${license.alumniAccess?.value}" domain="License" pk="${license.id}" field="alumniAccess" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="alumniAccess" value="${license.getNote('alumniAccess')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>ILL - Inter Library Loans</td>
                    <td><g:refdataValue val="${license.ill?.value}" domain="License" pk="${license.id}" field="ill" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="ill" value="${license.getNote('ill')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Include In Coursepacks</td>
                    <td><g:refdataValue val="${license.coursepack?.value}" domain="License" pk="${license.id}" field="coursepack" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="coursepack" value="${license.getNote('coursepack')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Include in VLE</td>
                    <td><g:refdataValue val="${license.vle?.value}" domain="License" pk="${license.id}" field="vle" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="vle" value="${license.getNote('vle')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Enterprise Access</td>
                    <td><g:refdataValue val="${license.enterprise?.value}" domain="License" pk="${license.id}" field="enterprise" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="enterprise" value="${license.getNote('enterprise')}" class="${editable?'fieldNote':''}"/></td></tr>
                <tr><td>Post Cancellation Access Entitlement</td>
                    <td><g:refdataValue val="${license.pca?.value}" domain="License" pk="${license.id}" field="pca" cat='YNO' class="${editable?'refdataedit':''}"/></td>
                    <td><g:singleValueFieldNote domain="pca" value="${license.getNote('pca')}" class="${editable?'fieldNote':''}"/></td></tr>
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
