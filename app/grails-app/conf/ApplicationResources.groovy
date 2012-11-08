import org.codehaus.groovy.grails.commons.ApplicationHolder

// OK this works, but isn't ideal
// import grails.util.Environment
// switch (Environment.current) {
//     case Environment.DEVELOPMENT:
//         println("AppRes - Development");
//         break
//     case Environment.PRODUCTION:
//         println("AppRes - Prod");
//         break
// }

modules = {
  application {
    resource url:'js/application.js'
    resource url:'js/plugins.min.js'
  }
  jeditable {
    resource url:'js/jquery.jeditable.mini.js'
  }
  kbplus {
    resource url:'css/bootstrap.css'
    resource url:'css/style.css'
    // println("Including css: css/${ApplicationHolder.application.config.defaultCssSkin?:'live.css'}");
    // resource url:"css/${ApplicationHolder.application.config.defaultCssSkin?:'live.css'}"
  }
}
