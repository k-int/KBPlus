<div class="control-group ">
	<label class="control-label" for="orgType">Org Type</label>
	<div class="controls">
		 <g:set value="${com.k_int.kbplus.RefdataCategory.findByDesc('OrgType')}" var="orgtypecat"/>
		 <g:set value="${com.k_int.kbplus.RefdataValue.findAllByOwner(orgtypecat)}" var="refvalues"/>
	   	 <g:select from="${refvalues}" optionKey="id" name="orgType" />
	</div>
</div>
