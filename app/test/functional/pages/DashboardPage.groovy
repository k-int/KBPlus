package pages

/**
 * Created by ioannis on 29/05/2014.
 */
class DashboardPage extends BasePage {
    static url = "/demo/home/index"
    static at = { browser.page.title.startsWith "KB+ Institutional Dash" };

    static content = {

        subscriptions {
            $("a", text: "Subscriptions").click(SubscrDetailsPage)
        }
        licences {
            $("a", text: "Licences").click(LicencePage)
        }
        toDo { ref ->
            $("a", text: ref).click(LicencePage)
        }
        generateWorksheet {
            $("a", text: "Generate Renewals Worksheet").click(MyInstitutionsPage)
        }

    }
}
