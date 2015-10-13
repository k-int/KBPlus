<%@ page import="com.k_int.custprops.PropertyDefinition" %>

<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'propertyDefinition.label', default: 'PropertyDefinition')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="row-fluid">

    <div class="span3">
        <div class="well">
            <ul class="nav nav-list">
                <li class="nav-header">${entityName}</li>
                <li>
                    <g:link class="list" action="list">
                        <i class="icon-list"></i>
                        <g:message code="default.list.label" args="[entityName]"/>
                    </g:link>
                </li>
                <li>
                    <g:link class="create" action="create">
                        <i class="icon-plus"></i>
                        <g:message code="default.create.label" args="[entityName]"/>
                    </g:link>
                </li>
            </ul>
        </div>
    </div>

    <div class="span9">

        <div class="page-header">
            <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
        </div>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${propDefInstance}">
            <bootstrap:alert class="alert-error">
                <ul>
                    <g:eachError bean="${propDefInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </bootstrap:alert>
        </g:hasErrors>

        <fieldset>
              <g:set var="usages" value="${propDefInstance.countOccurrences('com.k_int.kbplus.LicenseCustomProperty','com.k_int.kbplus.SystemAdminCustomProperty','com.k_int.kbplus.OrgCustomProperty')}" />

            <g:form class="form-horizontal" action="edit" id="${propDefInstance?.id}">
                <g:hiddenField name="version" value="${propDefInstance?.version}"/>
                <fieldset>
                    <div class="control-group ">
                        <label class="control-label" for="name">Name</label>
                        <div class="controls">
                            <input type="text" name="name" value="${propDefInstance.name}" required="" id="name">
                            
                        </div>
                    </div>
                    <div class="control-group ">
                        <label class="control-label" for="name">Description</label>
                        <div class="controls">
                        <g:select name="descr" value="${propDefInstance.descr}" from="${PropertyDefinition.AVAILABLE_DESCR}" rows="1"/> 
                        </div>
                    </div>
                    <div class="control-group ">
                        <label class="control-label" for="name">Type</label>
                        <div class="controls">
                            <input type="text" name="type" value="${propDefInstance.type}" required="" id="name">
                            
                        </div>
                    </div>
                    <div class="control-group ">
                        <label class="control-label" for="name">RefdataCategory</label>
                        <div class="controls">
                            <input type="text" name="refdataCategory" value="${propDefInstance.refdataCategory}" id="name">
                            
                        </div>
                    </div>
                    <div class="control-group ">
                        <label class="control-label" for="name">Number of existing usages</label>
                        <div class="controls">
                            <input type="text" disabled="" value="${usages}"/>
                            
                        </div>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="icon-ok icon-white"></i>
                            <g:message code="default.button.update.label" default="Update"/>
                        </button>
                        <button type="submit" <%= ( ( usages == 0  ) ) ? '' : 'disabled' %> class="btn btn-danger" name="_action_delete" formnovalidate>
                            <i class="icon-trash icon-white"></i>
                            <g:message code="default.button.delete.label" default="Delete"/>
                        </button>
                    </div>
                </fieldset>
            </g:form>
        </fieldset>

    </div>

</div>
</body>
</html>