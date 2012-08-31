<!doctype html>

<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>

  <body>
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner navbar-public">
        <div class="container">
          <img src="images/kb_large_icon.png">
        </div>
      </div>
    </div>

    <div class="navbar-push-public"></div>

    <div class="container">
      <h1>Exports</h1>
    </div>

    <div class="container">
      <div class="row">
        <div class="span8">
          <p><a rel="license" href="http://creativecommons.org/licenses/by-sa/3.0/deed.en_US"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-sa/3.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" href="http://purl.org/dc/dcmitype/Dataset" property="dct:title" rel="dct:type">KBPlus Public Exports</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://www.kbplus.ac.uk" property="cc:attributionName" rel="cc:attributionURL">JISC COllections</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-sa/3.0/deed.en_US">Creative Commons Attribution-ShareAlike 3.0 Unported License</a>.<br />Based on a work at <a xmlns:dct="http://purl.org/dc/terms/" href="http://www.kbplus.ac.uk" rel="dct:source">http://www.kbplus.ac.uk</a>.<br />Permissions beyond the scope of this license may be available at <a xmlns:cc="http://creativecommons.org/ns#" href="http://www.kbplus.ac.uk" rel="cc:morePermissions">http://www.kbplus.ac.uk</a>.</p>
        </div>
        <div class="span4">
          <p>To the extent possible under law, <strong>KB+</strong> has waived all copyright and related or neighboring rights to Knowledge Base Plus. This work is published from: United Kingdom.</p>

          <g:each in="${subscriptions}" var="s">
            <div class="well">
              <h4>${s.name}</h4>
              <g:link action="so" params="${[format:'csv', id:s.identifier]}">CSV With KBPlus header</g:link><br/>
              <g:link action="so" params="${[format:'csv',omitHeader:'Y', id:s.identifier]}">CSV Without KBPlus header (KBart)</g:link>
            </div>
          </g:each>
        </div>
      </div>
    </div>

  </body>

</html>
