
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::User Merge</title>
  </head>

  <body>
  	<g:if test="${success}">
  	HAHAHAHAHAHHAHAHA WE MADE ITTT
  	</g:if>
    <div class="row-fluid">
   
        <g:form action="userMerge" method="GET">

			<g:select name="userToKeep" from="${users}" optionKey="id" 
				optionValue="displayName" noSelection="[null:'-Choose user to keep-']" />

			<g:select name="userToMerge" from="${users}" optionKey="id" 
			optionValue="displayName" noSelection="[null:'-Choose user to merge-']"/>
         <input type="submit" class="btn btn-primary"/>
      </g:form>

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
	         <ul>
		        <g:each in="${userAffiliations}" var="affil">
			        <li> ${affil.org.name} :: ${affil.formalRole.authority}</li>
		        </g:each>
	        </ul>
             <input type="submit" class="btn btn-primary"/>

        </g:form>
	</div>
    </div>
  </body>
</html>
