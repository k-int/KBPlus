package pages

/**
 * Created by ioannis on 29/05/2014.
 */
class TitleDetailsPage extends BasePage {
    static url = "/demo/titleDetails/index"
    static at = { browser.page.title.startsWith "KB+ Titles" };

    static content = {
        isPageSize { size ->
            $("div.paginateButtons").text().contains("1 - " + size)
        }
        hasResults {
            $("div.paginateButtons").text().contains("Showing Results 1 -")
        }
        searchTitle{ text ->
            $("input",name:"title").value(text)
            $("button",name:"search").click()
        }
        numberOfResults{
            String numString = $("div.paginateButtons").text()
            numString = numString.substring(numString.lastIndexOf(" of ")+4)
            numString.toInteger()
        }
    }
}
