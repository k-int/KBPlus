import grails.plugin.geb.*
import grails.plugin.remotecontrol.*
import geb.spock.GebReportingSpec

class BaseSpec extends GebReportingSpec {

        def remote = new RemoteControl()

        String getMessage(String code, Object[] args = null, Locale locale=null) {
                remote.exec { ctx.messageSource.getMessage(code, args, locale) }
        }

}