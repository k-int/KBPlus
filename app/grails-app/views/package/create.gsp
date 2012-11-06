<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
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
             --typedown--
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
  </body>
</html>
