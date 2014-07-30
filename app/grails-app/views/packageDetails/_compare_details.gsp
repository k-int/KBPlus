<div class="modal hide" id="compare_details${pkgA.id}">

	<div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Further details</h3>
    </div>

    <div class="modal-body">
    	<label><b>Parameter</b></label>
    	<h6>Package A value | Package B value</h6>
		<label><b>Coverage Depth:</b></label> ${pkgA.coverageDepth} | ${pkgB.coverageDepth}
		<label><b>Embargo:</b></label> ${pkgA.embargo} | ${pkgB.embargo}

		<label><b>Platform Host URL:</b></label> ${pkgA.hostPlatformURL} | ${pkgB.hostPlatformURL}

		<label><b>Hybrid OA:</b></label> ${pkgA.hybridOA} | ${pkgB.hybridOA}
	</div>
</div>
