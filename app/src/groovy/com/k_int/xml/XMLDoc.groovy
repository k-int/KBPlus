package com.k_int.xml

import groovy.util.slurpersupport.NodeChild

import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import org.springframework.core.io.InputStreamSource

class XMLDoc {

  private XPath XPath
  private org.w3c.dom.Node doc
  private DocumentNSResolver nsr

  protected void setDoc(org.w3c.dom.Node doc) {
    this.doc = doc
  }
  
  public XMLDoc (InputStreamSource iss) {
    this ( iss.inputStream )
  }
  
  public XMLDoc (InputStream is) {
    
    // Set the doc.
    doc = readXML(is)
    refreshNamespaceResolver()
  }
  
  private void refreshNamespaceResolver(override = null) {
    if (!override) {
      nsr = new DocumentNSResolver(doc)
    } else {
    nsr = new DocumentNSResolver(override)
    }
  }
  
  public XMLDoc (org.w3c.dom.Node node) {
    
    // The document.
    org.w3c.dom.Document document = node.getOwnerDocument()
    if (document == null) {
      // Assume it was already a document.
      document = node
    }
    
    // Create a namespace resolver that uses the current doc.
    doc = node
    refreshNamespaceResolver(document)
  }
  
  public org.w3c.dom.Node getDoc() {
    doc
  }

  public XPath getXPath() {
    XPath = XPath ?: XPathFactory.newInstance().newXPath()
    XPath.setNamespaceContext(nsr)
    XPath
  }

  public void transform ( out, out_props = [:] ) {
    Transformer transformer = TransformerFactory.newInstance().newTransformer()
    
    // Add each output property.
    out_props.each {prop, val ->
      transformer.setOutputProperty(prop, val);
    }

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
  private org.w3c.dom.Node readXML (InputStream is) {

    // Create an XML document builder.
    def docFactory = DocumentBuilderFactory.newInstance()
    docFactory.setNamespaceAware(true)
    def builder = docFactory.newDocumentBuilder()

    // Return the document element.
    builder.parse(is)
  }

  public Object XPath (String XPath_statment, QName retType = XPathConstants.NODESET) {
    getXPath().evaluate("${XPath_statment}", doc, retType)
  }
  
  public NodeChild toGPath () {
    
    // Create the string writer to receive the XML.
    StringWriter sw = new StringWriter()
    
    // Omit the declaration.
    transform( sw, ["${OutputKeys.OMIT_XML_DECLARATION}" : "yes"] )
    
    // Slurp up the XML.
    new XmlSlurper().parseText( sw.toString() )
  }
  
  def nodeToMap = { node ->
    
    def data = [:] as TreeMap
    data['_name'] = node.name()
    data['_ns'] = node.namespaceURI()
    data['_attr'] = node.attributes()
    
    def children = node.childNodes()
    if (children) {
      children.each { n ->
        if (data[n.name()] == null) data[n.name()] = []
        data[n.name()] << nodeToMap (n)
      }
      data['_type'] = "parent"
    } else {
      // Add the content.
      data['_type'] = "leaf"
      data['_content'] = node.text()
    }
    
    data
  }
  
  /**
   * If param determine_equality_with is supplied then the result is compared with the supplied value.
   * An extra property _equality will be set on the returned data and cascaded up the map.
   * 
   * @return a map representation of the XML.
   */
  public Map toMap (Map determine_equality_with = null) {
    
    // Get the element.
    Map result = nodeToMap (toGPath())
    
    // Determine equality if necessary.
    if (determine_equality_with) {
      boolean eq = false
      if (result) {
        eq = determineEqualityOfNodeMaps (determine_equality_with, result)
      }
      
      // Set the value.
      result['_equality'] = eq
    }
    
    result
  }
  
  private static boolean determineEqualityOfNodeMaps (Map primary, Map secondary) {
    
    final def compare = ['_ns', '_content']
    
    // Check the type.
    if (primary['_type'] == "leaf") {
      
      // Compare the various points for equality.
      boolean eq = true
      for (int i=0; eq && i<compare.size(); i++) {
        
        // Get the comparison value.
        def c = compare[i]
        
        // Compare as space-normalised and lower-cased values.
        def val1 = "${primary[c]}"?.replaceAll("\\s{2,}", " ").trim().toLowerCase()
        def val2 = "${secondary[c]}"?.replaceAll("\\s{2,}", " ").trim().toLowerCase()
        eq = (val1 == val2)
      }
      
      // Set the equality value.
      if (!secondary['_equality']) {
        secondary['_equality'] = eq
      }
      
      return eq
      
    } else {
    
      // Get the keys.
      for (String key : primary.keySet()) {
        if (!key.startsWith("_")) {
          
          // We need to look for each value on the secondary map.
          List<Map> pri_vals = primary["${key}"]
          List<Map> sec_vals = secondary["${key}"]
          
          if (sec_vals) {
            
            // Get each primary value.
            boolean eq = true
            for (int pri_num=0; eq && pri_num<pri_vals.size(); pri_num++) {
              boolean found = false
              for (int sec_num=0; sec_num<sec_vals.size(); sec_num++) {
                found = (determineEqualityOfNodeMaps( pri_vals[pri_num], sec_vals[sec_num] ) || found)
              }
              eq = eq && found
            }
            
            // Set the equality.
            if (!secondary['_equality']) {
              secondary['_equality'] = eq
            }
          }
        }
      }
    }
    
    // Return the equality value.
    secondary['_equality']
  }
}
