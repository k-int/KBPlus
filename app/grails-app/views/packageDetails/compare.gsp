
<%@ page import="com.k_int.kbplus.Package" %>
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
				<td> <input type="hidden" name="pkgA" id="packageASelect" value="${pkgA}"/> </td>
				<td> <input type="hidden" name="pkgB" id="packageBSelect" value="${pkgB}" /> </td>
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
			<th> On ${pkgDates.get(0)} (A)</th>
			<th> On ${pkgDates.get(1)} (B)</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${unionList}" var="unionTitle">
			<tr>
				<td><b>${unionTitle}</b></td>
				<g:set var="pkgATipp" value="${listA.find {it.title.title.equals(unionTitle)}}"/>
				<g:set var="pkgBTipp" value="${listB.find {it.title.title.equals(unionTitle)}}"/>
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

				<g:if test="${pkgATipp?.coverageNote}">
					<tr>
						<td colspan="6"><p/>coverageNote (A): ${pkgATipp.coverageNote}</td>
					</tr>						
				</g:if>
				<g:if test="${pkgBTipp?.coverageNote}">
					<tr>
						<td colspan="6"><p/>coverageNote (B): ${pkgBTipp.coverageNote}</td>
					</tr>
				</g:if>				
			</tr>
		</g:each>
	</tbody>
</table>
<div class="paginateButtons" style="text-align:center">
	<g:paginate controller="packageDetails" params="[pkgA:pkgA, pkgB:pkgB, dateA: dateA, dateB:dateB]"
		action="compare" max="${max}"total="${unionListSize}" />
</div>

</div>
</g:if>

</div>
   
<r:script language="JavaScript">
    function applySelect2(element) {
      $(element).select2({
      	width: "resolve",
        placeholder: "Type package name...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
            url: '<g:createLink controller='ajax' action='lookup'/>',
            dataType: 'json',
            data: function (term, page) {
                return {
                    q: term, // search term
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
    $(function(){
    	applySelect2("#packageASelect")
     	applySelect2("#packageBSelect")
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
