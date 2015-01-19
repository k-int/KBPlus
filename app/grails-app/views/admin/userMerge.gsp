
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Admin::User Merge</title>
  </head>

  <body>
    <div class="row-fluid">
   
        <g:form action="userMerge" method="POST">

<g:select name="userToKeep" from="${users}" optionKey="id" optionValue="displayName" noSelection="[null:'-Choose user to keep-']" />

<g:select name="userToMerge" from="${users}" optionKey="id" optionValue="displayName" noSelection="[null:'-Choose user to merge-']"/>
         <input type="submit" class="btn btn-primary"/>
      </g:form>
    </div>
  </body>
</html>
