package com.k_int.kbplus.onixpl

import grails.transaction.Transactional

import com.k_int.xml.XMLDoc
import javax.xml.xpath.XPathConstants


/**
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 *
 * Helper service for ONIX-PL data extraction.
 */
@Transactional
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
  
  public String lookupCodeAnnotation (String name) {
    getCodeList().XPath("/xs:schema/xs:simpleType[@name='${name}']/xs:annotation", XPathConstants.STRING)
  }
  
}
