    <g:form name="kb_core_form" action="changeIsCoreStatus" contoller = "${controller}">
        <input type='hidden' name='orgtiID' value="${data?.id}"/>
        <input type='hidden' name='jusp_ti' value="${params.jusp_ti}"/>
        <input type='hidden' name='jusp_inst' value="${params.jusp_inst}"/>
        <g:if test="${data}">
        <span id="kb_core_status" >Core Status: <g:select id="kb_core_select" name="core" from="${['Yes','No']}"  value="${data?.isCore == true ? 'Yes' : 'No'}"/> </span>
        <input type="submit" name="kb_core_submit" value="Change status"/>
        </g:if>
        <g:else>
            <span id="kb_core_error"> Jusp Identifiers ${wrongIDs} don't match KB+ records.</span>
        </g:else>
    </g:form>
