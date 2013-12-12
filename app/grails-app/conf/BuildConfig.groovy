grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// This is commented out so as not to cause probelms in the CI environment
// grails.plugin.location."functional-test" = "../../grails-functional-test"


grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://oss.sonatype.org/content/repositories/releases"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        compile 'commons-codec:commons-codec:1.6'
        runtime 'mysql:mysql-connector-java:5.1.26'
        runtime 'com.gmongo:gmongo:1.1'
        runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.4.0'
        // runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.3.0'
        runtime 'gov.loc:bagit:4.0'
        runtime 'org.apache.poi:poi:3.8'
        runtime 'net.sf.opencsv:opencsv:2.0'
        runtime 'com.googlecode.juniversalchardet:juniversalchardet:1.0.3'
        test 'org.hamcrest:hamcrest-all:1.3'

        runtime ( 'org.codehaus.groovy.modules.http-builder:http-builder:0.5.2' ) { 
          excludes "org.codehaus.groovy", "groovy"
        }
    }

    plugins {
        compile ":h2:0.2.6"
        runtime ':hibernate:3.6.10.2'
        runtime ":resources:1.2"
        runtime ':fields:1.3'
        compile ":scaffolding:2.0.1"
        // This is commented out so as not to cause probelms in the CI environment
        // compile ":functional-test:2.0.RC1"
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"
        build ':tomcat:7.0.40.1'

        // runtime ":database-migration:1.3.2"

        compile ':cache:1.0.1'

        compile ':mail:1.0.1', {
           excludes 'spring-test'
        }

        // compile ":profiler:0.5"

    }
}
