package pages
/**
 * Created by ioannis on 28/05/2014.
 */
class FreedomPage extends BasePage {
    static at = { browser.page.title.startsWith "Freedom of Information" }
}
