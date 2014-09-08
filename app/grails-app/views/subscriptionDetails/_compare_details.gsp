
<div class="modal hide" id="compare_details${currentTitle.id}">
	<div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Further details</h3>
    </div>

    <div class="modal-body">
    	<table class="table table-bordered">
	    	<thead>
	    		<tr>
	    			<th>Attribute</th>
	    			<th> ${subAName} </th>
	    			<th> ${subBName} </th>
	    		</tr>
	    	</thead>
	    	<tbody>
	    		<tr>
	    			<td><b>Coverage Depth</b></td>
	    			<td>${subA?.coverageDepth} </td>
	    			<td>${subB?.coverageDepth} </td>
	    		</tr>
	    		<tr>
	    			<td><b>Embargo</b></td>
	    			<td>${subA?.embargo}</td>
	    			<td>${subB?.embargo}</td>
	    		</tr>
	    	</tbody>  		
    	</table>
	</div>
</div>
