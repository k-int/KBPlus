<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.list.label" args="[entityName]" /></title>
  </head>
  <body>

    <div class="container">
      <div class="page-header">
        <h1>Track ${item.name}(${item.identifier}) from ${item.source.name}</h1>
      </div>
      <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </g:if>
    </div>

    <div class="container">
      <p>This form will create a new tracker for "${item.name}" from "${item.source.name}".
         In order to do this, a new unique identifier must be created. An identifier is proposed below</p>
      <g:form action="newTracker" id="${params.id}">
        <fieldset class="inline-lists">

          <dl>
            <dt>New Tracker Name</dt>
            <dd><input type="text" name="trackerName" value="${item.name}" class="input-xxlarge"/></dd>
          </dl>

          <dl>
            <dt>New Tracker Id</dt>
            <dd><input type="text" name="trackerId" value="${(item.source.name+" "+item.name).trim().toLowerCase().replaceAll('\\p{Punct}','_').trim().replaceAll('\\W','_')}" class="input-xxlarge"/></dd>
          </dl>

          <input type="submit"/>
        </fieldset>
      </g:form>

  
    </div>

  </body>
</html>
