<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>

    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
    <h2>${institution?.name} Subscription Taken : ${subscriptionInstance?.name}</span></h2>
    <hr/>
    <div class="tabbable"> <!-- Only required for left/right tabs -->
      <dl>
        <dt>License</td>
        <dd><g:relation domain='Subscription' 
                        pk='${subscriptionInstance.id}' 
                        field='owner' 
                        class='reldataEdit'
                        id='ownerLicense'>${subscriptionInstance?.owner?.reference}</g:relation></dd>

        <g:if test="${subscriptionInstance?.issueEntitlements}">
          <dt>Entitlements</td>
          <dd>
            <table  class="table table-striped table-bordered table-condensed">
              <tr>
                <th></th>
                <th>Title</th>
                <th>ISSN</th>
                <th>eISSN</th>
                <th>Core</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Embargo</th>
                <th>Content URL</th>
                <th>Coverage</th>
                <th>Docs</th>
                <th>JUSP</th>
              </tr>
              <tr>
                <th colspan="4"><button>Apply Batch Changes:</button></th>
                <th>edit</th>
                <th>edit <input type="hidden" class="hdp" /></th>
                <th>edit <input type="hidden" class="hdp" /></th>
                <th>edit</th>
                <th colspan="4"></th>
              </tr>
              <g:each in="${subscriptionInstance.issueEntitlements}" var="ie">
                <tr>
                  <td><input type="checkbox" name="batchedit"/>
                  <td><g:link controller="titleInstance" action="show" id="${ie.tipp.title.id}">${ie.tipp.title.title}</g:link></td>
                  <td>${ie?.tipp?.title?.getIdentifierValue('ISSN')}</td>
                  <td>${ie?.tipp?.title?.getIdentifierValue('eISSN')}</td>
                  <td><g:refdataValue val="${ie.coreTitle}" 
                                      domain="IssueEntitlement" 
                                      pk="${ie.id}" 
                                      field="coreTitle" 
                                      cat="isCoreTitle"
                                      class="coreedit"/></td>
                  <td>
                      <span><g:formatDate format="dd MMMM yyyy" date="${ie.startDate}"/></span>
                      <input id="IssueEntitlement:${ie.id}:startDate" type="hidden" class="dp1" />
                  </td>
                  <td><span><g:formatDate format="dd MMMM yyyy" date="${ie.endDate}"/></span>
                      <input id="IssueEntitlement:${ie.id}:endDate" type="hidden" class="dp2" />
                  </td>
                  <td><g:inPlaceEdit domain="IssueEntitlement" pk="${ie.id}" field="embargo" id="embargo" class="newipe">${ie.embargo}</g:inPlaceEdit></td>
                  <td>${ie.tipp?.platform?.primaryUrl}</td>
                  <td>${ie.coverageDepth}<br/>${ie.coverageNote}</td>
                  <td>docs</td>
                  <td>JUSP</td>
                </tr>
              </g:each>
            </table>
          </dd>
        </g:if>
        <dt>Org Links</td>
        <dd>
          <ul>
            <g:each in="${subscriptionInstance.orgRelations}" var="or">
              <li>${or.org.name}:${or.roleType?.value}</li>
            </g:each>
          <ul>
        </dd>
      </dl>
    </div>

    <script language="JavaScript">
      $(document).ready(function() {

        var datepicker_config = {
          buttonImage: '../../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          onSelect: function(dateText, inst) { 
            var elem_id = inst.input[0].id;
            $.ajax({url: '<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>?elementid='+
                             elem_id+'&value='+dateText+'&dt=date&idf=MM/dd/yyyy&odf=dd MMMM yyyy',
                   success: function(result){inst.input.parent().find('span').html(result)}
                   });
          }
        };

        $(".dp1").datepicker(datepicker_config);
        $(".dp2").datepicker(datepicker_config);

        $(".hdp").datepicker({
          buttonImage: '../../../images/calendar.gif',
          buttonImageOnly: true,
          changeMonth: true,
          changeYear: true,
          showOn: 'both',
          onSelect: function(dateText, inst) {
            alert(dateText)
          }
        });

        $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
          type      : 'textarea',
          cancel    : 'Cancel',
          submit    : 'OK',
          id        : 'elementid',
          rows      : 3,
          tooltip   : 'Click to edit...'
        });

        $('.coreedit').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
           data   : {'true':'true', 'false':'false'},
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...'
         });

         $('.reldataEdit').editable('<g:createLink controller="ajax" params="${[resultProp:'reference']}"action="genericSetRel" absolute="true"/>', {
           loadurl: '<g:createLink controller="MyInstitutions" params="${[shortcode:params.shortcode]}" action="availableLicenses" absolute="true"/>',
           type   : 'select',
           cancel : 'Cancel',
           submit : 'OK',
           id     : 'elementid',
           tooltip: 'Click to edit...',
           callback : function(value, settings) {
           }
         });

         var checkEmptyEditable = function() {
           $('.ipe, .refdataedit, .fieldNote, .reldataEdit').each(function() {
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }


      });
    </script>
  </body>
</html>
