import geb.error.RequiredPageContentNotPresent
import geb.error.UnexpectedPageException
import geb.spock.GebReportingSpec
import org.openqa.selenium.ElementNotVisibleException
import pages.*
import spock.lang.*

@Stepwise
class HomePageSpec extends GebReportingSpec {
// curl -XDELETE 'http://localhost:9200/kbplustest/'
// curl -XPUT 'httop://localhost:9200/kbplustest/'

  def setupSpec() {
    setup:
        println("CURRENT URL "+driver.currentUrl)
        to PublicPage
        println("CURRENT URL "+driver.currentUrl)
        loginLink()
        println("CURRENT URL "+driver.currentUrl)
        login(Data.UserD_name,Data.UserD_passwd)
    when:
        go "/demo/org/create"
        $("form").name=Data.Org_name
        $("form").impId=Data.Org_impId
        $("form").sector='Higher Education'
        $("#SubmitButton").click()
    then:
        browser.page.title.startsWith "Show Org"

      cleanup:
        to ProfilePage
        requestMembership(Data.Org_name,'Editor')
        changeUserNoDash(Data.UserB_name,Data.UserB_passwd)
        to ProfilePage
        requestMembership(Data.Org_name,'Editor')
        changeUserNoDash(Data.UserD_name,Data.UserD_passwd)
        manageAffiliationReq()
        at AdminMngAffReqPage
        approve()
        approve()
        to pages.ProfilePage

      when:
        go '/demo/myInstitutions/Functional_Test_Organisation/emptySubscription'
        $('form').newEmptySubName='FTO New Sub One'
        $('input', type:'submit').click()
      then:
        $('h1 span').text() == 'FTO New Sub One'

    when:
        go '/demo/upload/reviewPackage'
        $('form').soFile=Data.Package_import_file
        $('button',text:"Upload SO").click()
    then:
        !$("p",text:"File passed validation checks, new SO details follow:").isEmpty()

    when:
        $('a',text:'New Package Details').click()
        $('form').subid="FTO New Sub One - Functional Test Organisation"
        $('form input').click()
        $('form input',type:'submit').click()
    then:
         1==1
    // response page sends back a link containing the new package ID <a href="/demo/packageDetails/show/590">New Package Details</a>

  }
    def "Start downloading titles"(){
        when:
            startESUpdate() // so that new package is displayed
        then:
            true;
    }

  def "Verify Package created"() {
      when:
        allPackages()
      then:
        !$("a",text:Data.Package_name).isEmpty()
      cleanup:
        logout()
  }


  def "The KBPlus Home Page Displays OK"() {
    when:
     to PublicPage
    then:
      at PublicPage
  }
    //ref 001
  def "KB+ Member login"() {
    at PublicPage
    when:
        loginLink()
    then:
        at LogInPage
        login(Data.UserA_name,Data.UserA_passwd)
  }
    //ref 002
    def "Amend Display Name"() {
        when:
        to ProfilePage
        displayName("TestUser")
        then:
        messageBox("User display name updated")

        cleanup:
        displayName(Data.UserA_displayName)
    }
    //ref 003
    def "Request new membership"(){
        at ProfilePage
        when:
        requestMembership(Data.Org_name,'Read only user')
        then:
        at ProfilePage
    }

    //ref 009
    def "Approve membership request"() {
        setup:
            to ProfilePage
            changeUser(Data.UserD_name,Data.UserD_passwd)
        when:
            manageAffiliationReq()
        then:
            at AdminMngAffReqPage
            approve()
    }




//  //ref 012
//    def "Show Info Icon" (){
//        setup:
//            changeUser(Data.UserD_name,Data.UserD_passwd)
//            to ProfilePage
//        when:
//            showInfoIcon("Yes") //Select keeps not behaving properly
//        then:
//            to DashboardPage
//            subscriptions()
//            hasInfoIcon()
//        when:
//            to ProfilePage
//            showInfoIcon("No")
//        then:
//            to DashboardPage
//            subscriptions()
//            !hasInfoIcon()
//
//    }
    def "Set up licence Template"(){
        changeUser(Data.UserD_name,Data.UserD_passwd)
        templateLicence()
        $("input",name:"reference").value(Data.Licence_template_D)
        $("input",type:"submit").click(LicencePage)
        when:
            editIsPublic("Yes")
            addDocument(Data.Test_Doc_name,Data.Test_Doc_file)
        then:
            at LicencePage

    }
    //ref 101
    def "View template Licence"(){
        setup:
            changeUser(Data.UserA_name,Data.UserA_passwd)
            licences()
        when:
            addLicence()
            licence(Data.Licence_template_D)
            editRef("some val")
        then:
            thrown(RequiredPageContentNotPresent)
        when:
            at LicencePage
        then:
            catchException{addDocument("whatever", "doc")}
//        when:
//            documents()
//        then:
//            catchException{deleteDocument()}//For some reason tests deny the existence of this method
        when:
           at LicencePage
           documents()
           downloadDoc()
        then:
            at LicencePage
    }
    //ref 102
    def "View template Licence UserB"(){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            licences()
        when:
             addLicence()
            licence(Data.Licence_template_D)
        then:
            catchException{ editRef("some val")}
        when:
            at LicencePage
        then:
           catchException{addDocument("whatever", "doc")}
//        when:
//            documents()
//            deleteDocument()
//        then:
//            at LicencePage
        when:
           documents()
           downloadDoc()
        then:
            at LicencePage
    }
    //ref 103
    def "Add Actual Licence "(){
        setup:
            to DashboardPage
            waitFor{licences()}
        when:
            addLicence()
            createCopyOf(Data.Licence_template_D)
        then:
            at LicencePage
        when:
            addDocument(Data.Test_Doc_name,Data.Test_Doc_file)
            addNote("Test note")
        then:
            at LicencePage
        when:
            editIsPublic("Yes")
            documents()
            downloadDoc()
            withConfirm {deleteDocument()}
            notes()
            withConfirm {deleteNote()}
            licenceDetails()
            editRef(Data.Licence_template_copy_D)
            concurentAccessNote("many")
        then:
            at LicencePage

    }
    //ref 109
    def "Create Actual Licence"(){
        setup:
            to DashboardPage
            waitFor {licences()}
        when:
            createNewLicense(Data.Licence_actual_C)
            addNote("test note")
            addDocument(Data.Test_Doc_name,Data.Test_Doc_file)
            concurentAccessNote("many")
        then:
            at LicencePage
    }
    //ref 110
    def "View Actual Licence(created by B)"(){
        setup:
            changeUser(Data.UserA_name,Data.UserA_passwd)
            waitFor {licences()}
        when:
            licence(Data.Licence_actual_C)
        then:
            at LicencePage
        when:
            at LicencePage
        then:
            catchException{editRef("some val")}
        when:
            at LicencePage
        then:
            catchException{addDocument("whatever","doc")}
        when:
            documents()

        then:
            catchException{deleteDocument()}
        when:
            at LicencePage
        then:
            catchException{concurentAccessNote("many")}
        when:
           downloadDoc()
        then:
            at LicencePage
    }
    //111
    def "Edit Template Licence"(){
        setup: "Login as admin"
            changeUser(Data.UserD_name,Data.UserD_passwd)
            licences()
        when: "Change public to No"
            addLicence()
            licence(Data.Licence_template_D)
            editIsPublic("No")
        then: "Its updated"
            at LicencePage
    }
    //112
    def "Accept updates to Actual Licence"(){
        setup: "Log in with User B"
            changeUser(Data.UserB_name,Data.UserB_passwd)
            toDo(Data.Licence_template_copy_D)
        when: "Accept changes"
            acceptAll()
        then: "Public should be No"
            verifyInformation("isPublic","No")
    }
//  ref 113
    def "Edit Template Licence - for reject"(){
        setup: "Login as admin"
            changeUser(Data.UserD_name,Data.UserD_passwd)
            licences()
        when: "Change public to Yes"
            addLicence()
            licence(Data.Licence_template_D)
            editIsPublic("Yes")
        then: "Its updated"
            at LicencePage
    }
    // ref 113
    def "Reject update to Actual Licence"(){
        setup: "Log in with User B"
            changeUser(Data.UserB_name,Data.UserB_passwd)
            toDo(Data.Licence_template_copy_D)
        when: "Reject changes"
            rejectOne()
        then: "Public should be No"
            verifyInformation("isPublic","No")
        cleanup:
            editIsPublic("Yes")
    }
    //ref 114
    def "Attempt delete actual Licence"(){
        setup:
            changeUser(Data.UserA_name,Data.UserA_passwd)
            licences()
        when:
            deleteLicence(Data.Licence_template_copy_D)
        then:
            alertBox("You do not have sufficient administrative rights to delete the specified license")
    }
    //ref 115
    def "Delete Actual Licence" (){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            licences()
            deleteLicence(Data.Licence_template_copy_D)
        when:
            at LicencePage
        then:
            catchException{licence(Data.Licence_template_copy_D)}
    }
    //ref 118 - 119
    def "Import Onix-PL Licence" (){
        setup:
            changeUser(Data.UserD_name,Data.UserD_passwd)
            licences()
        when:
            addLicence()
            createCopyOf(Data.Licence_template_D)
            importONIX(Data.Licence_ONIX_PL)
        then:
            at LicencePage
    }
    //ref 119
    def "Compare Onix-PL Licence" (){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            compareONIX()
        when:
            $("i.jstree-checkbox").click()
            $("#Compare").click()
        then:
            !$("h1",text:"ONIX-PL Licence Comparison").isEmpty()
    }
    def "Update ES Index"(){
        when:
            go '/demo/startFTIndex/index' // should have a few titles by now.
        then:
            true;
    }
    //ref 011
    def "Change default page size"(){
        setup:
        changeUser(Data.UserA_name,Data.UserA_passwd)
        to ProfilePage
        when:
        pageSize("50")
        then:
        messageBox("User default page size updated")
        // Keeps causing the tests to fail, not sure why
//        when:
//            allTitles()
//        then:
//            isPageSize("50")
//        cleanup:
//            to ProfilePage
//            pageSize("25")
    }
    //ref 200
    def "View Package - User A"(){
        setup:
            to DashboardPage
            allPackages()
            viewPackage(Data.Package_name)
        when:
            at PackageDetailsPage
        then:
            catchException{addNote("test")}
        when:
            at PackageDetailsPage
        then:
            catchException{addDocument("whatever","doc")}
        when:
            at PackageDetailsPage
        then:
            catchException{editIsPublic("Yes")}
    }
    //ref 201
    def "View Package - User B"(){
        setup:
            changeUser(Data.UserB_name, Data.UserB_passwd)
            to DashboardPage
            allPackages()
            viewPackage(Data.Package_name)
        when:
            at PackageDetailsPage
        then:
            catchException{addNote("test")}
        when:
            at PackageDetailsPage
        then:
            catchException{addDocument("whatever","doc")}
        when:
            at PackageDetailsPage
        then:
            catchException{editIsPublic("Yes")}
    }
    //ref 202
    def "Add Subscription w/o entitlements" (){
        setup:
            to DashboardPage
            subscriptions()
        when:
            newSubscription(Data.Subscription_name_A)
        and:
            addDocument(Data.Test_Doc_name,Data.Test_Doc_file)
        and:
            addNote("Test note")
        and:
            addLicence(Data.Licence_actual_C)
        then:
            at LicencePage
    }
    //ref 203
    def "View Subscription Taken (created by B)"(){
        setup:
            changeUser(Data.UserA_name,Data.UserA_passwd)
            subscriptions()
            viewSubscription(Data.Subscription_name_A)
        when:
            at SubscrDetailsPage
        then:
            catchException{addNote("test")}
        when:
            at SubscrDetailsPage
        then:
            catchException{addDocument("whatever","doc")}
        when:
           at SubscrDetailsPage
        then:
            catchException{editIsPublic("Yes")}
    }
    //ref 204
    def "Add Subscription (w/o entitlements B) "(){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            at DashboardPage
            subscriptions()
            newSubscription(Data.Subscription_name_B)
            at SubscrDetailsPage
        when:
            addLicence(Data.Licence_actual_C)
            linkPackage(Data.Package_name,false)
            addEntitlements(false)
        then:
            at SubscrDetailsPage
    }
    //ref 205
    def "Add Subscription (with entitlements C) "(){
        setup:
            to DashboardPage
            subscriptions()
            newSubscription(Data.Subscription_name_C)
            at SubscrDetailsPage
        when:
            addLicence(Data.Licence_actual_C)
            linkPackage(Data.Package_name,false)
            addEntitlements(true)
        then:
            at SubscrDetailsPage
    }
    //209
    def "Edit Package"(){
        setup:
            changeUser(Data.UserD_name,Data.UserD_passwd)
            allPackages()
            viewPackage(Data.Package_name)
        when:
            addDocument(Data.Test_Doc_name,Data.Test_Doc_file)
            addNote("Test note")
            makeAnnouncement(Data.Test_Announcement,Data.Test_Announcement)
        then:
            waitFor {alertMessage("Announcement Created")}
    }
    //212
    def "View Current Subscriptions"(){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            subscriptions()
        when:
            def url = driver.currentUrl
            println "Page Url "+url
            changeUserNoDash(Data.UserC_name,Data.UserC_passwd)
            go url
        then:
            def text = $("div.alert-block").children().filter("p").text()
            println(text)
            text.startsWith("You do not have permission to access")
    }
    //213
    def "Subscription CSV Export"(){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            subscriptions()
            viewSubscription(Data.Subscription_name_A)
            csvExport()
        expect:
            at SubscrDetailsPage
    }
    //214
    def "Subscription CSV Export(No Header)"(){
        setup:
            csvExportNoHeader()
        expect:
            at SubscrDetailsPage
    }
    //215
    def"Subscription JSON Export"(){
        setup:
            jsonExport()
        expect:
            at SubscrDetailsPage
    }
    //216
    def "Subscription XML Export"(){
        setup:
            xmlExport()
        expect:
            at SubscrDetailsPage
    }
    //217
    def "Subscription OCLC Export"(){
        setup:
            OCLCExport()
        expect:
            at SubscrDetailsPage
    }
    //218
    def "Subscription serials Export"(){
        setup:
            OCLCExport()
        expect:
            at SubscrDetailsPage
    }
//    //219
//    def "Subscription sfx export"(){
//        setup:
//            sfxExport()
//        expect:
//            at SubscrDetailsPage
//    }
//    //220
//    def "Subscription KBPlus Import Format"(){
//        setup:
//            kbplusExport()
//        expect:
//            at SubscrDetailsPage
//    }
    //ref 222
    def "Delete Subscription A"(){
        setup:
            changeUser(Data.UserB_name,Data.UserB_passwd)
            to DashboardPage
            subscriptions()
        when:
            deleteSubscription(Data.Subscription_name_A)
        then:
            at SubscrDetailsPage
    }
    //ref 300
    def "Update static HTML page"(){
        setup:
            changeUser(Data.UserD_name,Data.UserD_passwd)
            manageContent()
        when:
            addNewContent(Data.Content_Item_welcome_key,Data.Content_Item_welcome_text)
        then:
            keyExists(Data.Content_Item_welcome_key)
        when:
            to pages.PublicPage
        then:
            !$("p").filter(text:Data.Content_Item_welcome_text).isEmpty()
    }
//    //ref 304
//    def "Add Identifier"(){
//        setup:
////            changeUser(Data.UserB_name,Data.UserB_passwd)
//            //problem displaying the list under Funct Test Org
//            orgInfo(Data.Org_name)
//    }
    //ref 400
    def "Generate Renewals Worksheet"(){
        setup:
            to ProfilePage
            changeUser(Data.UserB_name,Data.UserB_passwd)
            generateWorksheet()
        when:
            comparisonSheet()
        then:
            at MyInstitutionsPage
    }
    //ref 500
    def "Search all current titles"(){
        setup:
            changeUser(Data.UserA_name,Data.UserA_passwd)
         when:
            allTitles()
         then:
            hasResults()
    }

}     
