
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::User Merge</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="admin" action="userMerge">User Merge </g:link> </li>
      </ul>
    </div>

  <div class="container">
   <div class="span8">


    <g:if test="${flash.message}">
	    <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>
    <g:if test="${flash.error}">
	    <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
    </g:if>
    
     <p>Select the user to keep, and the user whose rights will be transferred. When 'merge' is clicked,
      a confirmation screen with 'user to merge' current rights will be displayed.</p>

    <g:form action="userMerge" method="GET">
	 <dl>
        <div class="control-group">
          <dt>User to Keep</dt>
          <dd>
		<g:select name="userToKeep" from="${usersActive}" optionKey="id" 
			optionValue="displayName" noSelection="${['null':'-Choose user to keep-']}" />
			</dd>
			<dt> User to Merge</dt>
			<dd>

		<g:select name="userToMerge" from="${usersAll}" optionKey="id" 
		optionValue="displayName" noSelection="${['null':'-Choose user to merge-']}"/>
		</dd>
     </dl>
     <input type="submit" value="Merge" class="btn btn-primary"/>
	</g:form>
  </div>
  </div>

  
	<div id="user_merge_modal" class="modal hide">
		 
		 <div class="modal-header">
		   <button type="button" class="close" data-dismiss="modal">×</button>
		   <h3>Merge ${usrMrgName} into ${userKeepName} </h3>
		 </div>
	    <g:form action="userMerge" method="POST">
	    
	    <div class="modal-body">
	        <input type="hidden" name="userToKeep" value="${params.userToKeep}"/>
	        <input type="hidden" name="userToMerge" value="${params.userToMerge}"/>

	        <p>Current Roles and Affiliations that will be copied to ${userKeepName}</p>

	        <b> User Roles </b>
	        <ul>
		        <g:each in="${userRoles}" var="userRole">
			        <li> ${userRole.authority}</li>
		        </g:each>
	        </ul>
	        <b> Affiliations </b>

	        <div style="height:300px;line-height:3em;overflow:auto;padding:5px;">
	         <ul>
		        <g:each in="${userAffiliations}" var="affil">
			        <li> ${affil.org.name} :: ${affil.formalRole.authority}</li>
		        </g:each>
	        </ul>
	        </div>
        </div>
        
        <div class="modal-footer">
		    <input type="submit" value="Apply" class="btn btn-primary btn-small"/>
		 </div>
        </g:form>
	</div>


	<g:if test="${userRoles}">
	    <r:script language="JavaScript">
			$('#user_merge_modal').modal('show');
		</r:script>
	</g:if>
  </body>
</html>
