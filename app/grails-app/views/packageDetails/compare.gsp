<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
  </head>
 <body>

<div class="container">
<div class="row">
	<h2>Package Comparison</h2>
	<br/>
      <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="index">All Packages</g:link><span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="compare">Compare</g:link></li>

        <li class="dropdown pull-right">
          <a class="dropdown-toggle" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>

          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
            <li><g:link action="compare" params="${params+[format:'csv']}">CSV Export</g:link></li>
            
          </ul>
        </li>

      </ul>
      
	    <g:if test="${flash.message}">
		    <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
	    </g:if>

	<g:form action="compare" controller="packageDetails" method="GET">
		<table class="table table-bordered">
			<thead>
				<tr>
					<th></th>
					<th>Package A</th>
					<th>Package B</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Package name</td>
					<td>Restrict this list to packages starting after- <g:simpleHiddenValue id="startA" name="startA" type="date" value="${params.startA}"/>
							and/or ending before- <g:simpleHiddenValue id="endA" name="endA" type="date" value="${params.endA}"/><br/>
                                              Now selct first package to compare (Filtered by dates above)<br/>
                                              <input type="hidden" name="pkgA" id="packageSelectA" value="${pkgA}"/> 
					</td>
					<td> 
					    Restrict this list to packages starting after- <g:simpleHiddenValue id="startB" name="startB" type="date" value="${params.startB}"/>
							and/or ending before- <g:simpleHiddenValue id="endB" name="endB" type="date" value="${params.endB}"/><br/>
                                              Select second package to compare (Filtered by dates above)<br/>
                                              <input type="hidden" name="pkgB" id="packageSelectB" value="${pkgB}" />
					</td>
				</tr>
				<tr>
					<td> Package On date</td>
					<td>
						<div class="input-append date">
							<input class="span2" size="16" type="text" 
							name="dateA" id="dateA" value="${dateA}">
							<span class="add-on"><i class="icon-th"></i></span> 
						</div>
					</td>
					<td> 
						<div class="input-append date">
							<input class="span2" size="16" type="text" 
							name="dateB" id="dateB" value="${dateB}">
							<span class="add-on"><i class="icon-th"></i></span> 
						</div>
					</td>
				</tr>
			</tbody>
		</table>

		<input type="submit" class="btn btn-primary" value="Compare">
	</g:form>
</div>


<g:if test="${pkgInsts?.get(0) && pkgInsts?.get(1)}">
<div class="row">
	<h3>Comparing '${pkgInsts.get(0).name}'(A) and <br/>'${pkgInsts.get(1).name}'(B)</h3>
       <br/>

<g:form action="compare" method="get" class="form-inline">
	<input type="hidden" name="pkgA"value="${pkgA}"/>
	<input type="hidden" name="pkgB" value="${pkgB}"/>
	<input type="hidden" name="dateA" value="${dateA}"/>
	<input type="hidden" name="dateB" value="${dateB}"/>
	<table>
		<tr>
			<td>
				Filters - Title: <input name="filter" value="${params.filter}"/>
			</td>
			<td>
				Coverage Starts Before:
	<g:simpleHiddenValue id="startsBefore" name="startsBefore" type="date" value="${params.startsBefore}"/>
			</td>
		</tr>
		<tr>
		<td>
			Coverage note: <input name="coverageNoteFilter" value="${params.coverageNoteFilter}"/>
		</td>
		<td>
			Coverage Ends After:
			<g:simpleHiddenValue id="endsAfter" name="endsAfter" type="date" value="${params.endsAfter}"/>
		</td>
		<td> <input type="submit" class="btn btn-primary" value="Filter Results" /> </td>
		</tr>
	</table>
	
</g:form>


<div class="span6 offset3">
<dt class="center">Showing Titles (${offset+1} to ${offset+ unionList.size()}  of ${unionListSize})</dt>
</div>
<table class="table table-bordered">
	<thead>
		<tr> 
			<th> Title </th>
			<th> ${pkgInsts.get(0).name} on ${pkgDates.get(0)} </th>
			<th> ${pkgInsts.get(1).name} on ${pkgDates.get(1)} </th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${unionList}" var="unionTitle">
			<g:set var="pkgATipp" value="${listA.find {it.title.title.equals(unionTitle)}}"/>
			<g:set var="pkgBTipp" value="${listB.find {it.title.title.equals(unionTitle)}}"/>
			<g:set var="currentTitle" value="${pkgATipp?.title ?:pkgBTipp?.title}"/>
			<tr>
				
				<td>
				<b><g:link action="show" controller="titleDetails" id="${currentTitle.id}">${unionTitle}</g:link></b> 
				<i onclick="showMore('${currentTitle.id}')"class="icon-info-sign"></i>

				<g:each in="${currentTitle.ids}" var="id">
                    <br>${id.identifier.ns.ns}:${id.identifier.value}
                </g:each>
				</td>
			
				<g:if test="${pkgATipp}">
					<g:if test="${pkgBTipp}">
						<g:if test="${pkgATipp?.compareTo(pkgBTipp) == 1}">
					 		<td class="warning">
				 		</g:if>
				 		<g:else>
				 			<td>
				 		</g:else>
						<g:render template="compare_cell" model="[obj:pkgATipp]"/>
						</td>
					</g:if>
					<g:else>
						<td class="danger">
							<g:render template="compare_cell" model="[obj:pkgATipp]"/>
	
						</td>
					</g:else>
				</g:if>
				<g:else><td></td></g:else>
				
			
				<g:if test="${pkgBTipp}">
					<g:if test="${pkgATipp}">
						<g:if test="${pkgBTipp?.compareTo(pkgATipp) == 1}">
					 		<td class="warning">
				 		</g:if>
				 		<g:else>
				 			<td>
				 		</g:else>
						<g:render template="compare_cell" model="[obj:pkgBTipp]"/>

						</td>
					</g:if>
					<g:else>
						<td class="success">
						<g:render template="compare_cell" model="[obj:pkgBTipp]"/>
						</td>
					</g:else>
				</g:if>
				
				<g:else><td></td></g:else>
				
			</tr>
			
		</g:each>
	</tbody>
</table>
<div class="paginateButtons" style="text-align:center">
	<g:paginate controller="packageDetails" params="${params}"
		action="compare" max="${max}"total="${unionListSize}" />
</div>

</g:if>
</div>
%{-- Hiding the tables from compare_details inside the main table, breaks the modal hide.
 --}%
 <g:each in="${unionList}" var="unionTitle">
		<g:set var="pkgATipp" value="${listA.find {it.title.title.equals(unionTitle)}}"/>
		<g:set var="pkgBTipp" value="${listB.find {it.title.title.equals(unionTitle)}}"/>
		<g:set var="currentTitle" value="${pkgATipp?.title ?:pkgBTipp?.title}"/>

		<g:render template="compare_details"
		 model="[pkgA:pkgATipp,pkgB:pkgBTipp,currentTitle:currentTitle, pkgAName:"${pkgInsts.get(0).name}",
		 pkgBName:"${pkgInsts.get(1).name}" ]"/>
</g:each>

<r:script language="JavaScript">
    function applySelect2(filter) {
      $("#packageSelect"+filter).select2({
      	width: "element",
        placeholder: "Type package name...",
        minimumInputLength: 1,
        ajax: { 
            url: '<g:createLink controller='ajax' action='lookup'/>',
            dataType: 'json',
            data: function (term, page) {
                return {
                	hideIdent: 'true',
                	hasDate: 'true',
                	startDate: $("#start"+filter).val(),
                	endDate: $("#end"+filter).val(),
                    q: term , // search term
                    page_limit: 10,
                    baseClass:'com.k_int.kbplus.Package'
                };
            },
            results: function (data, page) {
                return {results: data.values};
            }
        }
	    });
    }

    function showMore(ident) {
		$("#compare_details"+ident).modal('show')
    }

    $(function(){
    	applySelect2("A")
     	applySelect2("B")
    });

    $('#dateA').datepicker({
    	format:"yyyy-mm-dd"
    });
    $('#dateB').datepicker({
    	format:"yyyy-mm-dd"
    });

</r:script>

  </body>
</html>
