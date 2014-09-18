<%@ page import ="com.k_int.kbplus.Subscription" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="mmbootstrap">
		<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}"/>
		<title><g:message code="default.edit.label" args="[entityName]"/></title>
	</head>

	<body>
		<div class="container">
			<div class="row">
				<h2> Subscription Comparison </h2>

				<br/>
			      <ul class="breadcrumb">
			        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span>
			        <li><g:link controller="subscriptionDetails" action="compare">Compare Subscriptions</g:link></li>

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

				<g:form action="compare" controller="subscriptionDetails" method="GET">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th></th>
								<th> Subscription A </th>
								<th> Subscription B </th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Subscription name </td>
					<td>Restrict this list to subscriptions starting after- 
					<g:simpleHiddenValue id="startA" name="startA" type="date" value="${params.startA}"/>
					and/or ending before- <g:simpleHiddenValue id="endA" name="endA" type="date" value="${params.endA}"/><br/> Now selct first subscription to compare (Filtered by dates above)<br/>
                      <input type="hidden" name="subA" id="subSelectA" value="${subA}"/> 
					</td>
					<td> 
					    Restrict this list to subscriptions starting after- 
					    <g:simpleHiddenValue id="startB" name="startB" type="date" value="${params.startB}"/>
				and/or ending before- <g:simpleHiddenValue id="endB" name="endB" type="date" value="${params.endB}"/><br/>
                          Select second subscription to compare (Filtered by dates above)<br/>
	                      <input type="hidden" name="subB" id="subSelectB" value="${subB}" />
					</td>
							</tr>
							<tr>
								<td> Subscriptions on Date</td>
								<td>
									<div class="input-append date">
										<input class="span2" size="16" type="text" name="dateA" id="dateA" value="${dateA}"/>
										<span class="add-on"><i class="icon-th"></i></span>
									</div>
								</td>
								<td>
									<div class="input-append date">
										<input class="spann2" size="16" type="text" name="dateB" id="dateB" value="${dateB}"/>
										<span class="add-on"><i class="icon-th"></i></span>
									</div>
								</td>
							</tr>
						</tbody>
					</table>	
					<input type="submit"class="btn btn-primary" value="Compare"/>			
				</g:form>
			</div>

			<g:if test="${subInsts?.get(0) && subInsts?.get(1)}">
				<div class="row">
				<g:form action="compare" method="GET" class="form-inline">
					<input type="hidden" name="subA" value="${subA}"/>
					<input type="hidden" name="subB" value="${subB}"/>
					<input type="hidden" name="dateA" value="${dateA}"/>
					<input type="hidden" name="dateB" value="${dateB}"/>

					<table>
						<tr>
							<td>
								Filters - Title: <input name="filter" value="${params.filter}">
							</td>
							<td> <input type="submit" class="btn btn-primary" value="Filter Results" /> </td>
						</tr>
					</table>
				</g:form>

				<div class="span6 offset3">
				<dt class="center">Showing Titles ${offset+1} to ${offset+unionList.size()} of ${unionListSize}</dt>
				</div>
				<table class="table table-bordered">
					<thead>
						<tr>
							<th> Title </th>
							<th> ${subInsts.get(0).name} on ${subDates.get(0)}</th>
							<th> ${subInsts.get(1).name} on ${subDates.get(1)}</th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${unionList}" var="unionTitle">
							<g:set var="subAIE" value="${listA.find {it.tipp.title.title.equals(unionTitle)}}"/>
							<g:set var="subBIE" value="${listB.find {it.tipp.title.title.equals(unionTitle)}}"/>
							<g:set var="currentTitle" value="${subAIE?.tipp?.title ?:subBIE?.tipp?.title}"/>
							<tr>
								
								<td>
								<b><g:link action="show" controller="titleDetails" id="${currentTitle.id}">${unionTitle}</g:link></b> 
								<i onclick="showMore('${currentTitle.id}')"class="icon-info-sign"></i>

								<g:each in="${currentTitle.ids}" var="id">
				                    <br>${id.identifier.ns.ns}:${id.identifier.value}
				                </g:each>
								</td>
							
								<g:if test="${subAIE}">
									<g:if test="${subBIE}">
										<g:if test="${subAIE?.hasChanged(subBIE)}">
									 		<td class="warning">
								 		</g:if>
								 		<g:else>
								 			<td>
								 		</g:else>
										<g:render template="compare_cell" model="[obj:subAIE]"/>
										</td>
									</g:if>
									<g:else>
										<td class="danger">
											<g:render template="compare_cell" model="[obj:subAIE]"/>
					
										</td>
									</g:else>
								</g:if>
								<g:else><td></td></g:else>
								
							
								<g:if test="${subBIE}">
									<g:if test="${subAIE}">
										<g:if test="${subBIE?.hasChanged(subAIE)}">
									 		<td class="warning">
								 		</g:if>
								 		<g:else>
								 			<td>
								 		</g:else>
										<g:render template="compare_cell" model="[obj:subBIE]"/>

										</td>
									</g:if>
									<g:else>
										<td class="success">
										<g:render template="compare_cell" model="[obj:subBIE]"/>
										</td>
									</g:else>
								</g:if>
								
								<g:else><td></td></g:else>
								
							</tr>
							
						</g:each>						
					</tbody>
				</table>
				<div class="paginateButtons" style="text-align:center">
					<g:paginate controller="subscriptionDetails" params="${params}"
						action="compare" max="${max}"total="${unionListSize}" />
				</div>	
				</div>
			</g:if>
		</div>
		%{-- Hiding the tables from compare_details inside the main table, breaks the modal hide.
 --}%
 <g:each in="${unionList}" var="unionTitle">
		<g:set var="subAIE" value="${listA.find {it.tipp.title.title.equals(unionTitle)}}"/>
		<g:set var="subBIE" value="${listB.find {it.tipp.title.title.equals(unionTitle)}}"/>
		<g:set var="currentTitle" value="${subAIE?.tipp?.title ?:subBIE?.tipp?.title}"/>

		<g:render template="compare_details"
		 model="[subA:subAIE,subB:subBIE,currentTitle:currentTitle, subAName:"${subInsts.get(0).name}",
		 subBName:"${subInsts.get(1).name}" ]"/>
</g:each>
<r:script language="JavaScript">
    function applySelect2(filter) {
      $("#subSelect"+filter).select2({
        width: '90%',
        placeholder: "Type package name...",
        minimumInputLength: 1,
        ajax: { 
            url: '<g:createLink controller='ajax' action='lookup'/>',
            dataType: 'json',
            data: function (term, page) {
                return {
    	            hasDate: 'true',
                	hideIdent: 'true',
                	hideDeleted: 'true',
                    q: term + "{{"+ $("#start"+filter).val()+","+$("#end"+filter).val()+"}}", // search term
                    page_limit: 10,
                    baseClass:'com.k_int.kbplus.Subscription'
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