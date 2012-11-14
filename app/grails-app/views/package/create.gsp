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
             <input class="input-xxlarge" type="text" name="contentProviderName" id="provider-typeahead" onChange="update()"/>
          </dd>
        </dl>
        <dl>   
          <dt>Package Name*</dt>
          <dd>      
             <input class="input-xxlarge" type="text" name="packageName" id="packageName" onKeyUp="update()"/>
          </dd>     
        </dl>   
        <dl id="idcontrolgroup" class="control-group error">   
          <dt>Identifier*</dt>
          <dd>      
             <input class="input-xxlarge" type="text" name="identifier" id="packageIdentifier" onKeyUp="validateIdentifier()"/>
          </dd>     
        </dl>   
        <button id="addbtn" class="btn btn-primary disabled">Create Package</button>
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
                          // console.log("%o",data);
                          process(data.options);
                        },
                        select: function(event, ui) {
                        }
                      });
                    }
        };

        $('#provider-typeahead').typeahead(options);
      });

      function update() {
        $('#packageIdentifier').val($('#provider-typeahead').val()+':'+$('#packageName').val());
        validateIdentifier();
      }

      function validateIdentifier() {
        var prov = $('#provider-typeahead').val()
        var name = $('#packageName').val()
        var id = $('#packageIdentifier').val()

        var valid = false;
        if ( ( prov != '' ) && ( name != '' ) && ( id != '' ) ) {
          ajaxValidate(id)
        }
        else {
          $('#idcontrolgroup').removeClass('success');
          $('#idcontrolgroup').addClass('error');
          $('#addbtn').addClass('disabled');
          $('#addbtn').attr('disabled','disabled');
        }
      }

      function ajaxValidate(value) {
        var result=false;
        $.ajax({
          url: '<g:createLink controller="ajax" action="validatePackageId" />?id='+value,
          success: function(data) {
            console.log("%o",data);
            if ( data.response ) {
              $('#idcontrolgroup').removeClass('error');
              $('#idcontrolgroup').addClass('success');
              $('#addbtn').removeClass('disabled');
              $('#addbtn').removeAttr('disabled');
            }
            else {
              $('#idcontrolgroup').removeClass('success');
              $('#idcontrolgroup').addClass('error');
              $('#addbtn').addClass('disabled');
              $('#addbtn').attr('disabled','disabled');
            }
          },
        });
      }
    </script>
  </body>
</html>
