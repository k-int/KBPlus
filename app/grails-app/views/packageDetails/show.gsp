<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">

        <div class="page-header">
          <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${packageInstance}">
        <bootstrap:alert class="alert-error">
        <ul>
          <g:eachError bean="${packageInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </bootstrap:alert>
        </g:hasErrors>

        <fieldset>
            <g:hiddenField name="version" value="${packageInstance?.version}" />

            <!--
              packageType
              packageStatus
              contentProvider
              nominalPlatform
              packageListStatus
              identifier
              impId
              name
              orgs
              subscriptions
              tipps
            -->
            <fieldset>

              <dl>
                <dt>Org Links</dt>
                <dd><g:render template="orgLinks" 
                            contextPath="../templates" 
                            model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',editmode:true]}" /></dd>
              </dl>

              <dl>
                <dt>Package Type</dt>
                <dd>
                  <g:relation domain='Package'
                            pk='${packageInstance?.id}'
                            field='packageType'
                            class='refdataedit'
                            id='PackageType'>${packageInstance?.packageType?.value?:'Not set'}</g:relation>
                </dd>
              </dl>

        <dl>
          <dt>Titles</dt>
          <dd>
          <table border="1" cellspacing="5" cellpadding="5">
            <thead>
            <tr>
              <th rowspan="2" style="width: 20%;">Title</th>
              <th rowspan="2" style="width: 20%;">Platform</th>
              <th rowspan="2" style="width: 10%;">ISSN</th>
              <th rowspan="2" style="width: 10%;">eISSN</th>
              <th rowspan="2" style="width: 10%;">Start Date</th>
              <th rowspan="2" style="width: 10%;">Start Volume</th>
              <th rowspan="2" style="width: 10%;">Start Issue</th>
              <th rowspan="2" style="width: 10%;">End Date</th>
              <th rowspan="2" style="width: 10%;">End Volume</th>
              <th rowspan="2" style="width: 10%;">End Issue</th>
              <th rowspan="2" style="width: 10%;">Coverage Depth</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${packageInstance?.tipps}" var="t">
              <tr>
                <td>
                   ${t.title.title}
                   <g:link controller="titleDetails" action="edit" id="${t.title.id}">(Title)</g:link>
                   <g:link controller="titleDetails" action="edit" id="${t.title.id}">(TIPP)</g:link>
                </td>
                <td>${t.platform?.name}</td>
                <td>${t.title?.getIdentifierValue('ISSN')}</td>
                <td>${t?.title?.getIdentifierValue('eISSN')}</td>
                <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.startDate}"/>
                    <input id="TitleInstancePackagePlatform:${t.id}:startDate" type="hidden" class="dp1" /></td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="startVolume" id="startVolume" class="newipe">${t.startVolume}</g:inPlaceEdit></td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endIssue" id="endIssue" class="newipe">${t.startIssue}</g:inPlaceEdit> </td>
                <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${t.endDate}"/>
                    <input id="TitleInstancePackagePlatform:${t.id}:endDate" type="hidden" class="dp1" /></td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endVolume" id="endVolume" class="newipe">${t.endVolume}</g:inPlaceEdit></td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="endIssue" id="endIssue" class="newipe">${t.endIssue}</g:inPlaceEdit></td>
                <td><g:inPlaceEdit domain="TitleInstancePackagePlatform" pk="${t.id}" field="coverageDepth" id="coverageDepth" class="newipe">${t.coverageDepth}</g:inPlaceEdit></td>
              </tr>
            </g:each>
            </tbody>
          </table>
          </dd>
        </dl>


            </fieldset>
        </fieldset>


      </div>


    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',recip_prop:'pkg']}" />

    <script language="JavaScript">

      $(document).ready(function(){
         $('dd span.refdataedit').editable('<g:createLink controller="ajax" params="${[resultProp:'value']}" action="genericSetRel" />', {
           loadurl: '<g:createLink controller="ajax" params="${[id:'PackageType',format:'json']}" action="refdataSearch" />',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...',
           onblur        : 'ignore'
         });


         // On jEditable click remove the hide the icon and show it
         // when one of the buttons are clicked or ESC is hit.

         $('.newipe').click(function() {
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


        var datepicker_config = {
          buttonImage: '../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          showButtonPanel: true,
          showClearButton: true,
          clearText: "Clear",
          onSelect: function(dateText, inst) {
            var elem_id = inst.input[0].id;
            $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue"/>?elementid='+
                             elem_id+'&value='+dateText+'&dt=date&idf=MM/dd/yyyy&odf=${session.sessionPreferences?.globalDateFormat}',
                   success: function(result){inst.input.parent().find('span').html(result)}
                   });
          },
          beforeShow: function( input ) {
            setTimeout(function() {
                var buttonPane = $( input )
                    .datepicker( "widget" )
                    .find( ".ui-datepicker-buttonpane" );

                $( "<button/>", {
                    text: "Clear",
                    click: function() {
                      // var parent=$(this).parent('.dp1')
                      console.log("%o",input)
                      $(input).parent().find('span.datevalue').html("")
                      $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue"/>?elementid='+input.id+'&value=__NULL__',});
                      $(input).value="__NULL__"
                    }
                }).appendTo( buttonPane ).addClass("ui-datepicker-clear ui-state-default ui-priority-primary ui-corner-all");
            }, 1 );
          }
        };

        $("input.dp1").datepicker(datepicker_config);

      });

    </script>

  </body>
</html>
