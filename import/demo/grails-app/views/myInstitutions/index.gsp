<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="row-fluid">
      <ul>
        <g:each in="${userAlerts}" var="ua">
          <li>
            ${ua.rootObj.class.name}:${ua.rootObj.id}
            <ul>
              <g:each in="${ua.notes}" var="n">
                <li>
                  ${n.alert.createTime} ${n.alert.sharingLevel} ${n.owner.content}
                </li>
              </g:each>
            </ul>
          </li>
        </g:each>
      </ul>
    </div>
  </body>
</html>
