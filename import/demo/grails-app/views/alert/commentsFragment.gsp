      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <g:if test="${alert}">
        <h3>Alert Comments</h3>
        </g:if>
        <g:else>
          Unknown alert code. Please report.
        </g:else>
      </div>
      <div class="modal-body">
        <g:if test="${alert}">
          <div id="thecomments">
            <table width="100%">
              <thead>
                <tr>
                  <th>Comment Date</th>
                  <th>Comment</th>
                  <th>By</th>
                </tr>
              </thead>
              </tbody>
                <g:each in="${alert.comments}" var="comment">
                  <tr>
                    <td>${comment.commentDate}</td>
                    <td>${comment.comment}</td>
                    <td>${comment.by.displayName}</td>
                  </tr>
                </g:each>
                <g:if test="${alert.comments.size() == 0}">
                  <tr>
                    <td colspan="3">No comments yet</td>
                  </tr>
                </g:if>
                <tr>
              </tbody>
            </table>
            <hr>
            <h4>New comment</h4>
            <g:form controller="alert" action="addComment">
              <input type="hidden" name="alertid" value="${params.id}"/>
              <textarea name="newcomment" height="4"/><br/>
              <input type="submit" class="btn btn-primary" value="Save Comment">
            </g:form>
          </div>
        </g:if>
        <g:else>
          Unknown alert code. Please report.
        </g:else>
      </div>
      <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Close</a>
      </div>

