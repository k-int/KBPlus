<!doctype html>
<html>
    <head>
        <meta name="layout" content="mmbootstrap"/>
        <title>KB+ Licence</title>
</head>

<body>

    <div class="container">
      <g:render template="breadcrumb" model="${[ license:license, params:params ]}"/>
    </div>

    <div class="container">
        <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" style="padding-top: 5px;">${license.reference}</span></h1>

<g:render template="nav" />

    </div>

    <div class="container">
        <g:render template="/templates/notes_table" model="${[instance: license, redirect: 'notes']}"/>

    </div>
<g:render template="/templates/addNote"
          model="${[doclist: license.documents, ownobj: license, owntp: 'license']}"/>

</body>
</html>
