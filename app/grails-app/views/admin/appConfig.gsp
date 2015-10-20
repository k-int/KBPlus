<%@ page import="com.k_int.kbplus.Org; com.k_int.custprops.PropertyDefinition" %>

<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <title>KB+ App Config</title>
    <r:require module="annotations" />
    <g:javascript src="custom_properties.js"/>
  </head>
  <body>

    <div class="container">
 
    </div>

    <div class="container">
      
     <h6>${message(code:'sys.properties')}</h6>
              <div id="custom_props_div" class="span12">
                  <g:render template="/templates/custom_props" model="${[ prop_desc:PropertyDefinition.SYS_CONF,ownobj:adminObj ]}"/>
              </div>
    <g:form action="appConfig" method="POST">
      <input type="submit" name="one"class="btn"value="Refresh"  />
    </g:form>   
    <h3> Current output for Holders.config</h3>
    <ul>
    <g:each in="${currentconf.keySet().sort()}" var="key">
      <li>${key}: &nbsp; &nbsp; <g:textArea readonly="" name="key" value="${currentconf.get(key)}"/> </li>
    </g:each>
    <ul>
    </div>

    <r:script language="JavaScript">

     runCustomPropsJS("<g:createLink controller='ajax' action='lookup'/>");

    </r:script>

  </body>
</html>
