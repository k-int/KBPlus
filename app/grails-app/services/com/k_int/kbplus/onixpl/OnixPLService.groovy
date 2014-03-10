package com.k_int.kbplus.onixpl

import java.util.List;
import java.util.Map;

import grails.transaction.Transactional
import groovy.util.slurpersupport.GPathResult

import com.k_int.kbplus.OnixplLicense
import com.k_int.xml.OnixPLDoc
import com.k_int.xml.XMLDoc


/**
 * This service handles the manipulation of the Onix-pl XML documents so they can be displayed, and compared.
 * 
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 */
class OnixPLService {
  
  public static final String COMPARE_RETURN_ALL = "ALL"
  public static final String COMPARE_RETURN_SAME = "EQUAL"
  public static final String COMPARE_RETURN_DIFFERENT = "DIFFERENT"
  
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
   * Get all comparison points available as a flat list of XPath terms.
   *
   * @return List of entries for the treeSelect widget
   */
  public List<String> getAllComparisonPoints () {
    buildAllComparisonPoints (grailsApplication.config.onix.comparisonPoints)
  }
  
  /**
   * Do the actual build of the full comparison points as a flat list of XPath terms. 
   * 
   * @param values from which we extract the relevant data.
   * @return list of XPath terms
   */
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
  
  /**
   * Compares the licenses and returns the results as a map.
   * @param license
   * @param licenses_to_compare
   * @return
   */
  public Map compareLicenses (OnixplLicense license, List<OnixplLicense> licenses_to_compare, List<String> sections = null, String return_filter = COMPARE_RETURN_ALL) {
    
    // Map for the result.
    def result = [:]
    
    // Get the main license as a map.
    result.main = license.toMap()
    
    // Now we need to check each license and decide whether it satisfies our filter (if not all).
    for (OnixplLicense l : licenses_to_compare) {
      
      // Get each map in turn passing in the main license for comparison.
      def license_map = l.toMap(sections, result.main)
      
      // Add the map to the results?
      boolean filter_out = false
      switch (return_filter) {
        case COMPARE_RETURN_SAME:
          filter_out = !license_map['_equality']
          break
        case COMPARE_RETURN_DIFFERENT:
          filter_out = license_map['_equality']
          break
      }
      
      // Add the licence to the map, if we are to add it.
      if (!filter_out) {
        result["${l.title}"] = license_map
      }
    }
    
    // Return the result.
    result
  }
}
