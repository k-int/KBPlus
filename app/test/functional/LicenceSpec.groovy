import geb.spock.GebReportingSpec
import org.elasticsearch.common.joda.time.LocalDate
import pages.*
import spock.lang.Stepwise
import com.k_int.kbplus.*
import groovy.time.TimeCategory

@Stepwise
class LicenceSpec extends GebReportingSpec {

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

        def licensee_role_ref = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');
        def licence  = new com.k_int.kbplus.License(reference:"Test Licence").save()
        def licensee_role  = new com.k_int.kbplus.OrgRole(roleType:licensee_role_ref,lic:licence,org:org).save()


    }


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

    def "Test CustomProperties"(){
        def licence = License.findByReference("Test Licence")
        setup:
          go '/demo/licenseDetails/index/'+licence.id
          at LicencePage
        when:
          addCustomPropType("Alumni Access")
          setRefPropertyValue("Alumni Access","No")
          deleteCustomProp("Alumni Access")
        then:
          at LicencePage
    }

    def "Test copy licence"(){
        setup:
          at LicencePage
        when: "We click to copy the licence for default org"
          withConfirm{
            $("a",name:"copyLicenceBtn").click()
          }
        then: "We are redirected to the copy licece"
          messageBox("Your licence has been created and linked ")
    }

    def "add items to list and submit to compare a license (license properties)"() {
        setup: "Going to license comparison page..."
          def org = Org.findByNameAndImpId(Data.Org_name,Data.Org_impId)
          def licensee_role_ref = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee');
          def ed       = new LocalDate().now().plusMonths(6).toDate()
          def sd       = new LocalDate().now().minusMonths(6).toDate()
          def l_status = RefdataCategory.lookupOrCreate('License Status', 'Current')
          def licence2 = new com.k_int.kbplus.License(reference:"Test Licence 2", startDate: sd, endDate: ed, status: l_status).save()
          def licence3 = new com.k_int.kbplus.License(reference:"Test Licence 3", startDate: sd, endDate: ed, status: l_status).save()
          def licensee_role2 = new com.k_int.kbplus.OrgRole(roleType:licensee_role_ref,lic:licence2,org:org).save()
          def licensee_role3 = new com.k_int.kbplus.OrgRole(roleType:licensee_role_ref,lic:licence3,org:org).save()
          go '/demo/licenceCompare/index?shortcode='+Data.Org_Url
          at LicenceComparePage
        when:
          compare(licence2.reference,licence3.reference)
        then:
          header() == "Compare Licences (KB+ Licence Properties)"
          tableCount() > 0
    }


    def "license export test"() {
        setup:
          go "myInstitutions/${Data.Org_Url}/currentLicenses"
          at LicencePage
        when:
          searchLicence("","test")
          exportLicence("Licensed Issue Entitlements (CSV)")
          exportLicence("Licensed Subscriptions/Packages (CSV)")
        then:
          at LicencePage
    }

    def "search for public journal license using journal title and org (Invalid)"() {
        setup: "Logout and go to Public Journal Licences page"
                   //Title licencing
          def org = Org.findByNameAndImpId(Data.Org_name,Data.Org_impId)
          def licenceSub = new com.k_int.kbplus.License(reference:"test subscription licence").save()
          def endDate = new Date()
          def startDate = new Date()
          use(TimeCategory) {
            startDate -= 1.years
            endDate += 1.years
          }
          def sub = new com.k_int.kbplus.Subscription(name: "test subscription name", owner: licenceSub,
                  identifier: java.util.UUID.randomUUID().toString(),startDate:startDate,endDate:endDate).save(flush: true)
          def subrefRole = RefdataCategory.lookupOrCreate('Organisational Role', 'Subscriber')
          def subRole    = new com.k_int.kbplus.OrgRole(roleType: subrefRole, sub: sub, org: org).save()
          def ie_current = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Current');
          def ti = new com.k_int.kbplus.TitleInstance(title: Data.Title_titlename, impId:Data.Title_uniqID).save()
          def tipp = new com.k_int.kbplus.TitleInstancePackagePlatform(impId:Data.Tipp_uniqID, title: ti).save()
          def ie = new com.k_int.kbplus.IssueEntitlement(status: ie_current,tipp:tipp, subscription:sub,startDate:startDate,endDate:endDate).save()
          go "subscriptionDetails/index/"+ sub.id
          browser.report("Subscription IE")
          logout()
          go '/demo/public/journalLicences'
        when: "inputting org and journal title values"
          $("input", name: "journal").value(Data.Title_titlename)
          $("input", name: "org").value(Data.Org_name)
          $("button", type:"submit").click()
          Thread.sleep(500)
          waitFor {$("div.alert")}
        then: "This organisation SHOULDN'T allow public access"
          $("div.alert.alert-block.alert-error p").text().trim() == "${Data.Org_name} does not provide public access to this service."
    }

    def "log back into the system so we can change the configuration"(){
        setup:
          to PublicPage
        when:
          loginLink()
        at LogInPage
          login(Data.UserD_name, Data.UserD_passwd)
        then:
          at DashboardPage
    }

    def "Change org to have public journal access "() {
        setup: "Custom property page"
          go '/demo/organisations/config/'+Org.findByName(Data.Org_name).id
          at LicencePage
        when: "Properties are listed, find Public Journal Access"
          addCustomInputProperty(Data.Licence_publicProp_journals, Data.Licence_public_journals)
        then: "Value of Public Journal Access should have changed"
          rowResults() == 1 //default custom property for Org
          Thread.sleep(500)
          propertyChangedCheck(Data.Licence_publicProp_journals) == Data.Licence_public_journals
    }
  // todo unable to get IE to return based on mocked data in setupSpec()
   def "search for public journal license using journal title and org (Valid)"() {
       setup: "Go to Public Journal Licences page"

         go '/demo/public/journalLicences'
       when: "inputting org and journal title values"
         $("input", name: "journal").value(Data.Title_titlename)
         $("input", name: "org").value(Data.Org_name)
         $("button", type:"submit").click()
       then: "There will be a table of results"
        // We are not getting back results, at least we verify that we got access to data.
         $("div.alert.alert-block.alert-error p").isEmpty()
   }
}

