<%--
  Created by IntelliJ IDEA.
  User: ioannis
  Date: 15/05/2014
  Time: 14:00
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
            <input type="hidden" name="redirectAction" value="documents"/>
            <input type="hidden" name="subId" value="${params.id}"/>
            <input type="hidden" name="ctx" value="documents"/>
            <input type="submit" class="btn btn-danger" value="Delete Selected Documents"/>
            <g:render template="addDocument" contextPath="../templates" model="${[doclist:packageInstance.documents, ownobj:packageInstance, owntp:'pkg']}" />

        </g:if>



        <table class="table table-striped table-bordered table-condensed" style="table-layout: fixed; word-wrap: break-word;">
            <thead>
            <tr>
                 <g:if test="${editable}"><th>Select</th></g:if>
                <th>Title</th>
                <th>File Name</th>
                <th>Download Link</th>
                <th>Creator</th>
                <th>Type</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${packageInstance.documents}" var="docctx">
                <g:if test="${((docctx.owner.contentType==1)||(docctx.owner?.contentType==3)) && (docctx.status?.value != 'Deleted')}">
                    <tr>
                    <g:if test="${editable}"> <td><input type="checkbox" name="_deleteflag.${docctx.id}" value="true"/> </g:if>

                        <td style="max-width: 300px;overflow: hidden;text-overflow: ellipsis;">
                            <g:xEditable owner="${docctx.owner}" field="title" id="title"/>
                        </td>
                        <td style="max-width: 300px;overflow: hidden;text-overflow: ellipsis;">
                            <!-- Consider  max-width:200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; -->
                            <g:xEditable owner="${docctx.owner}" field="filename" id="filename"/>
                        </td>
                        <td>
                            <g:if test="${((docctx.owner?.contentType==1)||(docctx.owner?.contentType==3))}">
                                <g:link controller="docstore" id="${docctx.owner.uuid}">Download Doc</g:link>
                            </g:if>
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
<%--
<div class="modal hide" id="modalCreateDocument">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Create New Document</h3>
    </div>
    <g:form id="upload_new_doc_form" url="[controller:'docWidget',action:'uploadDocument']" method="post" enctype="multipart/form-data">
        <input type="hidden" name="ownerid" value="${packageInstance.id}"/>
        <input type="hidden" name="ownertp" value="pkg"/>
        <input type="hidden" name="ownerclass" value="${packageInstance.class.name}"/>
        <div class="modal-body">
            <div class="inline-lists">
                <dl>
                    <dt>
                        <label>Document Name:</label>
                    </dt>
                    <dd>
                        <input type="text" name="upload_title">
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <label>File:</label>
                    </dt>
                    <dd>
                        <input type="file" name="upload_file" />
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <label>Document Type:</label>
                    </dt>
                    <dd>
                        <select name="doctype">
                            <option value="License">License</option>
                            <option value="General">General</option>
                            <option value="General">Addendum</option>
                        </select>
                    </dd>
                </dl>
            </div>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <input type="submit" class="btn btn-primary" value="Save Changes">
        </div>
    </g:form>
</div>
--%>
</body>

</html>