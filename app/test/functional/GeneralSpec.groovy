import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise
import spock.lang.Ignore

@Stepwise
class GeneralSpec extends BaseSpec {
	// curl -XDELETE 'http://localhost:9200/kbplustest/'
	// curl -XPUT 'httop://localhost:9200/kbplustest/'

	// def "Login"(){
	// 	when:
	// 	to PublicPage
	// 	loginLink()
	// 	at LogInPage
	// 	login(Data.UserD_name, Data.UserD_passwd)
	// 	then:
	// 	at DashboardPage
	// }

	def "Create organisation" (){
		setup:
		to PublicPage
		loginLink()
		at LogInPage
		login(Data.UserD_name, Data.UserD_passwd)
		when:
		go "/demo/org/create"
		$("form").name = Data.Org_name
		$("form").impId = Data.Org_impId
		$("form").sector = "Higher Education"
		report "google home page"
		$("#SubmitButton").click()
		then:
		browser.page.title.startsWith "Show Org"
	}

	def "Setup user affiliations"(){
		when:
		to ProfilePage
		requestMembership(Data.Org_name, 'Editor')
		changeUserNoDash(Data.UserB_name, Data.UserB_passwd)
		to ProfilePage
		requestMembership(Data.Org_name, 'Editor')
		changeUserNoDash(Data.UserD_name, Data.UserD_passwd)
		manageAffiliationReq()
		at AdminMngAffReqPage
		approve()
		approve()
		to ProfilePage
		then:
		at ProfilePage
	}

	def "Setup subscription"(){
		when:
		go "/demo/admin/globalSync"
		go '/demo/myInstitutions/'+Data.Org_Url+'/emptySubscription'
		$('form').newEmptySubName = Data.Subscription_name_A
		$('input', type: 'submit').click()
		then:
		$('h1 span').text() == Data.Subscription_name_A
	}

	def "Setup new package" (){
		when:
		go '/demo/upload/reviewPackage'
		$('form').soFile = Data.Package_import_file
		$('button', text: "Upload SO").click()
		then:
		waitFor{$("div.alert-success")}

		when:
		$('a', text: 'New Package Details').click()
		$('form').subid = Data.Subscription_name_A +" - Functional Test Organisation"
		$('#addEntitlementsCheckbox').click()
		$('#add_to_sub_submit_id').click()
		then:
		1 == 1
		// response page sends back a link containing the new package ID <a href="/demo/packageDetails/show/590">New Package Details</a>
	}

	def "Start downloading titles"() {
		when:
		go '/demo/admin/fullReset' // so that new package is displayed
		then:
		true;
	}

	def "Verify Package created"() {
		setup:
		to DashboardPage
		when:
		allPackages()
		then:
		!$("a", text: Data.Package_name).isEmpty()
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
		when:
		go "/demo/home/index"
		then:
		at LogInPage
		login(Data.UserA_name, Data.UserA_passwd)
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
	def "Request new membership"() {
		when:
		at ProfilePage
		requestMembership(Data.Org_name, 'Read only user')
		then:
		at ProfilePage
	}

	//ref 009
	def "Approve membership request"() {
		setup:
		to ProfilePage
		changeUser(Data.UserD_name, Data.UserD_passwd)
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
	//            showInfoIcon("Yes")
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
	//    }

	def "Upload existing Jasper Report"(){
		setup:
		uploadJasper()
		at JasperPage
		when:
		addReport(Data.JasperReportExistingFile)
		then:
		errorMsg("A report template with the name")
	}

	def "Upload new Jasper Report"(){
		setup:
		uploadJasper()
		at JasperPage
		when:
		addReport(Data.JasperReportNewFile)
		then:
		alertMsg("Upload successful")
	}

	def "Generate Jasper Report"(){
		setup:
		generateJasper()
		at JasperPage
		when:
		selectReport("titles")
		getReport()
		then:
		at JasperPage
	}

	def "Setup licence Template"() {
		setup:
		changeUser(Data.UserD_name, Data.UserD_passwd)
		templateLicence()
		$("input", name: "reference").value(Data.Licence_template_D)
		$("input", type: "submit").click(LicencePage)
		when:
		editIsPublic("Yes")
		addDocument(Data.Test_Doc_name, Data.Test_Doc_file)
		//            addCustomPropType("FunctTestProp")
		then:
		at LicencePage
	}   


	//ref 101
	def "View template Licence"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
		licences()
		when:
		viewTemplateLicences()
		openLicence(Data.Licence_template_D)
		editRef("some val")
		then:
		thrown(RequiredPageContentNotPresent)
		when:
		at LicencePage
		then:
		catchException { addDocument("whatever", "doc") }
		//        when:
		//            documents()
		//        then:
		//            catchException{deleteDocument()}//For some reason tests deny the existence of this method
		when:
		at LicencePage
		$("a", text: "Documents").click()
		$("a", text: "Download Doc").click()
		then:
		at LicencePage
	}

	//ref 102
	def "View template Licence UserB"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		licences()
		when:
		viewTemplateLicences()
		openLicence(Data.Licence_template_D)
		then:
		catchException { editRef("some val") }
		when:
		at LicencePage
		then:
		catchException { addDocument("whatever", "doc") }
		//        when:
		//            documents()
		//            deleteDocument()
		//        then:
		//            at LicencePage
		when:
		$("a", text: "Documents").click()
		$("a", text: "Download Doc").click()
		then:
		at LicencePage
	}

	//ref 103
	def "Add Actual Licence "() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		to DashboardPage             waitFor { licences() }
		when:
		viewTemplateLicences()
		createCopyOf(Data.Licence_template_D)
		then:
		at LicencePage
		when:
		addDocument(Data.Test_Doc_name, Data.Test_Doc_file)
		addNote("Test note")
		then:
		at LicencePage
		when:
		editIsPublic("Yes")
		documents()
		downloadDoc()
		withConfirm { deleteDocument() }
		notes()
		withConfirm { deleteNote() }
		licenceDetails()
		editRef(Data.Licence_template_copy_D)
		then:
		at LicencePage
	}

	//ref 109
	def "Create Actual Licence"() {
		setup:
		to DashboardPage
		waitFor { licences() }
		when:
		createNewLicense(Data.Licence_actual_C)
		addNote("test note")
		addDocument(Data.Test_Doc_name, Data.Test_Doc_file)
		then:
		at LicencePage
	}


	//ref 110
	def "View Actual Licence(created by B)"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
		waitFor { licences() }
		when:
		searchLicence("",Data.Licence_actual_C)
		openLicence(Data.Licence_actual_C)
		then:
		at LicencePage
		when:
		at LicencePage
		then:
		catchException { editRef("some val") }
		when:
		at LicencePage
		then:
		catchException { addDocument("whatever", "doc") }
		when:
		documents()
		then:
		catchException { deleteDocument() }
		when:
		downloadDoc()
		then:
		at LicencePage
	}

	//111
	def "Edit Template Licence"() {
		setup: "Login as admin"
		changeUser(Data.UserD_name, Data.UserD_passwd)
		licences()
		when: "Change public to No"
		viewTemplateLicences()
		openLicence(Data.Licence_template_D)
		editIsPublic("No")
		then: "Its updated"
		at LicencePage
	}

	def "Add/Edit/Delete Custom Proeprty"(){
		setup:
			at LicencePage
		when:
			addCustomPropType("Alumni Access")
			setRefPropertyValue("Alumni Access","No")
			deleteCustomProp("Alumni Access")
		then:
			at LicencePage
	}

	//112
	def "Accept updates to Actual Licence"() {
		setup: "Log in with User B"
		changeUser(Data.UserB_name, Data.UserB_passwd)
		toDo(Data.Licence_template_copy_D)
		when: "Accept changes"
		acceptAll()
		then: "Public should be No"
		alertBox(getMessage("pendingchange.inprogress"))
	    waitFor(15) {
			driver.navigate().refresh();
			verifyInformation("isPublic", "No")
        }
	}

	//  ref 113
	def "Edit Template Licence - for reject"() {
		setup: "Login as admin"
		changeUser(Data.UserD_name, Data.UserD_passwd)
		licences()
		when: "Change public to Yes"
		viewTemplateLicences()
		openLicence(Data.Licence_template_D)
		editIsPublic("Yes")
		then: "Its updated"
		at LicencePage
	}

	// ref 113
	def "Reject update to Actual Licence"() {
		setup: "Log in with User B"
		changeUser(Data.UserB_name, Data.UserB_passwd)
		toDo(Data.Licence_template_copy_D)
		when: "Reject changes"
		rejectOne()
		then: "Public should be No"
		verifyInformation("isPublic", "No")
		cleanup:
		editIsPublic("Yes")
	}
	//ref 114
	def "Attempt delete actual Licence"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
		licences()
		when:
		searchLicence("",Data.Licence_template_copy_D)
		deleteLicence(Data.Licence_template_copy_D)
		then:
		alertBox("You do not have sufficient administrative rights to delete the specified licence")
	}

	//ref 115
	def "Delete Actual Licence"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		licences()
		searchLicence("",Data.Licence_template_copy_D)
		deleteLicence(Data.Licence_template_copy_D)
		when:
		at LicencePage
		then:
		catchException { openLicence(Data.Licence_template_copy_D) }
	}

	//ref 118 - 119
	def "Import Onix-PL Licence"() {
		setup:
		changeUser(Data.UserD_name, Data.UserD_passwd)
		licences()
		when:
		viewTemplateLicences()
		createCopyOf(Data.Licence_template_D)
		importONIX(Data.Licence_ONIX_PL)
		then:
		at LicencePage
	}

	//ref 119
	def "Compare Onix-PL Licence"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		compareONIX()
		when:
            $("#select2-chosen-1").click()
            $("#s2id_autogen1_search").value(Data.Licence_ONIX_PL_title)
            waitFor{$("div.select2-result-label").click()}
            $("#addToList").click()
            $("#select2-chosen-1").click()
            $("#s2id_autogen1_search").value(Data.Licence_ONIX_PL_title)
            waitFor{$("div.select2-result-label").click()}
	        $("#addToList").click()
			$("i.jstree-checkbox").click()
			$("input[name='Compare']").click()
		then:
		!$("h1", text: getMessage("menu.institutions.comp_onix")).isEmpty()
	}

	def "Update ES Index"() {
		when:
		go '/demo/startFTIndex/index' // should have a few titles by now.
		then:
		true;
	}


	//ref 011
	def "Change default page size"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
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
	def "View Package - User A"() {
		setup:
		to DashboardPage
		allPackages()
		viewPackage(Data.Package_name)
		when:
		at PackageDetailsPage
		then:
		catchException { addNote("test") }
		when:
		at PackageDetailsPage
		then:
		catchException { addDocument("whatever", "doc") }
		when:
		at PackageDetailsPage
		then:
		catchException { editIsPublic("Yes") }
	}


	//ref 201
	def "View Package - User B"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		to DashboardPage
		allPackages()
		viewPackage(Data.Package_name)
		when:
		at PackageDetailsPage
		then:
		catchException { addNote("test") }
		when:
		at PackageDetailsPage
		then:
		catchException { addDocument("whatever", "doc") }
		when:
		at PackageDetailsPage
		then:
		catchException { editIsPublic("Yes") }
	}


	//ref 202
	def "Add Subscription w/o entitlements"() {
		setup:
		to DashboardPage
		subscriptions()
		when:
		newSubscription(Data.Subscription_name_A)
		and:
                $("a",text:"Details").click()
                and:
		addDocument(Data.Test_Doc_name, Data.Test_Doc_file)
		and:
		addNote("Test note")
		then:
		at SubscrDetailsPage
	}


	//ref 203
	def "View Subscription Taken (created by B)"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
		subscriptions()
		viewSubscription(Data.Subscription_name_A)
		when:
		at SubscrDetailsPage
		then:
		catchException { addNote("test") }
		when:
		at SubscrDetailsPage
		then:
                $("a",text:"Details").click()
                and:
		catchException { addDocument("whatever", "doc") }
		when:
		at SubscrDetailsPage
		then:
                $("a",text:"Details").click()
                and:
		catchException { editIsPublic("Yes") }
	}


	//ref 204
	def "Add Subscription (w/o entitlements B) "() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		at DashboardPage
		subscriptions()
		newSubscription(Data.Subscription_name_B)
		at SubscrDetailsPage
		when:
		linkPackage(Data.Package_name, false)
		addEntitlements()
		then:
		at SubscrDetailsPage
	}


	//ref 205
	def "Add Subscription (with entitlements C) "() {
		setup:
		to DashboardPage
		subscriptions()
		newSubscription(Data.Subscription_name_C)
		at SubscrDetailsPage
		when:
		linkPackage(Data.Package_name, true)
		then:
		at SubscrDetailsPage
	}


	//209
	def "Edit Package"() {
		setup:
		changeUser(Data.UserD_name, Data.UserD_passwd)
		allPackages()
		viewPackage(Data.Package_name)
		when:
		addDocument(Data.Test_Doc_name, Data.Test_Doc_file)
		addNote("Test note")
		makeAnnouncement(Data.Test_Announcement, Data.Test_Announcement)
		then:
		waitFor { alertMessage("Announcement Created") }
	}

	def "Compare Subscriptions" () {
		setup:
		to DashboardPage
		toCompareSubscriptions()
		when:
		at SubscrDetailsPage
		compareSubscriptions(Data.Subscription_name_B,Data.Subscription_name_C)

		then:
		$("h3",text:"Subscriptions Compared") != null
	}
	def "Compare Packages" () {
		setup:
		to DashboardPage
		when:
		toComparePackages()
		comparePackages(Data.Package_name,Data.Package_name)
		then:
		$("h3",text:"Packages Compared") != null
	}

	//212
	def "View Current Subscriptions"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		subscriptions()
		when:
		def url = driver.currentUrl
		println "Page Url " + url
		changeUserNoDash(Data.UserC_name, Data.UserC_passwd)
		go url
		then:
		def text = $("div.alert-block").children().filter("p").text()
		println(text)
		text.startsWith("You do not have permission to access")
	}

	//213
	def "Subscription CSV Export"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		subscriptions()
		viewSubscription(Data.Subscription_name_A)
		csvExport()
		expect:
		at SubscrDetailsPage
	}

	//214
	def "Subscription CSV Export(No Header)"() {
		setup:
		csvExportNoHeader()
		expect:
		at SubscrDetailsPage
	}

	//215
	def "Subscription JSON Export"() {
		setup:
		jsonExport()
		expect:
		at SubscrDetailsPage
	}

	//216
	def "Subscription XML Export"() {
		setup:
		xmlExport()
		expect:
		at SubscrDetailsPage
	}

	//217
	def "Subscription OCLC Export"() {
		setup:
		OCLCExport()
		expect:
		at SubscrDetailsPage
	}

	//218
	def "Subscription serials Export"() {
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
	def "Delete Subscription A"() {
		setup:
		changeUser(Data.UserB_name, Data.UserB_passwd)
		to DashboardPage
		subscriptions()
		when:
		deleteSubscription(Data.Subscription_name_A)
		then:
		at SubscrDetailsPage
	}

	//ref 300
	def "Update static HTML page"() {
		setup:
		changeUser(Data.UserD_name, Data.UserD_passwd)
		manageContent()
		when:
		addNewContent(Data.Content_Item_welcome_key, Data.Content_Item_welcome_text)
		then:
		keyExists(Data.Content_Item_welcome_key)
		when:
		to PublicPage
		then:
		!$("p").filter(text: Data.Content_Item_welcome_text).isEmpty()
	}

	def "Get DM Change log changes"(){
		setup:
		to DashboardPage
		when:
		dmChangeLog()
		then:
		changeLogAllChanges()
	}

	def "DM Changelog CSV"(){
		setup:
		at DataManagerPage
		when:
		changeLogExportCSV()
		then:
		at DataManagerPage
	}

	def "Institution Change log"(){
		when:
		at DataManagerPage
		go '/demo/myInstitutions/'+Data.Org_Url+'/changeLog'
		then:
		$("div.pagination").text().contains("Showing 4 changes")
		when:
		$("a",text:"Exports").click()
		$("a",text:"CSV Export").click()
		then:
		browser.page.title.startsWith("KB+")
	}

	//    //ref 304
	//    def "Add Identifier"(){
	//        setup:
	////            changeUser(Data.UserB_name,Data.UserB_passwd)
	//            //problem displaying the list under Funct Test Org
	//            orgInfo(Data.Org_name)
	//    }


	//ref 400
	def "Generate Renewals Worksheet"() {
		setup:
		to ProfilePage
		changeUser(Data.UserB_name, Data.UserB_passwd)
		generateWorksheet()
		when:
		comparisonSheet()
		then:
		at MyInstitutionsPage
	}
	
	//ref 500
	def "Search all current titles"() {
		setup:
		changeUser(Data.UserA_name, Data.UserA_passwd)
		when:
		allTitles()
		then:
		hasResults()
	}


	//ref 501
	def "Search within current titles"(){
		setup:
		to DashboardPage
		$("a", text: "Institutions").click()
		$("a", text: "All Titles").click(TitleDetailsPage)
		when:
		def totalTitles = numberOfResults()
		searchTitle("A")
		then:
		totalTitles != numberOfResults()
	}


	//ref 505
	def "Search within all Packages"(){
		setup:
		to DashboardPage
		allPackages()
		when:
		def pkgs = numberOfResults()
		searchPackage("G")
		then:
		pkgs != numberOfResults()
	}

	def "Renewals Upload" (){
		setup:
		changeUser(Data.UserB_name,Data.UserB_passwd)
		importRenewals()
		when:
		renewalsUpload(Data.RenewalsUploadFile)
		then:
		at MyInstitutionsPage
	} 

	def "Spotlight Search"(){
	setup:
	to ProfilePage
	when:
		$("a.dlpopover").click()
		$("#spotlight_text").value("Art Journals Master")
		waitFor{$("a", text:"Art Journals:Master:2014")}
		$("a", text:"Art Journals:Master:2014").click()
	then:
		at PackageDetailsPage
	}

}


