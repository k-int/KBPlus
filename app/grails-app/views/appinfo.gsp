<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ App Info</title>
  </head>

  <body>
    <h1>Application Info</h1>
    <ul>
      <li>Build Number: <g:meta name="app.buildNumber"/></li>
      <li>App version: <g:meta name="app.version"/></li>
      <li>Grails version: <g:meta name="app.grails.version"/></li>
      <li>Groovy version: ${GroovySystem.getVersion()}</li>
      <li>JVM version: ${System.getProperty('java.version')}</li>
      <li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
    </ul>
  </body>
</html>
