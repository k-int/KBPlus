package pages


class DataManagerPage extends BasePage {

    static at = { browser.page.title.startsWith "KB+" }

    static content = {
        changeLogAllChanges {
            $("input",name:"licenses").value(true)
            $("input",name:"tipps").value(true)
            $("input",name:"titles").value(true)
            $("input",name:"updates").value(true)
            $("input",name:"packages").value(true)
            $("input",name:"creates").value(true)
           
            $("input",type:"submit").click()
            !$("span",text:"23 changes").isEmpty()
        }
        changeLogExportCSV {
            $("a",text:"Exports").click()
            $("a",text:"CSV Export").click()
        }
       
    }
}
