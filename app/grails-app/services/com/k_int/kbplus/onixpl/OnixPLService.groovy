package com.k_int.kbplus.onixpl

import grails.util.GrailsNameUtils
import grails.util.Holders

import org.apache.commons.collections.list.TreeList
import org.codehaus.groovy.grails.commons.GrailsApplication

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
  
  private static OnixPLHelperService onixPLHelperService
  private static GrailsApplication grailsApplication
  
  public static GrailsApplication getGrailsApplication() {
    if (grailsApplication == null) grailsApplication = Holders.getGrailsApplication()
    grailsApplication
  }
  
  public static OnixPLHelperService getOnixPLHelperService() {
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
  private Map<String, String> buildAllComparisonPointsMap (values, parent_path = null, template = null, processor = null) {
    
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
      
      // Processor default.
      def proc = props['processor'] ?: processor
      if (proc) {
        options[opt]['processor'] = proc
      }
      
      // Check for children.
      if (props['children']) {
        options.putAll(buildAllComparisonPointsMap (props['children'], opt, the_template, proc))
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
  public static Map getRowHeadingData (Map row_data) {
    
    // Just find the first example of an entry regardless of which license it's defined against.
    row_data[row_data.keySet()[0]]
  }
  
  /**
   * Returns a single sanitised onix value along with it's definition data.
   * 
   * @param data
   * @param name
   * @return
   */
  public static String getSingleValue (Map data, String name) {
    String t = ""
    String content = data?.get("${name}")?.getAt(0)?.get('_content')
    if (content) {
      if (content.startsWith("onixPL:")) {
        t = "<span class='onix-code ${getClassValue(content)}' title='${getOnixValueAnnotation(content).encodeAsHTML()}' >${formatOnixValue(content)}</span>"
      } else {
        t = content.encodeAsHTML()
      }
    }
    return t
  }

  public static String getUsageQuantity(Map data){
    String result = ""
    String type = OnixPLService.getSingleValue(data,'UsageQuantityType')
    result += type + ": "
    String proximity = OnixPLService.getSingleValue(data['QuantityDetail'][0],'Proximity')
    result += proximity
    result += " ${OnixPLService.getSingleValue(data['QuantityDetail'][0],'Value')} "
    String unit = OnixPLService.getSingleValue(data['QuantityDetail'][0],'QuantityUnit')
    result += unit
    return result  
  }

  /**
   * Sorts the values into their correct order.
   * 
   * @param elements Should be TextElement from the Onix data.
   * @return Sorted values.
   */
  public static List sortTextElements (List elements) {
    elements.sort { a, b ->
      
      // Sort values
      String sorta = (a?.get('SortNumber') ?: "")
      sorta += (a?.get('DisplayNumber') ?: "")
      
      String sortb = (b?.get('SortNumber') ?: "")
      sortb += (b?.get('DisplayNumber') ?: "")
      
      sorta <=> sortb
    }
  }
  
  /**
   * Sanitises 1..n Onix values and looks up their definitions from the spec.
   * 
   * @param data data to extract the data from
   * @param name key name which we are to target
   * @param separator The separator to use when setting out the multi-values.
   * @param last_sep [Optional] Used when separating out the last 2 items of the list.
   * @return HTML string for use in displaying.
   */
  public static String getAllValues (Map data, String name, String separator, String last_sep = null) {
    
    // The text.
    String text = ""
    
    // The item.
    List items =  data?.get("${name}")
    
    if (items) {
    
      // Get the size of the elements.
      int ds = items.size() ?: 0
      
      // Each element.
      items.eachWithIndex { item, index ->
        
        String content = item['_content']
        if (content) {
          text += (index > 0 ? (last_sep && (index + 1 == ds) ? last_sep : separator) : "")
          if (content.startsWith("onixPL:")) {
             text += "<span class='onix-code ${getClassValue(content)}' title='${getOnixValueAnnotation(content).encodeAsHTML()}' >${formatOnixValue(content)}</span>"
          } else {
            text += content.encodeAsHTML()
          }
        }
      }
    }
    
    text
  }
  
  /**
   * Creates a value suitable to be used as a CSS class name from the supplied string.
   * @param text
   * @return sanitised string value.
   */
  public static String getClassValue (String text) {
    String t = ""
    if (text) {
      t = GrailsNameUtils.getScriptName(text)
    }
    t
  }
  
  /**
   * Looks up the annotation from the OnixPL specification.
   *
   * @param text Text to treat.
   * @return The treated text.
   */
  public static String getOnixValueAnnotation (String text) {
    
    if (text?.startsWith("onixPL:")) {
      return getOnixPLHelperService().lookupCodeValueAnnotation(text)
    }

    text
  }
  
  /**
   * Formats a string by changing values like onixPL:MyValue to "My Value" for use when displaying
   * onix values out.
   * @param text
   * @return
   */
  public static String formatOnixValue (String text) {
    String t = text
    
    if (t) {
      def matcher = t =~ /onixPL:([\w]*)/
      
      matcher.each { match ->
        
        // Each string match without the onixPL:
        t = t.replaceAll(match[0], GrailsNameUtils.getNaturalName(match[1]))
      }
    }
    
    t
  }
  
  /**
   * Treat the supplied text for comparison.
   * 
   * @param text Text to treat.
   * @return The treated text.
   */
  public static String treatTextForComparison (String text) {
    text?.toLowerCase()
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
  private static void flattenRow (Map rows, Map data, List<String> exclude, String license_name) {
    
    // Get the name of the element from the XML that this row is built from.
    String name = data['_name']
    if (name != null) {
      
      // The treemap to hold the row columns
      Map row_cells = new TreeMap()
      
      // Initial value of key is the heading.
      TreeList<String> keys = []
      
      // Create list of element names.
      List el_names = data.keySet() as List
      Map<String,List<String>> priority = ["User":[]];

      generateKeys(data, exclude, keys,priority)
      priority.entrySet().each{
        if(it.getValue()){
          keys.add(0,it.getValue())
        }
      }
      
      // Go through each element in turn now and get the value for a column.
      for (String el_name in el_names) {
        
        // Using a switch on the name allows us to have a default case but also,
        // gives the option for special case handling.
        switch (el_name) {
          default :
            
            // Add the cell.
            if (!el_name.startsWith('_')) {
              row_cells["${el_name}"] = data["${el_name}"]
            }
            break
        }
      }
      
      String key = "${keys.join('/')}"
      
      if (rows[key] == null) {
        rows[key] = new TreeMap()
      }
      
      rows[key][license_name] = row_cells
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
  private static void generateKeys (Map val, List<String> exclude, List keys, Map<String,List<String>> priority) {
    
    // Name.
    String name = val['_name']
    
    if (name) {
      
      // Add any key values to the keys list.
      for (String cp in val.keySet()) {


        if (!cp.startsWith('_') && !exclude.contains(cp)) {
          List value = val.get(cp)
          if (value) {
            value.each {
              String key = it?.get("_content")
              if (key) {
                keys << treatTextForComparison(key)
                if(priority.containsKey(it.get("_name"))){
                  priority[it.get("_name")]+= key
                }
              }
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
                  generateKeys (v, exclude, keys,priority)
                }
              }
              break
          }
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
  private static void tabularize (Map tables, OnixplLicense license, List<String> exclude, List<String> sections = null, OnixplLicense compare_to = null) {
    
    if (!(tables instanceof MapWithDefault)) {
      tables = tables.withDefault {
        new TreeMap()
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
          flattenRow (tables["${table}"], row, exclude, title)
        }
      }
      
      tables["${table}"].putAll([
        "_title"    : GrailsNameUtils.getNaturalName(table)
      ])
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
      'DisplayNumber',
      'TextPreceding',
      'Text',
      'AnnotationType',
      'AnnotationText',
      'UsageStatus',
      'Description',
      'Name',
      'AgentPlaceRelator',
      'AgentType',
      'DocumentLabel',
      'IDValue',
      'PlaceIDType'
    ]
    
    // Get the main license as a map.
    // This will form the base of each of our tables.
    Map result = [:]
    
    // Tabularise the license.
    tabularize (result, license, exclude, sections)
    for (OnixplLicense l in licenses_to_compare) {
      tabularize (result, l, exclude, sections)
    }
    
    // Return the result.
    result
  }
}
