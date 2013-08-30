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

    <div class="container">
      <g:form action="editContentItem">
        <dl>
          <dt>Key</dt>
          <dd><input name="key" type="text"/></dd>

          <dt>Locale (Or blank for none)</dt>
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
          <dt>Content (Markdown)</dt>
          <dd><textarea name="content" rows="5"></textarea></dd>
        </dl>
        <input type="submit" class="btn btn-primary"/>
      </g:form>
    </div>
  </body>
</html>
