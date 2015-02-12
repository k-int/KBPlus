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
              <input type="text" name="invoiceNumberFilter" class="input-medium"/>
            </th>
            <th>Order#<br/>
              <input type="text" name="orderNumberFilter" class="input-medium"/>
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
            <th>Date</th>
            <th>Amount</th>
            <th>Reference</th>
            <th colspan="5">Description</th>
          </tr>
        </thead>
        <tbody>
          <g:if test="${1==1}">
            <tr><td colspan="6" style="text-align:center">&nbsp;<br/>No Cost Items Found<br/>&nbsp;</td></tr>
          </g:if>
          <g:else>
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
          </g:else>
        </tbody>
        <tfoot>
          <tr>
            <td rowspan="2" style="vertical-align: top;">Add new <br/>cost item</td>
            <td><input type="text" name="newInvoiceNumber" class="input-medium" placeholder="New item invoice #"/></td>
            <td><input type="text" name="newOrderNumber" class="input-medium" placeholder="New Order #"/></td>
            <td>
              <select name="newSubscription" class="input-medium">
                <option value="all">All</option>
                <g:each in="${institutionSubscriptions}" var="s">
                  <option value="${s.id}">${s.name}</option>
                </g:each>
              </select>
            </td>
            <td>
              <select name="newPackage" class="input-medium">
                <option value="all">All</option>
              </select>
            </td>
            <td>
              <select name="newIe" class="input-medium">
                <option value="all">All</option>
              </select>
            </td>
          </tr>
          <tr>
            <td><input type="date" name="newDate"/></td>
            <td><input type="number" name="newValue" placeholder="New Cost"/></td>
            <td><input type="text" name="newReference" placeholder="New Item Reference"/></td>
            <td colspan="5"><input type="text" name="newDescription" placeholder="New Item Description"/></td>
          </tr>

        </tfoot>
      </table>

    </div>
  </body>

  <r:script type="text/javascript">
    function filtersUpdated() {
    }
  </r:script>
</html>
