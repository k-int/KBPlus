<%@ page import="com.k_int.kbplus.RefdataValue; com.k_int.custprops.PropertyDefinition" %>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Manage Property Definitions</title>
  </head>

  <body>


    <div class="container">
        <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller='admin' action='index'>Admin</g:link> <span class="divider">/</span> </li>
        <li class="active">Manage Property Definitions</li>
      </ul>
    </div>

    <div class="container">

    <g:hasErrors bean="${newProp}">
        <bootstrap:alert class="alert-error">
        <ul>
            <g:eachError bean="${newProp}" var="error">
                <li> <g:message error="${error}"/></li>
            </g:eachError>
        </ul>
        </bootstrap:alert>
    </g:hasErrors>

    <g:if test="${error}">
        <bootstrap:alert class="alert-danger">${error}</bootstrap:alert>
    </g:if>
        <h2>Manage Property Definitions</h2>

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


        <h6> Existing Property Definitions</h6>
        <table class="table table-striped table-bordered table-condensed">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Description</th>            
                </tr>
            </thead>
            <tbody>
            <g:each in="${definitions}" var="prop">

            <g:set var="type" value="${PropertyDefinition.validTypes.entrySet().find{
                        it.getValue() == prop.type }}"/>
                <tr>
                    <td>${prop.name}</td>
                    <td> 
                    <g:if test="${type.getValue() == RefdataValue.toString() }">
                        ${type.getKey()} - ${prop.refdataCategory}
                    </g:if>
                    <g:else>
                        ${type.getKey()}
                    </g:else>

                    </td>
                    <td>${prop.descr}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

  </body>
  <g:javascript>


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
