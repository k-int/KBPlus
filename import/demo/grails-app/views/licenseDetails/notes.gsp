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
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li>Licences</li>
      </ul>
    </div>

    <div class="container">
      <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h1>

      <ul class="nav nav-pills">
        <li><g:link controller="licenseDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">License Details</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li><g:link controller="licenseDetails" 
                    action="links" 
                    params="${[id:params.id]}">Links</g:link></li>

        <li class="active"><g:link controller="licenseDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>
      </ul>

    </div>

    <div class="container">
      <g:form id="delete_doc_form" url="[controller:'licenseDetails',action:'deleteDocuments']" method="post">
        <input type="hidden" name="licid" value="${params.id}"/>
        <input type="submit" class="btn btn-danger" value="Delete Selected Notes"/>
        <table class="table table-striped table-bordered table-condensed">
          <thead>
            <tr>
              <td>Select</td>
              <td>Title</td>
              <td>Note</td>
              <td>Creator</td>
              <td>Type</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${license.documents}" var="docctx">
              <g:if test="${docctx.owner.contentType==0 && ( docctx.status == null || docctx.status?.value != 'Deleted')}">
                <tr>
                  <td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/></td>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="title" id="doctitle" class="newipe">${docctx.owner.title}</g:inPlaceEdit></td>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="content" id="doctitle" class="newipe">${docctx.owner.content}</g:inPlaceEdit></td>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="creator" id="docCreator" class="newipe">${docctx.owner.creator}</g:inPlaceEdit></td>
                  <td>${docctx.owner?.type?.value}</td>
                </tr>
              </g:if>
            </g:each>
          </tbody>
        </table>
      </g:form>
    </div>
    
    <script language="JavaScript">
      $(document).ready(function() {
         $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" absolute="true"/>', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...'
         });
       });
    </script>

  </body>
</html>
