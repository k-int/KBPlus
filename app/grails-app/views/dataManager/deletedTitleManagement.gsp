<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data Manager Dashboard</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="dataManager" action="index">Data Manager Dashboard</g:link> <span class="divider">/</span> </li>
        <li> Deleted Title Management </li>
      </ul>
    </div>

    <g:if test="${flash.message}">
      <div class="container">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
      </div>
    </g:if>

    <g:if test="${flash.error}">
      <div class="container">
        <bootstrap:alert class="error-info">${flash.error}</bootstrap:alert>
      </div>
    </g:if>

    <div class="container">
      <h2>Deleted Title Management : ${titleInstanceTotal} Deleted Titles</h2>
    </div>

    <div class="container">

      <table class="table table-bordered table-striped">
        <thead>
          <tr>
            <g:sortableColumn property="title" title="${message(code: 'title', default: 'Title')}" />
            <th></th>
          </tr>
        </thead>
        <tbody>
          <g:each in="${titleInstanceList}" var="titleInstance">
            <tr>
              <td>${fieldValue(bean: titleInstance, field: "title")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>

      <div class="pagination">
        <bootstrap:paginate  action="deletedTitleManagement" controller="dataManager" params="${params}" next="Next" prev="Prev" max="${max}" total="${titleInstanceTotal}" />
      </div>
    </div>


  </body>
</html>
