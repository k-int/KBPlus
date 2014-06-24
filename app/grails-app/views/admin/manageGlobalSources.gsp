<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Manage Global Sorces</title>
  </head>

  <body>

    <div class="container">
        <ul class="breadcrumb">
           <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
           <li>Global Sources</li>
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


    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span8">
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>Identifier</td>
                <td>Name</td>
                <td>Type</td>
                <td>Up To</td>
                <td>URL</td>
                <td>List Prefix</td>
                <td>Full Prefix</td>
                <td>Principal</td>
                <td>Credentials</td>
                <td>RecType</td>
              </tr>
            </thead>
            <tbody>
              <g:each in="${sources}" var="source">
                <tr>
                  <td>${source.identifier}</td>
                  <td>${source.name}</td>
                  <td>${source.type}</td>
                  <td>${source.haveUpTo}</td>
                  <td>${source.uri}</td>
                  <td>${source.listPrefix}</td>
                  <td>${source.fullPrefix}</td>
                  <td>${source.principal}</td>
                  <td>${source.credentials}</td>
                  <td>${source.rectype}</td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="span4">
          <g:form action="newGlobalSource">
            <dl>
              <dt>Global Source Identifier</dt>
              <dd><input type="text" name="identifier" placeholder="eg GOKbLive"/></dd>
              <dt>GLobal Source Name</dt>
              <dd><input type="text" name="name" placeholder="eg GOKb Live Server"/></dd>
              <dt>GLobal Source Type</dt>
              <dd><select name="type"><option value="OAI">GOKb OAI Source</option></select>
              <dt>Global Source URI</dt>
              <dd><input type="text" name="uri" placeholder="eg https://gokb.kuali.org/gokb/oai/packages" value="https://some.host/gokb/oai/packages"/></dd>
              <dt>List Records Prefix</dt>
              <dd><input type="text" name="listPrefix" placeholder="oai_dc" value="oai_dc"/></dd>
              <dt>Full Record Prefix</dt>
              <dd><input type="text" name="fullPrefix" placeholder="gokb" value="gokb"/></dd>
              <dt>Principal (Username)</dt>
              <dd><input type="text" name="principal" placeholder=""/></dd>
              <dt>Credentials (Password)</dt>
              <dd><input type="text" name="credentials" placeholder=""/></dd>
              <input type="hidden" name="rectype" value="0"/>
            </dl>
            <input type="submit" class="btn btn-primary"/>
          </g:form>
        </div>
      </div>
    </div>
  </body>
</html>
