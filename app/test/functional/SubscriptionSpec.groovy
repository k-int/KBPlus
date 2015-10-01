import geb.spock.GebReportingSpec
import org.elasticsearch.common.joda.time.LocalDate
import pages.*
import spock.lang.Stepwise
import com.k_int.kbplus.*
import groovy.time.TimeCategory

@Stepwise
class SubscriptionSpec extends GebReportingSpec {
    //The following will setup everything required for this test case
    def setupSpec(){
        def org = new com.k_int.kbplus.Org(name:Data.Org_name,impId:Data.Org_impId,sector:"Higher Education").save()
        def user = com.k_int.kbplus.auth.User.findByUsername(Data.UserA_name)
        def userAdm = com.k_int.kbplus.auth.User.findByUsername(Data.UserD_name)
        def formal_role = com.k_int.kbplus.auth.Role.findByAuthority('INST_ADM')
        def userOrg = new com.k_int.kbplus.auth.UserOrg(dateRequested:System.currentTimeMillis(),
                status:1,
                org:org,
                user:user,
                formalRole:formal_role).save()
        def userOrgAdmin = new com.k_int.kbplus.auth.UserOrg(dateRequested:System.currentTimeMillis(),
                status:1,
                org:org,
                user:userAdm,
                formalRole:formal_role).save()




    }


    def "Setup subscription"(){
        setup:
        to PublicPage
        loginLink()
        at LogInPage
        login(Data.UserD_name, Data.UserD_passwd)
        when:
        def endDate = new Date()
        def startDate = new Date()
          use(TimeCategory) {
            startDate -= 1.years
            endDate += 1.years
          }
        def org = Org.findByNameAndImpId(Data.Org_name,Data.Org_impId)
        def subA = new com.k_int.kbplus.Subscription(name: Data.Subscription_name_A, identifier: java.util.UUID.randomUUID().toString(),startDate:startDate,endDate:endDate).save(flush: true)
        def subrefRole = RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')
        def subRole    = new com.k_int.kbplus.OrgRole(roleType: subrefRole, sub: subA, org: org).save()

        go "subscriptionDetails/details/"+subA.id

        then:
        $('h1 span').text() == Data.Subscription_name_A

    }

    def "Setup subscription 2"(){
        when:
        go "admin/globalSync"
        go 'myInstitutions/'+Data.Org_Url+'/emptySubscription'
        $('form').newEmptySubName = Data.Subscription_name_B
        $('input', type: 'submit').click()
        then:
        $('h1 span').text() == Data.Subscription_name_B

    }

    def "Setup new package" (){
        when:
        go 'upload/reviewPackage'
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
    def "Go to subscription and check title core status"(){
        when:
            def subID = Subscription.findByName( Data.Subscription_name_A).id
            go '/demo/subscriptionDetails/index/'+ subID
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
            Thread.sleep(100)
            browser.report("Delete button should appear")
            waitElement {$("a.delete-coreDate",text:"Delete").verifyNotEmpty()}
    }

    def "Check that the core status has changed to True"(){
        setup:
            def subID = Subscription.findByName( Data.Subscription_name_A).id

        when:
            go '/demo/subscriptionDetails/index/'+ subID
        then:
            $("a.editable-click",name:"show_core_assertion_modal").text() == 'True(Now)'
    }

    def "Now lets delete the dates and see that status will change again"(){
        setup:
            def subID = Subscription.findByName( Data.Subscription_name_A).id

            at SubscrDetailsPage
            $("a.editable-click",name:"show_core_assertion_modal").click()
            browser.report("Modal Open")
        when:
            $("a.delete-coreDate",text:"Delete").click()
            browser.report("Delete clicked")
            go '/demo/subscriptionDetails/index/'+ subID
        then:
            $("a.editable-click",name:"show_core_assertion_modal").text() == 'False(Never)'
    }
    def "Add and show duplicate identifiers"() {
        setup:
            go "subscriptionDetails/details/"+Subscription.findByName(Data.Subscription_name_A ).id
            def identStr = "hello:one"
        when: "We add the first ident with no problems"
            $("#select2-chosen-1").click()
            waitFor{ $("#s2id_autogen1_search").value(identStr) }
            Thread.sleep(1000)
            waitFor{$("span.select2-match",text:identStr).head().click()}
            $("#addIdentBtn").click()
            go "subscriptionDetails/details/"+Subscription.findByName(Data.Subscription_name_B ).id
            $("#select2-chosen-1").click()
            waitFor{ $("#s2id_autogen1_search").value(identStr) }
            Thread.sleep(100)
            waitFor{$("span.select2-match",text:identStr).tail().click()}
        then:
            withConfirm{$("#addIdentBtn").click()}


    }
}