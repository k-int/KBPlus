<!doctype html>
<%@ page import="com.k_int.kbplus.RefdataValue; com.k_int.custprops.PropertyDefinition" %>

<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Manage Custom Properties</title>
  </head>

  <body>

    <div class="container">
        <ul class="breadcrumb">
           <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
           <li>Custom Properties</li>
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

    <g:hasErrors bean="${newProp}">
        <bootstrap:alert class="alert-error">
        <ul>
            <g:eachError bean="${newProp}" var="error">
                <li> <g:message error="${error}"/></li>
            </g:eachError>
        </ul>
        </bootstrap:alert>
    </g:hasErrors>
   
    <div class="container">
    <div class="span5">
    <div class="well">
      <p>Use the following form to create additional property definitions. Property definition names are unique.</p>
       <g:form id="create_cust_prop" url="[controller: 'ajax', action: 'addCustPropertyType']" >
          <input type="hidden" name="redirect" value="yes"/>
          <input type="hidden" name="ownerClass" value="${this.class}"/>

          <div class="modal-body">
              <dl>
                  <dt><label class="control-label">New Property Definition:</label></dt>
                  <dd>
                      <label class="property-label">Name:</label> <input type="text" name="cust_prop_name"/>
                  </dd>
                  <dd>
                      <label class="property-label">Type:</label> <g:select 
                          from="${PropertyDefinition.validTypes.entrySet()}"
                                  optionKey="value" optionValue="key"
                                  name="cust_prop_type"
                                  id="cust_prop_modal_select"/>
                  </dd>

                  <div class="hide" id="cust_prop_ref_data_name">
                      <dd>
                          <label class="property-label">Refdata Category:</label> 
                          <input type="hidden" name="refdatacategory" id="cust_prop_refdatacatsearch"/>
                      </dd>
                  </div>
                  <dd>
                      <label class="property-label">Description:</label> <g:textArea name="cust_prop_desc" rows="1"/>
                  </dd>
                  <input type="submit" value="Create Property" />
              </dl>
          </div>
          </g:form>
      </div>
      </div>
</div>
    <div class="container">
      <div class="row">
        <div class="span8">
          <table class="table table-bordered table-striped">
            <thead>
              <tr>
                <td>Name</td>
                <td>Description</td>
                <td>Type</td>
                <td>Category</td>
                <td>Number of License CPs</td>
                <td>Number of Sub CPs</td>
                <td>Actions</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${items}" var="item">
                <tr>
                  <g:set var="num_lcp" value="${item.countOccurrences('com.k_int.kbplus.LicenseCustomProperty')}" />
                  <g:set var="num_scp" value="${item.countOccurrences('com.k_int.kbplus.SubscriptionCustomProperty')}" />
                  <td>${item.name}</td>
                  <td>${item.descr}</td>
                  <td>${item.type}</td>
                  <td>${item.refdataCategory}</td>
                  <td>${num_lcp}</td>
                  <td>${num_scp}</td>
                  <td><g:link class="button btn-warn" controller="admin" action="deleteCustprop" id="${item.id}" onClick="return chk(${num_lcp},${num_scp});">Delete</g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </body>
  <g:javascript>
    function chk(n1,n2) {
      if ( n1===0 && n2 ===0 ) {
        return true;
      }
      else {
        return confirm("Deleting this property will also delete "+n1+" License Value[s] and "+n2+" Subscription Value[s]. Are you sure you want to HARD delete these values? Deletions will NOT be recoverable!");
      }
      return false;
    }



    $('#cust_prop_modal_select').change(function() {
        var selectedText = $( "#cust_prop_modal_select option:selected" ).val();
        if( selectedText == "class com.k_int.kbplus.RefdataValue") {
            $("#cust_prop_ref_data_name").show();
        }else{
            $("#cust_prop_ref_data_name").hide();
        }
    });

    $("#cust_prop_refdatacatsearch").select2({
        placeholder: "Type category...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
            url: '${createLink(controller:'ajax', action:'lookup')}',
            dataType: 'json',
            data: function (term, page) {
                return {
                    q: term, // search term
                    page_limit: 10,
                    baseClass:'com.k_int.kbplus.RefdataCategory'
                };
            },
            results: function (data, page) {
                return {results: data.values};
            }
        }
    });
    

  </g:javascript>
</html>
