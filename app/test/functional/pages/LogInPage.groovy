package pages

import geb.Page

/**
 * Created by ioannis on 28/05/2014.
 */
class LogInPage extends BasePage {
    static url = "/demo/login/auth"
    static at = { browser.page.title.startsWith "KB+ Login"  };

    static content = {
        login {name, passwd ->
            $("form").j_username= name
            $("form").j_password= passwd
            $("#submit",value:"Login").click()
        }
    }
}
