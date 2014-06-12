package pages

import org.openqa.selenium.Keys

/**
 * Created by ioannis on 28/05/2014.
 */
class ProfilePage extends BasePage {
    static url = "/demo/profile/index"
    static at = { browser.page.title.startsWith "KB+ User Profile" };

    static content = {
        updateProfile {
            $("input", value: "Update Profile").click()
        }
        displayName { newName ->
            if (newName != null) {
                $("input", name: "userDispName").value(newName)
                updateProfile()
            } else {
                $("input", name: "userDispName").value()
            }
        }
        messageBox { msg ->
            $("div.alert-block").children().filter("p").text().contains(msg)
        }
        errorBox { msg ->
            $("div.alert-block").children().filter("p").text().contains(msg)
        }
        requestMembership { org, role ->
            $('form', 1).org = org
            $('form', 1).formalRole = role
            $('#submitARForm').click()
        }
        setEmail { email ->
            $("input", name: "email").value(email)
            updateProfile()
        }
        pageSize { size ->
            $("input", name: "defaultPageSize").value(size)
            updateProfile()
        }
        showInfoIcon { option ->
            $("span", 'data-name': "showInfoIcon").click()
            waitFor { $("form.editableform") }
            if (option.equals("No")) {
                $("select.input-medium") << Keys.ARROW_DOWN
            }
            $("button.editable-submit").click()
        }
    }
}