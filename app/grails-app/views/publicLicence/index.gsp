<!doctype html>



<html>

  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>KB+ Public Licences</title>
    <r:require module='annotations' />
  </head>


  <body class="public">

  <g:render template="public_navbar" contextPath="/templates" model="['active': 'publicLicence']"/>


  <div class="container">
      <h1>Public Licences</h1>
  </div>


  <div class="container">


    <div id="resultsarea">
      <table class="table table-bordered table-striped">
        <thead>
          <tr style="white-space: nowrap">
          <g:sortableColumn property="reference" title="Reference" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${licences}" var="lic">
            <tr>
              
      <td> <g:link action="show" id="${lic.id}">${lic.reference}</g:link></td>
            </tr>
          </g:each>
        </tbody>
      </table>
    </div>
    <div class="paginateButtons" style="text-align:center">
    <span><g:paginate max="${max}" controller="${controller}" action="index" params="${params}" next="Next" prev="Prev" total="${licences.totalCount}" /></span></div>
     
  </div>



  </body>

</html>
