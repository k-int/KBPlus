package com.k_int.kbplus.onixpl

import java.util.List;
import java.util.Map;

import grails.transaction.Transactional
import grails.util.GrailsNameUtils
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
   * Get all comparison points available as a flat list of XPath terms. Using the config definitions.
   *
   * @return List of entries for the treeSelect widget
   */
  public List<String> getAllComparisonPoints () {
    buildAllComparisonPointsMap (grailsApplication.config.onix.comparisonPoints).keySet() as List
  }
  
  /**
   * Get all comparison points available as a map of XPath terms and names. Using the config definitions.
   *
   * @return List of entries for the treeSelect widget
   */
  public Map<String, String> getAllComparisonPointsMap () {
    buildAllComparisonPointsMap (grailsApplication.config.onix.comparisonPoints)
  }
  
  /**
   * Get all comparison points as a one dimensional map of XPath terms and names. 
   * 
   * @param values from which we extract the relevant data.
   * @return list of XPath terms
   */
  private Map<String, String> buildAllComparisonPointsMap (values, parent_path = null, template = null) {
    
    // Copy the entries so as not to keep a reference.
    TreeMap entries = [:]
    entries.putAll(values)
    
    // Create a List of Maps and define the defaults.
    LinkedHashMap<String,String> options = [:]
    
    def temp = entries.remove("template")
    entries.values.each { val, Map properties ->
      
      // Get the properties.
      TreeMap props = [:]
      props.putAll(properties)
      def the_template = null
      if (!template && parent_path) {
        
        // Set the template group.
        the_template = GrailsNameUtils.getPropertyName(props['text']);
      }
      
      // Replace the value marker with the actual values.
      def opt = (parent_path ? "${parent_path}/" : "") + temp.replaceAll("\\\$value\\\$", "${val}")
      options[opt] = [
        "name" : props['text'],
        "group" : the_template
      ]
      
      // Check for children.
      if (props['children']) {
        options.putAll(buildAllComparisonPointsMap (props['children'], opt, the_template))
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
    TreeMap result = [:]
    
    // Get the main license as a map.
    def main = license.toMap(sections)
    
    // Add the main to the result.
    result["${license.title}"] = main
    
    // Now we need to check each license and decide whether it satisfies our filter (if not all).
    for (OnixplLicense l : licenses_to_compare) {
      
      // Get each map in turn passing in the main license for comparison.
      def license_map = l.toMap(sections, main)
      
      // Add the map to the results?
      boolean filter_out = false
//      switch (return_filter) {
//        case COMPARE_RETURN_SAME:
//          def vals = license_map.values()?.getAt(0)?.values()
//          for (int i=0; !filter_out && i<vals.size(); i++) {
//            def val = vals[i]
//            def keys = val.keySet()
//            def eq = val['_equality']
//            filter_out = !val['_equality']
//          }
//          break
//        case COMPARE_RETURN_DIFFERENT:
//          def vals = license_map.values()
//          for (int i=0; !filter_out && i<vals.size(); i++) {
//            def val = vals[i]
//            filter_out = val['_equality']
//          }
//          break
//      }
      
      // Add the licence to the map, if we are to add it.
      if (!filter_out) {
        result["${l.title}"] = license_map
      }
    }
    
    // Return the result.
    result
  }
}
