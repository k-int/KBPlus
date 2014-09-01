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
        <div class="span9">
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
                <td># Local Copies</td>
                <td>Actions</td>
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
                  <td>${source.rectype==0?'Package':'Title'}</td>
                  <td>${source.getNumberLocalPackages()}</td>
                  <td>
                    <g:link class="btn btn-default" 
                            controller="admin" 
                            onclick="return confirm('Deleting this package will remove all tracking info and unlink any local packages - Are you sure?')"
                            action="deleteGlobalSource" 
                            id="${source.id}">Delete</g:link>
                  </td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="span3">
          <g:form action="newGlobalSource">
            <dl>
              <dt>Global Source Identifier</dt>
              <dd><input type="text" name="identifier" placeholder="eg GOKbLive"/></dd>
              <dt>GLobal Source Name</dt>
              <dd><input type="text" name="name" placeholder="eg GOKb Live Server"/></dd>
              <dt>GLobal Source Type</dt>
              <dd><select name="type"><option value="OAI">GOKb OAI Source</option></select>
              <dt>Record Type</dt>
              <dd><select name="rectype">
                    <option value="0">Package</option>
                    <option value="1">Title</option>
                   </select>
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
            </dl>
            <input type="submit" class="btn btn-primary"/>
          </g:form>
        </div>
      </div>
    </div>
  </body>
</html>
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
                <td># Local Copies</td>
                <td>Actions</td>
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
                  <td>${source.rectype==0?'Package':'Title'}</td>
                  <td>${source.getNumberLocalPackages()}</td>
                  <td>
                    <g:link class="btn btn-default" 
                            controller="admin" 
                            onclick="return confirm('Deleting this package will remove all tracking info and unlink any local packages - Are you sure?')"
                            action="deleteGlobalSource" 
                            id="${source.id}">Delete</g:link>
                  </td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div class="span3" style="border:1px solid black;">
          <g:form action="newGlobalSource">
            <dl>
              <dt>Global Source Identifier</dt>
              <dd><input type="text" name="identifier" placeholder="eg GOKbLive"/></dd>
              <dt>GLobal Source Name</dt>
              <dd><input type="text" name="name" placeholder="eg GOKb Live Server"/></dd>
              <dt>GLobal Source Type</dt>
              <dd><select name="type"><option value="OAI">GOKb OAI Source</option></select>
              <dt>Record Type</dt>
              <dd><select name="rectype">
                    <option value="0">Package</option>
                    <option value="1">Title</option>
                   </select>
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
            </dl>
            <input type="submit" class="btn btn-primary"/>
          </g:form>
        </div>
      </div>
    </div>
  </body>
</html>
