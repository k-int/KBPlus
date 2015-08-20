package pages

import org.openqa.selenium.Keys

/**
 * Created by ioannis on 28/05/2014.
 */
class UploadIssnLPage extends BasePage {
    static url = "/demo/admin/uploadIssnL"
    static at = { browser.page.title.startsWith "KB+ Admin::Identifier Same-As Upload" };

    static content = {

        // updateProfile {
        //     $("input", value: "Update Profile").click()
        // }

        // displayName { newName ->
        //     if (newName != null) {
        //         $("input", name: "userDispName").value(newName)
        //         updateProfile()
        //     } else {
        //         $("input", name: "userDispName").value()
        //     }
        // }

        // requestMembership { org, role ->
        //     $('form', 1).org = org
        //     $('form', 1).formalRole = role
        //     $('#submitARForm').click()
        // }
        // setEmail { email ->
        //     $("input", name: "email").value(email)
        //     updateProfile()
        // }
        // pageSize { size ->
        //     $("input", name: "defaultPageSize").value(size)
        //     updateProfile()
        // }
        // showInfoIcon { option ->
        //     $("span", 'data-name': "showInfoIcon").click()
        //     waitFor { $("form.editableform") }
        //     waitFor { $("select.input-medium")}
        //     $("select.input-medium").value(option)
        //     $("button.editable-submit").click()
        // }
    }
}
