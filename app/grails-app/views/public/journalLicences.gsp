<!doctype html>
<html>
<head>
    <meta name="layout" content="pubbootstrap"/>
    <title>Journals | Knowledge Base+</title>
	<r:require modules="onixMatrix" />
</head>

<body class="public">

<g:render template="public_navbar" contextPath="/templates" model="['active': 'journals']"/>

<div class="container">
<h1>${message(code:'menu.public.journalLicence')}</h1>


    <g:if test="${flash.error}">
      <div class="container">
        <bootstrap:alert class="alert-error">${flash.error}</bootstrap:alert>
      </div>
    </g:if>


<p> Use the following form to lookup Journals for a specific institution. Only journals from institutions that have opted in for this service will be searchable.</p>

<g:form action="journalLicences" method="get">
<div class="well form-horizontal">
	    Search Journal: <input placeholder="Title or Ident. kb: for KB+ ID" name="journal" value="${journal}"/>
	    Instituion: <input placeholder="Shortcode or KB+ ID" name="org" value="${org}"/>
	  
	    <button type="submit">Search</button>
	  </div>
</div>
</g:form>

<g:if test="${comparisonMap}">
<div class="container">
  <div class="onix-matrix-wrapper">

  <table class="onix-matrix">
    <thead>
    	<tr>
      <th class="cell-1">Issue Entitlement</th>
	    <g:each in="${licIEMap}" var="lic_entry" status="counter">
	      <th class="cell-${ (counter + 2) }"><span class="cell-inner" >
	      <g:each in="${lic_entry.getValue()}" var="ie">
	      	${ie.tipp.title.title}
	      	 <br/>Start: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.startDate}"/>, End: <g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${ie.endDate}"/>
 	      	 <br/>
	      </g:each>
	      	</span></th>
      </g:each>
      </tr>
      <tr>
      <th class="cell-1"> Licence</th>
      <g:each in="${licIEMap}" var="lic_entry" status="counter">
      <th class="cell-${ (counter + 2) }"><span class="cell-inner" >${lic_entry.getKey().reference}</span></th>
      </g:each>
      </tr>
    </thead>
    <tbody>
    <g:each in="${comparisonMap}" var="entry">
    <tr>
    <th>${entry.getKey()}</th>
    <g:each in="${licIEMap}" var="entry_key">
      <g:set var="lic" value="${entry_key.getKey()}"/>
      <g:if test="${entry.getValue().containsKey(lic.reference)}">
      <td>
        <g:set var="point" value="${entry.getValue().get(lic.reference)}"/>
        <div class="onix-icons">
          <g:if test="${point.getNote()}">
              <span class='main-annotation' ><i class='icon-edit' data-content='Click the icon to view the annotations.' title='Annotations'></i></span>
              <div class="textelement" > 
                <ul><li>${ point.getNote().encodeAsHTML() }</span></li></ul>
              </div>
          </g:if>
        </div>
        <g:if test="${['stringValue','intValue','decValue'].contains(point.getValueType())}">
            <span class="cell-inner">  ${point.getValue()}</span>
        </g:if>
        <g:else>
            <g:set var="val" value="${point.getValue()}"/>
            <g:if test="${val == 'Y' || val=="Yes"}">
            <span class="cell-inner">
              <span title='Detailed by license' class="onix-status onix-tick" />
              </span>
            </g:if>
            <g:elseif test="${val=='N' || val=="No"}">
              <span class="cell-inner">
                <span title='Prohibited by the license' class="onix-status onix-pl-prohibited" />
              </span>
            </g:elseif>
            <g:elseif test="${['O','Other','Specified'].contains(val)}">
              <span class="cell-inner">
                <span title='Detailed by license' class="onix-status onix-info" />
              </span>
            </g:elseif>
            <g:elseif test="${['U','Unknown','Not applicable','Not Specified']}">
              <span class="cell-inner-undefined">
                <span title='Not defined by the license' class="onix-status onix-pl-undefined" ></span>
              </span>
            </g:elseif>
            <g:else>
               <span class="cell-inner">  ${point.getValue()}</span>
            </g:else>
        </g:else>
        </td>
      </g:if>
      <g:else>
        <td>
          <span class="cell-inner-undefined">
            <span title='Not defined by the license' class="onix-status onix-pl-undefined" ></span>
          </span>
        </td>
      </g:else>
    </g:each>
    </tr>
    </g:each>
    </tbody>
   </table>
   </div>

    </div>

   <div id="onix-modal" class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
  </div>
  </div>
</div>
    <r:script language="JavaScript">
        $(function(){
          $(".onix-pl-undefined").replaceWith("<span title='Not defined by the license' style='height:1em' class='onix-status fa-stack fa-4x'> <i class='fa fa-info-circle fa-stack-1x' style='color:#166fe7;' ></i> <i class='fa fa-ban fa-stack-1x' style='color:#FF0000'></i> </span>")
            // Tooltips.
          $('.onix-code, .onix-status').tooltip(
              {placement: 'bottom', trigger:'hover', html: true, container: 'body'}
          );
          $('.onix-icons span i').popover(
            {placement: 'left', trigger:'hover', html: true, container: 'body'}
          );
        });

    </r:script>
  </div>
</g:if>
</body>
</html>