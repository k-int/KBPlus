<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>${ti.title}</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
      <div class="container">
        <div class="row">
          <div class="span12">

            <div class="page-header">
              <h1><span id="titleEdit" 
                        class="xEditableValue"
                        data-type="textarea" 
                        data-pk="${ti.class.name}:${ti.id}"
                        data-name="title" 
                        data-url='<g:createLink controller="ajax" action="editableSetValue"/>'
                        data-original-title="${ti.title}">${ti.title}</span></h1>
            </div>

            <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
            </g:if>

            <g:if test="${flash.error}">
            <bootstrap:alert class="alert-info">${flash.error}</bootstrap:alert>
            </g:if>

            <h3>Identifiers</h3>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Identifier Namespace</th>
                  <th>Identifier</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.ids}" var="io">
                  <tr>
                    <td>${io.id}</td>
                    <td>${io.identifier.ns.ns}</td>
                    <td>${io.identifier.value}</td>
                    <td><g:link controller="ajax" action="deleteThrough" params='${[contextOid:"${ti.class.name}:${ti.id}",contextProperty:"ids",targetOid:"${io.class.name}:${io.id}"]}'>Delete Identifier</g:link></td>
                  </tr>
                </g:each>
              </tbody>
            </table>

            <g:form controller="ajax" action="addToCollection">
              <input type="hidden" name="__context" value="${ti.class.name}:${ti.id}"/>
              <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.IdentifierOccurrence"/>
              <input type="hidden" name="__recip" value="ti"/>
              <input type="hidden" name="identifier" id="addIdentifierSelect"/>
              <input type="submit" value="Add Identifier..."/>
            </g:form>

            <h3>Org Links</h3>
            <table class="table table-bordered">
              <thead>
                <tr>
                  <th>ID</td>
                  <th>Org</th>
                  <th>Role</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${ti.orgs}" var="org">
                  <tr>
                    <td>${org.id}</td>
                    <td>${org.org.name}</td>
                    <td>${org.roleType.value}</td>
                    <td><g:link controller="ajax" action="deleteThrough" params='${[contextOid:"${ti.class.name}:${ti.id}",contextProperty:"orgs",targetOid:"${org.class.name}:${org.id}"]}'>Delete Org Link</g:link></td>
                  </tr>
                </g:each>
              </tbody>
            </table>

            <g:form controller="ajax" action="addToCollection">
              <input type="hidden" name="__context" value="${ti.class.name}:${ti.id}"/>
              <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.OrgRole"/>
              <input type="hidden" name="__recip" value="title"/>
              <input type="hidden" name="org" id="addOrgSelect"/>
              <input type="hidden" name="roleType" id="orgRoleSelect"/>
              <input type="submit" value="Add Identifier..."/>
            </g:form>

          </div>
        </div>
      </div>


  <script language="JavaScript">

    $(function(){
      // moved to mm_bootstrap
      // $.fn.editable.defaults.mode = 'inline';
      // $('.xEditableValue').editable();

      $("#addIdentifierSelect").select2({
        placeholder: "Search for an identifier...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.kbplus.Identifier'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        },
        createSearchChoice:function(term, data) {
          return {id:'com.k_int.kbplus.Identifier:__new__:'+term,text:term};
        }
      });

      $("#addOrgSelect").select2({
        placeholder: "Search for an org...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.kbplus.Org'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        },
        createSearchChoice:function(term, data) {
          return {id:'com.k_int.kbplus.Org:__new__:'+term,text:term};
        }
      });

      $("#orgRoleSelect").select2({
        placeholder: "Search for an role...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.kbplus.RefdataValue'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        }
      });




    });
  </script>

  </body>
</html>
