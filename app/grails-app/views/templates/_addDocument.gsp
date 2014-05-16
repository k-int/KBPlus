
<div class="modal hide" id="modalCreateDocument">
    <g:form id="upload_new_doc_form" url="[controller:'docWidget',action:'uploadDocument']" method="post" enctype="multipart/form-data">
        <input type="hidden" name="ownerid" value="${ownobj.id}"/>
        <input type="hidden" name="ownerclass" value="${ownobj.class.name}"/>
        <input type="hidden" name="ownertp" value="${owntp}"/>
        <div class="modal-body">
            <div class="inline-lists">
                <dl>
                    <dt>
                        <label>Document Name:</label>
                    </dt>
                    <dd>
                        <input type="text" name="upload_title">
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <label>File:</label>
                    </dt>
                    <dd>
                        <input type="file" name="upload_file" />
                    </dd>
                </dl>
                <dl>
                    <dt>
                        <label>Document Type:</label>
                    </dt>
                    <dd>
                        <select name="doctype">
                            <option value="License">License</option>
                            <option value="General">General</option>
                            <option value="General">Addendum</option>
                        </select>
                    </dd>
                </dl>
            </div>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <input type="submit" class="btn btn-primary" value="Save Changes">
        </div>
    </g:form>

</div>

