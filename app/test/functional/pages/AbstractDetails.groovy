package pages

import geb.error.RequiredPageContentNotPresent

/**
 * Created by ioannis on 03/06/2014.
 */
class AbstractDetails extends BasePage {
    static content = {
        addDocument { title, file ->
            $("input", value: "Add new document").click()
            $("input", type: "text", name: "upload_title").value(title)
            $("input", type: "file", name: "upload_file").value(file)
            $("input.btn", name: "SaveDoc", value: "Save Changes").click()
        }
        addNote { text ->
            $("input", value: "Add new note").click()
            $("textarea", name: "licenceNote").value(text)
            $("input.btn", name: "SaveNote", value: "Save Changes").click()
        }
        deleteDocument {
            $("input", type: "checkbox").value("true")
            $("#delete-doc").click()
        }
        deleteNote {
            $("input", type: "checkbox").value("true")
            $("input.delete-document").click()
        }

        editRef { val ->
            $("span", 'data-name': "reference").click()
            $("textarea.input-large").value(val)
            $("button.editable-submit").click()
        }
        editIsPublic { option ->
            $("span", 'data-name': "isPublic").click()
            try {
                waitFor { $("form.editableform") }
            } catch (geb.waiting.WaitTimeoutException e) {
                throw new RequiredPageContentNotPresent()
            }
            $("select.input-medium").value(option)
            $("button.editable-submit").click()
        }

        documents {
            $("a", text: "Documents").click()
        }
        notes {
            $("a", text: "Notes").click()
        }
    }
}
