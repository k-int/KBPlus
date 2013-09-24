package com.k_int.kbplus;


// http://grails.1312388.n4.nabble.com/Simple-Canoo-Webtest-is-failing-td3869767.html

public class ChromeBrowserClient extends com.grailsrocks.functionaltest.client.BrowserClient {


   ChromeBrowserClient(com.grailsrocks.functionaltest.client.ClientAdapter listener) {
     this.listener = listener
     browser='CHROME'
     _client = new com.gargoylesoftware.htmlunit.WebClient(com.gargoylesoftware.htmlunit.BrowserVersion['CHROME'])
     _client.addWebWindowListener((com.grailsrocks.functionaltest.client.BrowserClient)this)
     _client.redirectEnabled = false // We're going to handle this thanks very much
     _client.popupBlockerEnabled = true
     _client.javaScriptEnabled = true
     _client.throwExceptionOnFailingStatusCode = false
     _client.pageCreator = interceptingPageCreator
   }
}
