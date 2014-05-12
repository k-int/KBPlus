package com.k_int.kbplus.onixpl

import grails.converters.JSON
import grails.util.GrailsNameUtils
import org.apache.commons.collections.list.TreeList
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsNameUtils

import com.k_int.kbplus.OnixplLicense
import grails.util.Holders

/**
 * This service handles the manipulation of the Onix-pl XML documents so they can be displayed, and compared.
 * 
 * @author Steve Osguthorpe <steve.osguthorpe@k-int.com>
 */
class OnixPLService {
  
  public static final String COMPARE_RETURN_ALL = "ALL"
  public static final String COMPARE_RETURN_SAME = "EQUAL"
  public static final String COMPARE_RETURN_DIFFERENT = "DIFFERENT"
  
  private static OnixPLHelperService onixPLHelperService
  private static GrailsApplication grailsApplication
  
  private static GrailsApplication getGrailsApplication() {
    if (grailsApplication == null) grailsApplication = Holders.getGrailsApplication()
    grailsApplication
  }
  
  private static OnixPLHelperService getOnixPLHelperService() {
    if (onixPLHelperService == null) onixPLHelperService = getGrailsApplication().mainContext.getBean('onixPLHelperService')
    onixPLHelperService
  }
  
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
    buildAvailableComparisonPointsForTreeSelect(getGrailsApplication().config.onix.comparisonPoints)
  }
  
  /**
   * Get all comparison points available as a flat list of XPath terms. Using the config definitions.
   *
   * @return List of entries for the treeSelect widget
   */
  public List<String> getAllComparisonPoints () {
    buildAllComparisonPointsMap (getGrailsApplication().config.onix.comparisonPoints).keySet() as List
  }
  
  /**
   * Get all comparison points available as a map of XPath terms and names. Using the config definitions.
   *
   * @return List of entries for the treeSelect widget
   */
  public Map<String, String> getAllComparisonPointsMap () {
    buildAllComparisonPointsMap (getGrailsApplication().config.onix.comparisonPoints)
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
    
    String temp = entries.remove("template")
    entries.values.each { val, Map properties ->
      
      // Get the properties.
      TreeMap props = [:]
      props.putAll(properties)
      String the_template = template
      if (the_template == null && parent_path != null) {
        
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
   * Try and derive a title for the row.
   * 
   * @param data Row data
   * @return Title if found or null if not
   */
  private static String getRowHeading (Map data) {
    
    String title = null
    String name = data['_name']
    if (name != null) {
    
      // Map to house the single value.
      Map row = new HashMap(1,2)
      
      // Check if there is a "type" element.
      List entry = data.remove("${name}Type")
      
      if (entry != null) {
        // Row heading.
        return entry[0]['_content']
      }
    }
  }
  
  /**
   * Treat the supplied text for display.
   *
   * @param text Text to treat.
   * @return The treated text.
   */
  private static String treatTextForDisplay (String text) {
    
    if (text?.startsWith("onixPL:")) {
      return getOnixPLHelperService().lookupCodeValueAnnotation(text)
    }

    text
  }
  
  /**
   * Treat the supplied text for comparison.
   * 
   * @param text Text to treat.
   * @return The treated text.
   */
  private static String treatTextForComparison (String text) {
    "${text?.toLowerCase()}"
  }
  
  /**
   * Flatten the supplied structured row data for use in a table display.
   * 
   * Resulting map will have a single key => value pair. Key will be made up of comparison points 
   * supplied and the value will be a Map of column names to column values. 
   * 
   * @param data Structured row data to be flattened
   * @param compare_points List of comparison points.
   * @return The flattened representation of the row.
   */
  private static void flattenRow (Map row, Map data, List<String> compare_points, String license_name) {
    
    // Get the name of the element from the XML that this row is built from.
    String name = data['_name']
    if (name != null) {
      
      // The treemap to hold the row columns
      Map row_cells = new TreeMap()
      
      // Get a row heading.
      String heading = getRowHeading(data)
      
      // Initial value of key is the heading.
      TreeList<String> keys = [treatTextForComparison (heading)]
      
      // Add to the data as a cell.
      row_cells["heading"] = [
        "content"   : heading,
        "text"      : heading ? GrailsNameUtils.getNaturalName( heading.replaceAll("onixPL:", "") ) : null,
        "sub-text"  : treatTextForDisplay(heading)
      ]
      
      // Create list of element names.
      List el_names = data.keySet() as List
      
      int counter = 0
      
      // Go through each element in turn now and get the value for a column.
      for (String el_name in el_names) {
        
        // Using a switch on the name allows us to have a default case but also,
        // gives the option for special case handling.
        switch (el_name) {
          default :
            // Get the element(s).
            def vals = data.remove(el_name)
            
            // Cell data.
            TreeMap<String, String> cell_data = [:]
            
            switch (vals) {
              case {it instanceof Map} :
                extractContent (vals, cell_data, compare_points, keys, counter)
                break
                
              case {it instanceof Collection} :
                // Treat each value as a separate entry into the same cell.
                for (Map val in vals) {
                  extractContent (val, cell_data, compare_points, keys, counter)
                }
                break
            }
            
            // Add the cell.
            if (!el_name.startsWith('_') && cell_data) {
              row_cells["${el_name.toLowerCase()}_${counter}"] = cell_data
            }
            break
        }
        
        counter ++
      }
      
      if (!row["${keys.join('/')}"]) {
        row["${keys.join('/')}"] = new TreeMap()
      }
      
      row["${keys.join('/')}"][license_name] = row_cells
    }
  }
  
  /**
   * Extract the content from the supplied val and it to the supplied cells map.
   * Also add to the key if necessary too.
   * 
   * @param val Structured val to try and extract display values from.
   * @param cells Cell map.
   * @param compare_points Points used for comparison and also to construct the key.
   * @param key Current key to which we should append.
   * @return
   */
  private static void extractContent (Map val, Map cell, List<String> compare_points, List keys, int counter) {
    
    // Name.
    String name = val['_name']
    
    if (name) {
      
      // Add any key values to the keys list.
      for (String cp in val.keySet()) {
        
        if (!cp.startsWith('_') && !compare_points.contains(cp)) {
          List value = val.get(cp)
          if (value) {
            value.each {
              keys << treatTextForComparison(it?.get("_content"))
            }
          }
        }
      }
      
      if (val['_content'] == null) {
      
        // Add each sub element.
        for (String prop in val.keySet()) {
          switch (prop) {
            default :
              if (!prop.startsWith("_")) {
                
                // Recursively call this method.
                for (Map v in val[prop]) {
                  extractContent (v, cell, compare_points, keys, counter)
                }
              }
              break
          }
        }
        
      } else {
        
        // The text.
        String content = val['_content']
        
        if (content) {
          // Leaf add the text.
          cell["${name.toLowerCase()}_${counter}"] = [
            "content" : content,
            "text"    : treatTextForDisplay(content)
          ]
        }
      }
    }
  }
  
  /**
   * Get the Map representation of the supplied sections of the License.
   * 
   * @param license
   * @param sections
   * @return
   */
  private static void tabularize (Map tables, OnixplLicense license, List<String> compare_points, List<String> sections = null, OnixplLicense compare_to = null) {
    
    if (!(tables instanceof MapWithDefault)) {
      tables = tables.withDefault {
        new LinkedHashMap()
      }
    }
    
    // Get the title.
    String title = license.title
    
    // Get the data.
    Map data = license.toMap(sections)
    
    // Go through the items and add to the table.
    for (String table in data.keySet()) {
      
      // The xpath used to return the elements.
      for (String xpath in data["${table}"].keySet()) {
        
        // Each entry here is a row in the table.
        for (Map row in data["${table}"]["${xpath}"]) {
          
          // The table rows need a composite key to group "equal" values in the table across licenses.
          flattenRow (tables["${table.toLowerCase()}"], row, compare_points, title)
        }
      }
    }
  }
  
  /**
   * Compares the licenses and returns the results as a map.
   * @param license
   * @param licenses_to_compare
   * @return
   */
  public Map compareLicenses (OnixplLicense license, List<OnixplLicense> licenses_to_compare, List<String> sections = null, String return_filter = COMPARE_RETURN_ALL) {
    
    // The attributes for comparison. These will be lower-cased and compared. 
    List<String> exclude = [
      'SortNumber',
      'DisplayNumber'
    ]
    
    // Get the main license as a map.
    // This will form the base of each of our tables.
    Map result = [:]
    
    // Tabularise the license.
    tabularize (result, license, exclude, sections)
    
    // Return the result.
    result
  }
}
