<div class="well notes">
  <h7>Notes</h7>
  <ul>
    <li class="ipe">Integer eu sapien vitae velit malesuada varius quis vel diam. Vivamus
                            nec nulla sed lectus scelerisque placerat. Praesent sed nisl nisi, ut auctor
                            ipsum.</li>
    <li class="ipe">Integer eu sapien vitae velit malesuada varius quis vel diam. Vivamus
                            nec nulla sed lectus scelerisque placerat. Praesent sed nisl nisi, ut auctor
                            ipsum.</li>
    <li class="ipe">Integer eu sapien vitae velit malesuada varius quis vel diam. Vivamus
                            nec nulla sed lectus scelerisque placerat. Praesent sed nisl nisi, ut auctor
                            ipsum.</li>
  </ul>
  <div class="pagination">
    <ul>
      <li>
        <a href="#">&laquo;</a>
      </li>
      <li class="active">
                                <a href="#">1</a>
      </li>
      <li>
                                <a href="#">2</a>
      </li>
      <li>
                                <a href="#">3</a>
      </li>
      <li>
                                <a href="#">4</a>
      </li>
      <li>
                                <a href="#">&raquo;</a>
      </li>
    </ul>
  </div>
  <input type="submit" class="btn btn-primary" value="Add new note" data-toggle="modal" href="#modalCreateNote" />
</div>

<!-- Lightbox modal for creating a note taken from licenceNotes.html -->
<div class="modal hide" id="modalCreateNote">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal">×</button>
    <h3>Create New Note</h3>
  </div>
  <form action="" method="POST">
    <div class="modal-body">
      <dl>
        <dt>
          <label>Note:</label>
        </dt>
        <dd>
          <textarea name="licenceNote"></textarea>
        </dd>
      </dl>
      <dl>
        <dt>
          <label>Shared:</label>
        </dt>
        <dd>
          <select name="licenceNoteShared">
            <option value="0">Not Shared</option>
            <option value="1">JISC Collections</option>
            <option value="2">Community</option>
          </select>
        </dd>
      </dl>
    </div>
    <div class="modal-footer">
      <a href="#" class="btn" data-dismiss="modal">Close</a>
      <a href="#" class="btn btn-primary">Save changes</a>
    </div>
  </form>
</div>
