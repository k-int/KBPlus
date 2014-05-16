<%--
  Created by IntelliJ IDEA.
  User: ioannis
  Date: 15/05/2014
  Time: 15:00
--%>

<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="index">All Packages</g:link><span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="show"
                    id="${packageInstance.id}">${packageInstance.name}</g:link></li>

        <li class="dropdown pull-right">
            <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#"
               href="">Exports<b class="caret"></b></a>

            <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
                <li><g:link action="show" params="${params + [format: 'json']}">Json Export</g:link></li>
                <li><g:link action="show" params="${params + [format: 'xml']}">XML Export</g:link></li>
                <g:each in="${transforms}" var="transkey,transval">
                    <li><g:link action="show" id="${params.id}"
                                params="${[format: 'xml', transformId: transkey]}">${transval.name}</g:link></li>
                </g:each>
            </ul>
        </li>

        <li class="pull-right">
            View:
            <div class="btn-group" data-toggle="buttons-radio">
                <g:link controller="packageDetails" action="show" params="${params + ['mode': 'basic']}"
                        class="btn btn-primary btn-mini ${((params.mode == 'basic') || (params.mode == null)) ? 'active' : ''}">Basic</g:link>
                <g:link controller="packageDetails" action="show" params="${params + ['mode': 'advanced']}"
                        class="btn btn-primary btn-mini ${params.mode == 'advanced' ? 'active' : ''}">Advanced</g:link>
            </div>
            &nbsp;
        </li>

    </ul>
</div>
<g:if test="${flash.message}">
    <div class="container"><bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert></div>
</g:if>

<g:if test="${flash.error}">
    <div class="container"><bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert></div>
</g:if>

<g:render template="nav" contextPath="." />
<div class="container">
    <g:form id="delete_doc_form" url="[controller:'packageDetails',action:'deleteDocuments']" method="post">
           <g:if test="${editable}">
            <input type="hidden" name="redirectAction" value="notes"/>
            <input type="hidden" name="subId" value="${params.id}"/>
            <input type="hidden" name="ctx" value="notes"/>
            <input type="submit" class="btn btn-danger" value="Delete Selected Notes"/>
            <input type="submit" class="btn btn-primary" value="Add new Note" data-toggle="modal" href="#modalCreateNote" />
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
            <g:each in="${packageInstance.documents}" var="docctx">
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

<!-- Lightbox modal for creating a note taken from licenceNotes.html -->
    <div class="modal hide" id="modalCreateNote">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">×</button>
            <h3>Create New Note</h3>
        </div>
        <g:form id="create_note" url="[controller:'docWidget',action:'createNote']" method="post">
            <input type="hidden" name="ownerid" value="${packageInstance.id}"/>
            <input type="hidden" name="ownerclass" value="${packageInstance.class.name}"/>
            <input type="hidden" name="ownertp" value="pkg"/>
            <div class="modal-body">
                <dl>
                    <dt>
                        <label>Note:</label>
                    </dt>
                    <dd>
                        <textarea name="licenceNote"></textarea>
                    </dd>
                </dl>
                <input type="hidden" name="licenceNoteShared" value="0"/>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn" data-dismiss="modal">Close</a>
                <input type="submit" class="btn btn-primary" value="Save Changes">
            </div>
        </g:form>
    </div>
</div>
</body>
</html>
