package com.k_int.kbplus.onixpl

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult

import com.k_int.kbplus.OnixplLicense


/**
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 *
 */
class OnixPLService {
  OnixPLHelperService onixPLHelperService
  def grailsApplication
  
  /**
   * Build the list of comparison points into a format for the treeSelect.
   * 
   * @return List of entries for the treeSelect widget.
   */
  private List<Map> buildAvailableComparisonPointsForTreeSelect (values, parent_path = null) {
    
    // Copy the entries so as not to keep a reference.
    TreeMap entries = [:]
    entries.putAll(values)
    
    // Create a List of Maps and define the defaults.
    List<Map> options = []
    
    def temp = entries.remove("template")
    entries.values.each { val, Map properties ->
      
      // Get the properties.
      TreeMap props = [:]
      props.putAll(properties)
      
      // Replace the value marker with the actual values.
      props['value'] = (parent_path ? "${parent_path}/" : "") + temp.replaceAll("\\\$value\\\$", "${val}")
      
      // Check for children.
      if (props['children']) {
        props['children'] = (buildAvailableComparisonPointsForTreeSelect (props['children'], props['value']))
      }
      
      // Add to the options.
      options << props
    }
    
    options
  }
  
  /**
   * Builds if necessary and then returns the comparison points for the treeSelect widget.
   * 
   * @return List of entries for the treeSelect widget
   */
  public List<Map> getTsComparisonPoints () {
    buildAvailableComparisonPointsForTreeSelect(grailsApplication.config.onix.comparisonPoints)
  }
  
  /**
   * Get all comparison points available.
   *
   * @return List of entries for the treeSelect widget
   */
  public List<String> getAllComparisonPoints () {
    buildAllComparisonPoints (grailsApplication.config.onix.comparisonPoints)
  }
  
  private List<String> buildAllComparisonPoints (values, parent_path = null) {
    
    // Copy the entries so as not to keep a reference.
    TreeMap entries = [:]
    entries.putAll(values)
    
    // Create a List of Maps and define the defaults.
    List<String> options = []
    
    def temp = entries.remove("template")
    entries.values.each { val, Map properties ->
      
      // Get the properties.
      TreeMap props = [:]
      props.putAll(properties)
      
      // Replace the value marker with the actual values.
      def opt = (parent_path ? "${parent_path}/" : "") + temp.replaceAll("\\\$value\\\$", "${val}")
      options << opt
      
      // Check for children.
      if (props['children']) {
        options += (buildAllComparisonPoints (props['children'], opt))
      }
    }
    
    options
  }
  
  public GPathResult getSampleDoc () {
    new XmlSlurper().parse(
      grailsApplication.mainContext.getResource(
        "/WEB-INF/resources/HEFCEDataset SubBritish Film InstituteBFI InView01092009-31082014.xml"
      ).inputStream
    ).declareNamespace("onix" : "http://www.editeur.org/onix-pl")
  }
}
