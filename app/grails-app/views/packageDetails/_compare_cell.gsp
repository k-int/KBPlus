<table>
	<thead>
		<th>Coverage Start</th>
		<th>Coverage End</th>
	</thead>
	<tbody>
		<tr>
			<td style="white-space: nowrap">
			  Date: <g:formatDate format="yyyy-MM-dd" date="${obj.startDate}"/> <br/>
			  Volume: ${obj.startVolume} <br/>
			  Issue: ${obj.startIssue}      
			</td>	

			<td style="white-space: nowrap"> 
			   Date: <g:formatDate format="yyyy-MM-dd" date="${obj.endDate}"/> <br/>
			   Volume: ${obj.endVolume} <br/>
			   Issue: ${obj.endIssue} 
			</td>
		</tr>
		<tr >
			<td colspan="2">coverageNote: ${obj.coverageNote}</td>
		</tr>
	</tbody>
</table>
