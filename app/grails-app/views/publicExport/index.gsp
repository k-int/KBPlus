<!doctype html>

<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->

<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>KB+ Data import explorer</title>
  </head>


  <body class="public">
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner navbar-public">
        <div class="container">
          <img class="brand" alt="Knowledge Base + logo" src="images/kb_large_icon.png" />
          <div class="nav-collapse">
            <ul class="nav">
              <li>
                <a href="${createLink(uri: '/')}"> Home </a>
              </li>
              <li>
                <a href="${createLink(uri: '/about')}"> About KB+ </a>
              </li>
              <li>
                <a href="${createLink(uri: '/signup')}"> Sign Up </a>
              </li>
              <li class="active last">
                <a href="${createLink(uri: '/publicExport')}"> Exports </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

   <div class="navbar-push-public"></div>

    <div class="container">
      <h1>Exports</h1>
    </div>

    <div class="container">
      <div class="row">
        <div class="span12">

<p xmlns:dct="http://purl.org/dc/terms/" xmlns:vcard="http://www.w3.org/2001/vcard-rdf/3.0#">
 <a rel="license"
    href="http://creativecommons.org/publicdomain/zero/1.0/">
   <img src="http://i.creativecommons.org/p/zero/1.0/88x31.png" style="border-style: none;" alt="CC0" />
 </a>
 <br />
 To the extent possible under law,
 <a rel="dct:publisher"
    href="http://www.kbplus.ac.uk/exports">
   <span property="dct:title">JISC Collections</span></a>
 has waived all copyright and related or neighboring rights to
 <span property="dct:title">KBPlus Public Exports</span>.
This work is published from:
<span property="vcard:Country" datatype="dct:ISO3166"
     content="GB" about="www.kbplus.ac.uk/exports">
 United Kingdom</span>.
</p>

        </div>
      </div>
    </div>

    <div class="container">
      <div class="row">
        <div class="span12">
          <div class="well">
            <h4>Cufts style index of subscriptions offered</h4>
            <p>
              Use the contents of this URI to drive a full crawl of the KB+ subscriptions offered data. Each row gives an identifier that can be used to
              construct individual subscription requests.
            </p>
            <g:link action="idx" params="${[format:'csv']}">Simple CSV</g:link><br/>
            <g:link action="idx" params="${[format:'xml']}">XML</g:link><br/>
            <g:link action="idx" params="${[format:'json']}">JSON</g:link><br/>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="row">
        <g:each in="${subscriptions}" var="s">
          <div class="span4"><div class="well">
            <h4>${s.name}</h4>
            <g:link action="so" params="${[format:'csv', id:s.identifier]}">CSV With KBPlus header</g:link><br/>
            <g:link action="so" params="${[format:'csv',omitHeader:'Y', id:s.identifier]}">CSV Without KBPlus header (KBART)</g:link><br/>
            <g:link action="so" params="${[format:'json',id:s.identifier]}">JSON</g:link>
          </div></div>
        </g:each>
      </div>
    </div>

  </body>

</html>
