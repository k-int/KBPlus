package pages
/**
 * Created by ioannis on 02/06/2014.
 */
class PackageDetailsPage extends AbstractDetails {
    static url = "/demo/packageDetails/show/*"
    static at = {
        browser.page.title.startsWith("Edit Package") ||
                browser.page.title.startsWith("KB+ Packages")
    };

    static content = {

        viewPackage { name ->
            $("a", text: name).click()
        }
        makeAnnouncement { title, msg ->
            $("a", text: "Mention this package in an announcement").click()
            $("input", name: "subjectTxt").value(title)
            $("textarea", name: "annTxt").value(msg)
            $("input.btn", type: "submit").click()
        }
        alertMessage { msgtext ->
            !$("div.alert-block").children().filter("p", text: msgtext).isEmpty()
        }
    }
}
