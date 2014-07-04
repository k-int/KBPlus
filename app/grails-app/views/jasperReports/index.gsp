<%--
  Created by IntelliJ IDEA.
  User: ioannis
  Date: 30/06/2014
  Time: 16:22
--%>

<%@ page import="com.k_int.kbplus.JasperReportsController; org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>KB+ Jasper Reports</title>
    <meta name="layout" content="mmbootstrap"/>
</head>

<body>

<div class="container">
    <div class="span8">

        <ul class="breadcrumb">
            <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
            <li><g:link controller="jasperReports" action="index">Jasper Reports</g:link> <span
                    class="divider">/</span></li>
        </ul>

        <g:if test="${flash.message}">
            <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>
        <g:if test="${flash.error}">
            <bootstrap:alert class="alert-danger">${flash.error}</bootstrap:alert>
        </g:if>

        <div class="inline-lists">
            <dl>
                <dt>Selected Report:</dt>
                <dd>
                    <span>
                        <g:select id="available_reports" name="report_name" from="${available_reports}"/>
                    </span>
                </dd>
                <dt>Download Format:</dt>
                <dd>
                    <span>
                        <g:select name="_format" from="${JasperReportsController.formats}"/>
                    </span>
                </dd>
            </dl>
        </div>

        <div id="report_details">
            <g:render template="report_details" model="params"/>
        </div>
    </div>
</div>

</body>
<r:script language="JavaScript">
    $(function () {
        $('#available_reports').change(function() {
            var repname = $('#available_reports option:selected').text()
            jQuery.ajax({type:'POST',data:{'report_name': repname}, url:'${createLink(controller: 'jasperReports', action: 'index')}'
            ,success:function(data,textStatus){jQuery('#report_details').html(data);}
            ,error:function(XMLHttpRequest,textStatus,errorThrown){}
            ,complete:function(XMLHttpRequest,textStatus){runJasperJS()}});
        });
    });
    function runJasperJS(){
    copyReportName();
    activateDatepicker();
    }
    function copyReportName() {
        $("#hiddenReportName").val($("#available_reports").val())
    }

    function activateDatepicker(){
//          $("div.date").children().find("input").each(function()
//          {
//            this.datepicker();
//          });
        $("div.date").children('input').datepicker()
//        $("input[name='start_date']").datepicker()
    }
    document.onload = runJasperJS();
</r:script>
</html>