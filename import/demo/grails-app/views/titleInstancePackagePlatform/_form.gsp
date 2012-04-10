<%@ page import="com.k_int.kbplus.TitleInstancePackagePlatform" %>



<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'startDate', 'error')} ">
	<label for="startDate">
		<g:message code="titleInstancePackagePlatform.startDate.label" default="Start Date" />
		
	</label>
	<g:textField name="startDate" value="${titleInstancePackagePlatformInstance?.startDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'startVolume', 'error')} ">
	<label for="startVolume">
		<g:message code="titleInstancePackagePlatform.startVolume.label" default="Start Volume" />
		
	</label>
	<g:textField name="startVolume" value="${titleInstancePackagePlatformInstance?.startVolume}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'startIssue', 'error')} ">
	<label for="startIssue">
		<g:message code="titleInstancePackagePlatform.startIssue.label" default="Start Issue" />
		
	</label>
	<g:textField name="startIssue" value="${titleInstancePackagePlatformInstance?.startIssue}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'endDate', 'error')} ">
	<label for="endDate">
		<g:message code="titleInstancePackagePlatform.endDate.label" default="End Date" />
		
	</label>
	<g:textField name="endDate" value="${titleInstancePackagePlatformInstance?.endDate}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'endVolume', 'error')} ">
	<label for="endVolume">
		<g:message code="titleInstancePackagePlatform.endVolume.label" default="End Volume" />
		
	</label>
	<g:textField name="endVolume" value="${titleInstancePackagePlatformInstance?.endVolume}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'endIssue', 'error')} ">
	<label for="endIssue">
		<g:message code="titleInstancePackagePlatform.endIssue.label" default="End Issue" />
		
	</label>
	<g:textField name="endIssue" value="${titleInstancePackagePlatformInstance?.endIssue}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'embargo', 'error')} ">
	<label for="embargo">
		<g:message code="titleInstancePackagePlatform.embargo.label" default="Embargo" />
		
	</label>
	<g:textField name="embargo" value="${titleInstancePackagePlatformInstance?.embargo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'coverageDepth', 'error')} ">
	<label for="coverageDepth">
		<g:message code="titleInstancePackagePlatform.coverageDepth.label" default="Coverage Depth" />
		
	</label>
	<g:textField name="coverageDepth" value="${titleInstancePackagePlatformInstance?.coverageDepth}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'coverageNote', 'error')} ">
	<label for="coverageNote">
		<g:message code="titleInstancePackagePlatform.coverageNote.label" default="Coverage Note" />
		
	</label>
	<g:textField name="coverageNote" value="${titleInstancePackagePlatformInstance?.coverageNote}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'pkg', 'error')} required">
	<label for="pkg">
		<g:message code="titleInstancePackagePlatform.pkg.label" default="Pkg" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="pkg" name="pkg.id" from="${com.k_int.kbplus.Package.list()}" optionKey="id" required="" value="${titleInstancePackagePlatformInstance?.pkg?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'platform', 'error')} required">
	<label for="platform">
		<g:message code="titleInstancePackagePlatform.platform.label" default="Platform" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="platform" name="platform.id" from="${com.k_int.kbplus.Platform.list()}" optionKey="id" required="" value="${titleInstancePackagePlatformInstance?.platform?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: titleInstancePackagePlatformInstance, field: 'title', 'error')} required">
	<label for="title">
		<g:message code="titleInstancePackagePlatform.title.label" default="Title" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="title" name="title.id" from="${com.k_int.kbplus.TitleInstance.list()}" optionKey="id" required="" value="${titleInstancePackagePlatformInstance?.title?.id}" class="many-to-one"/>
</div>

