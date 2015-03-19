
<div name="coreAssertionEdit" class="modal hide">
  <table>
    <thead>
      <th>Core Start Date</th>
      <th>Core End Date</th>
    </thead>
    <tbody>
       <g:each in="${coreDates}" var="coreDate">
          <tr>
            <td>
             <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${coreDate.startDate}"/>
              </td>
              <td>
             <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${coreDate.endDate}"/>
              </td>
          </tr>
       </g:each>
    </tbody>
  </table>
  <g:form controller="ajax" action="coreExtend">
    <input type="hidden" name="tipID" value="${tipID}"/>
     <dl>
      <dt><label class="control-label">Extend Core Dates:</label></dt>
      <dd>
          <label class="property-label">Core Start:</label> 
          <input class="span2 datepicker-class" size="16" type="text" 
              name="coreStartDate">
      </dd>
      <dd>
          <label class="property-label">Core End:</label> 
                   <input class="span2 datepicker-class" size="16" type="text" 
              name="coreEndDate">
      </dd>
      <input type="submit" value="Extend" class="btn btn-primary btn-small"/>
    </dl>
  </g:form>
</div>
