package pages

import org.openqa.selenium.Keys

/**
 * Created by ioannis on 29/05/2014.
 * For downloading document: https://blog.codecentric.de/en/2010/07/file-downloads-with-selenium-mission-impossible/
 */
class LicencePage extends AbstractDetails {
    static at = {
        browser.page.title.startsWith("KB+ Current Licences") ||
                browser.page.title.startsWith("KB+")
    }
    static content = {
        createNewLicense { ref ->
            $("a", text: "Create New Licence").click()
            editRef(ref)
        }

        licence { ref ->
            $("a", text: ref).click()
        }
        downloadDoc {
            $("a", text: "Download Doc").click()
        }

        deleteLicence { ref ->
            String licenceUrl = $("a", text: ref).@href
            String licenceId = licenceUrl.substring(licenceUrl.lastIndexOf("/") + 1, licenceUrl.length())
            $("input", type: "radio", name: "baselicense").value(licenceId)
            $("input", name: "delete-licence").click()
        }

        licenceDetails {
            $("a", text: "Licence Details").click()
        }
        addLicence {
            $("a", text: "Add Licence").click()
        }
        createCopyOf { ref ->
            String licenceUrl = $("a", text: ref).@href
            String licenceId = licenceUrl.substring(licenceUrl.lastIndexOf("/") + 1, licenceUrl.length())
            $("input", type: "radio", name: "baselicense").value(licenceId)
            $("input.btn", value: "Copy Selected").click()
        }
        alertBox { text ->
            !$("div.alert-block").children().filter("p", text: text).isEmpty()
        }
        //Following replaced by custom property
        concurentAccessNote { val ->
            $("#concurrentUsers").click()
            waitElement{$("textarea.input-large")}
            $("textarea.input-large").value(val)
            $("button.editable-submit").click()
        }
        acceptAll {
            $("a", text: "Accept All").click(LicencePage)
        }
        rejectOne {
            $("a", text: "Reject").click(LicencePage)
        }
        verifyInformation { column, value ->
            $("label", for: column).parent().next().text().equals(value)

        }
        importONIX { fileName ->
            $("a", text: "Import an ONIX-PL licence").click()
            waitFor{$("input", type: "file", name: "import_file").value(fileName)}
            $("button", text: "Import licence").click()
            def createNew = $("#replace_opl")
            if (!createNew.isEmpty()) {
                createNew.click()
            }
            !$("h2", text: "Upload successful").isEmpty()
            $("a.btn-info").click()
        }
        addCustomPropType{ name ->
            $("#select2-chosen-2").click()
            waitFor { $("#s2id_autogen2_search").isDisplayed() }

            $("#s2id_autogen2_search").value (name)

            waitFor { $("#select2-result-label-3").click()}
            waitFor{ $("input",name:"cust_prop_name").value(name)}
            $("#cust_prop_desc").value("Some random text discr")
            $("#new_cust_prop_add_btn",value:"Add").click()

            $("#select2-chosen-2").click()
            waitFor { $("#s2id_autogen2_search").isDisplayed() }

            $("#s2id_autogen2_search").value (name)
            waitFor{ $("span.select2-match")}
            $("#s2id_autogen2_search") <<Keys.ENTER
            $("#s2id_autogen2_search") << Keys.ENTER
            $("input",value:"Add Property...").click()
        }

    }
}
