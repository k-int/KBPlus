<table>
	<thead>
		<th>Coverage Start</th>
		<th>Coverage End</th>
	</thead>
	<tbody>
		<tr>
			<td style="white-space: nowrap">
			  Date:
			  <g:if test="${obj.startDate != null}">
			  	<g:formatDate format="yyyy-MM-dd" date="${obj.startDate}"/> <br/>
			  </g:if> <g:else>
			  	<g:formatDate format="yyyy-MM-dd" date="${obj.tipp.startDate}"/> <br/>
			  </g:else>
  			  Volume:
  			  <g:if test="${obj.startVolume != null}">
  			  	 ${obj.startVolume} <br/>
  			  </g:if> <g:else>
  			  	 ${obj.tipp.startVolume} <br/>
  			  </g:else>
			  Issue:
			  <g:if test="${obj.startIssue != null}">
			  	${obj.startIssue}
			  </g:if> <g:else>
			  	${obj.tipp.startIssue}
			  </g:else>
     
			</td>	

			<td style="white-space: nowrap"> 
				Date:
			  <g:if test="${obj.endDate != null}">
			  	<g:formatDate format="yyyy-MM-dd" date="${obj.endDate}"/> <br/>
			  </g:if> <g:else>
			  	<g:formatDate format="yyyy-MM-dd" date="${obj.tipp.endDate}"/> <br/>
			  </g:else>
			  Volume:
  			  <g:if test="${obj.endVolume != null}">
  			  	${obj.endVolume} <br/>
  			  </g:if> <g:else>
  			  	${obj.tipp.endVolume} <br/>
  			  </g:else>
  			  Issue:
			  <g:if test="${obj.endIssue != null}">
			  	${obj.endIssue} 
			  </g:if> <g:else>
			  	${obj.tipp.endIssue} 
			  </g:else>
	
			</td>
		</tr>
		<tr >
			<td colspan="2">coverageNote: 
			  <g:if test="${obj.endIssue != null}"> 
			  ${obj.coverageNote}</td>
			  </g:if> <g:else>
			  	${obj.tipp.coverageNote}</td>
			  </g:else>
			
		</tr>
	</tbody>
</table>
