import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise

@Stepwise
class JuspCoreSpec extends GebReportingSpec {

	// //The following will setup everything required for this test case
	// def "Setup our database"(){
	// 	when:
	// 		to PublicPage
	// 		loginLink()
	// 		at LogInPage
	// 		login(Data.UserD_name, Data.UserD_passwd)
	// 		go "/demo/org/create"
	// 		$("form").name = Data.Org_name
	// 		$("form").impId = Data.Org_impId
	// 		$("form").sector = 'Higher Education'
	// 		$("#SubmitButton").click()
	// 		to ProfilePage
	// 		requestMembership(Data.Org_name, 'Editor')
	// 		manageAffiliationReq()
	// 		at AdminMngAffReqPage
	// 		approve()
	// 		go '/demo/myInstitutions/'+Data.Org_Url+'/emptySubscription'
	// 		$('form').newEmptySubName = Data.Subscription_name_A
	// 		$('input', type: 'submit').click()
	// 		go '/demo/upload/reviewPackage'
	// 		$('form').soFile = Data.Package_import_file
	// 		$('button', text: "Upload SO").click()
	// 		waitFor{$("div.alert-success")}
	// 		$('a', text: 'New Package Details').click()
	// 		$('form').subid = Data.Subscription_name_A +" - " + Data.Org_name
	// 		$('#addEntitlementsCheckbox').click()
	// 		$('#add_to_sub_submit_id').click()
	// 	then:
	// 		true
	// 		to DashboardPage
	// }

	def "First log in to the system"(){
		setup:
			to PublicPage
		when:
			loginLink()
			at LogInPage
			login(Data.UserD_name, Data.UserD_passwd)
		then:
			at DashboardPage
	}

	def "Go to subscription and check title core status"(){
		when:
			go '/demo/subscriptionDetails/index/3'
		then:
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'False(Never)'
	}
	def "Extend the core dates for the title"(){
		setup:
			at SubscrDetailsPage
			$("a.editable-click",name:"show_core_assertion_modal").click()
		when:
			waitElement {$('a','data-hidden-id':'coreStartDate').click()}
			waitElement {$('form.editableform input.input-small')}
			$('form.editableform input.input-small').value('2015-04-01')
			$('form.editableform button.editable-submit').click()

			waitElement {$('a','data-hidden-id':'coreEndDate').click()}
			waitElement {$('form.editableform input.input-small')}
			$('form.editableform input.input-small').value('2030-05-01')
			$('form.editableform button.editable-submit').click()

			$('input', value:'Apply').click()
		then:
			waitElement {$("a.delete-coreDate",text:"Delete")}
	}

	def "Check that the core status has changed to True"(){
		when:
			go '/demo/subscriptionDetails/index/3'
		then:
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'True(Now)'
	}

	def "Now lets delete the dates and see that status will change again"(){
		setup:
			at SubscrDetailsPage
			$("a.editable-click",name:"show_core_assertion_modal").click()
		when:
			$("a.delete-coreDate",text:"Delete").click()
			go '/demo/subscriptionDetails/index/3'
		then:
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'False(Never)'
	}
}
