package pages

/**
 * Created by ioannis on 04/06/2014.
 */
class AdminMngContentItemsPage extends BasePage {
    static at = { browser.page.title.startsWith "KB+ Manage Content Items" }
    static content = {
        addNewContent { key, contents ->
            $("input", name: "key").value(key)
            $("textarea", name: "content").value(contents)
            $("input.btn", type: "submit").click()
        }
        keyExists { key ->
            !$("td").filter(text: key).isEmpty()
        }
    }
}
