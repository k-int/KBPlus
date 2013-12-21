/*
        This is the Geb configuration file.
        See: http://www.gebish.org/manual/current/configuration.html
*/

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver

// driver = { new ChromeDriver() }
driver = { new FirefoxDriver() }
// driver = { 
//   def d = new HtmlUnitDriver()
//   d.setJavascriptEnabled(true) 
//   return d
// }

environments {
        
        // run as “grails -Dgeb.env=chrome test-app”
        // See: http://code.google.com/p/selenium/wiki/ChromeDriver
        chrome {
                driver = { new ChromeDriver() }
        }
        
        // run as “grails -Dgeb.env=firefox test-app”
        // See: http://code.google.com/p/selenium/wiki/FirefoxDriver
        firefox {
                driver = { new FirefoxDriver() }
        }

}
