package pages

class JasperPage extends BasePage{

    static at = { browser.page.title.contains "Jasper" };

    static content = {
    	addReport { file ->
    		$("input",name:"report_files").value(file)
    		$("input",type:"submit").click(JasperPage)
    	}
    	errorMsg { msg ->
    		$("div.alert-error").children().filter("p").text()?.contains(msg)
    	}

    	alertMsg { msg ->
    		$("div.alert-info").children().filter("p").text()?.contains(msg)
    	}
    	selectReport{ name ->
    		$("#available_reports").value(name)
    		waitFor{$("td",text:"Show titles before ID")}
    	}
    	getReport{
    		$("#submit").click()
    	}



    }

}