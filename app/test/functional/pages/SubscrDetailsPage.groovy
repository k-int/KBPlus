package pages
/**
 * Created by ioannis on 03/06/2014.
 */
class SubscrDetailsPage extends AbstractDetails {
    static url = "/demo/subscriptionDetails/show/*"
    static at = {
        browser.page.title.endsWith("- Current Subscriptions") ||
                browser.page.title.startsWith("KB+ Subscription")||
                  browser.page.title.equals("Edit Subscription") 
    };
    static content = {
        newSubscription { ref ->
            $("a", text: "Add New Subscription").click()
            waitElement{$("input", name: "newEmptySubName")}
            $("input", name: "newEmptySubName").value(ref)
            $("input", value: "Create").click()
        }
        licenceCategory {
            $("span", 'data-name': "licenceCategory").click()
            waitElement{$("button.editable-submit")}
            $("button.editable-submit").click()
        }
        viewSubscription { ref ->
            $("a", text: ref).click()
        }

        addLicence { ref ->
            $("span", 'data-name': "owner").click()
            waitElement { $("form.editableform") }
            waitFor{$("select.input-medium")}
            $("select.input-medium").value(ref)
            $("button.editable-submit").click()
        }
        linkPackage { ref, entitlements ->
            $("a", text: "Link Package").click()
            def td = waitFor{$("a", text: ref).parent().siblings().next()}
            if (entitlements) {
                withConfirm { td.find("a", text: "Link (with Entitlements)").click() }
            } else {
                withConfirm { td.find("a", text: "Link (no Entitlements)").click() }

            }
        }
        addEntitlements {
            $("a", text: "Add Entitlements").click()
            $("input", name: "chkall").click()
            $("input", value: "Add Selected Entitlements").click()
        }
        csvExport {
            $("a", text: "Exports").click()
            $("a", text: "KBPlus (CSV)").click()
        }
        jsonExport {
            $("a", text: "Exports").click()
            $("a", text: "JSON").click()
        }
        compareSubscriptions{ ref1, ref2 ->
            $("#select2-chosen-1").click()
            $("#s2id_autogen1_search").value(ref1)
            waitFor{$("div.select2-result-label").click()}
            $("#select2-chosen-2").click()
            $("#s2id_autogen2_search").value(ref2)
            waitFor{$("div.select2-result-label").click()}
            $("input",type:"submit",value:"Compare").click()
        }

        jsonExport {
            $("a", text: "Exports").click()
            $("a", text: "JSON").click()
        }
        xmlExport {
            $("a", text: "Exports").click()
            $("a", text: "XML").click()
        }
        OCLCExport {
            $("a", text: "Exports").click()
            $("a", text: "OCLC Resolver").click()
        }
        serialsExport {
            $("a", text: "Exports").click()
            $("a", text: "Serials Solutions Resolver").click()
        }
        sfxExport {
            $("a", text: "Exports").click()
            $("a", text: "SFX Resolver").click()
        }
        kbplusExport {
            $("a", text: "Exports").click()
            $("a", text: "KBPlus Import Format").click()

        }
        deleteSubscription { ref ->
            withConfirm { 
                $("a", text: ref).parent().siblings().find("a",text:"Delete").click()
            }
        }
    }
}
