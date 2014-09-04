%{--To use, add the g:render custom_props inside a div with id=custom_props_div, add g:javascript src=custom_properties.js--}%
%{--on head of container page, and on window load execute  runCustomPropsJS("<g:createLink controller='ajax' action='lookup'/>");--}%

<%@ page import="com.k_int.kbplus.RefdataValue; com.k_int.custprops.PropertyDefinition" %>

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


<g:formRemote url="[controller: 'ajax', action: 'addCustomPropertyValue']" method="post" name="cust_prop_add_value"
              class="form-inline" update="custom_props_div" onComplete="runCustomPropsJS('${createLink(controller:'ajax', action:'lookup')}')">
    <input type="hidden" name="propIdent" id="customPropSelect"/>
    <input type="hidden" name="ownerId" value="${ownobj.id}"/>
    <input type="hidden" name="editable" value="${editable}"/>
    <input type="hidden" name="ownerClass" value="${ownobj.class}"/>
    <input type="submit" value="Add Property" class="btn btn-primary btn-small"/>
</g:formRemote>
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
            <td>${prop.type.name}</td>
        <td>
            <g:if test="${prop.type.type == Integer.toString()}">
                <g:xEditable owner="${prop}" type="text" field="intValue"/>
            </g:if>
            <g:elseif test="${prop.type.type == String.toString()}">
                <g:xEditable owner="${prop}" type="text" field="stringValue"/>
            </g:elseif>
            <g:elseif test="${prop.type.type == BigDecimal.toString()}">
                <g:xEditable owner="${prop}" type="text" field="decValue"/>
            </g:elseif>
            <g:elseif test="${prop.type.type == RefdataValue.toString()}">
                <g:xEditableRefData owner="${prop}" type="text" field="refValue" config="${prop.type.refdataCategory}"/>
            </g:elseif>
        </td>
        <td><g:xEditable owner="${prop}" type="textarea" field="note"/>
        </td>
        <td>
            <g:if test="${editable == true}">
            <g:remoteLink controller="ajax" action="delCustomProperty" 
                before="if(!confirm('Delete the property ${prop.type.name}?')) return false"
                params='[propclass: prop.getClass(),ownerId:"${ownobj.id}",ownerClass:"${ownobj.class}", editable:"${editable}"]' id="${prop.id}"
                onComplete="runCustomPropsJS('${createLink(controller:'ajax', action:'lookup')}')" update="custom_props_div">Delete</g:remoteLink>
            </g:if>
        </td>
        </tr>
    </g:each></tbody>
</table>

<div id="cust_prop_add_modal" class="modal hide">

    <g:formRemote id="create_cust_prop" name="modal_create_cust_prop"
                  url="[controller: 'ajax', action: 'addCustPropertyType']" method="post" update="custom_props_div" 
                  onComplete="runCustomPropsJS('${createLink(controller:'ajax', action:'lookup')}')">
        <input type="hidden" name="ownerId" value="${ownobj.id}"/>
        <input type="hidden" name="ownerClass" value="${ownobj.class}"/>
        <input type="hidden" name="editable" value="${editable}"/>

        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">×</button>

            <h3>Create Custom Property</h3>
        </div>

        <input type="hidden" name="parent" value="${parent}"/>

        <div class="modal-body">
            <dl>
                <dt><label class="control-label">New Custom Property:</label></dt>
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
                        <label class="property-label">Refdata Category:</label> <input type="hidden"
                                                                                       name="refdatacategory"
                                                                                       id="cust_prop_refdatacatsearch"/>
                    </dd>
                </div>
                <dd>
                    <label class="property-label">Description:</label> <g:textArea name="cust_prop_desc" rows="1"/>
                </dd>
                <dd>
                    <label class="property-label">Add property:</label><g:checkBox name="autoAdd" checked="true"/>
                </dd>
            </dl>
        </div>

        <div class="modal-footer">
            <input id="new_cust_prop_add_btn" type="submit" class="btn btn-primary" value="Add">
            <a href="#" data-dismiss="modal" class="btn btn-primary">Close</a>
        </div>
    </g:formRemote>

</div>
