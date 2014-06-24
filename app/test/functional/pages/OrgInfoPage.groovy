package pages

import org.openqa.selenium.Keys

/**
 * Created by ioannis on 04/06/2014.
 */
class OrgInfoPage extends BasePage {
    static at = { browser.page.title.startsWith "KB+ Show Org" }
    static content = {
        addIdentifier {
            $("a.select2-default").click()
            $("input.select2-input").value("a")
            $("input.select2-input") << Keys.chord(Keys.ARROW_DOWN)
            $("input.select2-input") << Keys.chord(Keys.ENTER)
            $("input.btn-primary", value: "Add Identifier...").click()
        }
    }
}
