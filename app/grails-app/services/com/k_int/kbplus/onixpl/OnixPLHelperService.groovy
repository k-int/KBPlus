package com.k_int.kbplus.onixpl

import grails.transaction.Transactional

import com.k_int.xml.XMLDoc
import javax.xml.xpath.XPathConstants


/**
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 *
 * Helper service for ONIX-PL data extraction.
 */
class OnixPLHelperService {
  def grailsApplication
  
  static scope = "singleton"
  private XMLDoc codeList
  
  public XMLDoc getCodeList() {
    codeList = codeList ?: new XMLDoc(
      grailsApplication.mainContext.getResource(
        "/WEB-INF/resources/${grailsApplication.config.onix.codelist}"
      )
    )
  }
  
  public String lookupCodeTypeAnnotation (String type) {
    getCodeList().XPath("/xs:schema/xs:simpleType[@name='${type}']/xs:annotation", XPathConstants.STRING)
  }
  
  public String lookupCodeValueAnnotation (String value) {
    def result = getCodeList().XPath("/xs:schema//*[@value='${value}']/xs:annotation", XPathConstants.STRING)
    result
  }
  
}
