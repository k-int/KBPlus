// Grapes are a way to import dependencies into a groovy scriptlet.
// Here we really only need MySQL, a number of secondary useful modules are commented out
// They are handy for related tasks

@GrabConfig(systemClassLoader=true)

@Grapes([
  // MySQL
  @Grab(group='mysql', module='mysql-connector-java', version='5.1.25'),

  // OpenCSV - Useful for reading and writing CSV files
  // @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),

  // Mongo client library - Useful for injecting data into / getting data from mongo
  // @Grab(group='com.gmongo', module='gmongo', version='1.1'),

  // Following libs useful for calling out to REST web services
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @GrabExclude('xml-apis:xml-apis')
])


// Handy library for handling JDBC statements
import groovy.sql.Sql
import com.mysql.jdbc.Driver
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.JSON
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset


println("Usage:  groovy ./JuspToBibJson.groovy \"<<base url of service>>\"");
println("   eg:  groovy ./JuspToBibJson.groovy \"http://localhost:8080/demo/\"");

println("Client uri is ${args[0]}");

def driver = Class.forName("com.mysql.jdbc.Driver").newInstance();
def http = new RESTClient(args[0]);


println("Get SQL connection");
// Connect ( JDBC URL, User, Pass, Driver Class
sql = Sql.newInstance( 'jdbc:mysql://localhost/JUSP?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8', 'k-int', 'k-int', 'com.mysql.jdbc.Driver' )

// Our result will be a JSON list
def result = []

def count = 0;

println("Processing Journal table");

// Select some data
sql.eachRow( 'select * from Journal' ) { 
  // This nurse will be a JSON map
  def journal = [:]

  journal.id = it.JID
  journal.title = it.Title
  journal.identifier = []
  addIdIfPresent(journal.identifier, 'ISSN', it.ISSN)
  addIdIfPresent(journal.identifier, 'eISSN', it.eISSN)
  addIdIfPresent(journal.identifier, 'DOI', it.DOI)
  addIdIfPresent(journal.identifier, 'JUSP', it.JID)
  post(http, journal);
  // def json_string = groovy.json.JsonOutput.toJson(journal)
  println("[${++count}]");
}

println("Processing JournalAuthority table");

sql.eachRow( 'select * from JournalAuthority' ) {
  // This nurse will be a JSON map
  def journal = [:]

  journal.id = it.JAID
  journal.title = it.JATitle
  journal.identifier = []
  // addIdIfPresent(journal.identifier, 'ISSN', it.JAISSN)
  // addIdIfPresent(journal.identifier, 'eISSN', it.JAeISSN)
  addIdIfPresent(journal.identifier, 'DOI', it.JADOI)
  addIdIfPresent(journal.identifier, 'JUSP', it.JAID)
  post(http, journal);
  // def json_string = groovy.json.JsonOutput.toJson(journal)
  println("[${++count}]");
}


def addIdIfPresent(l, tp, v) {
  if ( ( v != null ) && ( v != '' ) ) {
    l.add(["type":tp, id:v]);
  }
}

def post(h, obj) {
  println("Post...${h}");
  h.post( path : 'api/uploadBibJson',
          requestContentType : ContentType.JSON,
          body : obj) { resp, json ->
    println("Result: ${resp}, ${json}");
  }

}

