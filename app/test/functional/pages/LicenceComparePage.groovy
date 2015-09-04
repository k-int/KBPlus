package pages
/**
 * Created by ryan@k-int.com
 */
class LicenceComparePage extends BasePage {
    static at = {
        browser.page.title.startsWith("KB+")
    }

    static content = {
        header { $("h1").text().trim() }

        addLicense{ val ->
            $("#select2-chosen-1").click()
            $("#s2id_autogen1_search").value(val)
            waitFor{$("div.select2-result-label").click()}
            $("#addToList").click()
        }

        compare { $("input", type:"submit", value: "Compare").click() }

        tableCount {$("table").size()}
    }
}
