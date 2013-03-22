#!/usr/bin/groovy

@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.0'),
])

import org.apache.log4j.*
import au.com.bytecode.opencsv.CSVReader


CSVReader r = new CSVReader( new InputStreamReader(new FileInputStream(args[0]),'utf8') )

while ((nl = r.readNext()) != null) {
  println("update title_instance set title = '${nl[2]}' where title='${nl[1]}';");
}
