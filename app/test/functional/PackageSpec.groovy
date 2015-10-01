import geb.spock.GebReportingSpec
import org.elasticsearch.common.joda.time.LocalDate
import pages.*
import spock.lang.Stepwise
import com.k_int.kbplus.*
import groovy.time.TimeCategory

@Stepwise
class PackageSpec extends GebReportingSpec {
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

    def "Upload Package"(){
        setup:
        to PublicPage
        loginLink()
        at LogInPage
        login(Data.UserD_name, Data.UserD_passwd)
        when:
        go '/demo/upload/reviewPackage'
        $('form').soFile = Data.Package_import_file
        $('button', text: "Upload SO").click()
        then:
        waitFor{$("div.alert-success")}
    }

    def "View Package"(){
        setup:
            def pkg_id = Package.findByName(Data.Package_name).id
        when:
            go "packageDetails/show/${pkg_id}"
        then:
            $("h1",text:Data.Package_name).verifyNotEmpty()
    }

    def "Test As At "(){
        setup:
            at PackageDetailsPage
            def pkg_id = Package.findByName(Data.Package_name).id
            editDate("endDate","2015-01-01","#comk_intkbplusPackage_1_endDate")
        when:
            go "packageDetails/show/${pkg_id}"
        then:
            $("h1",text:"Snapshot on 2015-01-01 from ").verifyNotEmpty()
    }
}