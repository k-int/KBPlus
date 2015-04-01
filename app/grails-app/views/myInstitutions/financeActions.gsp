<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
</head>
<body>

    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
            <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Finance</g:link> </li>
        </ul>
    </div>

    <div class="container">
        <ul class="">
            <li> <g:link controller="finance" action="newCostItem" params="${[shortcode:params.shortcode]}">New cost item</g:link>  </li>
            <li> <g:link controller="finance" action="search" params="${[shortcode:params.shortcode]}">Search</g:link>  </li>
            <li> <g:link controller="finance" action="index" params="${[shortcode:params.shortcode]}">All in one</g:link>  </li>
        </ul>
    </div>


</body>
</html>