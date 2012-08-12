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
        <li> <g:link controller="home">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Notes</g:link> </li>
      </ul>
    </div>

   <div class="container">

      ${institution?.name} ${subscriptionInstance?.type?.value} Subscription Taken

       <h1><g:inPlaceEdit domain="Subscription" pk="${subscriptionInstance.id}" field="name" id="name" class="newipe">${subscriptionInstance?.name}</g:inPlaceEdit></h1>

      <ul class="nav nav-pills">
        <li><g:link controller="subscriptionDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">Current Entitlements</g:link></li>

        <li><g:link controller="subscriptionDetails" 
                    action="addEntitlements" 
                    params="${[id:params.id]}">Add Entitlements</g:link></li>

        <li class="active"><g:link controller="subscriptionDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>
      </ul>

    </div>

    <div class="container">
      <g:form id="delete_doc_form" url="[controller:'subscriptionDetails',action:'deleteDocuments']" method="post">
        <input type="hidden" name="subid" value="${params.id}"/>
        <input type="submit" value="Delete Selected Notes"/>
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
            <g:each in="${subscriptionInstance.documents}" var="docctx">
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
