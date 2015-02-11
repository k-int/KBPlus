<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ ${institution.name} :: Financial Information</title>
  </head>

  <body>

    <div class="container">
      <ul class="breadcrumb">
        <li> <g:link controller="home" action="index">Home</g:link> <span class="divider">/</span> </li>
        <li> <g:link controller="myInstitutions" action="finance" params="${[shortcode:params.shortcode]}">${institution.name} Current Subscriptions</g:link> </li>
      </ul>
    </div>

    <div class="container">
      <h1>${institution.name} Cost Items</h1>
      <table class="table table-striped table-bordered table-condensed table-tworow">
        <thead>
          <tr>
            <th rowspan="2" style="vertical-align: top;">Cost Item#</th>
            <th>Invoice#<br/>
              <input type="text" name="invoiceNumber" class="input-medium"/>
            </th>
            <th>Order#<br/>
              <input type="text" name="orderNumber" class="input-medium"/>
            </th>
            <th>Subscription#<br/>
              <select name="subscriptionFilter" class="input-medium">
                <option value="all">All</option>
                <g:each in="${institutionSubscriptions}" var="s">
                  <option value="${s.id}">${s.name}</option>
                </g:each>
              </select>
            </th>
            <th>Package#<br/>
              <select name="packageFilter" class="input-medium">
                <option value="all">All</option>
              </select>
            </th>
            <th>IE#<br/>
              <select name="ieFilter" class="input-medium">
                <option value="all">All</option>
              </select>
            </th>
          </tr>
          <tr>
            <th>Date<br/>xxxx</th>
            <th>Amount<br/>xxxx</th>
            <th>Reference<br/>xxxx</th>
            <th colspan="5">Description</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td rowspan="2"></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
          <tr>
            <td></td>
            <td></td>
            <td></td>
            <td colspan="2"></td>
          </tr>
        </tbody>
      </table>

      <h3>Add cost item</h3>

    </div>
  </body>
</html>
