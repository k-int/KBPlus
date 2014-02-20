package com.k_int

import org.grails.plugin.resource.ResourceTagLib
/**
 * @author Steve Osguthorpe
 * 
 * Outputs a structure as a select control. Use CSS to indent as the optgroups are not selectable.
 */

class TreeSelectTagLib {
  static defaultEncodeAs = 'raw'
  def grailsApplication
  
  private void addRequirements (props) {
    
    // Get the resources taglib.
    ResourceTagLib resourceTagLib = grailsApplication.mainContext.getBean(
      'org.grails.plugin.resource.ResourceTagLib'
    )
    
    // Add the dependency.
    resourceTagLib.require (props)
  }

  /**
   * Outputs a select tag applying classes with levels to indicate the tree indentation.
   * Any attributes supplied to the tag will be added to the select element, apart from the ones specifically mentioned.
   *
   * @attr options REQUIRED a Multi-dimensional List of maps that contains the options and their attributes.
   * @attr name REQUIRED the name of the tree control. 
   */
  def treeSelect = { attr ->
    
    addRequirements ('modules' : 'treeSelects')
    
    // Pop the options from the attributes.
    def options = attr.remove('options')
    def classes = attr.remove("class")
    classes = (classes != null ? "${classes} " : "") + "tree"
    
    out << "<select"
    attr.each { att, val ->
      out << " ${att}='${val}' "
    }
    out << "class='${classes}' >"
    
    outputChildren (out, options)
    
    out << "</select>"
  }
  
  private void outputChildren (out, List<Map> children, level = 1) {
    
    // Each child.
    children.each { Map child ->
      
      // Add an extra class to indicate the level.
      def classes = child.remove("class")
      classes = (classes != null ? "${classes} " : "") + "level${level}"
      
      // Remove the value.
      def text = child.remove("text")
      def child_els = child.remove("children")
      
      // Output the tag and the attributes.
      out << "<option"
      
      child.each { att, val ->
        
        switch (att) {
          case "selected" :
            if (!val) {
              break
            } else {
              val = "${att}"
            }
          default :
            out << " ${att}=\"${val.encodeAsURL()}\" "
        }
      }
      out << "class=\"${classes}\" >${text}</option>"
      
      // If there are children then  re-run this method with them.
      outputChildren (out, child_els, (level + 1))
    }
  }
}
