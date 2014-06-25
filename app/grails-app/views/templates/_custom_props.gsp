<%@page import="com.k_int.kbplus.RefdataValue; com.k_int.custprops.PropertyDefinition" %>

<h6>Custom Properties</h6>

<g:form controller="ajax" action="addCustomPropertyValue" class="form-inline">
	<input type="hidden" name="propIdent" id="customPropSelect"/>
	<input type="hidden" name="owner" value="${ownobj.id}" />
	<input type="submit" value="Add Property..." class="btn btn-primary btn-small"/>
</g:form>
<br/>
<table id="custom_props_table" class="table table-bordered licence-properties">
    <thead>
        <tr>
           <th>Property</th>
     	   <th>Value</th>
     	   <th>Notes</th>
           <th>Delete</th>
        </tr>
    </thead>
       <tbody>
            <g:each in="${ownobj.customProperties}" var="prop">
            	<tr>
            		<td>${prop.owner.name}</td>
            		<td>
	      				<g:if test="${prop.owner.type == Integer.toString()}">
	            			<g:xEditable owner="${prop}" type="text" field="intValue" />
	            		</g:if>
	            		<g:elseif test="${prop.owner.type == String.toString()}">
	            			<g:xEditable owner="${prop}" type="text" field="stringValue" />
	            		</g:elseif>
	            		<g:elseif test="${prop.owner.type == BigDecimal.toString()}">
	            			<g:xEditable owner="${prop}" type="text" field="decValue" />
	            		</g:elseif>
	            		<g:elseif test="${prop.owner.type == RefdataValue.toString()}">
	            			<g:xEditableRefData owner="${prop}" type="text" field="refValue" config="${prop.owner.refdataCategory}" />
	            		</g:elseif>
					</td>
            		    <td><g:xEditable owner="${prop}" type="textarea" field="note" />
                    </td>
                    <td>
                        <g:link controller="ajax" action="delCustomProperty" params='[propclass: prop.getClass()]' id="${prop.id}" onclick="return confirm('Really delete this property?')">Delete</g:link>
                    </td>
            	</tr>          
    		</g:each>
		</tbody>
</table>


<div id="cust_prop_add_modal" class="modal hide">

    <g:form id="create_cust_prop" url="[controller:'ajax',action:'addCustPropertyType']" method="post">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">×</button>
            <h3>Create Custom Property</h3>
        </div>

        <input type="hidden" name="parent" value="${parent}"/>
        <div class="modal-body">
            <dl>
                <dt><label class="control-label">New Custom Property:</label></dt>
                <dd>
                   <label class="property-label">Name:</label> <input type="text" name="cust_prop_name">
                </dd>
                <dd>
                    <label class="property-label">Type:</label> <g:select from="${PropertyDefinition.validTypes}" name="cust_prop_type" id="cust_prop_modal_select"/>
                </dd>
                <div class="hide" id="cust_prop_ref_data_name">
                    <dd>
                        <label class="property-label">Refdata Category:</label> <input type="hidden" name="refdatacategory" id="cust_prop_refdatacatsearch">
                    </dd>
                </div>
                <dd>
                   <label class="property-label">Description:</label> <input type="textarea" name="cust_prop_desc">
                </dd>
            </dl>
      
        </div>

        <div class="modal-footer">
            <input id="cust_prop_add_btn" type="submit" class="btn btn-primary" value="Add">
            <a href="#" data-dismiss="modal" class="btn btn-primary">Close</a>
        </div>
    </g:form>

</div>
  <r:script language="JavaScript">
    $(function(){
      $("#cust_prop_refdatacatsearch").select2({
        placeholder: "Type category...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
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
    });
    $(function(){
      $("#customPropSelect").select2({
        placeholder: "Search for a custom property...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.custprops.PropertyDefinition'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        },
        createSearchChoice:function(term, data) {
          return {id:-1, text:"New Property: "+term};
        }
      });
    });
    $("#customPropSelect").on("select2-selecting", function(e) { 
    	if(e.val == -1){
    		 $('#cust_prop_add_modal').modal('show');
    	}
    })

    window.onload = function() {
        var gSelect = document.getElementById('cust_prop_modal_select');
        gSelect.onchange = function() {
            if(gSelect.value == "${RefdataValue.toString()}") {
            	$("#cust_prop_ref_data_name").show()
             }else{
                $("#cust_prop_ref_data_name").hide()
             }
        }
    }

 </r:script>