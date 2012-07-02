<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>KB+</title>
    <r:require modules="jeditable"/>
    <r:require module="jquery-ui"/>
  </head>
  <body>
    <h2>${institution?.name} Subscription Taken : ....</span></h2>
    <hr/>
    <div class="tabbable"> <!-- Only required for left/right tabs -->
      <ul class="nav nav-tabs">
        <li class="active"><a href="#detailtab" data-toggle="tab">License Detail</a></li>
        <li><a href="#docstab" data-toggle="tab">Documents</a></li>
        <li><a href="#linkstab" data-toggle="tab">Links</a></li>
        <li><a href="#notestab" data-toggle="tab">Notes</a></li>
      </ul>
      <div class="tab-content">
        <div class="tab-pane active" id="detailtab">
          <div class="row-fluid">
          </div>
        </div>
        <div class="tab-pane" id="docstab">
          <div class="row-fluid">
          </div>
        </div>
        <div class="tab-pane" id="linkstab">
          <div class="row-fluid">
            <div class="span12">
              Tab3
            </div>
          </div>
        </div>
        <div class="tab-pane" id="notestab">
          <div class="row-fluid">
            <div class="span12">
              Tab4
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
