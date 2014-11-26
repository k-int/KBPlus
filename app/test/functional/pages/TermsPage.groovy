package pages
/**
 * Created by ioannis on 28/05/2014.
 */
class TermsPage extends BasePage {
    static at = { browser.page.title.startsWith "Terms and Conditions" }
    static url = "/demo/terms-and-conditions"

    static content = {
    	downloadTerms {
    		$("a",text:"View the terms and conditions (Word Doc, 123KB)").click()
    	}
    }
}
