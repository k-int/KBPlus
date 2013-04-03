import org.codehaus.groovy.grails.commons.ApplicationHolder

modules = {
  scaffolding {
    // dependsOn 'bootstrap'
    // resource url: 'css/scaffolding.css'
    // resource url: 'css/bootstrap.css'
    // resource url: 'css/style.css'
    resource url: "css/instances/${ApplicationHolder.application.config.defaultCssSkin?:'standard.css'}"
    resource url: "js/jquery-1.9.1.min.js"
    resource url: "js/bootstrap.min.js"
  }
}
