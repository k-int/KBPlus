package com.k_int.kbplus

import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReport
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import javax.servlet.http.HttpSession
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.j2ee.servlets.ImageServlet
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import net.sf.jasperreports.engine.export.JRCsvExporter
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.xml.JRXmlLoader
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleWriterExporterOutput




class JasperReportsController {
def dataSource

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def index(){
		def result=[:]
		result.available_reports= availableReportNames()
		def reportName= params.report_name?:result.available_reports[0]

		if(reportName != null){
			def available_formats = availableReportFormats()
			InputStream inputStream = new ByteArrayInputStream(JasperReportFile.findByName(reportName).reportFile)
			JasperReport jreport = JasperCompileManager.compileReport(inputStream)
			def rparams = jreport.getParameters()
			def report_parameters = []
			rparams.each{
				if(it.isForPrompting() && ! it.isSystemDefined()){
					if(it.getName().equals("report_description")){
						result.reportdesc= it.getDescription()
					}else{
						report_parameters.add(it)
					}
				}
			}

			result.available_formats = available_formats
			result.report_parameters = report_parameters
		}

		if(params.report_name){
			render(template:"report_details",model:result)
		}else{
			result
		}
	}

	@Secured(['ROLE_ADMIN','IS_AUTHENTICATED_FULLY'])
	def uploadReport(){
		def result = [:]
		def errors = []
 
 		if(request  instanceof MultipartHttpServletRequest){
			def files = request.getMultipartFiles().get("report_files")
			
			files.each { file->
				def fileName = file.originalFilename

				if(fileName.endsWith(".jrxml") || fileName.endsWith(".jasper")){
					if(JasperReportFile.findByName(fileName) == null){
						JasperReportFile newReport = new JasperReportFile(name:fileName, reportFile:file.getBytes()).save(flush:true)
						if(newReport.hasErrors()){
							errors.add("An error occured while storing "+fileName)
						}else{
							println "Stored file "+ fileName
						}
					}else{
						errors.add("A report file with name "+fileName+" already exists.")
					} 
				}else{
					errors.add("One of the files uploaded is not a .jrxml or .jasper file and will be ignored.")
				}
			}
			if(errors.isEmpty() && !files?.isEmpty()){
				flash.message = "Upload Completed"
			}
			flash.error = errors
		}

	}

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def generateReport(){

		if(params._file.isEmpty()){
			flash.error = ["Please select a report for download."]
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");

		def filteredParams =params.findAll {it.key.toString().contains("date") }
		filteredParams.each { key, value ->
			def stringVal = value
			def newVal = new Timestamp(sdf.parse(stringVal).getTime())
			params.putAt(key,newVal) 
		}
		InputStream inputStream = new ByteArrayInputStream(JasperReportFile.findByName(params._file).reportFile)
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

		generateResponse(jasperPrint,response.outputStream, params._format, params)

	}

	def generateResponse(jasperPrint, outputStream, reportFormat, params){
		def generateResponse = true;
		switch(reportFormat){
			case "PDF":
				JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream)
				break;
			case "CSV":
				JRCsvExporter exporter = new JRCsvExporter()
				exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream))
				exporter.setExporterInput( new SimpleExporterInput(jasperPrint))
				exporter.exportReport()
				break;
			default:
				generateResponse = false
				flash.error = "Format export implementation not complete. Please select another format."
		}
		if(generateResponse){
			def exportFormat = JasperExportFormat.determineFileFormat(reportFormat)  
			response.setHeader("Content-disposition", "attachment; filename=" + (params._file.replace(reportFormat,'')) + "." + exportFormat.extension)
	        response.contentType = exportFormat.mimeTyp     
	        response.characterEncoding = "UTF-8"
		}else{
			redirect action: 'index'
		}
	}


    private void addJasperPrinterToSession(HttpSession session, JasperPrint jasperPrinter) {
        session[ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE] = jasperPrinter
    }

    private void addImagesURIIfHTMLReport(Map parameters, String contextPath) {
        if (JasperExportFormat.HTML_FORMAT == JasperExportFormat.determineFileFormat(parameters._format)) {
            parameters.IMAGES_URI = "${contextPath}/reports/image?image="
        }
    }
	def availableReportFormats(){
		def formats = [] 
		JasperExportFormat.values().each{
			formats.add(it.extension.toUpperCase())
		}
		formats
	}
	def availableReportNames(){
		def names=[]

		JasperReportFile.getAll().each{
			names.add(it.getName())
		}
		names
	}

}