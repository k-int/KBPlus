<!doctype html>
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
  </g:javascript>
</html>
