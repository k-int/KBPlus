<%@ page import="com.k_int.kbplus.JasperReportsController" %>
<%--
  Created by IntelliJ IDEA.
  User: ioannis
  Date: 02/07/2014
  Time: 14:33
--%>

<div class="well">
    Report Description: ${reportdesc}

</div>

<g:form controller="jasperReports" action="generateReport">
    <input type="hidden" id="hiddenReportName" name="_file">
    <input type="hidden" id="hiddenReportFormat" name="_format">
    <table class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th class="text-center" colspan="2">
                Report Parameters
            </th>
        </tr>
        <tr>
            <th>Description</th>
            <th>Value</th>
        </tr>
        </thead>
        <tbody>

        <g:each in="${report_parameters}" var="rparam">
            <tr>
            <td>${rparam.getDescription()}</td>
            <td>
                <g:if test="${rparam.getValueClass().equals(java.sql.Timestamp) || 
                    rparam.getValueClass().equals(java.sql.Date) }">
                    <div class="input-append date">
                        <input class="span2" size="16" type="text" name="${rparam.getName()}">
                        <span class="add-on"><i class="icon-th"></i></span>
                    </div>
                </g:if>
                <g:elseif test="${rparam.getName().contains("select")}">
                    <g:select name="${rparam.getName()}" 
                    from="${rparam.getName().substring(rparam.getName().indexOf('&')+1).split('&')}"/>
                </g:elseif>
                <g:else>
                    <g:if test="${rparam.getName().contains("search")}">

                       <input type="hidden" id="${rparam.getName()}" name="${rparam.getName()}"/>
                        <script type="text/javascript">
                            createSelect2Search('#${rparam.getName()}',
                                '${rparam.getValueClass().toString().replace("class ","")}');
                        </script>
                    </g:if>
                    <g:else>
                        <input type="text" name="${rparam.getName()}"/>
                    </g:else>
                </g:else>
            </td>
        </g:each>
        </tr>
        </tbody>
    </table>
    <g:submitButton name="submit" class="btn-primary" value="Generate Report"/>
</g:form>
