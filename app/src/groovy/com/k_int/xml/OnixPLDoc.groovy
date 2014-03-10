package com.k_int.xml

import java.util.Map;

import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

import javax.xml.namespace.QName
import javax.xml.xpath.XPath

import org.springframework.core.io.InputStreamSource
import org.w3c.dom.Node

class OnixPLDoc extends XMLDoc {
  
  /**
   * @param InputStream is 
   */
  public OnixPLDoc (InputStream is) {
    super(is)
  }

  /**
   * @param InputStreamSource iss
   */
  public OnixPLDoc (InputStreamSource iss) {
    super(iss)
  }

  /**
   * @param org.w3c.dom.Node node
   */
  public OnixPLDoc (org.w3c.dom.Node node) {
    super(node)
  }

  public Map toMap (Map determine_equality_with = null) {
    
    // Get the full document so we can look up the elements.
    org.w3c.dom.Node doc = getDoc()
    if (doc.getOwnerDocument() != null) {
      // Create the owning XML.
      doc = doc.getOwnerDocument()
    }
    
    // XML
    GPathResult mainDoc = new XMLDoc(doc).toGPath()
    
    // GPath.
    GPathResult segment = toGPath()
    
    // Replace all the LicenseTextLink items with the linked text.
    segment.'**'.findAll { it ->
      
      if (it.name() == 'LicenseTextLink' ) {
        
        // Use the id of the current element.
        // Get the ID
        def the_ref = it.'@href'
        
        // Lookup the TextElement with the matching id.
        GPathResult replacement = mainDoc.'**'.find { it.'@id' == the_ref }
        // Replace.
        it.replaceNode {
          
          def replacement_el = "licenseText" (replacement)
          
          return replacement_el
        }
      }
    }
    
    // Because the XMLSlurper is lazy in modifications we need to serialize and re-parse.
    segment = new XmlSlurper().parseText(
      groovy.xml.XmlUtil.serialize(segment)
    )
    
    // Get the element.
    Map result = nodeToMap (segment)
    
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
}
