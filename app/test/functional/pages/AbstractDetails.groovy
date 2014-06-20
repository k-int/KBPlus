package pages

import geb.error.RequiredPageContentNotPresent

/**
 * Created by ioannis on 03/06/2014.
 */
class AbstractDetails extends BasePage {
    static content = {
        addDocument { title, file ->
            $("input", value: "Add new document").click()
            waitElement { $("input", type: "text", name: "upload_title")}
            $("input", type: "text", name: "upload_title").value(title)
            $("input", type: "file", name: "upload_file").value(file)
            $("input.btn", name: "SaveDoc", value: "Save Changes").click()
        }
        addNote { text ->
            $("input", value: "Add new note").click()
            waitElement{$("textarea", name: "licenceNote")}
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
            browser.report( "Clicked span")
            waitElement{$("textarea.input-large")}
            $("textarea.input-large").value(val)
            browser.report( "Clicked span")

            $("button.editable-submit").click()
        }
        editIsPublic { option ->
            $("span", 'data-name': "isPublic").click()
            waitElement { $("form.editableform") }
            waitFor {$("select.input-medium")}
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
