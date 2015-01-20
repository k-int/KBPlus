
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::User Merge</title>
  </head>

  <body>

    <div class="container">
      <div class="span10">

	    <g:if test="${flash.message}">
	    <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
	    </g:if>
        <g:if test="${flash.error}">
		    <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
	    </g:if>
	    
        <h1>User Merge</h1>
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
         <input type="submit" value="Merge" class="btn btn-primary"/>
         </dl>
      </g:form>
      </div>

	<div id="cust_prop_add_modal" <g:if test="${userRoles}"> class="modal show" </g:if><g:else>class="modal hide"</g:else>>
        <g:form action="userMerge" method="POST">
	        <input type="hidden" name="userToKeep" value="${params.userToKeep}"/>
	        <input type="hidden" name="userToMerge" value="${params.userToMerge}"/>
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
             <input type="submit" class="btn btn-primary"/>

        </g:form>
	</div>
	      </div>

  </body>
</html>
