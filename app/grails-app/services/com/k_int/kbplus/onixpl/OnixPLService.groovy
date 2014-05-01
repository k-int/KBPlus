package com.k_int.kbplus.onixpl

import grails.util.GrailsNameUtils

import com.k_int.kbplus.OnixplLicense


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
   * Build the table data
   */
  private static Map buildTable (Map xmldata, List comp) {
    
    // Data to return
    Map data = (new TreeMap()).withDefault {
      [:]
    }
    
    // Check that we have a name.
    String name = xmldata['_name']
    if (name) {
      
      // The key.
      String key = ""
      
      // Create a composite key for quick comparison.
      for (String point in comp) {
        String val = xmldata[point]
        if (val) {
          key += val.toLowerCase()
        }
      }
    
      // Output the table data. Adding a switch here will enable us to easily add
      // special cases.
      switch (name) {
        default :
        
          // If this is a parent element then look at the children
          if (xmldata['_type'] == 'parent') {
        
            // For each element.
            for (String element_key in xmldata.keySet()) {
              if (!element_key.startsWith("_")) {
                
                List elements = xmldata[element_key]
                if (elements) {
                  for (Map element in elements) {
                    data[key] += buildTable (element, comp)
                  }
                }
              }
            }
          } else {
            // Leaf.
            String val = xmldata['_content']
            if (val) {
              data[key] = val
            } 
          }
          break
      }
    }
    
    data
  }
  
  /**
   * Compares the licenses and returns the results as a map.
   * @param license
   * @param licenses_to_compare
   * @return
   */
  public Map compareLicenses (OnixplLicense license, List<OnixplLicense> licenses_to_compare, List<String> sections = null, String return_filter = COMPARE_RETURN_ALL) {
    
    // The attributes for comparison. These will be lower-cased and compared. 
    def comp = [
      '_ns',
      '_name',
      '_content'
    ]
    
    // Map for the result.
    Map result = (new TreeMap()).withDefault {
      [:]
    }
    
    // Get the main license as a map.
    // This will form the base of each of our tables.
    Map main = license.toMap(sections)
    
    // Use the main license, going through each section forming a map representing
    // each table and its data.
    for (String tableName in main.keySet()) {
      
      // Construct a path that we are looking at.
      def path = [tableName]
      
      // The data.
      def data = main[tableName]
      
      // Should be a single keyed map.
      def xpath = data.keySet()[0]
      path << xpath
      
      // Now go through the data.
      data = data[xpath]
      
      result["${tableName}"]["${license.title}"] = buildTable (data, comp)
    }
    
    // Add the main to the result.
//    result["${license.title}"] = main
    
    // Return the result.
    result
  }
}
