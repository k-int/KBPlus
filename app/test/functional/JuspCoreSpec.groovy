import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise

@Stepwise
class JuspCoreSpec extends GebReportingSpec {

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
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'False'
	}
	def "Extend the core dates for the title"(){
		setup:
			at SubscrDetailsPage
			$("a.editable-click",name:"show_core_assertion_modal").click()
		when:
			$('a','data-hidden-id':'coreStartDate').click()
			$('form.editableform input.input-small').value('2015-04-01')
			$('form.editableform button.editable-submit').click()

			$('a','data-hidden-id':'coreEndDate').click()
			$('form.editableform input.input-small').value('2015-05-01')
			$('form.editableform button.editable-submit').click()

			$('input', value:'Apply').click()
		then:
			waitElement {$("a.delete-coreDate",text:"Delete")}
	}

	def "Check that the core status has changed to True"(){
		when:
			go '/demo/subscriptionDetails/index/3'
		then:
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'True'
	}

	def "Now lets delete the dates and see that status will change again"(){
		setup:
			at SubscrDetailsPage
			$("a.editable-click",name:"show_core_assertion_modal").click()
		when:
			$("a.delete-coreDate",text:"Delete").click()
			go '/demo/subscriptionDetails/index/3'
		then:
			$("a.editable-click",name:"show_core_assertion_modal").text() == 'False'
	}
}