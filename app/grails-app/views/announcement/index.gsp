<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="announcement" action="index">Announcements</g:link> </li>
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
      <h2>Create announcement</h2>
      <g:form action="createAnnouncement">
        Subject: <input type="text" name="subjectTxt" class="span12" value="${params.as}"/><br/>
        <textarea name="annTxt" class="span12">${params.at}</textarea><br/>
        <input type="submit" class="btn btn-primary" value="Create Announcement..."/>
      </g:form>
    </div>

    <div class="container">
      <h2>previous announcements</h2>
      <table class="table">
        <g:each in="${recentAnnouncements}" var="ra">
          <tr>
            <td><strong>${ra.title}</strong> <br/>
            ${ra.content} 
            <span class="pull-right">posted by <em><g:link controller="userDetails" action="pub" id="${ra.user.id}">${ra.user.displayName}</g:link></em> on <g:formatDate date="${ra.dateCreated}" format="yyyy-MM-dd hh:mm a"/></span>
          </tr>
        </g:each>
      </table>
    </div>

  </body>
</html>
