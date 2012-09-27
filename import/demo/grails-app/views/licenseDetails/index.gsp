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
      <h1>${license.licensee?.name} ${license.type?.value} Licence : 

<g:inPlaceEdit domain="License" pk="${license.id}" style="padding-top: 5px;" field="reference" id="reference" class="${editable?'newipe':''}">${license.reference}</g:inPlaceEdit></h1>

      <ul class="nav nav-pills">
        <li class="active"><g:link controller="licenseDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">License Details</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="links" 
                    params="${[id:params.id]}">Links</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>
      </ul>

    </div>

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
                        <p id="reference" class="${editable?'ipe':''}" >${license.reference}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="noticePeriod">Notice Period</label></dt>
                      <dd>
                        <p id="noticePeriod" class="${editable?'ipe':''}" >${license.noticePeriod}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseUrl">Licence Url</label></dt>
                      <dd>
                        <p id="licenseUrl" class="${editable?'ipe':''}">${license.licenseUrl}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licensorRef">Licensor Ref</label></dt>
                      <dd>
                        <p id="licensorRef" class="${editable?'ipe':''}">${license.licensorRef}</p>
                      </dd>
                  </dl>
      
                  <dl>
                      <dt><label class="control-label" for="licenseeRef">Licensee Ref</label></dt>
                      <dd>
                        <div id="licenseeRef" class="${editable?'ipe':''}" >${license.licenseeRef}</div>
                      </dd>
                  </dl>
                  <div class="clearfix"></div>
                  </div>
  
            <h6>Licence Properties</h6>

            <table class="table table-bordered licence-properties">
              <thead>
                <tr>
                  <td>Property</td>
                  <td>Status</td>
                  <td>Notes</td>
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
    
    <script language="JavaScript">
      <g:if test="${editable}">
      $(document).ready(function() {
         var checkEmptyEditable = function() {
           $('.ipe, .refdataedit, .fieldNote').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         checkEmptyEditable();

         if ( '${license.concurrentUsers?.value}'==='Specified' ) {
           $('#cucwrap').show();
         }
         else {
           $('#cucwrap').hide();
         }
         
         // On jEditable click remove the hide the icon and show it 
         // when one of the buttons are clicked or ESC is hit.

         $('.ipe, .intedit, .refdataedit, .cuedit, .fieldNote, .newipe').click(function() {
            // Ensure we're not clicking in an editing element.
            if($(this).hasClass('clicked')) {
                return;
            }
            
         	// Hide edit icon with overwriting style.
         	$(this).addClass('clicked');
            
            var e = $(this);
            
            var outsideElements;
                        
            setTimeout(function() {
                outsideElements = e.parent().find("span:not(.clicked)");
                console.log(outsideElements);
                outsideElements.hide();
            }, 1);
         	
         	var removeClicked = function() {
         		setTimeout(function() {
         			e.removeClass('clicked');
         			if(outsideElements) {
         				outsideElements.show();
         			}
         		}, 1);
         	}
         	
         	setTimeout(function() {
         		e.find('form button').click(function() {
         			removeClicked();
         		});
         		e.keydown(function(event) {
         			if(event.keyCode == 27) {
         				removeClicked();
         			}
         		});
         	}, 1);
         });
         
         $('.ipe').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setValue" />', { 
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...',
           onblur	 : 'ignore'
         });

         $('.intedit').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setValue" />', {
           type      : 'text',
           cols      : '5',
           width     : '30',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           tooltip   : 'Click to edit...',
           onblur	 : 'ignore'
         });


         $('.refdataedit').editable('<g:createLink controller="ajax" action="genericSetRef" />', {
           data   : {'Yes':'Yes', 'No':'No','Other':'Other'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           onblur	 : 'ignore',
           callback : function(value) {
               var iconList = {
                   'Yes' : 'greenTick',
                   'No' : 'redCross',
                   'Other' : 'purpleQuestion'
               };
               
               var icon = $(document.createElement('span'));
               $(this).prepend(icon.addClass('select-icon').addClass(iconList[value]));
           }
         });

         $('.cuedit').editable('<g:createLink controller="ajax" action="genericSetRef" />', {
           data   : {'No limit':'No limit', 'Specified':'Specified','Not Specified':'Not Specified', 'Other':'Other'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           onblur	 : 'ignore',
           callback : function(value, settings) {
             if ( value==='Specified' ) {
               $('#cucwrap').show();
             }
             else {
               $('#cucwrap').hide();
             }
             
             var iconList = {
                 'No limit' : 'redCross',
                 'Specified' : 'greenTick',
                 'Not Specified' : 'purpleQuestion',
                 'Other' : 'purpleQuestion'
             };
               
             var icon = $(document.createElement('span'));
             $(this).prepend(icon.addClass('select-icon').addClass(iconList[value]));
           }
         });

         $('.fieldNote').editable('<g:createLink controller="ajax" params="${[type:'License']}" id="${params.id}" action="setFieldNote" />', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...',
           onblur	 : 'ignore'
         });

         $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...',
           onblur	 : 'ignore'
         });

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

    </script>

  </body>
</html>
