<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>
  <body>

    <div class="container">
      <ul class="breadcrumb">
         <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions"  action="addSubscription" params="${[shortcode:params.shortcode]}">${institution.name} Add Subscripton</g:link> </li>
      </ul>
    </div>


    <div class="container">
      <h1>${institution?.name} - Add Subscription</h1>
      <g:render template="subsNav" contextPath="." />
    </div>

        
    <div class="container">
      <p>This form will create a new subscription not attached to any packages. You will need to add packages using the Add Package tab
         on the subscription details page</p>
      <g:form action="processEmptySubscription" params="${[shortcode:params.shortcode]}" controller="myInstitutions" method="post" class="form-inline"> 
        <dl>
          <dt><label>New Subscription Name: </label></dt><dd> <input type="text" name="newEmptySubName" placeholder="New Subscription Name"/>&nbsp;</dd>
          <dt><label>New Subscription Identifier: </label></dt><dd> <input type="text" name="newEmptySubId" value="${defaultSubIdentifier}"/>&nbsp;</dd>
          <dt><label>Valid From: </label></dt><dd> <g:simpleHiddenValue id="valid_from" name="validFrom" type="date" value="${defaultStartYear}"/>&nbsp;</dd>
          <dt><label>Valid To: </label></dt><dd> <g:simpleHiddenValue id="valid_to" name="validTo" type="date" value="${defaultEndYear}"/>&nbsp;</dd>
          <br/>
          <input type="submit" class="btn btn-primary" value="Create ->" />
        </dl>
      </g:form>
    </div>       
    
  </body>
</html>
