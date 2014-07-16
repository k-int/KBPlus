
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

<h2>Package Comparison</h2>

    <g:if test="${flash.message}">
	    <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </g:if>

<g:form action="compare" controller="packageDetails">
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
				<td>	<input type="hidden" name="packageA" id="packageASelect" /></td>
				<td>	<input type="hidden" name="packageB" id="packageBSelect"/> </td>
			</tr>
			<tr>
				<td> Package On date</td>
				<td> <div class="input-append date">
					<input class="span2" size="16" type="text" name="packageADate" id="packageADate">
					<span class="add-on"><i class="icon-th"></i></span> 
					</div>
				</td>
				<td> <div class="input-append date">
					<input class="span2" size="16" type="text" name="packageBDate" id="packageBDate">
					<span class="add-on"><i class="icon-th"></i></span>
					</div>
				</td>
			</tr>
		</tbody>
	</table>


	<input type="submit" class="btn btn-primary" value="Compare">
</g:form>

<set var="pkgA" value="${pkgA}"/>
<set var="pkgB" value="${pkgB}"/>
<set var="dateA" value="${dateA}"/>
<set var="dateB" value="${dateB}"/>

<g:if test="${pkgInsts?.get(0) && pkgInsts?.get(1)}">
	<h3>Comparing '${pkgInsts.get(0).name}'(1) and <br/>'${pkgInsts.get(1).name}'(2)</h3>

<table class="table table-bordered">
	<thead>
		<tr> 
			<th> Title </th>
			<th> On ${pkgDates.get(0)} (1)</th>
			<th> On ${pkgDates.get(1)} (2)</th>

		</tr>
	</thead>
	<tbody>
		<g:each in="${unionList}" var="item">
			<tr>
				<td>"${item.title.title}"</td>
				
				<g:if test="${listA.contains(item)}">
					<g:if test="${listB.contains(item)}">
						<td>Both have it</td>
					</g:if>
					<g:else><td class="danger">Got it but removed</td></g:else>
				</g:if>
				<g:else><td></td></g:else>
				
			
				<g:if test="${listB.contains(item)}">
					<g:if test="${listA.contains(item)}">
						<td>Both have it</td>
					</g:if>
					<g:else>
						<td class="success"> Got it</td>
					</g:else>
				</g:if>
				
				<g:else><td></td></g:else>
				
			</tr>
		</g:each>
	</tbody>
</table>
<div class="paginateButtons" style="text-align:center">
	<g:paginate controller="packageDetails" params="[pkgA:pkgA, pkgB:pkgB, dateA: dateA, dateB:dateB]"
		action="compare" max="${max}"total="${unionList.size()}" />
</div>
</g:if>

</div>
   
<r:script language="JavaScript">
    function applySelect2(element) {
      $(element).select2({
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

    $('#packageADate').datepicker({
    	format:"dd/mm/yyyy"
    });
    $('#packageBDate').datepicker({
    	format:"dd/mm/yyyy"
    });

</r:script>

  </body>
</html>
