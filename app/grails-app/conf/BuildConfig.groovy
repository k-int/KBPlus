grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.7
grails.project.source.level = 1.7


// grails.project.fork = [
//    test: [maxMemory: 768, minMemory: 64, debug: true, maxPerm: 256], // Removed ", daemon:true" because geb doesn't play nice with forked mode atm
//    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256],
//    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256],
//    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
// ]

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        excludes "grails-docs"
        // uncomment to disable ehcache
        excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    def gebVersion = "0.9.3"
    def seleniumVersion = "2.44.0"

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenCentral()
        mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://oss.sonatype.org/content/repositories/releases"
        // mavenRepo "http://projects.k-int.com/nexus-webapp-1.4.0/content/repositories/snapshots"
        mavenRepo "http://projects.k-int.com/nexus-webapp-1.4.0/content/repositories/releases"
        mavenRepo "http://jaspersoft.artifactoryonline.com/jaspersoft/third-party-ce-artifacts/"
        mavenRepo "http://jasperreports.sourceforge.net/maven2/com/lowagie/itext/2.1.7.js2/"

        // Added because I'm strugging to get cglib - CGLib is causing problems - not sure what
        mavenRepo "http://central.maven.org/maven2/"

        mavenRepo "http://nexus.k-int.com/content/repositories/releases"

        // For shibboleth native-sp
        // mavenRepo "http://projects.k-int.com/nexus-webapp-1.4.0/content/repositories/releases"
        mavenRepo "http://nexus.k-int.com/content/repositories/releases/"


    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        build('org.grails:grails-docs:2.3.11') {
            excludes 'itext'
        }
        compile ('com.k-int:goai:1.0.2') {
          exclude 'groovy'
        }
        compile 'commons-codec:commons-codec:1.6'
        runtime 'xerces:xerces:2.4.0'
        runtime 'xerces:xercesImpl:2.11.0'
        runtime 'mysql:mysql-connector-java:5.1.30'

        // Would very much like to upgrade to these - but seems to cause a weird class version error when I do
        runtime 'org.elasticsearch:elasticsearch:1.3.7'
        runtime 'org.elasticsearch:elasticsearch-client-groovy:1.3.2'

        runtime 'gov.loc:bagit:4.0'
        runtime 'org.apache.poi:poi:3.8'
        runtime 'net.sf.opencsv:opencsv:2.0'
        runtime 'com.googlecode.juniversalchardet:juniversalchardet:1.0.3'

        runtime 'org.apache.commons:commons-exec:1.2'
        compile 'org.apache.httpcomponents:httpcore:4.3.2'

        compile 'org.apache.httpcomponents:httpclient:4.3.5'
        test 'org.hamcrest:hamcrest-all:1.3'
        test("org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion") {
            exclude 'xml-apis'
        }
        test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
        test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
        
        // http://www.gebish.org/manual/current/build-integrations.html#grails
        // https://github.com/geb/geb-example-grails
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
        test "org.gebish:geb-spock:$gebVersion"

        runtime ( 'org.codehaus.groovy.modules.http-builder:http-builder:0.5.2' ) { 
          excludes "org.codehaus.groovy", "groovy"
        }
        compile "net.sf.jasperreports:jasperreports:5.6.0"
        compile "org.eclipse.jdt.core.compiler:ecj:4.4"
  
        // II Commented out..
        // compile 'cglib:cglib:2.2.2'
        compile "com.lowagie:itext:2.1.7"

    }

    plugins {
        compile ":h2:0.2.6"
        runtime ':hibernate:3.6.10.18'  // 18 is latest
        runtime ":resources:1.2.8"
        runtime ':fields:1.3'
        compile ":scaffolding:2.0.3"
        
        // This is commented out so as not to cause probelms in the CI environment
        // compile ":functional-test:2.0.RC1"
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"
        build ':tomcat:7.0.54'

        runtime ":database-migration:1.4.0"

        compile ':cache:1.1.7'

        compile ':mail:1.0.1', {
           excludes 'spring-test'
        }

        // compile ":profiler:0.5"
        test ":spock:0.7", {
          exclude "spock-grails-support"
        }
        test ":geb:$gebVersion"

        test ":remote-control:2.0"

        
        // Font awesome for font based icons.
        compile ":font-awesome-resources:4.3.0.1"

        compile ':spring-security-core:1.2.7.3'
        compile ':spring-security-ldap:1.0.6'
        compile ':spring-security-shibboleth-native-sp:1.0.3'

        runtime ":gsp-resources:0.4.4"
        runtime ":jquery:1.9.1"

        runtime ":audit-logging:1.0.3"
        runtime ":executor:0.3"
        runtime ":markdown:1.1.1"
        runtime ":quartz:1.0.1"
        runtime ":rest:0.7"
        compile ":grails-melody:1.53.0"
        // runtime "com.k-int:domain-model-oai-pmh:0.1"
        compile ":jsonp:0.2"

        compile ":remote-pagination:0.4.8" //AJAX Pagination - Finance
    }
}
