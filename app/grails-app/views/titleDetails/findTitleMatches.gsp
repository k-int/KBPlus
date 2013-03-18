<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">
        <div class="row">
          <div class="span12">

            <div class="page-header">
              <h1>New Title - Step 1</h1>
            </div>

            <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
            </g:if>

            <p>Please enter the new title below. Close matches will then be reported to ensure the title is not already present. After confirming
               you wish the new title to be added you can create the new title and move to the edit screen</p>

            <g:form controller="titleDetails" action="findTitleMatches" method="GET" class="form-inline">
                <label>Proposed Title:</label> 
                <input type="text" name="proposedTitle" value="${params.proposedTitle}" />
                <input type="submit" value="Search" class="btn btn-primary">
            </g:form>

            <br/>

            <g:if test="${titleMatches != null}">
              <g:if test="${titleMatches.size()>0}">
                <table class="table table-bordered">
                  <thead>
                    <tr>
                      <th>Title</th>
                      <th>Identifiers</th>
                      <th>Orgs</th>
                      <th>key</th>
                    </tr>
                  </thead>
                  <tbody>
                    <g:each in="${titleMatches}" var="titleInstance">
                      <tr>
                        <td>${titleInstance.title} <g:link controller="titleDetails" action="edit" id="${titleInstance.id}">(edit)</g:link></td>
                        <td><ul><g:each in="${titleInstance.ids}" var="id"><li>${id.identifier.ns.ns}:${id.identifier.value}</li></g:each></ul></td>
                        <td>
                          <ul>
                            <g:each in="${titleInstance.orgs}" var="org">
                              <li>${org.org.name} (${org.roleType.value})</li>
                            </g:each>
                          </ul>
                        </td>
                        <td>${titleInstance.keyTitle}</td>
                      </tr>
                    </g:each>
                  </tbody>
                </table>
                <bootstrap:alert class="alert-info">
                  The title <em>"${params.proposedTitle}"</em> matched one or more records in the database. You can still create another title with this name using the button below,
                  but please do confirm this really is a new title for the system before proceeding.
                </bootstrap:alert>
                <g:link controller="titleDetails" action="createTitle" class="btn btn-warning" params="${[title:params.proposedTitle]}">Create New Title for <em>"${params.proposedTitle}"</em></g:link>
              </g:if>
              <g:else>
                <bootstrap:alert class="alert-info">There were no matches for the title string <em>\"${params.proposedTitle}\"<em>. This is a good sign that the title
                      you wish to create does not exist in the database. However,
                      cases such as mis-spellings, alternate word spacing and abbreviations may not be caught. To ensure the highest quality database,
                      please double check the title for the previous variants. Click the button below when you are sure the current title does not already exist.</bootstrap:alert>
                <g:link controller="titleDetails" action="createTitle" class="btn btn-success" params="${[title:params.proposedTitle]}">Create New Title for <em>"${params.proposedTitle}"</em></g:link>
              </g:else>


            </g:if>
            <g:else>
            </g:else>

          </div>
        </div>
      </div>

  </body>
</html>
