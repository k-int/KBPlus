import geb.spock.GebReportingSpec
import spock.lang.*
import pages.*

@Stepwise
class HomePageSpec extends GebReportingSpec {

  def "The KBPlus Home Page Displays OK"() {
    when:
    go "/demo"
    then:
    browser.page.title=="Knowledge Base+"
  }

  def "Check Administrative Login Prompt Is Working"() {
    when:
    // go "/demo/home/index"
    go "/demo/profile/index"
    report "login screen"
    $("form").j_username='admin'
    $("form").j_password='admin'
    $("#submit").click()

    then:
    browser.page.title.startsWith "KB+ User Profile"
  }

  def "Check Add organisation works as expected"() {
    when:
    go "/demo/org/create"
    $("form").name='Functional Test Organisation'
    $("form").impId='FunctionalTestOrganisation'
    $("form").sector='Higher Education'
    $("#SubmitButton").click()
    then:
    browser.page.title.startsWith "Show Org"
  }

  def "Check Request Affiliation"() {
    when:
    go '/demo/profile/index'
    $('form',1).org='Functional Test Organisation'
    $('form',1).formalRole='Editor'
    $('#submitARForm').click()
    then:
    browser.page.title.startsWith "KB+ User Profile"
  }

  def "Check Affiliation Approval"() {
    when:
    go '/demo/admin/manageAffiliationRequests'
    $('td', text:'Functional Test Organisation').parent().find('a', text:'Approve').click()
    then:
    browser.page.title.startsWith "KB+ Manage Affiliation Requests"
  }

  def "Check user sets default institution"() {
    when:
    go '/demo/profile/index'
    $('form').defaultDash='Functional Test Organisation'
    then:
    browser.page.title.startsWith "KB+ User Profile"
  }

  def "Check that home sends user to default inst"() {
    when:
    go '/demo/home/index'
    then:
    browser.page.title.startsWith "KB+ Institutional Dash :: Functional Test Organisation"
  }

  def "Check empty subscription creation works"() {
    when:
    go '/demo/myInstitutions/Functional_Test_Organisation/emptySubscription'
    $('form').newEmptySubName='FTO New Sub One'
    $('input', type:'submit').click()
    then:
    $('h1 span').text() == 'FTO New Sub One'
  }

  def "Test Package Import And Add To Sub"() {
    when:
    go '/demo/upload/reviewPackage'
    // $('form').soFile='/tmp/Art Journals_Master_2014.csv'
    $('form').soFile="${System.getProperty('user.dir')}/../manual_test_data/so_v3/Art Journals_Master_2014.csv"
    $('button').click()
    then:
    browser.page.title.startsWith "Edit Package"

    when:
    $('a',text:'New Package Details').click()

    then:
    browser.page.title.startsWith "Edit Package"

    when:
    $('form').subid='FTO New Sub One - Functional Test Organisation'
    $('form input').click()
    $('form input',type:'submit').click()

    then:
    1==1
    // response page sends back a link containing the new package ID <a href="/demo/packageDetails/show/590">New Package Details</a>
  }

  def "Check Home Page Now Goes to Inst Dash and Shows New Sub"() {
    when:
    go '/demo/home/index'
    $('a',text:'Subscriptions').click()
    then:
    browser.page.title.startsWith "KB+ Functional Test Organisation - Current Subscriptions"
  }

  def "Test FTO New Sub One Link from current subscriptions"() {
    when:
    $('a',text:'FTO New Sub One').click()
    then:
    browser.page.title.startsWith "KB+"
  }
}     
