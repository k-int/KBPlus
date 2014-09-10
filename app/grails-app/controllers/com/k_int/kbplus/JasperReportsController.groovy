package com.k_int.kbplus

import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperReport
import grails.plugins.springsecurity.Secured
import org.jasper.JasperExportFormat
import javax.servlet.http.HttpSession
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.j2ee.servlets.ImageServlet
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import org.springframework.web.multipart.MultipartHttpServletRequest
import net.sf.jasperreports.engine.design.JasperDesign
import net.sf.jasperreports.engine.xml.JRXmlLoader
import net.sf.jasperreports.engine.JRExporterParameter
import net.sf.jasperreports.engine.*




class JasperReportsController {
def dataSource

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def index(){
		def result=[:]
		flash.error = ""
 		flash.message = ""
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
		flash.error = params.errorMsg?:""
 		flash.message = ""

 		if(request  instanceof MultipartHttpServletRequest){
			def files = request.getMultipartFiles().get("report_files")
			
			files.each { file->
				def fileName = file.originalFilename

				if(fileName.endsWith(".jrxml") || fileName.endsWith(".jasper")){
					fileName = fileName.substring(0,fileName.lastIndexOf("."))
					if(JasperReportFile.findByName(fileName) == null){
						JasperReportFile newReport = new JasperReportFile(name:fileName, reportFile:file.getBytes()).save(flush:true)
						if(newReport.hasErrors()){
							flash.error += message(code: 'jasper.upload.saveError', args: [fileName])+"<br/>"
						}else{
							log.debug("Jasper Report Stored "+ fileName)
						}
					}else{
						flash.error += message(code: 'jasper.upload.exists', args: [fileName])+"<br/>"
					} 
				}else{
					flash.error += message(code:'jasper.upload.wrongFormat',args:[fileName])"<br/>"
				}
			}
			if(flash.error.equals("") && !files?.isEmpty()){
				flash.message = message(code:'jasper.upload.success')
			}		
		}

	}

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def generateReport(){
		flash.error = ""
		flash.message = ""
		
		if(params._file.isEmpty()){
			flash.error = "Please select a report for download."
			chain action: 'index', model: [errorMsg:message(code:'jasper.generate.noSelection')]
		}
	//yyyy-MM-dd HH:mm:ss
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		def filteredParams =params.findAll {it.key.toString().contains("date") }
		filteredParams.each { key, value ->
			def stringVal = value
			def newVal = sdf2.format(sdf.parse(stringVal))+" 00:00:00"
			params.putAt(key,newVal) 
		}
		
		InputStream inputStream = new ByteArrayInputStream(JasperReportFile.findByName(params._file).reportFile)
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());

		addJasperPrinterToSession(request.getSession(), jasperPrint)
		generateResponse(jasperPrint,response, params)

	}

	def generateResponse(jasperPrint, response, params){

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream()

		def exportFormat = JasperExportFormat.determineFileFormat(params._format)  
		JRExporter exporter = JasperExportFormat.getExporter(exportFormat)
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)
      	exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8")
      	exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint)
		exporter.exportReport()
		/**
			SimpleCsvExporterConfiguration conf = new SimpleCsvExporterConfiguration()
			conf.setFieldDelimiter("")
			conf.setRecordDelimiter("")
			exporter.setConfiguration(conf)
		**/
		if(!exportFormat.inline){
			response.setHeader("Content-disposition", "attachment; filename=" + (params._file.replace(params._format,'')) + "." + exportFormat.extension)
	        response.contentType = exportFormat.mimeTyp     
	        response.characterEncoding = "UTF-8"
	        response.outputStream << byteArray.toByteArray()
		}else{
			render(text:byteArray,contentType:exportFormat.mimeTyp, encoding: "UTF-8")
		}
	}

    private void addJasperPrinterToSession(HttpSession session, JasperPrint jasperPrinter) {
        session[ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE] = jasperPrinter
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