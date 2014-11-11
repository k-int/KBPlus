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
            $("a", text: "Add Blank Licence").click()
            editRef(ref)
        }

        openLicence { ref ->
            $("a", text: ref).click()
        }
        downloadDoc {
            $("a", text: "Download Doc").click()
        }

        deleteLicence { ref ->
            $("a", text: ref).parent().siblings().find("a",text:"Delete").click()
        }

        licenceDetails {
            $("a", text: "Licence Details").click()
        }
        viewTemplateLicences {
            $("a", text: "Copy from Template").click()
        }
        createCopyOf { ref ->
            $("a", text: ref).parent().siblings().find("a",text:"Copy").click()
        }
        alertBox { text ->
            !$("div.alert-block").children().filter("p", text: text).isEmpty()
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

        searchLicence { date, ref ->
            $("input",name:"validOn").value(date)
            $("input",name:"keyword-search").value(ref)
            $("input",type:"submit",value:"Search").click()
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
            $("#select2-chosen").click()
            $("#s2id_autogen2_search").value(name)
            waitFor{$("div.select2-result-label").click()}
            $("#select2-chosen-2").click()

        }

    }
}
