<%--
  Created by IntelliJ IDEA.
  User: rwincewicz
  Date: 03/09/2013
  Time: 09:44
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+</title>
</head>

<body>

<div class="container">
    <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li>ONIX-PL License Comparison</li>
    </ul>
</div>

<div class="container">
    <h1>ONIX-PL Licence Comparison</h1>
</div>

<div class="container">
    <div class="row">
        <div class="span8">

            <g:form name="compare" action="matrix">
                <div>
                    <label for="license1">License 1:</label>
                    <g:select name="license1" class="compare-license" from="${list}" optionKey="id" optionValue="title" />
                </div>

                <div>
                    <label for="license2">License 2:</label>
                    <g:select name="license2" class="compare-license" from="${list}" optionKey="id" optionValue="title"
                              noSelection="${['all': "All"]}" multiple="true" value="all" />
                </div>

                <div>
                    <label for="section">Compare section:</label>
                    <g:select name="section" from="${termList}" optionKey="id" optionValue="value"
                              class="compare-section" noSelection="${['all': "All"]}" value="all" />
                </div>

                <div>
                    Find licenses:<br>
                    <g:radio id="same" class="compare-radio" name="match" value="true" />
                    The same
                    <g:radio id="diff" class="compare-radio" name="match" value="false" />
                    Different
                    <g:radio id="all" class="compare-radio" name="match" value="" checked="${true}" />
                    Show all
                </div>

                <div>
                    <label for="max">Number of results per page: </label>
                    <g:select name="max" from="${[5, 10, 15]}" class="compare-results"/>
                </div>

                <br>
                <g:submitButton name="Compare" class="btn btn-primary"/>
            </g:form>
        </div>
    </div>
</div>

</body>
</html>