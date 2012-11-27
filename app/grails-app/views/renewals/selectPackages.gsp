<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Package Planning - Select Candidate Packages</title>
  </head>

  <body>
    <div class="container">
      This page current lists all packages with any title overlap for entitlements in the current subscription. <br/>
      Showing alternatives packages for current subscription ${subscriptionInstance}<br/>
      There are ${titles_in_this_sub} titles currently in this subscription<br/>
      This subscription is currently formed from the following packages : <ul><g:each in="${subscriptionInstance.packages}" var="p">
        <li>${p?.pkg?.name}</li>
      </g:each></ul>
      <table>
        <tr>
          <th>Content Provider</th>
          <th>Candidate Packages</th>
          <th>Titles in Package</th>
          <th># Matching Titles</th>
          <th>Platform</th>
          <th>Overlap</th>
          <th>Select</th>
        </tr>
        <g:each in="${candidates}" var="c">
          <tr>
            <td>${c.value.pkg.contentProvider?.name}</td>
            <td>${c.value.pkg.name}</td>
            <td>${c.value.pkg_title_count}</td>
            <td>${c.value.titlematch}</td>
            <td>${c.value.platform.name}</td>
            <td>${c.value.titlematch/titles_in_this_sub*100}</td>
            <td><input type="checkbox"/></td>
          </tr>
        </g:each>
      </table>
    </div>
  </body>
</html>
