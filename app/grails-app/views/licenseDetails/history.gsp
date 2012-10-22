<!doctype html>
<html>
    <head>
        <meta name="layout" content="mmbootstrap"/>
        <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
</head>

<body>

    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="myInstitutions" action="dashboard">Home</g:link> <span class="divider">/</span> </li>
            <li>Licences</li>
        </ul>
    </div>

    <div class="container">
        <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" class="ipe" style="padding-top: 5px;">${license.reference}</span></h1>

        <ul class="nav nav-pills">
            <li><g:link controller="licenseDetails"
                        action="index"
                        params="${[id:params.id]}">License Details</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="documents"
                        params="${[id:params.id]}">Documents</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="links"
                        params="${[id:params.id]}">Links</g:link></li>

            <li><g:link controller="licenseDetails"
                        action="notes"
                        params="${[id:params.id]}">Notes</g:link></li>

            <li class="active"><g:link controller="licenseDetails" 
                        action="history" 
                        params="${[id:params.id]}">History</g:link></li>

        </ul>

    </div>

    <div class="container">
License history
    </div>

</body>
</html>
