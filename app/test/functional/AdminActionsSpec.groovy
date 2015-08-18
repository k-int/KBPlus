import geb.error.RequiredPageContentNotPresent
import geb.spock.GebReportingSpec
import pages.*
import spock.lang.Stepwise

@Stepwise
class AdminActionsSpec extends GebReportingSpec {

  // //The following will setup everything required for this test case
  // def "Setup our database"(){
  //   when:
  //     to PublicPage
  //     loginLink()
  //     at LogInPage
  //     login(Data.UserD_name, Data.UserD_passwd)
  //     go "/demo/org/create"
  //     $("form").name = Data.Org_name
  //     $("form").impId = Data.Org_impId
  //     $("form").sector = 'Higher Education'
  //     $("#SubmitButton").click()
  //     to ProfilePage
  //     changeUserNoDash(Data.UserA_name, Data.UserA_passwd)
  //     requestMembership(Data.Org_name, 'Editor')
  //     changeUserNoDash(Data.UserD_name, Data.UserD_passwd)
  //     manageAffiliationReq()
  //     at AdminMngAffReqPage
  //     approve()

  //   then:
  //     true
  //     to ProfilePage
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
      $('input',value:'Apply').click()
      go '/demo/myInstitutions/'+Data.Org_Url+'/dashboard'
    then:
      at DashboardPage
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
