
  <table class="table table-bordered table-striped">
    <thead>
    	<th> Property</th>
    	<g:each in="${licences}" var="licence">
    	<th>${licence.reference}</th>
    	</g:each>
    </thead>
    <tbody>
    <g:each in="${map}" var="entry">
    <tr>
    <th>${entry.getKey()}</th>
   	<g:each in="${licences}" var="point">

   		<g:if test="${entry.getValue().containsKey(point.reference)}">
   			<td>${entry.getValue().get(point.reference).getValue()}</td>
   		</g:if>
   		<g:else>
   			<td>No information</td>
   		</g:else>
   	</g:each>
    </tr>
    </g:each>
    </tbody>
   </table>