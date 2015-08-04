package pages

import geb.Page
import geb.error.RequiredPageContentNotPresent
import grails.plugin.remotecontrol.*

/**
 * Created by ioannis on 28/05/2014.
 */
class BasePage extends Page {
    def remote = new RemoteControl()

    String getMessage(String code, Object[] args = null, Locale locale=null) {
            remote.exec { ctx.messageSource.getMessage(code, args, locale) }
    }
    static content = {

        alertBox { msg ->
            $("div.alert-warning").children().filter("p").text().contains(msg)
        }
        messageBox { msg ->
            $("div.alert-block").children().filter("p").text().contains(msg)
        }
        errorBox { msg ->
            $("div.alert-block").children().filter("p").text().contains(msg)
        }
        terms {
            $("ul.footer-sublinks").children().find("a", text: "Terms & Conditions").click()
        }
        privacy {
            $("ul.footer-sublinks").children().find("a", text: "Privacy Policy").click()
        }
        freedom {
            $("ul.footer-sublinks").children().find("a", text: "Freedom of Information Policy").click()
        }
        help {
            $("a", text: "Institutions").click()
            $("a", text: "Help").click()
        }
        home {
            $("a.brand", text: "KB+").click()
        }
        support {
            $("#zenbox_tab").click()
        }
        logout {
            $("ul.pull-right").children().find("a.dropdown-toggle").click()
            waitFor{$("a", text: "Logout").click()}
        }
        manageAffiliationReq {
            $("a", text: "Admin Actions").click()
            $("a", text: "Manage Affiliation Requests").click()
        }
        templateLicence {
            $("a", text: "Data Managers").click()
            waitFor{ $("a", text: "New Licence").click()}
        }
        changeUserNoDash { user, passwd ->
            $("ul.pull-right").children().find("a.dropdown-toggle").click()
            $("a", text: "Logout").click()
            waitFor { $("a", text: "Knowledge Base+ Member Login").click() }
            $("form").j_username = user
            $("form").j_password = passwd
            $("#submit", value: "Login").click()
        }
        changeUser { user, passwd ->
            $("ul.pull-right").children().find("a.dropdown-toggle").click()
            waitFor{$("a", text: "Logout").click()}
            waitFor { $("a", text: "Knowledge Base+ Member Login").click() }
            $("form").j_username = user
            $("form").j_password = passwd
            $("#submit", value: "Login").click(DashboardPage)
        }
        hasInfoIcon {
            !$("i.icon-info-sign").isEmpty()
        }
        compareONIX {
            $("a", text: "Institutions").click()
            $("a", text: getMessage("menu.institutions.comp_onix")).click()
        }
        allPackages {
            $("a", text: "Institutions").click()
            $("a", text: "All Packages").click(PackageDetailsPage)
        }
        manageContent {
            $("a", text: "Admin Actions").click()
            $("a", text: "Manage Content Items").click(AdminMngContentItemsPage)
        }
        toCompareSubscriptions{
           $("a", text: "Data Managers").click()
           $("a", text: "Compare Subscriptions").click(SubscrDetailsPage) 
        }
        orgInfo { name ->
            $("input", name: "orgNameContains").value(name)
            $("input.btn-primary", value: "GO").click()
            $("a", text: name).click()
        }
        allTitles {
            $("a", text: "Institutions").click()
            $("a", text: "All Titles").click(TitleDetailsPage)
        }
        startESUpdate {
            $("a", text: "Admin Actions").click()
            $("a",text:"Batch tasks").click()
            $("a", text: "Start ES Index Update").click()
        }
        catchException { run ->
            def exec = false;
            try {
                run()
            } catch (RequiredPageContentNotPresent e) {
                exec = true;
            } catch (org.openqa.selenium.ElementNotVisibleException ex) {
                exec = true;
            }
            exec
        }
        waitElement {run ->
            try{
                waitFor{run()}
            } catch (geb.waiting.WaitTimeoutException e) {
                throw new RequiredPageContentNotPresent()
            }
        }

        uploadJasper { 
            $("a", text: "Admin Actions").click()
            waitFor{ $("a", text: "Upload Report Definitions").click(JasperPage)}
        }

        generateJasper {
            $("a", text: "Data Managers").click()
            waitFor{ $("a", text: "Reports").click(JasperPage)}
        }
        dmChangeLog {
            $("a", text: "Data Managers").click()
            waitFor{ $("a",text:"Data Manager Change Log").click(DataManagerPage)}
        }

        showInstMenu {
            $("a",text:"Institutions").click()
            $("a",text:"Functional Test Organisation").siblings("ul").jquery.show()
        }

        toComparePackages {
            $("a",text:"Institutions").click()
            $("a",text:"Compare Packages").click(PackageDetailsPage)
        }

    }
}
