import org.codehaus.groovy.grails.commons.ApplicationHolder

modules = {
  scaffolding {
    dependsOn 'bootstrap'
   // resource url: 'css/scaffolding.css'
    resource url: 'css/bootstrap.css'
    resource url: 'css/style.css'
    resource url: "css/${ApplicationHolder.application.config.defaultCssSkin?:'live.css'}"
  }
}
