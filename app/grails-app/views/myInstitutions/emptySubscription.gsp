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
        <table> 
          <tr>              
            <td><label>New Subscription Name: </label> <input type="text" name="newEmptySubName"/>&nbsp;</td>
            <td><label>Valid From: </label> <g:simpleHiddenValue id="valid_from" name="validFrom" type="date"/>&nbsp;</td>
            <td><label>Valid To: </label> <g:simpleHiddenValue id="valid_to" name="validTo" type="date"/>&nbsp;</td>
            <td><input type="submit" class="btn btn-primary" value="Create ->" /></td>
          </tr>
        </table>
      </g:form>
    </div>       
    
  </body>
</html>
