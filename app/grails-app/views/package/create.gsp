<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
    <r:require modules="bootstrap-typeahead"/>
  </head>
  <body>
    <div class="container">
      <p>
        Use this form to create a new package header record. Each package must have a globally unique identifier. Traditionally, KB+ has formed this identifier
        from the content provider and the package name. Identifier will be defaulted from this values, but must be checked and be unique within the database before
        the create button will activate.
      </p>
      <g:form class="form-horizontal" action="create" >
        <dl>
          <dt>Content Provider*</dt>
          <dd>
             <input type="text" id="provider-typeahead"/>
          </dd>
        </dl>
        <dl>   
          <dt>Package Name*</dt>
          <dd>      
             --typedown--
          </dd>     
        </dl>   
        <dl>   
          <dt>Identifier*</dt>
          <dd>      
             --typedown--
          </dd>     
        </dl>   
        <button class="btn btn-primary disabled">Create Package</button>
      </g:form>
    </div>
    <script language="JavaScript">
      $(document).ready(function() {
        var options = {
          // see http://stackoverflow.com/questions/9232748/twitter-bootstrap-typeahead-ajax-example
          // 'source':['one','two','three','four']
          'source': function (query, process) {
                      $.ajax({
                        url: '<g:createLink controller="ajax" action="orgs" />?query='+query,
                        success: function(data) {
                          process(data.options);
                        },
                        select: function(event, ui) {
                        }
                      });
                    }
        };

        $('#provider-typeahead').typeahead(options);

      });
    </script>
  </body>
</html>
