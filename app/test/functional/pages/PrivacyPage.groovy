package pages
/**
 * Created by ioannis on 28/05/2014.
 */
class PrivacyPage extends BasePage {
	static url = "/demo/privacy-policy"
    static at = { browser.page.title.startsWith "Privacy Policy" }

}
