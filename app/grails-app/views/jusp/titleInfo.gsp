<g:form name="isCoreForm" action="changeStatus" contoller = "${controller}">
	<input type='hidden' name='orgtiID' value="${orgtitle?.id}"/>
	<input type='hidden' name='jusp_ti' value="${params.jusp_ti}"/>
	<input type='hidden' name='jusp_org' value="${params.jusp_org}"/>
	<g:if test="${orgtitle}">
    Core Status: <g:select name="core" from="${['Yes','No']}"  value="${orgtitle?.isCore == true ? 'Yes' : 'No'}"/>
    <br></br><input type="submit" value="Change status"/>
    </g:if>
    <g:else>
    	<p> Jusp Identifiers ${wrongIDs} don't match KB+ records.</p>
    </g:else>
</g:form>
