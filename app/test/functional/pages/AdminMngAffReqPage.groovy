package pages

/**
 * Created by ioannis on 28/05/2014.
 */
class AdminMngAffReqPage extends BasePage {
    static at = { browser.page.title.startsWith "KB+ Manage Affiliation" };
    static content = {
        approve {
            $("a.btn", text: "Approve").click()
        }
        empty {
            $("a.btn", text: "Approve").isEmpty()
        }
    }
}
