package pages
/**
 * Created by ioannis on 02/06/2014.
 */
class PackageDetailsPage extends AbstractDetails {
    static url = "/demo/packageDetails/show/*"
    static at = {
        browser.page.title.startsWith("Edit Package") ||
                browser.page.title.startsWith("KB+ Packages")
    };

    static content = {

        viewPackage { name ->
            $("a", text: name).click()
        }
        makeAnnouncement { title, msg ->
            $("a", text: "Mention this package in an announcement").click()
            waitFor{$("input", name: "subjectTxt").value(title)}
            $("textarea", name: "annTxt").value(msg)
            $("input.btn", type: "submit").click()
        }
        alertMessage { msgtext ->
            !$("div.alert-block").children().filter("p", text: msgtext).isEmpty()
        }
        searchPackage{ text ->
            $("input",name:"q").value(text)
            $("button",name:"search").click()
        }
        
        numberOfResults{
            if(!$("div.paginateButtons").isEmpty()){
              return $("div.paginateButtons").text()
            }else{
                if(!$("p",text:"No matches found.").isEmpty()){
                  return "0"
                }
            }
        }

        comparePackages{ ref1, ref2 ->
           $("#select2-chosen-1").click()
           $("#s2id_autogen1_search").value(ref1)
           waitFor{$("div.select2-result-label").click()}
           $("#select2-chosen-2").click()
           $("#s2id_autogen2_search").value(ref2)
           waitFor{$("div.select2-result-label").click()}
           $("input",type:"submit",value:"Compare").click()
       }
    }
}
