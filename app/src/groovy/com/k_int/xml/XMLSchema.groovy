package com.k_int.xml

import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.apache.xerces.impl.xs.XSImplementationImpl
import org.apache.xerces.impl.xs.XSLoaderImpl
import org.apache.xerces.impl.xs.XSModelImpl
import org.springframework.core.io.InputStreamSource
import org.w3c.dom.bootstrap.DOMImplementationRegistry
import org.w3c.dom.ls.LSInput

import com.sun.org.apache.xerces.internal.xs.XSElementDeclaration
import com.sun.org.apache.xerces.internal.xs.XSModel

class XMLSchema {
  
  private XSModelImpl model
  private InputStreamSource resource
  
  public XMLSchema (InputStreamSource iss) {
    this.resource = iss
  }
  
  public XSModelImpl getModel() {
    model = model ?: readXMLSchema()
  }
  
  /**
   * Read in an XML file and return the root element.
   *
   * @param file_name The filename to read relative to the /WEB-INF/resources folder.
   * @return org.w3c.dom.Element root element of the supplied XML file.
   */
  private XSModelImpl readXMLSchema () {
    
    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance() 
    XSImplementationImpl impl = registry.getDOMImplementation("XS-Loader")
    XSLoaderImpl schemaLoader = impl.createXSLoader(null)
    LSInput input = impl.createLSInput()
    input.setByteStream(resource.inputStream)
    
    // Return the document element.
    XSModelImpl model = schemaLoader.load(input)
    
    model
  }
}
