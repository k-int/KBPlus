package pages

/**
 * Created by ioannis on 04/06/2014.
 */
class MyInstitutionsPage extends BasePage {
    static at = { browser.page.title.startsWith("KB+") }
    static content = {
        comparisonSheet {
            $("button", name: "addBtn", value: "1").click()
            $("button", name: "addBtn", value: "2").click()
            $("button", name: "generate").click()
        }
    }
}
