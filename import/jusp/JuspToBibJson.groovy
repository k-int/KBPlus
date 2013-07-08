// Grapes are a way to import dependencies into a groovy scriptlet.
// Here we really only need MySQL, a number of secondary useful modules are commented out
// They are handy for related tasks

@GrabConfig(systemClassLoader=true)

@Grapes([
  // MySQL
  @Grab(group='mysql', module='mysql-connector-java', version='5.1.25')

  // OpenCSV - Useful for reading and writing CSV files
  // @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),

  // Mongo client library - Useful for injecting data into / getting data from mongo
  // @Grab(group='com.gmongo', module='gmongo', version='1.1'),

  // Following libs useful for calling out to REST web services
  // @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  // @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
  // @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
  // @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2')
])



// Handy library for handling JDBC statements
import groovy.sql.Sql
import com.mysql.jdbc.Driver

def driver = Class.forName("com.mysql.jdbc.Driver").newInstance();

// Connect ( JDBC URL, User, Pass, Driver Class
sql = Sql.newInstance( 'jdbc:mysql://localhost/JUSP?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8', 'k-int', 'k-int', 'com.mysql.jdbc.Driver' )

// Our result will be a JSON list
def result = []

// Select some data
sql.eachRow( 'select * from Journal' ) { 
  // This nurse will be a JSON map
  def journal = [:]

  journal.id = it.JID
  def json_string = groovy.json.JsonOutput.toJson(journal)
  println(json_string)
}



