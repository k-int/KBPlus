package pages
/**
 * Created by ioannis on 28/05/2014.
 */
class PublicPage extends BasePage {
    static url = "/demo"
    static at = { browser.page.title.startsWith "Knowledge Base+" };

    static content = {
        loginLink {
            waitFor { $("a", text: "Knowledge Base+ Member Login")}
             $("a", text: "Knowledge Base+ Member Login").click()
        }
        
    }
}
