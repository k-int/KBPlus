<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>

    <div class="container">
        <ul class="breadcrumb">
           <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
           <li>Content Items</li>
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
      <div class="row">
        <div class="span8">
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>Key</td>
                <td>Locale</td>
                <td>Content</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${items}" var="item">
                <tr>
                  <td>${item.key}</td>
                  <td>${item.locale}</td>
                  <td>${item.content}</td>
                  <td><g:link action="editContentItem" id="${item.key}:${item.locale?:''}">Edit</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="span4">
          <g:form action="newContentItem">
            <dl>
              <dt>New Content Item Key</dt>
              <dd><input name="key" type="text"/></dd>

              <dt>New Content Item Locale (Or blank for none)</dt>
              <dd><select name="locale">
                    <option value="">No Locale (Default)</option>
                    <option value="en_GB">British English</option>
                    <option value="es">Español</option>
                    <option value="fr">Français</option>
                    <option value="it">Italiano</option>
                    <option value="ja">日本人</option>
                    <option value="zn-CH">中国的</option>
                    <option value="en_US">US English</option>
                  </select></dd>
              <dt>New Content (Markdown)</dt>
              <dd><textarea name="content" rows="5"></textarea></dd>
            </dl>
            <input type="submit" class="btn btn-primary"/>
          </g:form>
        </div>
      </div>
    </div>
  </body>
</html>
