#!/home/ibbo/.gvm/groovy/current/bin/groovy

import java.text.SimpleDateFormat
import java.util.*

def c = new GregorianCalendar()
c.add(Calendar.MONTH,-2)

// Remember months are zero based!
println("Year:${c.get(Calendar.YEAR)} Month:${c.get(Calendar.MONTH)}");

