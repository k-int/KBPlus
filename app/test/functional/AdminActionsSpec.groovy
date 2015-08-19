import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise

@Stepwise
class AdminActionsSpec extends GebReportingSpec {

  // //The following will setup everything required for this test case
  // def setupSpec(){
  //     def org = new com.k_int.kbplus.Org(name:Data.Org_name,impId:Data.Org_impId,sector:"Higher Education").save()
  //     def user = com.k_int.kbplus.auth.User.findByUsername(Data.UserA_name)
  //     def userAdm = com.k_int.kbplus.auth.User.findByUsername(Data.UserD_name)
  //     def formal_role = com.k_int.kbplus.auth.Role.findByAuthority('INST_ADM')
  //     def userOrg = new com.k_int.kbplus.auth.UserOrg(dateRequested:System.currentTimeMillis(),
  //                             status:1,
  //                             org:org,
  //                             user:user,
  //                             formalRole:formal_role).save()
  //    def userOrgAdmin = new com.k_int.kbplus.auth.UserOrg(dateRequested:System.currentTimeMillis(),
  //                       status:1,
  //                       org:org,
  //                       user:userAdm,
  //                       formalRole:formal_role).save()
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

  def "Go to User Merge screen and transfer affiliations from user A"(){

    when:
      go '/demo/admin/userMerge'
    then:
      browser.page.title.contains("User Merge")
    when:
      $('select', name:'userToKeep').value(Data.UserD_displayName); 
      $('select', name:'userToMerge').value(Data.UserA_displayName); 
      $('input', type:'submit').click()
    then:
      $('h3').text() == ("Merge "+ Data.UserA_displayName + " into "+ Data.UserD_displayName)
    when:
      waitFor{$("#mergeUsersBtn").verifyNotEmpty()}
      $("#mergeUsersBtn").click()
    then:
      messageBox("successful")
  }

  def "Load issn-l files"(){
    setup:
      to ProfilePage

    when:
      to UploadIssnLPage
      $('form').sameasfile = Data.issnl_mapping_file
      $('button', text: "Upload...").click()

    then:
      $('div.alert-info').verifyNotEmpty() 

  }
}
