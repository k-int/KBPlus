<!doctype html>
<html>
<head>
<meta name="layout" content="mmbootstrap" />
<title>KB+</title>
</head>
<body>

<r:require modules="onixMatrix" />

  <table class="onix-matrix">
    <thead>
      <th class="cell-1"> Property</th>
      <g:each in="${licences}" var="licence" status="counter">
      <th class="cell-${ (counter + 2) }"><span class="cell-inner" >${licence.reference}</span></th>
      </g:each>
    </thead>
    <tbody>
    <g:each in="${map}" var="entry">
    <tr>
    <th>${entry.getKey()}</th>
    <g:each in="${licences}" var="lic">
      <g:if test="${entry.getValue().containsKey(lic.reference)}">
      <td>
        <g:set var="point" value="${entry.getValue().get(lic.reference)}"/>
        <g:if test="${point.getNote()}">
          <div class="onix-icons">
            <span class='main-annotation' ><i class='icon-edit' data-content='Click the icon to view the annotations.' title='Annotations'></i></span>
            <div class="textelement" >
                <ul><li>${ point.getNote().encodeAsHTML() }</span></li></ul>
            </div>

          </div>
        </g:if>
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
     </body>

 </html>
