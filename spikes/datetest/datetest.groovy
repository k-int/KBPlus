#!/home/ibbo/.gvm/groovy/current/bin/groovy

import java.text.SimpleDateFormat

def possible_date_formats = [
    [regexp:'[0-9]{2}/[0-9]{2}/[0-9]{4}', format: new SimpleDateFormat('dd/MM/yyyy')],
    [regexp:'[0-9]{4}/[0-9]{2}/[0-9]{2}', format: new SimpleDateFormat('yyyy/MM/dd')],
    [regexp:'[0-9]{2}/[0-9]{2}/[0-9]{2}', format: new SimpleDateFormat('dd/MM/yy')],
    [regexp:'[0-9]{4}/[0-9]{2}', format: new SimpleDateFormat('yyyy/MM')],
    [regexp:'[0-9]{4}', format: new SimpleDateFormat('yyyy')]
];


parseDate('01/01/1994',possible_date_formats)
parseDate('13/01/1995',possible_date_formats)
parseDate('13/13/1996',possible_date_formats)
parseDate('1998',possible_date_formats)


def parseDate(datestr, possible_formats) {

    println("Trying to parse ${datestr}");

    def parsed_date = null;
    for(Iterator i = possible_formats.iterator(); ( i.hasNext() && ( parsed_date == null ) ); ) {
      try {
        def date_format_info = i.next();

        if ( datestr ==~ date_format_info.regexp ) {
          println("Date string ${datestr} passed regexp ${date_format_info.regexp} attempting parse");
          def formatter = date_format_info.format
          parsed_date = formatter.parse(datestr);
          java.util.Calendar c = new java.util.GregorianCalendar();
          c.setTime(parsed_date)
          if ( ( 0 <= c.get(java.util.Calendar.MONTH) ) && ( c.get(java.util.Calendar.MONTH) <= 11 ) ) {
            println("Parsed ${datestr} using ${formatter.toPattern()} : ${parsed_date}");
          }
          else {
            // Invalid date
            parsed_date = null
            println("Invalid month: (${c.get(java.util.Calendar.MONTH)})");
          }
        }
      }
      catch ( Exception e ) {
        println(e.message);
      }
    }
    parsed_date
}
  
