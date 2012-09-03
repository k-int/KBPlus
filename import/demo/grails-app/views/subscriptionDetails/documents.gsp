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
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Details</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

    <g:if test="${flash.message}">
      <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
    </g:if>

    <div class="container">

      ${institution?.name} ${subscriptionInstance?.type?.value} Subscription Taken

       <h1><g:inPlaceEdit domain="Subscription" pk="${subscriptionInstance.id}" field="name" id="name" class="${editable?'newipe':''}">${subscriptionInstance?.name}</g:inPlaceEdit></h1>

      <ul class="nav nav-pills">
        <li><g:link controller="subscriptionDetails" 
                                   action="index" 
                                   params="${[id:params.id]}">Current Entitlements</g:link></li>

        <g:if test="${editable}">
          <li><g:link controller="subscriptionDetails" 
                      action="addEntitlements" 
                      params="${[id:params.id]}">Add Entitlements</g:link></li>
        </g:if>

        <li class="active"><g:link controller="subscriptionDetails" 
                    action="documents" 
                    params="${[id:params.id]}">Documents</g:link></li>

        <li><g:link controller="subscriptionDetails" 
                    action="notes" 
                    params="${[id:params.id]}">Notes</g:link></li>

      </ul>

    </div>


    <div class="container">
  
      
      <g:form id="delete_doc_form" url="[controller:'subscriptionDetails',action:'deleteDocuments']" method="post">
      <g:if test="${editable}">
        <input type="hidden" name="subId" value="${params.id}"/>
        <input type="hidden" name="ctx" value="documents"/>
        <input type="submit" class="btn btn-danger" value="Delete Selected Notes"/>
        <input type="submit" class="btn btn-primary" value="Add new document" data-toggle="modal" href="#modalCreateDocument" />
      </g:if>


  
        <table class="table table-striped table-bordered table-condensed">
          <thead>
            <tr>
              <g:if test="${editable}"><td>Select</td></g:if>
              <td>Title</td>
              <td>File Name</td>
              <td>Download Link</td>
              <td>Creator</td>
              <td>Type</td>
              <td>Doc Store ID</td>
            </tr>
          </thead>
          <tbody>
            <g:each in="${subscriptionInstance.documents}" var="docctx">
              <g:if test="${(docctx.owner.contentType==1) && (docctx.status?.value != 'Deleted')}">
                <tr>
                  <g:if test="${editable}"><td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/></td></g:if>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="title" id="doctitle" class="${editable?'newipe':''}">${docctx.owner.title}</g:inPlaceEdit></td>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="filename" id="docfilename" class="${editable?'newipe':''}">${docctx.owner.filename}</g:inPlaceEdit></td>
                  <td>
                    <g:if test="${docctx.owner?.contentType==1}">
                      <g:link controller="docstore" id="${docctx.owner.uuid}">Download Doc</g:link>
                    </g:if>
                  </td>
                  <td><g:inPlaceEdit domain="Doc" pk="${docctx.owner.id}" field="creator" id="docCreator" class="${editable?'newipe':''}">${docctx.owner.creator}</g:inPlaceEdit></td>
                  <td>${docctx.owner?.type?.value}</td>
                  <td><g:if test="${docctx.owner?.uuid}">${docctx.owner?.uuid}</g:if></td>
                </tr>
              </g:if>
            </g:each>
          </tbody>
        </table>
      </g:form>
    </div>
    
    <script language="JavaScript">
      $(document).ready(function() {
         $('.newipe').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
           type      : 'textarea',
           cancel    : 'Cancel',
           submit    : 'OK',
           id        : 'elementid',
           rows      : 3,
           tooltip   : 'Click to edit...'
         });
       });
    </script>

<!-- Lightbox modal for creating a document taken from licenceDocuments.html -->
<div class="modal hide" id="modalCreateDocument">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Document</h3>
  </div>
  <g:form id="upload_new_doc_form" url="[controller:'docWidget',action:'uploadDocument']" method="post" enctype="multipart/form-data">
    <input type="hidden" name="ownerid" value="${subscriptionInstance.id}"/>
    <input type="hidden" name="ownertp" value="subscription"/>
    <input type="hidden" name="ownerclass" value="com.k_int.kbplus.Subscription"/>
    <div class="modal-body">
      <dl>
        <dt>
          <label>Document Name:</label>
        </dt>
        <dd>
          <input type="text" name="upload_title">
        </dd>
      </dl>
      <dl>
        <dt>
          <label>File:</label>
        </dt>
        <dd>
          <input type="file" name="upload_file" />
        </dd>
      </dl>
     <dl>
        <dt>
          <label>Document Type:</label>
        </dt>
        <dd>
          <select name="doctype">
            <option value="License">License</option>
            <option value="General">General</option>
            <option value="General">Addendum</option>
          </select>
        </dd>
      </dl>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
      <input type="submit" class="btn btn-primary" value="Save Changes">
    </div>
  </g:form>
</div>
<!-- End lightbox modal -->

  </body>
</html>
