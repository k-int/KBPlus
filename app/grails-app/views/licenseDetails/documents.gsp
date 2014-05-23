<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <g:if test="${license.licensee}">
            <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:license.licensee.shortcode]}"> ${license.licensee.name} Current Licenses</g:link> <span class="divider">/</span> </li>
        </g:if>
        <li> <g:link controller="licenseDetails" action="index" id="${params.id}">License Details</g:link> <span class="divider">/</span></li>
        <li> <g:link controller="licenseDetails" action="documents" id="${params.id}">License Documents</g:link> </li>

        <g:if test="${editable}">
            <li class="pull-right">Editable by you&nbsp;</li>
        </g:if>

    </ul>
</div>

<div class="container">
    <h1>${license.licensee?.name} ${license.type?.value} Licence : <span id="reference" style="padding-top: 5px;">${license.reference}</span></h1>

    <g:render template="nav" />

</div>

<div class="container">
    <g:render template="/templates/documents_table" model="${[instance:license, redirect:'documents']}"/>
</div>
<g:render template="/templates/addDocument" model="${[ownobj:license, owntp:'license']}" />

<r:script language="JavaScript">
  $(document).ready(function() {
      
      var checkEmptyEditable = function() {
           $('.fieldNote').each(function() {
           alert("field note")
             if($(this).text().length == 0) {
               $(this).addClass('editableEmpty');
             } else {
               $(this).removeClass('editableEmpty');
             }
           });
         }

         checkEmptyEditable();

        $('.fieldNote').click(function() {
                   alert("field note2 ")

            // Hide edit icon with overwriting style.
            $(this).addClass('clicked');  	
         	
            var e = $(this);
         	
            var removeClicked = function() {
                setTimeout(function() {
                    e.removeClass('clicked');
         			
                    if(iconStyle) {
                        e.parent().find('.select-icon').show();
                    }
                }, 1);
            };
         	
            setTimeout(function() {
                e.find('form button').click(function() {
                    removeClicked();
                });
                e.keydown(function(event) {
                    if(event.keyCode == 27) {
                        removeClicked();
                    }
                });
            }, 1);
         });
         
     $('.fieldNote').editable('<g:createLink controller="ajax" action="genericSetValue" />', {
       type      : 'textarea',
       cancel    : 'Cancel',
       submit    : 'OK',
       id        : 'elementid',
       rows      : 3,
       tooltip   : 'Click to edit...',
       onblur	 : 'ignore'
     });
   });
</r:script>




</body>
</html>
