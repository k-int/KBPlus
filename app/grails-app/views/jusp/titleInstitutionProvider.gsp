<table id="kb_tip_table">
    <thead>
      <tr>
        <td>TIP ID</td>
        <td>Core Date ID</td>
        <td>Start Date</td>
        <td>End Date</td>
      </tr>
    </thead>
    <tbody>
      <g:each in="${data}" var="tipmap">
      	<g:each in="${tipmap.value}" var="coreDate">
        <tr>
          <td>${coreDate.tiinp.id}</td>
          <td>${coreDate.id}</td>
          <td><g:formatDate format="yyyy-MM-dd" date="${coreDate.startDate}"/></td>
          <td><g:formatDate format="yyyy-MM-dd" date="${coreDate.endDate}"/></td>
        </tr>
        </g:each>
      </g:each>
    </tbody>
  </table>
