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
// resource url:"css/instances/${ApplicationHolder.application.config.defaultCssSkin?:'standard.css'}"

modules = {
  application {
    resource url:'js/application.js'
    resource url:'js/plugins.min.js'
  }
  kbplus {
    // resource url:'css/bootstrap.css'
    dependsOn : 'jsTree'
    resource url:'css/style.css'
    resource url:'css/jquery.dataTables.css'
    resource url:'css/bootstrap-editable.css'
    resource url:'css/select2.css'
    resource url:"css/instances/${ApplicationHolder.application.config.defaultCssSkin?:'standard.css'}"
//    resource url:'js/jquery-1.9.1.min.js'
    resource url:'js/bootstrap.min.js'
    resource url:'js/bootstrap-editable.min.js'
    resource url:'js/moment.min.js'
    resource url:'js/select2.min.js'
    resource url:'js/jquery.dataTables.min.js'
    resource url:'js/dataTables.scroller.js'
    resource url:'js/kbplusapp.js.gsp'
    resource url:'js/jquery.dotdotdot.min.js'
  }
  annotations {
    dependsOn 'kbplus'
    dependsOn 'font-awesome'
    resource url:'js/summernote.min.js'
    resource url:'css/summernote.css'
    resource url:'css/summernote-bs2.css'
    resource url:'js/annotations.js'
    resource url:'css/annotations.css'
  }
}
