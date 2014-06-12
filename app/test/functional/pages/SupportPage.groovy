package pages

import geb.Page

/**
 * Created by ioannis on 28/05/2014.
 */
class SupportPage extends Page {
    static at = { browser.page.title.startsWith("KB+ Support") }
}
