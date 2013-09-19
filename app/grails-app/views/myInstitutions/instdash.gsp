<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="instdash" params="${[shortcode:params.shortcode]}">${institution.name} Dashboard</g:link> </li>
      </ul>
    </div>


    <div class="container home-page">
      <h1>${institution.name} Dashboard</h1>
      <ul class="inline">
        <li><g:link controller="myInstitutions" 
                                       action="currentLicenses" 
                                       params="${[shortcode:params.shortcode]}">Licences</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="currentSubscriptions" 
                                       params="${[shortcode:params.shortcode]}">Subscriptions</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="currentTitles" 
                                       params="${[shortcode:params.shortcode]}">Titles</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="renewalsSearch" 
                                       params="${[shortcode:params.shortcode]}">Generate Renewals Worksheet</g:link></li>
        <li><g:link controller="myInstitutions" 
                                       action="renewalsUpload" 
                                       params="${[shortcode:params.shortcode]}">Import Renewals</g:link></li>
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

    <div class="container home-page">
      <div class="row">
        <div class="span4">
          <div class="well">
            <h6>ToDo</h6>
            <table class="table">
              <g:each in="${todos}" var="todo">
                <tr>
                  <td>
                    <span class="badge badge-warning">${todo.num_changes}</span> <em>${todo.item_with_changes.toString()}</em> </br>
                    <span class="pull-right">Changes between <g:formatDate date="${todo.earliest}" format="yyyy-MM-dd hh:mm a"/></span><br/>
                    <span class="pull-right">and <g:formatDate date="${todo.latest}" format="yyyy-MM-dd hh:mm a"/></span><br/>
                    <strong class="pull-right">
                      <g:if test="${todo.item_with_changes instanceof com.k_int.kbplus.Subscription}">
                        <g:link controller="subscriptionDetails" action="index" id="${todo.item_with_changes.id}">View Subscription</g:link>
                      </g:if>
                      <g:else>
                        <g:link controller="licenseDetails" action="index" id="${todo.item_with_changes.id}">View License</g:link>
                      </g:else>
                    </strong>
                  </td>
                </tr>
              </g:each>
            </table>
            <div style="text-align:center;">
              <g:link action="todo" params="${[shortcode:params.shortcode]}" class="btn">View ToDo List</g:link>
            </div>
            &nbsp;<br/>
          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Announcements</h6>

            <table class="table">
              <g:each in="${recentAnnouncements}" var="ra">
                <tr>
                  <td><strong>${ra.title}</strong> <br/>
                  ${ra.content} <span class="pull-right">posted by <em><g:link controller="userDetails" action="pub" id="${ra.user.id}">${ra.user.displayName}</g:link></em> on <g:formatDate date="${ra.dateCreated}" format="yyyy-MM-dd hh:mm a"/></span></td>
                </tr>
              </g:each>
            </table>
            <div style="text-align:center;">
              <g:link action="announcements" params="${[shortcode:params.shortcode]}" class="btn">View Announcements</g:link>
            </div>
            &nbsp;<br/>

          </div>
        </div>
        <div class="span4">
          <div class="well">
            <h6>Latest Discussions</h6>
            <g:if test="${forumActivity}">
              <table class="table">
                <g:each in="${forumActivity.results}" var="fa">
                  <tr>
                    <td>
                      ${fa.title}<br>
                      <g:if test="${fa.result_type=='topic'}">
                        <span class="pull-right"><a href="${grailsApplication.config.ZenDeskBaseURL}/entries/${fa.id}">View Topic</a></span>
                      </g:if>
                      <g:else>
                        <span class="pull-right"><a href="${fa.url}">View ${fa.result_type}</a></span>
                      </g:else>
                    </td>
                  </tr>
                </g:each>
              </table>
            </g:if>
            <g:else>
              Recent forum activity not available.
              Please retry later.
            </g:else>
            <div style="text-align:center;">
              <a href="${grailsApplication.config.ZenDeskBaseURL}/forums" class="btn">Visit Forums</a>
            </div>
            &nbsp;<br/>
          </div>
        </div>
      </div>
    </div>

  </body>
</html>
