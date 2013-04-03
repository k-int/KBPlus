
<%@ page import="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
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
                <g:message code="default.list.label" args="[entityName]" />
              </g:link>
            </li>
<sec:ifAnyGranted roles="ROLE_ADMIN">
            <li>
              <g:link class="create" action="create">
                <i class="icon-plus"></i>
                <g:message code="default.create.label" args="[entityName]" />
              </g:link>
            </li>
            </sec:ifAnyGranted>
          </ul>
        </div>
      </div>
      
      <div class="span9">

        <div class="page-header">
          <h1><g:message code="default.show.label" args="[entityName]" /></h1>
        </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

          <div class="inline-lists">

              <g:if test="${subscriptionInstance?.name}">
                  <dl>
                      <dt><g:message code="subscription.name.label" default="Subscription Name" /></dt>
                      <dd>${subscriptionInstance?.name}</dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.status}">
                  <dl>
                      <dt><g:message code="subscription.status.label" default="Status" /></dt>
                      <dd><g:link controller="refdataValue" action="show" id="${subscriptionInstance?.status?.id}">${subscriptionInstance?.status?.encodeAsHTML()}</g:link></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.type}">
                  <dl>
                      <dt><g:message code="subscription.type.label" default="Type" /></dt>
                      <dd>${subscriptionInstance?.type?.value?.encodeAsHTML()}</dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.owner}">
                  <dl>
                      <dt><g:message code="subscription.owner.label" default="License" /></dt>
                      <dd><g:link controller="license" action="show" id="${subscriptionInstance?.owner?.id}">${subscriptionInstance?.owner?.encodeAsHTML()}</g:link></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.impId}">
                  <dl>
                      <dt><g:message code="subscription.impId.label" default="Imp Id" /></dt>
                      <dd><g:fieldValue bean="${subscriptionInstance}" field="impId"/></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.endDate}">
                  <dl>
                      <dt><g:message code="subscription.endDate.label" default="End Date" /></dt>
                      <dd><g:formatDate format="dd MMMM yyyy" date="${subscriptionInstance?.endDate}" /></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.instanceOf}">
                  <dl>
                      <dt><g:message code="subscription.instanceOf.label" default="Instance Of" /></dt>
                      <dd><g:link controller="subscription" action="show" id="${subscriptionInstance?.instanceOf?.id}">${subscriptionInstance?.instanceOf?.name?.encodeAsHTML()}</g:link></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.identifier}">
                  <dl>
                      <dt><g:message code="subscription.identifier.label" default="Identifier" /></dt>
                      <dd><g:fieldValue bean="${subscriptionInstance}" field="identifier"/></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.name}">
                  <dl>
                      <dt><g:message code="subscription.name.label" default="Name" /></dt>
                      <dd><g:fieldValue bean="${subscriptionInstance}" field="name"/></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.noticePeriod}">
                  <dl>
                      <dt><g:message code="subscription.noticePeriod.label" default="Notice Period" /></dt>
                      <dd><g:fieldValue bean="${subscriptionInstance}" field="noticePeriod"/></dd>
                  </dl>
              </g:if>

              <g:if test="${subscriptionInstance?.packages}">
                  <dl>
                      <dt><g:message code="subscription.packages.label" default="Packages" /></dt>

                      <g:each in="${subscriptionInstance.packages}" var="p">
                          <dd><g:link controller="package" action="show" id="${p.pkg.id}">${p?.pkg?.name}</g:link></dd>
                      </g:each>
                  </dl>
              </g:if>

          </div>

          <g:if test="${subscriptionInstance?.orgRelations}">
              <h6>Relations</h6>
              <table class="table table-bordered table-striped">
                  <tr>
                      <th>Relation</th>
                      <th>To Org</th>
                  </tr>
                  <g:each in="${subscriptionInstance.orgRelations}" var="or">
                      <tr>
                          <td>${or.roleType?.value}</td>
                          <td><g:link controller="org" action="show" id="${or.org.id}">${or.org.name}</g:link></td>
                      </tr>
                  </g:each>
              </table>
          </g:if>

          <g:if test="${subscriptionInstance?.issueEntitlements}">
              <h6>Entitlements</h6>
              <table class="table table-bordered table-striped">
                  <tr>
                      <th>Title</th>
                      <th>ISSN</th>
                      <th>eISSN</th>
                      <th>Start Date</th>
                      <th>Start Volume</th>
                      <th>Start Issue</th>
                      <th>End Date</th>
                      <th>End Volume</th>
                      <th>End Issue</th>
                  </tr>
                  <g:each in="${subscriptionInstance.issueEntitlements}" var="ie">
                      <tr>
                          <td>${ie.tipp.title.title}</td>
                          <td>
                              ${ie?.tipp?.title?.getIdentifierValue('ISSN')}
                          </td>
                          <td>
                              ${ie?.tipp?.title?.getIdentifierValue('eISSN')}
                          </td>
                          <td><g:formatDate format="dd MMMM yyyy" date="${ie?.startDate}" /></td>
                      <td>${ie.startVolume}</td>
                      <td>${ie.startIssue}</td>
                      <td><g:formatDate format="dd MMMM yyyy" date="${ie?.endDate}" /></td>
                      <td>${ie.endVolume}</td>
                      <td>${ie.endIssue}</td>
                      </tr>
                  </g:each>
              </table>
          </g:if>

        <g:form>
          <sec:ifAnyGranted roles="ROLE_ADMIN">
          <g:hiddenField name="id" value="${subscriptionInstance?.id}" />
          <div class="form-actions">
            <g:link class="btn" action="edit" id="${subscriptionInstance?.id}">
              <i class="icon-pencil"></i>
              <g:message code="default.button.edit.label" default="Edit" />
            </g:link>
            <button class="btn btn-danger" type="submit" name="_action_delete">
              <i class="icon-trash icon-white"></i>
              <g:message code="default.button.delete.label" default="Delete" />
            </button>
          </div>
          </sec:ifAnyGranted>
        </g:form>

      </div>

    </div>
  </body>
</html>
