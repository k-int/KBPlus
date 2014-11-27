<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ App Info</title>
  </head>

  <body>
    <h1>KBPlus API Calls</h1>

    <ul>
      <li><g:link action="assertCore">AssertCore</g:link> - assert a JUSP Core status against an institution, title and optionally provider. If no provider is given then
          records will be created for all providers we know about for this title.
        <table>
          <tr><th>Parameter</th><th>Optonal/Mandatory</th><th>Description</th><th>Examples</th></tr>
          <tr>
            <td><strong>provider</strong></td>
            <td>Optional</td>
            <td>String defining an identifier for the title provider of the form [namespace:]identifier. Namespace may be omitted</td>
            <td><em>juspsid:18</em> (Wiley)</td>
          <tr>
            <td><strong>title</strong></td>
            <td>Mandatory</td>
            <td>Identifier for the title of the form [namespace:]identifier. Namespace may be omitted</td>
            <td><em>ISSN:0898-9621</em>, <em>0898-9621</em>, <em>eISSN:1545-5815</em>, <em>jusp:9498</em> (Accountability in Research)</td>
          </tr>
          <tr>
            <td><strong>inst</strong></td>
            <td>Mandatory</td>
            <td>String defining an identifier for the institution expressing the core status of the form [namespace:]identifier. Namespace may be omitted</td>
            <td><em>jusplogin:shu</em>,<em>shu</em> (Sheff Hallam Uni)</td>
          </tr>
        </table>
        <p>Example Call:  <em><strong>wget "http://localhost:8080/demo/api/assertCore?inst=jusplogin:ast&title=jusp:9498"</strong></em></p>
      </li>
    </ul>

  </body>
</html>
