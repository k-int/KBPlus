<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Current Licences</title>
  </head>
  <body>

    <div class="container">
        <ul class="breadcrumb">
            <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
           <li> <g:link controller="myInstitutions" action="currentLicenses" params="${[shortcode:params.shortcode]}">${institution.name} Current Licenses</g:link> </li>
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
      <h1>${institution?.name} - Licences</h1>

     <ul class="nav nav-pills">
       <li class="active"><g:link controller="myInstitutions" 
                                  action="currentLicenses" 
                                  params="${[shortcode:params.shortcode]}">Current Licences</g:link></li>

          <li><g:link controller="myInstitutions" 
                                  action="addLicense" 
                                  params="${[shortcode:params.shortcode]}">Add Licence</g:link></li>
      </ul>
    </div>

    <div class="container licence-searches">
        <div class="row">
            <div class="span6">
                <form class="form-inline">
                    <label>Search by Reference:</label>
                    <input type="text" name="keyword-search" placeholder="enter search term..." value="${params['keyword-search']?:''}" />
                    <p><label>Search by Property: </label>
                      <input id="selectVal" type="text" name="propertyFilter" placeholder="property value..." value="${params.propertyFilter?:''}" /></p>
             <p><label>Type: </label>
             <g:select id="availablePropertyTypes" name="availablePropertyTypes" from="${custom_prop_types}"
             optionKey="value" optionValue="key"/>
                <input type="hidden" id="propertyFilterType" name="propertyFilterType" value="${params.propertyFilterType}"/>
                <input type="submit" class="btn btn-primary" value="Search" /></p>
                </form>
            </div>
            <div class="span6">
                <div class="pull-right">
                    <g:link controller="myInstitutions" action="cleanLicense" params="${[shortcode:params.shortcode]}" class="btn btn-primary">Create New Licence</g:link>
                </div>
            </div>
        </div>
    </div>

    <g:form action="actionLicenses"
            controller="myInstitutions" 
            params="${[shortcode:params.shortcode]}">

      <div class="container">
          <div class="well licence-options">
              <input type="submit" name="delete-licence" value="Delete Selected" class="btn btn-danger delete-licence" />
          </div>
      </div>


        <div class="container licence-results">
          <table class="table table-bordered table-striped">
            <thead>
              <tr>
                <th>Select</th>
                <g:sortableColumn params="${params}" property="reference" title="Reference Description" />
                <th>Licensor</th>
                <g:sortableColumn params="${params}" property="status.value" title="Status" />
                <g:sortableColumn params="${params}" property="type.value" title="Type" />
                <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
                <g:sortableColumn params="${params}" property="endDate" title="End Date" />
              </tr>
            </thead>
            <tbody>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><input type="radio" name="baselicense" value="${l.id}"/></td>
                  <td><g:link action="index"
                              controller="licenseDetails" 
                              id="${l.id}">${l.reference?:message(code:'missingLicenseReference', default:'** No Licence Reference Set **')}</g:link></td>
                  <td>${l.licensor?.name}</td>
                  <td>${l.status?.value}</td>
                  <td>${l.type?.value}</td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.startDate}"/></td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.endDate}"/></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="pagination" style="text-align:center">
          <bootstrap:paginate action="currentLicenses" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${licenseCount}" />
        </div>

    </g:form>
    <r:script type="text/javascript">
        $('.licence-results input[type="radio"]').click(function () {
            $('.licence-options').slideDown('fast');
        });

        function availableTypesSelectUpdated(optionSelected){

          var selectedOption = $( "#availablePropertyTypes option:selected" )

          var selectedValue = selectedOption.val()

          //Set the value of the hidden input, to be passed on controller
          $('#propertyFilterType').val(selectedOption.text())
          
          updateInputType(selectedValue)

   
        }

        function updateInputType(selectedValue){

          //If we are working with RefdataValue, grab the values and create select box
          if(selectedValue.indexOf("RefdataValue") != -1){
            var refdataType = selectedValue.split("&&")[1]
            $.ajax({ url:'<g:createLink controller="ajax" action="sel2RefdataSearch"/>'+'/'+refdataType+'?format=json',
                        success: function(data) {
                          var select = ' <select id="selectVal" name="propertyFilter" > '
                          //we need empty when we dont want to search by property
                          select += ' <option></option> '
                          for(var index=0; index < data.length; index++ ){
                            var option = data[index]
                            select += ' <option value="'+option.text+'">'+option.text+'</option> '
                          }
                          select += '</select>'
                          $('#selectVal').replaceWith(select)
                        }
            });
          }else{
            //If we dont have RefdataValues,create a simple text input
            $('#selectVal').replaceWith('<input id="selectVal" type="text" name="propertyFilter" placeholder="property value" />')
          }

        }
        function setTypeAndSearch(){
          var selectedType = $("#propertyFilterType").val()
          //Iterate the options, find the one with the text we want and select it
          var selectedOption = $("#availablePropertyTypes option").filter(function() {
                return $(this).text() == selectedType ;
          }).prop('selected', true); //This will trigger a change event as well.


          //Generate the correct select box
          availableTypesSelectUpdated(selectedOption)

          //Set selected value for the actual search
          var paramPropertyFilter = "${params.propertyFilter}"
          $("#selectVal").val(paramPropertyFilter)

        }

        $('#availablePropertyTypes').change(function(e) {
          var optionSelected = $("option:selected", this);
          availableTypesSelectUpdated(optionSelected);
        });

        $('.licence-options .delete-licence').click(function () {
            $('.licence-results input:checked').each(function () {
                $(this).parent().parent().fadeOut('slow');
                $('.licence-options').slideUp('fast');
            })
        })
        window.onload = setTypeAndSearch()
    </r:script>


  </body>
</html>
