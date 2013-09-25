<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${subscriptionInstance.subscriber}">
          <li> <g:link controller="myInstitutions" action="currentSubscriptions" params="${[shortcode:subscriptionInstance.subscriber.shortcode]}"> ${subscriptionInstance.subscriber.name} Current Subscriptions</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="subscriptionDetails" action="index" id="${subscriptionInstance.id}">Subscription ${subscriptionInstance.id} Notes</g:link> </li>
        <g:if test="${editable}">
          <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>
      </ul>
    </div>

   <div class="container">

      ${institution?.name} ${subscriptionInstance?.type?.value} Subscription Taken

       <h1>${subscriptionInstance?.name}</h1>

       <g:render template="nav" contextPath="." />

    </div>

    <div class="container">
      <g:form id="delete_doc_form" url="[controller:'subscriptionDetails',action:'deleteDocuments']" method="post">
      <g:if test="${editable}">
        <input type="hidden" name="subId" value="${params.id}"/>
        <input type="hidden" name="ctx" value="notes"/>
        <input type="submit" class="btn btn-danger" value="Delete Selected Notes"/>
      </g:if>

        <table class="table table-striped table-bordered table-condensed">
          <thead>
            <tr>
              <g:if test="${editable}"><th>Select</th></g:if>
              <th>Title</th>
              <th>Note</th>
              <th>Creator</th>
              <th>Type</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${subscriptionInstance.documents}" var="docctx">
              <g:if test="${docctx.owner.contentType==0 && ( docctx.status == null || docctx.status?.value != 'Deleted')}">
                <tr>
                  <g:if test="${editable}"><td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/></td></g:if>
                  <td>
                    <g:xEditable owner="${docctx.owner}" field="title" id="title"/>
                  </td>
                  <td>
                    <g:xEditable owner="${docctx.owner}" field="content" id="content"/>
                  </td>
                  <td>
                    <g:xEditable owner="${docctx.owner}" field="creator" id="creator"/>
                  </td>
                  <td>${docctx.owner?.type?.value}</td>
                </tr>
              </g:if>
            </g:each>
          </tbody>
        </table>
      </g:form>
    </div>
    
  </body>
</html>
