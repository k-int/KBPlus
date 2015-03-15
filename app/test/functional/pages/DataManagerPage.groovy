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
            browser.report( "Page 1")
            $("a.step",text:"2").click()

            browser.report( "Page 2")

            $("a.step",text:"3").click()
            browser.report( "Page 3")

            !$("span",text:"21 changes").isEmpty()
        }
        changeLogExportCSV {
            $("a",text:"Exports").click()
            $("a",text:"CSV Export").click()
        }
       
    }
}
