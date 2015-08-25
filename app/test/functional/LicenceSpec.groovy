import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise
import com.k_int.kbplus.*

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
     def licence = new com.k_int.kbplus.License(reference:"Test Licence").save()
     def licensee_role = new com.k_int.kbplus.OrgRole(roleType:licensee_role_ref,lic:licence,org:org).save()
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
      browser.report("Before Add")
      addCustomPropType("Alumni Access")
      browser.report("Before Set")
      setRefPropertyValue("Alumni Access","No")
      browser.report("Before Delete")
      deleteCustomProp("Alumni Access")
    then:
      at LicencePage
  }

 
}

