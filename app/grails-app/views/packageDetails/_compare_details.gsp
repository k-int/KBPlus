
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
	    			<th> ${pkgAName} </th>
	    			<th> ${pkgBName} </th>
	    		</tr>
	    	</thead>
	    	<tbody>
	    		<tr>
	    			<td><b>Coverage Depth</b></td>
	    			<td>${pkgA?.coverageDepth} </td>
	    			<td>${pkgB?.coverageDepth} </td>
	    		</tr>
	    		<tr>
	    			<td><b>Embargo</b></td>
	    			<td>${pkgA?.embargo}</td>
	    			<td>${pkgB?.embargo}</td>
	    		</tr>
	    		<tr>
	    			<td><b>Platform Host URL</b></td>
	    			<td>${pkgA?.hostPlatformURL}</td>
	    			<td>${pkgB?.hostPlatformURL}</td>
	    		</tr>
	    		<tr>
	    			<td><b>Hybrid OA</b></td>
	    			<td>${pkgA?.hybridOA}</td>
	    			<td>${pkgB?.hybridOA}</td>
	    		</tr>	    		
	    	</tbody>  		
    	</table>
	</div>
</div>
