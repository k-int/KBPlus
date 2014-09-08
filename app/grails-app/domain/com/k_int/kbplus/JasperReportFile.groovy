package com.k_int.kbplus

class JasperReportFile {
    byte[] reportFile
    String name
    Date date = new Date()

    static constraints = {
        //Limit to 2MB
        reportFile(maxSize: 1024 * 1024 *2 )
    }
}
