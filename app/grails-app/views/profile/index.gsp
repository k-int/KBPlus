<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap"/>
    <title>KB+ User Profile</title>
  </head>

  <body>

    <div class="container">
        <ul class="breadcrumb">
        <li> <g:link controller="home">KBPlus</g:link> <span class="divider">/</span> </li>
        <li class="active">Profile</li>
      </ul>
    </div>

  <g:if test="${flash.error}">
      <div class="container">
          <bootstrap:alert class="error-info">${flash.error}</bootstrap:alert>
      </div>
  </g:if>

    <g:if test="${flash.message}">
    <div class="container">
       <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
    </div>
    </g:if>


		<div class="container">
			<div class="span12">
				<h1>Preferences</h1>
			</div>
		</div>
		<div class="container">
      <div class="span12">
        <dl class="dl-horizontal">
	        <div class="control-group">
            <dt>Show Info Icon</dt>
            <dd>
              <g:xEditableRefData owner="${user}" field="showInfoIcon" config="YN" />
            </dd>
          </div>
        </dl>
      </div>
    </div>

    <div class="container">
      <div class="span12">
        <h1>User Profile</h1>
      </div>
    </div>

    <div class="container">
      <div class="span12">
        <g:form action="updateProfile" class="form-inline">
          <dl class="dl-horizontal">

          <div class="control-group">
            <dt>Display Name</dt>
            <dd><input type="text" name="userDispName" value="${user.display}"/></dd>
          </div>

          <div class="control-group">
            <dt>Email Address</dt>
            <dd><input type="text" name="email" value="${user.email}"/></dd>
          </div>

          <div class="control-group">
            <dt>Default Page Size</dt>
            <dd><input type="text" name="defaultPageSize" value="${user.defaultPageSize}"/></dd>
          </div>

          <div class="control-group">
            <dt>Default Dashboard</dt>
            <dd>
              <select name="defaultDash" value="${user.defaultDash?.id}">
                <g:each in="${user.authorizedOrgs}" var="o">
                  <option value="${o.id}" ${user.defaultDash?.id==o.id?'selected':''}>${o.name}</option>
                </g:each>
              </select>
            </dd>
          </div>

          <div class="control-group">
            <dt></dt>
            <dd><input type="submit" value="Update Profile" class="btn btn-primary"/></dd>
          </div>

          <p>Please note, membership requests may be slow to process if you do not set a meaningful display name and email address. Please ensure
               these are set correctly before requesting institutional memberships</p>
        </g:form>
      </div>
    </div>


    <div class="container">
      <div class="span12">
        <h1>Administrative memberships</h1>
      </div>
    </div>

    <div class="container"><div class="row-fluid">
      <div class="span6">
        <div class="well">
          <h2>Existing Memberships</h2>

          <table class="table table-striped table-bordered table-condensed">
            <thead>
              <tr>
                <th>Organisation</th>
                <th>Role</th>
                <th>Status</th>
                <th>Date Requested / Actioned</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${user.affiliations}" var="assoc">
                <tr>
                  <td><g:link controller="organisations" action="info" id="${assoc.org.id}">${assoc.org.name}</g:link></td>
                  <td><g:message code="cv.roles.${assoc.formalRole?.authority}"/></td>
                  <td><g:message code="cv.membership.status.${assoc.status}"/></td>
                  <td><g:formatDate format="dd MMMM yyyy" date="${assoc.dateRequested}"/> / <g:formatDate format="dd MMMM yyyy" date="${assoc.dateActioned}"/></td>
                  <td><!--<button class="btn">Remove</button>--></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
      </div>

      <div class="span6">
        <div class="well">
          <h2>Request new membership</h2>
          <p>Select an organisation and a role below. Requests to join existing
             organisations will be referred to the administrative users of that organisation. If you feel you should be the administrator of an organisation
             please contact the KBPlus team for support.</p>

          <g:form name="affiliationRequestForm" controller="profile" action="processJoinRequest" class="form-search" method="get">

            <g:select name="org"
                      from="${com.k_int.kbplus.Org.findAllBySector('Higher Education',[sort:'name'])}"
                      optionKey="id"
                      optionValue="name"
                      class="input-medium"/>

            <g:select name="formalRole"
                      from="${com.k_int.kbplus.auth.Role.findAllByRoleType('user')}"
                      optionKey="id"
                      optionValue="${ {role->g.message(code:'cv.roles.'+role.authority) } }"
                      class="input-medium"/>

            <button id="submitARForm" data-complete-text="Request Membership" type="submit" class="btn btn-primary btn-small">Request Membership</button>
          </g:form>
        </div>
      </div>
    </div></div>

  <div class="container">
      <div class="span12">
          <h1>Misc</h1>
      </div>
  </div>



  <div class="container">
      <div class="row-fluid">
          <div class="span12">
              <div class="well">
                  <h2>Create new Reminders / Notifications</h2>
                  <p>Select the condition you are interested about and time period you wished to be notified about sed topic.</p>
                  <p><i>Ensure your email or other method of contact is a valid means of reaching yourself</i></p>

                  <g:form name="createReminder" controller="profile" action="createReminder" class="form-search" method="POST" url="[controller:'profile', action:'createReminder']">

                      Notify for:<g:select name="trigger"
                      from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','ReminderTrigger')}"
                      optionKey="id"
                      optionValue="value"
                      class="input-medium"/>

                      Method:<g:select name="method"
                      from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','ReminderMethod')}"
                      optionKey="id"
                      optionValue="value"
                      class="input-medium"/>

                      Period:<g:select name="unit"
                      from="${com.k_int.kbplus.RefdataValue.executeQuery('select rdv from RefdataValue as rdv where rdv.owner.desc=?','ReminderUnit')}"
                      optionKey="id"
                      optionValue="value"
                      class="input-medium"/>

                      Time:<select name="val" class="input-medium required-indicator" id="val" value="${params.val}" data-type="select">
                          <g:each in="${(1..7)}" var="s">
                              <option value="${s}" ${s==params.long('val')?'selected="selected"':''}>${s}</option>
                          </g:each>
                      </select>


                      <button id="submitReminder" type="submit" class="btn btn-primary btn-small">Create</button>
                  </g:form>
              </div>
          </div>
      </div>
  </div>

  <div class="container">
      <div class="row-fluid">
          <div class="span12">
              <div class="well">
                  <h2>Active Reminders</h2>

                  <table class="table table-striped table-bordered table-condensed">
                      <thead>
                      <tr>
                          <th><g:message code="reminder.trigger" default="Trigger"/></th>
                          <th><g:message code="reminder.method" default="Method"/></th>
                          <th>Time (<g:message code="reminder.unit" default="Unit"/>/<g:message code="reminder.number" default="Number"/>)</th>
                          <th><g:message code="reminder.lastNotification" default="Last Notification"/></th>
                          <th><g:message code="reminder.update" default="Delete / Disable"/></th>
                      </tr>
                      </thead>
                      <tbody>
                      <g:if test="${user.reminders.size() == 0}">
                          <tr><td colspan="5" style="text-align:center">&nbsp;<br/>No reminders exist presently...<br/>&nbsp;</td></tr>
                      </g:if>
                      <g:else>
                          <g:each in="${user.reminders}" var="r">
                              <tr>
                                  <td>${r.trigger.value}</td>
                                  <td>${r.reminderMethod.value}</td>
                                  <td>${r.amount} ${r.unit.value}${r.amount >1? 's':''} before</td>
                                  <g:if test="${r.lastRan}"><td><g:formatDate format="dd MMMM yyyy" date="${r.lastRan}" /></td></g:if>
                                  <g:else><td>Never executed!</td></g:else>
                                  <td>
                                      <button data-op="delete" data-id="${r.id}" class="btn btn-small reminderBtn">Remove</button>&nbsp;/&nbsp;
                                      <button data-op="toggle" data-id="${r.id}" class="btn btn-small reminderBtn">${r.active? 'disable':'enable'}</button>
                                  </td>
                              </tr>
                          </g:each>
                      </g:else>
                      </tbody>
                  </table>
              </div>
          </div>
      </div>
  </div>
  </body>
</html>

<r:script>
    $(document).ready(function () {
        $("#unit").on('change', function (e) {
            var unit = this.options[e.target.selectedIndex].text;
            var val = $(this).next();
            if (unit) {
                switch (unit) {
                    case 'Day':
                        setupUnitAmount(val,7)
                        break;
                    case 'Week':
                        setupUnitAmount(val,4)
                        break;
                    case 'Month':
                        setupUnitAmount(val,12)
                        break
                    default :
                        console.log('Impossible selection made!');
                        break
                }
            }
        });

        $(".reminderBtn").on('click', function (e) {
            //e.preventDefault();
            var element = $(this);
            var yn = confirm("Are you sure you wish to continue?");
            if(yn)
            {
                $.ajax({
                    method: 'POST',
                    url: "<g:createLink controller='profile' action='updateReminder'/>",
                        data: {
                        op: element.data('op'),
                        id: element.data('id')
                    }
                }).done(function(data) {
                    console.log(data)
                    data.op == 'delete'? element.parents('tr').remove() : element.text(data.active);
                });
            }

            //return false;
        });
    });

    function setupUnitAmount(type, amount) {
        console.log(type);
        type.children().remove()
        for (var i = 1; i <= amount; i++) {
            type.append('<option value="' + i + '">' + i + '</option>');
        }
    }
</r:script>