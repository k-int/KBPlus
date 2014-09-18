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
                                  params="${[shortcode:params.shortcode]}">Copy from Template</g:link></li>
        <g:if test="${is_admin}">
          <li><g:link controller="myInstitutions" 
                                     action="cleanLicense" 
                                     params="${[shortcode:params.shortcode]}">Add Blank License</g:link></li>
        </g:if>

      </ul>
    </div>

    <div class="container licence-searches">
        <div class="row">
            <div class="span6">
                <form class="form-inline">
                    <label>Search by Reference:</label><br/>
                    <input type="text" name="keyword-search" placeholder="enter search term..." value="${params['keyword-search']?:''}" /><br/>
                    <label>Search by License Property:</label><br/>
                            <g:select id="availablePropertyTypes" name="availablePropertyTypes" from="${custom_prop_types}" optionKey="value" optionValue="key" value="${params.propertyFilterType}"/>
                            <input id="selectVal" type="text" name="propertyFilter" placeholder="property value..." value="${params.propertyFilter?:''}" /></p>
                    <br/>
                <input type="hidden" id="propertyFilterType" name="propertyFilterType" value="${params.propertyFilterType}"/>
                <input type="submit" class="btn btn-primary" value="Search" /></p>
                </form>
            </div>
            <div class="span6">
            </div>
        </div>
    </div>

      <div class="container">
          <div class="well licence-options">
              <input type="submit" name="delete-licence" value="Delete Selected" class="btn btn-danger delete-licence" />
          </div>
      </div>


        <div class="container licence-results">
          <table class="table table-bordered table-striped">
            <thead>
              <tr>
                <g:sortableColumn params="${params}" property="reference" title="License Name" />
                <th>Licensor</th>
                <g:sortableColumn params="${params}" property="startDate" title="Start Date" />
                <g:sortableColumn params="${params}" property="endDate" title="End Date" />
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${licenses}" var="l">
                <tr>
                  <td><g:link action="index"
                              controller="licenseDetails" 
                              id="${l.id}">${l.reference?:message(code:'missingLicenseReference', default:'** No Licence Reference Set **')}</g:link>
                   <g:if test="${l.subscriptions && ( l.subscriptions.size() > 0 )}">
                      <ul>
                        <g:each in="${l.subscriptions}" var="sub">
                          <li><g:link controller="subscriptionDetails" action="index" id="${sub.id}">${sub.id} (${sub.name})</g:link><br/></li>
                        </g:each>
                      </ul>
                    </g:if>
                    <g:else>
                      <br/>No linked subscriptions.
                    </g:else>
                  </td>
                  <td>${l.licensor?.name}</td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.startDate}"/></td>
                  <td><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${l.endDate}"/></td>
                  <td>
                    <g:link controller="myInstitutions" action="actionLicenses" params="${[shortcode:params.shortcode,baselicense:l.id,'copy-licence':'Y']}" class="btn btn-success">Copy</g:link>
                    <g:link controller="myInstitutions" action="actionLicenses" params="${[shortcode:params.shortcode,baselicense:l.id,'delete-licence':'Y']}" class="btn btn-danger">Delete</g:link>
                  </td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="pagination" style="text-align:center">
          <bootstrap:paginate action="currentLicenses" controller="myInstitutions" params="${params}" next="Next" prev="Prev" max="${max}" total="${licenseCount}" />
        </div>

    <r:script type="text/javascript">

        $('.licence-results input[type="radio"]').click(function () {
            $('.licence-options').slideDown('fast');
        });

        function updateSelect(){
          var selectedOption = $( "#availablePropertyTypes option:selected" )
          var selectedValue = selectedOption.val()
          $('#propertyFilterType').val(selectedOption.text())

          if(selectedValue.indexOf("RefdataValue") != -1){
            var refdataType = selectedValue.split("&&")[1]
            $.ajax({ url:'<g:createLink controller="ajax" action="sel2RefdataSearch"/>'+'/'+refdataType+'?format=json',
                        success: function(data) {
                          var select = ' <select id="selectVal" name="propertyFilter" > ';
                          var index;
                          for(index=0; index < data.length; index++ ){
                            var option = data[index]
                            select += ' <option value="'+option.text+'">'+option.text+'</option> '
                          }
                          select += '</select>';
                          $('#selectVal').replaceWith(select);
                        }
            });
          }else{
            $('#selectVal').replaceWith('<input id="selectVal" type="text" name="propertyFilter" placeholder="property value" />');
          }
        }

        $('#availablePropertyTypes').change(function() {
          updateSelect();
        });

        $('.licence-options .delete-licence').click(function () {
            $('.licence-results input:checked').each(function () {
                $(this).parent().parent().fadeOut('slow');
                $('.licence-options').slideUp('fast');
            })
        });

        window.onload = updateSelect();
    </r:script>


  </body>
</html>
