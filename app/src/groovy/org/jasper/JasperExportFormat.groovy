/* Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.jasper

import java.lang.reflect.Field

import net.sf.jasperreports.engine.JRExporter
import net.sf.jasperreports.engine.export.JRCsvExporter
import net.sf.jasperreports.engine.export.JRCsvExporterParameter
import net.sf.jasperreports.engine.export.JRHtmlExporter
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.export.JRPdfExporterParameter
import net.sf.jasperreports.engine.export.JRRtfExporter
import net.sf.jasperreports.engine.export.JRTextExporter
import net.sf.jasperreports.engine.export.JRTextExporterParameter
import net.sf.jasperreports.engine.export.JRXlsExporter
import net.sf.jasperreports.engine.export.JRXlsExporterParameter
import net.sf.jasperreports.engine.export.JRXmlExporter
import net.sf.jasperreports.engine.export.JRXmlExporterParameter
import net.sf.jasperreports.engine.export.oasis.JROdsExporter
import net.sf.jasperreports.engine.export.oasis.JROdtExporter
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimpleWriterExporterOutput
import net.sf.jasperreports.export.SimpleHtmlExporterOutput
import net.sf.jasperreports.export.ExporterConfiguration
import net.sf.jasperreports.export.SimpleTextExporterConfiguration

/*
 * The supported file formats with their mimetype and file extension.
 * @author Sebastian Hohns
 */
enum JasperExportFormat implements Serializable {
  PDF_FORMAT("application/pdf", "pdf", false),
  HTML_FORMAT("text/html", "html", true),
  XML_FORMAT("text/xml", "xml", false),
  CSV_FORMAT("text/csv", "csv", false),
  XLS_FORMAT("application/vnd.ms-excel", "xls", false),
  RTF_FORMAT("text/rtf", "rtf", false),
  // TEXT_FORMAT("text/plain", "txt", true),
  ODT_FORMAT("application/vnd.oasis.opendocument.text", "odt", false),
  // ODS_FORMAT("application/vnd.oasis.opendocument.spreadsheetl", "ods", false),
  DOCX_FORMAT("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx", false),
  XLSX_FORMAT("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx", false),

  String mimeTyp
  String extension
  boolean inline

  private JasperExportFormat(String mimeTyp, String extension, boolean inline) {
    this.mimeTyp = mimeTyp
    this.extension = extension
    this.inline = inline
  }

  /**
   * Return the JasperExportFormat for a given format string.
   * @param format as String
   * @return JasperExportFormat
   */
  static JasperExportFormat determineFileFormat(String format) {
    switch (format) {
      case "PDF":  return JasperExportFormat.PDF_FORMAT
      case "HTML": return JasperExportFormat.HTML_FORMAT
      case "XML":  return JasperExportFormat.XML_FORMAT
      case "CSV":  return JasperExportFormat.CSV_FORMAT
      case "XLS":  return JasperExportFormat.XLS_FORMAT
      case "RTF":  return JasperExportFormat.RTF_FORMAT
      case "TXT": return JasperExportFormat.TEXT_FORMAT
      case "ODT":  return JasperExportFormat.ODT_FORMAT
      case "ODS":  return JasperExportFormat.ODS_FORMAT
      case "DOCX": return JasperExportFormat.DOCX_FORMAT
      case "XLSX": return JasperExportFormat.XLSX_FORMAT
      default: throw new Exception(message(code: "jasper.controller.invalidFormat", args: [format]))
    }
  }
  static JRExporter getExporter(JasperExportFormat format, jasperPrint, byteArray){
      getExporter(format, jasperPrint, byteArray, null) 
  }
  /**
   * Return the suitable Exporter for a given file format.
   * @param format
   * @return exporter
   */
  static JRExporter getExporter(JasperExportFormat format, jasperPrint, byteArray, ExporterConfiguration conf) {
    def exporter;
    switch (format) {
      case PDF_FORMAT:  
        exporter = new JRPdfExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray))
        break
      case HTML_FORMAT: 
        exporter = new JRHtmlExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput( new SimpleHtmlExporterOutput(byteArray));
        break
      case XML_FORMAT:  
          exporter = new JRXmlExporter()
        break
      case CSV_FORMAT:  
        /*SimpleCsvExporterConfiguration conf = new SimpleCsvExporterConfiguration()
        conf.setFieldDelimiter("")
        conf.setRecordDelimiter("")*/
        exporter = new JRCsvExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(byteArray));
        break
      case XLS_FORMAT:  
        exporter = new JRXlsExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray));
        break
      case RTF_FORMAT:  
        exporter = new JRRtfExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(byteArray));
        break
      // case TEXT_FORMAT: 
      // // Not working :
      // Character width in pixels or page width in characters must be specified and must be greater than zero.
      //   exporter = new JRTextExporter()
      //   exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      //   exporter.setExporterOutput(new SimpleWriterExporterOutput(byteArray));
      //   conf = new SimpleTextExporterConfiguration();
      //   break
      case ODT_FORMAT:  
        exporter = new JROdtExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray));
        break
      // case ODS_FORMAT:  
      // // excel doesnt show any data
      //   exporter = new JROdsExporter()
      //   exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
      //   exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray));
      //   break
      case DOCX_FORMAT: 
        exporter = new JRDocxExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray));
        break
      case XLSX_FORMAT: 
        exporter = new JRXlsxExporter()
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArray));
        break
      default: 
      throw new Exception(message(code: "jasper.controller.invalidFormat", args: [format]))
    }

    if(conf){
      exporter.setConfiguration(conf)
    }

    return exporter
  }

  /**
   * Return the available Fields for a given JasperExportFormat.
   * @param format
   * @return Field[] , null if no fields are available for the format
   */
  static Field[] getExporterFields(JasperExportFormat format) {
    switch (format) {
      case PDF_FORMAT:  return JRPdfExporterParameter.getFields()
      case HTML_FORMAT: return JRHtmlExporterParameter.getFields()
      case XML_FORMAT:  return JRXmlExporterParameter.getFields()
      case CSV_FORMAT:  return JRCsvExporterParameter.getFields()
      case XLS_FORMAT:  return JRXlsExporterParameter.getFields()
      case XLSX_FORMAT: return JRXlsExporterParameter.getFields()
      case RTF_FORMAT:  return JRTextExporterParameter.getFields()
      case DOCX_FORMAT: return JRDocxExporterParameter.getFields()
      default: return null
    }
  }
}
