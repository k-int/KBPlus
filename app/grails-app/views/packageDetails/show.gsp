<%@ page import="com.k_int.kbplus.Package" %>
<!doctype html>
<html>
  <head>
    <meta name="layout" content="mmbootstrap">
    <g:set var="entityName" value="${message(code: 'package.label', default: 'Package')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
  </head>
 <body>


    <div class="container">
      <ul class="breadcrumb">
        <li><g:link controller="home" action="index">Home</g:link> <span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="index">All Packages</g:link><span class="divider">/</span></li>
        <li><g:link controller="packageDetails" action="show" id="${packageInstance.id}">${packageInstance.name}</g:link></li>

        <li class="dropdown pull-right">
          <a class="dropdown-toggle badge" id="export-menu" role="button" data-toggle="dropdown" data-target="#" href="">Exports<b class="caret"></b></a>

          <ul class="dropdown-menu filtering-dropdown-menu" role="menu" aria-labelledby="export-menu">
            <li><g:link action="show" params="${params+[format:'json']}">Json Export</g:link></li>
            <li><g:link action="show" params="${params+[format:'xml']}">XML Export</g:link></li>
            <g:each in="${transforms}" var="transkey,transval">
              <li><g:link action="show" id="${params.id}" params="${[format:'xml',transformId:transkey,mode:params.mode]}"> ${transval.name}</g:link></li>
            </g:each>
          </ul>
        </li>

        <li class="pull-right">
          View:
          <div class="btn-group" data-toggle="buttons-radio">
            <g:link controller="packageDetails" action="show" params="${params+['mode':'basic']}" class="btn btn-primary btn-mini ${((params.mode=='basic')||(params.mode==null))?'active':''}">Basic</g:link>
            <g:link controller="packageDetails" action="show" params="${params+['mode':'advanced']}" class="btn btn-primary btn-mini ${params.mode=='advanced'?'active':''}">Advanced</g:link>
          </div>
          &nbsp;
        </li>
        
      </ul>
    </div>

    <g:render template="/templates/pendingChanges" model="${['pendingChanges': pendingChanges,'flash':flash,'model':packageInstance]}"/>


      <div class="container">
        <g:if test="${params.asAt}"><h1>Snapshot on ${params.asAt} from </h1></g:if>
        <div class="page-header">
          <div>
          <h1><g:if test="${editable}"><span id="packageNameEdit"
                        class="xEditableValue"
                        data-type="textarea"
                        data-pk="${packageInstance.class.name}:${packageInstance.id}"
                        data-name="name"
                        data-url='<g:createLink controller="ajax" action="editableSetValue"/>'>${packageInstance.name}</span></g:if><g:else>${packageInstance.name}</g:else></h1>
           <g:render template="nav" />
            <sec:ifAnyGranted roles="ROLE_ADMIN,KBPLUS_EDITOR">
            <g:link controller="announcement" action="index" params='[at:"Package Link: ${pkg_link_str}",as:"RE: Package ${packageInstance.name}"]'>Mention this package in an announcement</g:link>
            </sec:ifAnyGranted>
            <g:if test="${forum_url != null}">
              <a href="${forum_url}"> | Discuss this package in forums</a> <a href="${forum_url}" title="Discuss this package in forums (new Window)" target="_blank"><i class="icon-share-alt"></i></a>
            </g:if>

          </div>

        </div>
    </div>

        <g:if test="${flash.message}">
        <bootstrap:alert class="alert-info">${flash.message}</bootstrap:alert>
        </g:if>

        <g:hasErrors bean="${packageInstance}">
        <bootstrap:alert class="alert-error">
        <ul>
          <g:eachError bean="${packageInstance}" var="error">
          <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
          </g:eachError>
        </ul>
        </bootstrap:alert>
        </g:hasErrors>

    <div class="container">
      <div class="row">
        <div class="span8">
            <h6>Package Information
          <span class="btn-group pull-right" data-toggle="buttons-radio">
            <g:link controller="packageDetails" action="show" params="${params+['mode':'basic']}" class="btn btn-primary btn-mini ${((params.mode=='basic')||(params.mode==null))?'active':''}">Basic</g:link>
            <g:link controller="packageDetails" action="show" params="${params+['mode':'advanced']}" class="btn btn-primary btn-mini ${params.mode=='advanced'?'active':''}">Advanced</g:link>
          </span>
          &nbsp;
</h6>
            <g:hiddenField name="version" value="${packageInstance?.version}" />
            <fieldset class="inline-lists">

              <dl>
                <dt>Package Name</dt>
                <dd> <g:xEditable owner="${packageInstance}" field="name"/></dd>
              </dl>
              
              <dl>
                <dt>Package Persistent Identifier</dt>
                <dd>uri://kbplus/${grailsApplication.config.kbplusSystemId}/package/${packageInstance?.id}</dd>
              </dl>
              
              <dl>
                <dt>Other Identifiers</dt>
                <dd>
                  <table class="table table-bordered">
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Identifier Namespace</th>
                        <th>Identifier</th>
                      </tr>
                    </thead>
                    <tbody>
                      <g:each in="${packageInstance.ids}" var="io">
                          <tr>
                            <td>${io.id}</td>
                            <td>${io.identifier.ns.ns}</td>
                            <td>${io.identifier.value}</td>
                          </tr>
                      </g:each>
                     
                    </tbody>
                  </table>

                  <g:if test="${editable}">
                    <g:form controller="ajax" action="addToCollection" class="form-inline">
                      <input type="hidden" name="__context" value="${packageInstance.class.name}:${packageInstance.id}"/>
                      <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.IdentifierOccurrence"/>
                      <input type="hidden" name="__recip" value="pkg"/>
                      <input type="hidden" name="identifier" id="addIdentifierSelect"/>
                      <input type="submit" value="Add Identifier..." class="btn btn-primary btn-small"/>
                    </g:form>
                  </g:if>

                </dd>
              </dl>

              <dl>
                <dt>Public?</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="isPublic" config='YN'/>
                </dd>
              </dl> 

              <dl>
                <dt><g:message code="licence" default="Licence"/></dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="license" config='Licenses'/>
                </dd>
              </dl>

              <dl>
                <dt>Vendor URL</dt>
                <dd>
                  <g:xEditable owner="${packageInstance}" field="vendorURL" />
                </dd>
              </dl>

                <dl>
                  <dt>Start Date</dt>
                  <dd>
                    <g:xEditable owner="${packageInstance}" field="startDate" type="date"/>
                </dd>
                </dl>

               <dl>
                    <dt>End Date</dt>
                    <dd>
                       <g:xEditable owner="${packageInstance}" field="endDate" type="date"/>
                    </dd>
               </dl>



              <dl>
                <dt>Org Links</dt>
                <dd><g:render template="orgLinks" 
                            contextPath="../templates"
                            model="${[roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',editmode:editable]}" /></dd>
              </dl>

             <dl>
                <dt>List Status</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="packageListStatus" config='Package.ListStatus'/>
                </dd>
             </dl>

             <dl>
                <dt>Breakable</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="breakable" config='Package.Breakable'/>
                </dd>
             </dl>

             <dl>
                <dt>Consistent</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="consistent" config='Package.Consistent'/>
                </dd>
             </dl>

             <dl>
                <dt>Fixed</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="fixed" config='Package.Fixed'/>
                </dd>
             </dl>

              <dl>
                <dt>Package Scope</dt>
                <dd>
                  <g:xEditableRefData owner="${packageInstance}" field="packageScope" config='Package.Scope'/>
                </dd>
              </dl>

          </fieldset>
        </div>


        <div class="span4">

          <div class="well notes">
            <g:if test="${(subscriptionList != null) && (subscriptionList?.size() > 0)}">
              <h5>Add package to institutional subscription:</h5>
              <g:form controller="packageDetails" action="addToSub" id="${packageInstance.id}">
                <select name="subid">
                  <g:each in="${subscriptionList}" var="s">
                    <option value="${s.sub.id}">${s.sub.name ?: "unnamed subscription ${s.sub.id}"} - ${s.org.name}</option>
                  </g:each>
                </select><br/>
                Create Entitlements in Subscription: <input type="checkbox" id="addEntitlementsCheckbox" name="addEntitlements" value="true"/><br/>
                <input id="add_to_sub_submit_id" type="submit"/>
              </g:form>
            </g:if>
            <g:else>
              No subscriptions available to link to this package
            </g:else>
          </div>


          <g:render template="/templates/documents" model="${[ ownobj:packageInstance, owntp:'pkg']}" />
          <g:render template="/templates/notes"  model="${[ ownobj:packageInstance, owntp:'pkg']}" />
        </div>
      </div>
    </div>

    <div class="container">
      <br/>
      <p>
        <span class="pull-right">
          Currently
          <g:if test="${unfiltered_num_tipp_rows == num_tipp_rows}">
            Showing all TIPPs
          </g:if>
          <g:else>
            Showing filtered list of ${num_tipp_rows} from a total of ${unfiltered_num_tipp_rows} TIPPs
          </g:else>
        </span>
        Titles (${offset+1} to ${lasttipp}  of ${num_tipp_rows})
        <g:if test="${params.mode=='advanced'}">Includes Expected or Expired titles, switch to the <g:link controller="packageDetails" action="show" params="${params+['mode':'basic']}">Basic</g:link> view to hide them
        </g:if>
        <g:else>Expected or Expired titles are not shown, use the <g:link controller="packageDetails" action="show" params="${params+['mode':'advanced']}">Advanced</g:link> view to see them
        </g:else>
              )
     </p>

        <div class="well">
          <g:form action="show" params="${params}" method="get" class="form-inline">
             <input type="hidden" name="sort" value="${params.sort}">
             <input type="hidden" name="order" value="${params.order}">
             <input type="hidden" name="mode" value="${params.mode}">
             <label>Filters - Title:</label> <input name="filter" value="${params.filter}"/>
             <label>Coverage note:</label> <input name="coverageNoteFilter" value="${params.coverageNoteFilter}"/><br/>
              <label>Coverage Starts Before:</label> 
              <g:simpleHiddenValue id="startsBefore" name="startsBefore" type="date" value="${params.startsBefore}"/>, &nbsp;
              <label>Ends After:</label>
              <g:simpleHiddenValue id="endsAfter" name="endsAfter" type="date" value="${params.endsAfter}"/>, &nbsp;
              <g:if test="${params.mode!='advanced'}">
                <label>Show package contents on specific date:</label>
                <g:simpleHiddenValue id="asAt" name="asAt" type="date" value="${params.asAt}"/>
              </g:if>

             <input type="submit" class="btn btn-primary pull-right" value="Filter Results" />
          </g:form>
        </div>
          <g:form action="packageBatchUpdate" params="${[id:packageInstance?.id]}">
            <g:hiddenField name="filter" value="${params.filter}"/>
            <g:hiddenField name="coverageNoteFilter" value="${params.coverageNoteFilter}"/>
            <g:hiddenField name="startsBefore" value="${params.startsBefore}"/>
            <g:hiddenField name="endsAfter" value="${params.endsAfter}"/>
            <g:hiddenField name="sort" value="${params.sort}"/>
            <g:hiddenField name="order" value="${params.order}"/>
            <g:hiddenField name="offset" value="${params.offset}"/>
            <g:hiddenField name="max" value="${params.max}"/>
            <table class="table table-bordered">
            <thead>
            <tr class="no-background">

              <th>
                <g:if test="${editable}"><input type="checkbox" name="chkall" onClick="javascript:selectAll();"/></g:if>
              </th>

              <th colspan="7">
                <g:if test="${editable}">
                  <select id="bulkOperationSelect" name="bulkOperation" class="input-xxlarge">
                    <option value="edit">Batch Edit Selected Rows Using the following values</option>
                    <option value="remove">Batch Remove Selected Rows</option>
                  </select>
                  <br/>
                  <table class="table table-bordered">
                    <tr>
                      <td>Coverage Start Date: <g:simpleHiddenValue id="bulk_start_date" name="bulk_start_date" type="date"/> 
                          <input type="checkbox" name="clear_start_date"/> (Check to clear)</td>
                      <td>Start Volume: <g:simpleHiddenValue id="bulk_start_volume" name="bulk_start_volume" />
                          <input type="checkbox" name="clear_start_volume"/>(Check to clear)</td>
                      <td>Start Issue: <g:simpleHiddenValue id="bulk_start_issue" name="bulk_start_issue"/>
                          <input type="checkbox" name="clear_start_issue"/>(Check to clear)</td>
                    </tr>
                    <tr>
                      <td>Coverage End Date:  <g:simpleHiddenValue id="bulk_end_date" name="bulk_end_date" type="date"/>
                          <input type="checkbox" name="clear_end_date"/>(Check to clear)</td>
                      <td>End Volume: <g:simpleHiddenValue id="bulk_end_volume" name="bulk_end_volume"/>
                          <input type="checkbox" name="clear_end_volume"/>(Check to clear)</td>
                      <td>End Issue: <g:simpleHiddenValue id="bulk_end_issue" name="bulk_end_issue"/>
                          <input type="checkbox" name="clear_end_issue"/>(Check to clear)</td>
                    </tr>
                    <tr>
                       <td>Host Platform URL: <g:simpleHiddenValue id="bulk_hostPlatformURL" name="bulk_hostPlatformURL"/>
                          <input type="checkbox" name="clear_hostPlatformURL"/>(Check to clear)</td>
                        </td>
                      <td>Coverage Note: <g:simpleHiddenValue id="bulk_coverage_note" name="bulk_coverage_note"/>
                          <input type="checkbox" name="clear_coverage_note"/>(Check to clear)</td>
                      <td>Embargo:  <g:simpleHiddenValue id="bulk_embargo" name="bulk_embargo"/>
                          <input type="checkbox" name="clear_embargo"/>(Check to clear)</td>
                    </tr>
                    <g:if test="${params.mode=='advanced'}">
                      <tr>
                        <td>Delayed OA: <g:simpleHiddenRefdata id="bulk_delayedOA" name="bulk_delayedOA" refdataCategory="TIPPDelayedOA"/>
                          <input type="checkbox" name="clear_delayedOA"/>(Check to clear)</td>
                        </td>
                        <td>Hybrid OA: <g:simpleHiddenRefdata id="bulk_hybridOA" name="bulk_hybridOA" refdataCategory="TIPPHybridOA"/>
                          <input type="checkbox" name="clear_hybridOA"/>(Check to clear)</td>
                        </td>
                        <td>Payment: <g:simpleHiddenRefdata id="bulk_payment" name="bulk_payment" refdataCategory="TIPPPaymentType"/>
                          <input type="checkbox" name="clear_payment"/>(Check to clear)</td>
                        </td>
                      </tr>
                    </g:if>


                  </table>
                  <button name="BatchSelectedBtn" value="on" onClick="return confirmSubmit()" class="btn btn-primary">Apply Batch Changes (Selected)</button>
                  <button name="BatchAllBtn" value="on" onClick="return confirmSubmit()" class="btn btn-primary">Apply Batch Changes (All in filtered list)</button>
                </g:if>
              </th>
            </tr>
            <tr>
              <th>&nbsp;</th>
              <th>&nbsp;</th>
              <g:sortableColumn params="${params}" property="tipp.title.title" title="Title" />
              <th style="">Platform</th>
              <th style="">Hybrid OA</th>
              <th style="">Identifiers</th>
              <th style="">Coverage Start</th>
              <th style="">Coverage End</th>
            </tr>


            </thead>
            <tbody>
            <g:set var="counter" value="${offset+1}" />
            <g:each in="${titlesList}" var="t">
              <g:set var="hasCoverageNote" value="${t.coverageNote?.length() > 0}" />
               <tr>
                <td ${hasCoverageNote==true?'rowspan="2"':''}><g:if test="${editable}"><input type="checkbox" name="_bulkflag.${t.id}" class="bulkcheck"/></g:if></td>
                <td ${hasCoverageNote==true?'rowspan="2"':''}>${counter++}</td>
                <td style="vertical-align:top;">
                   <b>${t.title.title}</b>
                   <g:link controller="titleDetails" action="show" id="${t.title.id}">(Title)</g:link>
                   <g:link controller="tipp" action="show" id="${t.id}">(TIPP)</g:link><br/>
                   <ul>
                     <g:each in="${t.title.distinctEventList()}" var="h">
                       <li>

                         Title History: <g:formatDate date="${h.event.eventDate}" format="yyyy-MM-dd"/><br/>

                         <g:each status="st" in="${h.event.fromTitles()}" var="the">
                            <g:if test="${st>0}">, </g:if>
                            <g:link controller="titleDetails" action="show" id="${the.id}">${the.title}</g:link>
                            <g:if test="${the.isInPackage(packageInstance)}">(✔)</g:if><g:else>(✘)</g:else>
                         </g:each>
                         Became
                         <g:each status="st" in="${h.event.toTitles()}" var="the"><g:if test="${st>0}">, </g:if>
                            <g:link controller="titleDetails" action="show" id="${the.id}">${the.title}</g:link>
                            <g:if test="${the.isInPackage(packageInstance)}">(✔)</g:if><g:else>(✘)</g:else>
                         </g:each>
                       </li>
                     </g:each>
                   </ul>
                   <span title="${t.availabilityStatusExplanation}">Access: ${t.availabilityStatus?.value}</span>
                   <g:if test="${params.mode=='advanced'}">
                     <br/> Record Status: <g:xEditableRefData owner="${t}" field="status" config='TIPPStatus'/>
                     <br/> Access Start: <g:xEditable owner="${t}" type="date" field="accessStartDate" />
                     <br/> Access End: <g:xEditable owner="${t}" type="date" field="accessEndDate" />
                   </g:if>
                </td>
                <td style="white-space: nowrap;vertical-align:top;">
                   <g:if test="${t.hostPlatformURL != null}">
                     <a href="${t.hostPlatformURL}">${t.platform?.name}</a>
                   </g:if>
                   <g:else>
                     ${t.platform?.name}
                   </g:else>
                </td>
                <td style="white-space: nowrap;vertical-align:top;">
                   <g:xEditableRefData owner="${t}" field="hybridOA" config='TIPPHybridOA'/>
                </td>
                <td style="white-space: nowrap;vertical-align:top;">
                  <g:each in="${t.title.ids}" var="id">
                    <g:if test="${id.identifier.ns.hide != true}">
                      ${id.identifier.ns.ns}:${id.identifier.value}<br/>
                    </g:if>
                  </g:each>
                </td>

                <td style="white-space: nowrap">
                  Date: <g:xEditable owner="${t}" type="date" field="startDate" /><br/>
                  Volume: <g:xEditable owner="${t}" field="startVolume" /><br/>
                  Issue: <g:xEditable owner="${t}" field="startIssue" />     
                </td>

                <td style="white-space: nowrap"> 
                   Date: <g:xEditable owner="${t}" type="date" field="endDate" /><br/>
                   Volume: <g:xEditable owner="${t}" field="endVolume" /><br/>
                   Issue: <g:xEditable owner="${t}" field="endIssue" />
                </td>
              </tr>

              <g:if test="${hasCoverageNote==true || params.mode=='advanced'}">
               <tr>
                  <td colspan="8">coverageNote: ${t.coverageNote}
                  <g:if test="${params.mode=='advanced'}">
                    <br/> Host Platform URL: <g:xEditable owner="${t}" field="hostPlatformURL" />
                    <br/> Delayed OA: <g:xEditableRefData owner="${t}" field="delayedOA" config='TIPPDelayedOA'/> &nbsp;
                    Hybrid OA: <g:xEditableRefData owner="${t}" field="hybridOA" config='TIPPHybridOA'/> &nbsp;
                    Payment: <g:xEditableRefData owner="${t}" field="payment" config='TIPPPaymentType'/> &nbsp;
                  </g:if>
                  </td>
                </tr>
              </g:if>

            </g:each>
            </tbody>
            </table>
          </g:form>
          

        <div class="pagination" style="text-align:center">
          <g:if test="${titlesList}" >
            <bootstrap:paginate  action="show" controller="packageDetails" params="${params}" next="Next" prev="Prev" maxsteps="${max}" total="${num_tipp_rows}" />
          </g:if>
        </div>



        <g:if test="${editable}">
        
        <g:form controller="ajax" action="addToCollection">
          <fieldset>
            <legend>Add A Title To This Package</legend>
            <input type="hidden" name="__context" value="${packageInstance.class.name}:${packageInstance.id}"/>
            <input type="hidden" name="__newObjectClass" value="com.k_int.kbplus.TitleInstancePackagePlatform"/>
            <input type="hidden" name="__recip" value="pkg"/>

            <!-- N.B. this should really be looked up in the controller and set, not hard coded here -->
            <input type="hidden" name="status" value="com.k_int.kbplus.RefdataValue:29"/>

            <label>Title To Add</label>
            <g:simpleReferenceTypedown class="input-xxlarge" style="width:350px;" name="title" baseClass="com.k_int.kbplus.TitleInstance"/><br/>
            <span class="help-block"></span>
            <label>Platform For Added Title</label>
            <g:simpleReferenceTypedown class="input-large" style="width:350px;" name="platform" baseClass="com.k_int.kbplus.Platform"/><br/>
            <span class="help-block"></span>
            <button type="submit" class="btn">Add Title...</button>
          </fieldset>
        </g:form>


        </g:if>

      </div>


    <g:render template="enhanced_select" contextPath="../templates" />
    <g:render template="orgLinksModal" 
              contextPath="../templates" 
              model="${[linkType:packageInstance?.class?.name,roleLinks:packageInstance?.orgs,parent:packageInstance.class.name+':'+packageInstance.id,property:'orgs',recip_prop:'pkg']}" />

    <r:script language="JavaScript">
      $(function(){
        $.fn.editable.defaults.mode = 'inline';
        $('.xEditableValue').editable();
      });
      function selectAll() {
        $('.bulkcheck').attr('checked', true);
      }

      function confirmSubmit() {
        if ( $('#bulkOperationSelect').val() === 'remove' ) {
          var agree=confirm("Are you sure you wish to continue?");
          if (agree)
            return true ;
          else
            return false ;
        }
      }

      <g:if test="${editable}">
      $("#addIdentifierSelect").select2({
        placeholder: "Search for an identifier...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
          url: "<g:createLink controller='ajax' action='lookup'/>",
          dataType: 'json',
          data: function (term, page) {
              return {
                  q: term, // search term
                  page_limit: 10,
                  baseClass:'com.k_int.kbplus.Identifier'
              };
          },
          results: function (data, page) {
            return {results: data.values};
          }
        },
        createSearchChoice:function(term, data) {
          return {id:'com.k_int.kbplus.Identifier:__new__:'+term,text:term};
        }
      });
      </g:if>
     
      <g:if test="${params.asAt && params.asAt.length() > 0}"> $(function() {
        document.body.style.background = "#fcf8e3";
      });</g:if>
    </r:script>

  </body>
</html>
