<h1>Housekeeping email template...</h1>

<h2>The following possible duplicate titles have been identified</h2>
<table>
  <thead>
    <tr>
      <th>Title 1</th>
      <th>Title 2</th>
      <th>Score</th>
    </tr>
  <thead>
  <tbody>
    <% possibleDuplicates.each { pd -> %>
      <tr>
        <td>$pd[2]</td>
        <td>$pd[3]</td>
        <td>$pd[4]</td>
      </tr>
    <% } %>
  </tbody>
</table>


<h2>The following ${packagesInLastWeek.size()} packages have been added in the past week</h2>
<table>
  <thead>
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Date Added</th>
      <th>Last Updated</th>
    </tr>
  <thead>
  <tbody>
    <% packagesInLastWeek.each { pkg -> %>
      <tr>
        <td>$pkg.id</td>
        <td><a href="http://package/URL">$pkg.name<a></td>
        <td>$pkg.dateCreated</td>
        <td>$pkg.lastUpdated</td>
      </tr>
    <% } %>
  </tbody>
</table>
