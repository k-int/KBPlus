package com.k_int.kbplus.onixpl

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult

import com.k_int.kbplus.OnixplLicense


/**
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 *
 */
@Transactional
class OnixPLService {
  OnixPLHelperService onixPLHelperService
  def grailsApplication
  
  /**
   * Build the list of comparison points into a format for the treeSelect.
   * 
   * @return List of entries for the treeSelect widget.
   */
  private buildAvailableComparisonPointsForTreeSelect (values) {
    
    // Copy the entries so as not to keep a reference.
    TreeMap entries = [:]
    entries.putAll(values)
    
    // Create a List of Maps and define the defaults.
    List<Map> options = []
    
    // Go through each entry and add to options.
    int count = 0
    
    def temp = entries.remove("template")
    entries.values.each { val, Map properties ->
      
      // Get the properties.
      TreeMap props = [:]
      props.putAll(properties)
      
      // Replace the value marker with the actual values.
      props['value'] = temp.replaceAll("\\\$value\\\$", "${val}")
      
      // Check for children.
      if (props['children']) {
        props['children'] = (buildAvailableComparisonPointsForTreeSelect (props['children']))
      }
      
      // Add to the options.
      options << props
              
      count ++
    }
    
    options
  }  
  /**
   * Compare one or more licenses to a primary license.
   * 
   * @param primaryLicense
   * @param licenses
   * @return
   */
  public compare (OnixplLicense primaryLicense, OnixplLicense... licenses)   {
    
  }  
  
  /**
   * Builds if necessary and then returns the comparison points for the treeSelect widget.
   * 
   * @return List of entries for the treeSelect widget
   */
  public getTsComparisonPoints () {
    
    buildAvailableComparisonPointsForTreeSelect(grailsApplication.config.onix.comparisonPoints)
  }
  
  public GPathResult getSampleDoc () {
    new XmlSlurper().parse(
      grailsApplication.mainContext.getResource(
        "/WEB-INF/resources/HEFCEDataset SubBritish Film InstituteBFI InView01092009-31082014.xml"
      ).inputStream
    ).declareNamespace("onix" : "http://www.editeur.org/onix-pl")
  }
}
