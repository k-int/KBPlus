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
        <g:if test="${flash.error}">
		    <bootstrap:alert class="alert alert-error">${flash.error}</bootstrap:alert>
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
                                              Now selct first package to compare (Filtered by dates above). Use '%' as
                                              wildcard.<br/>
                                              <input type="hidden" name="pkgA" id="packageSelectA" value="${pkgA}"/> 
					</td>
					<td> 
					    Restrict this list to packages starting after- <g:simpleHiddenValue id="startB" name="startB" type="date" value="${params.startB}"/>
							and/or ending before- <g:simpleHiddenValue id="endB" name="endB" type="date" value="${params.endB}"/><br/>
                                              Select second package to compare (Filtered by dates above). Use '%' as wildcard.<br/>
                                              <input type="hidden" name="pkgB" id="packageSelectB" value="${pkgB}" />
					</td>
				</tr>
				<tr>
					<td> Package On date</td>
					<td>
						<div class="input-append date">
							<input class="span2" size="16" type="text" 
							name="dateA" id="dateA" value="${params.dateA}">
							<span class="add-on"><i class="icon-th"></i></span> 
						</div>
					</td>
					<td> 
						<div class="input-append date">
							<input class="span2" size="16" type="text" 
							name="dateB" id="dateB" value="${params.dateB}">
							<span class="add-on"><i class="icon-th"></i></span> 
						</div>
					</td>
				</tr>
				<tr>
					<td> Add Filter</td>
					<td colspan="2">
        <input type="checkbox" name="insrt" value="Y" ${params.insrt=='Y'?'checked':''}/>  Insert&nbsp;
        <input type="checkbox" name="dlt" value="Y" ${params.dlt=='Y'?'checked':''}/> Delete &nbsp;
        <input type="checkbox" name="updt" value="Y" ${params.updt=='Y'?'checked':''}/> Update &nbsp;
        <input type="checkbox" name="nochng" value="Y" ${params.nochng=='Y'?'checked':''}/> No Change &nbsp;
					</td>		
				</tr>
			</tbody>
		</table>

		<input type="submit" class="btn btn-primary" value="Compare">
	</g:form>
</div>


<g:if test="${pkgInsts?.get(0) && pkgInsts?.get(1)}">

	<div class="row">
	<h3>Packages Compared</h3>
	<table class="table table-bordered">
		<thead>
			<tr>
				<th>Value</th>
				<th>${pkgInsts.get(0).name}</th>
				<th>${pkgInsts.get(1).name}</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>Date Created</td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(0).dateCreated}"/></td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(1).dateCreated}"/></td>
			</tr>
			<tr>
				<td>Start Date</td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(0).startDate}"/></td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(1).startDate}"/></td>
			</tr>
			<tr>
				<td>End Date</td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(0).endDate}"/></td>
				<td><g:formatDate format="yyyy-MM-dd" date="${pkgInsts.get(1).endDate}"/></td>
			</tr>
			<tr>
				<td>Number of TIPPs</td>
				<td>${params.countA}</td>
				<td>${params.countB}</td>
			</tr>
		</tbody>
	</table>
	</div>
<div class="row">
<g:form action="compare" method="get" class="form-inline">
	<input type="hidden" name="pkgA"value="${params.pkgA}"/>
	<input type="hidden" name="pkgB" value="${params.pkgB}"/>
	<input type="hidden" name="dateA" value="${params.dateA}"/>
	<input type="hidden" name="dateB" value="${params.dateB}"/>
	<input type="hidden" name="insrt" value="${params.insrt}"/>
	<input type="hidden" name="dlt" value="${params.dlt}"/>
	<input type="hidden" name="updt" value="${params.updt}"/>
	<input type="hidden" name="nochng" value="${params.nochng}"/>
	<input type="hidden" name="countA" value="${params.countA}"/>
	<input type="hidden" name="countB" value="${params.countB}"/>

	<table>
		<tr>
			<td>
				Filters - Title: <input type="text" name="filter" value="${params.filter}"/>
			</td>
			<td>
				Coverage Starts Before:
	<g:simpleHiddenValue id="startsBefore" name="startsBefore" type="date" value="${params.startsBefore}"/>
			</td>
			<td> <input type='button' class="btn btn-primary" id="resetFilters" value='Clear'/></td>
		</tr>
		<tr>
		<td>
			Coverage note: <input type="text" name="coverageNoteFilter" value="${params.coverageNoteFilter}"/>
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
<dt class="center">Showing Titles (${offset+1} to ${offset+ comparisonMap.size()}  of ${unionListSize})</dt>
</div>
<table class="table table-bordered">
	<thead>
		<tr> 
			<td> Title </td>
			<td> ${pkgInsts.get(0).name} on ${pkgDates.get(0)} </td>
			<td> ${pkgInsts.get(1).name} on ${pkgDates.get(1)} </td>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td><b>Total TIPPs for query</b></td>
			<td><b>${listACount}</b></td>
			<td><b>${listBCount}</b></td>
		<tr>
		<g:each in="${comparisonMap}" var="entry">
		<g:set var="pkgATipp" value="${entry.value[0]}"/>
		<g:set var="pkgBTipp" value="${entry.value[1]}"/>
		<g:set var="currentTitle" value="${pkgATipp?.title ?:pkgBTipp?.title}"/>
		<g:set var="highlight" value="${entry.value[2]}"/>

		<tr>
			
			<td>
			<b><g:link action="show" controller="titleDetails" id="${currentTitle.id}">${entry.key}</g:link></b> 
			<i onclick="showMore('${currentTitle.id}')"class="icon-info-sign"></i>

			<g:each in="${currentTitle.ids}" var="id">
                <br>${id.identifier.ns.ns}:${id.identifier.value}
            </g:each>
			</td>
			
			<g:if test="${pkgATipp}">		
				<td class="${highlight }"><g:render template="compare_cell" model="[obj:pkgATipp]"/></td>
			</g:if>
			<g:else><td></td></g:else>
			
			<g:if test="${pkgBTipp}">			
				<td class="${highlight }"><g:render template="compare_cell" model="[obj:pkgBTipp]"/></td>
			</g:if>
			<g:else><td></td></g:else>

		</tr>
			
		</g:each>
	</tbody>
</table>
<div class="pagination" style="text-align:center">

 <bootstrap:paginate  action="compare" controller="packageDetails" params="${params}" first="first" last="Last" maxsteps="${max}" total="${unionListSize}" />
</div>

</g:if>
</div>
%{-- Hiding the tables from compare_details inside the main table, breaks the modal hide.
 --}%
<g:each in="${comparisonMap}" var="entry">
		<g:set var="pkgATipp" value="${entry.value[0]}"/>
		<g:set var="pkgBTipp" value="${entry.value[1]}"/>
		<g:set var="currentTitle" value="${pkgATipp?.title ?:pkgBTipp?.title}"/>

		<g:render template="compare_details"
		 model="[pkgA:pkgATipp,pkgB:pkgBTipp,currentTitle:currentTitle, pkgAName:"${pkgInsts.get(0).name}",
		 pkgBName:"${pkgInsts.get(1).name}" ]"/>
</g:each>

<r:script language="JavaScript">
    function applySelect2(filter) {
      var pkgA = {id:'${pkgInsts?.get(0)?.id}',text:"${pkgInsts?.get(0)?.name}"};
      var pkgB = {id:'${pkgInsts?.get(1)?.id}',text:"${pkgInsts?.get(1)?.name}"};

      $("#packageSelect"+filter).select2({
      	width: "90%",
        placeholder: "Type package name...",
        minimumInputLength: 1,
        ajax: { 
            url: '<g:createLink controller='ajax' action='lookup'/>',
            dataType: 'json',
            data: function (term, page) {
                return {
                	hideIdent: 'true',
                	hasDate: 'true',
                	inclPkgStartDate: 'true',
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
        },
	    allowClear: true,
         formatSelection: function(data) { 
            return data.text; 
        },
        initSelection : function (element, callback) {
	        var obj
         	if(filter == "A"){
         		obj = pkgA;
         	}else{
         		obj = pkgB;
         	}
            callback(obj);
        }
        }).select2('val',':');
    }

	$("#resetFilters").click(function() {
	    $(this).closest('form').find("input[name=filter], input[type=coverageNoteFilter],input[type=coverageNoteFilter],input[name=startsBefore],input[name=endsAfter]").val("");
	    $(this).closest('form').submit();
	});

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
