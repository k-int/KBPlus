package com.k_int.xml

import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.springframework.core.io.InputStreamSource
import org.w3c.dom.Document

class XMLDoc {

  private XPath XPath
  private Document doc

  public XMLDoc (InputStreamSource iss) {
    this (iss.inputStream)
  }
  
  public XMLDoc (InputStream is) {
    doc = readXML(is)
  }

  public XPath getXPath() {
    XPath = XPath ?: XPathFactory.newInstance().newXPath()
    XPath.setNamespaceContext(new DocumentNSResolver(doc))
    XPath
  }

  public transform = { out ->
    Transformer transformer = TransformerFactory.newInstance().newTransformer()

    // Create source and result.
    DOMSource source = new DOMSource(doc)
    StreamResult result = new StreamResult(out)

    transformer.transform(source, result)
  }

  /**
   * Read in an XML file and return the root element.
   *
   * @param file_name The filename to read relative to the /WEB-INF/resources folder.
   * @return org.w3c.dom.Element root element of the supplied XML file.
   */
  private Document readXML (InputStream is) {

    // Create an XML document builder.
    def docFactory = DocumentBuilderFactory.newInstance()
    docFactory.setNamespaceAware(true)
    def builder = docFactory.newDocumentBuilder()

    // Return the document element.
    builder.parse(is)
  }

  public XPath (String XPath_statment, QName retType = XPathConstants.NODESET) {
    getXPath().evaluate("${XPath_statment}", doc, retType)
  }
}
