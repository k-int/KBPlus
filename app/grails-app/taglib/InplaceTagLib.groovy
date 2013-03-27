import com.k_int.kbplus.*

class InplaceTagLib {

  def refdataValue = { attrs, body ->
    log.debug("refdataValue ${attrs}");
    if ( attrs.cat ) {
      def category = RefdataCategory.findByDesc(attrs.cat)
      if ( category ) {
        def value = RefdataValue.findByOwnerAndValue(category, attrs.val)

        def id = "${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.cat}:${attrs.id}"
        if ( value ) {
          //  out << "<span class=\"select-icon ${value?.icon}\">&nbsp;</span><span id=\"${id}\" class=\"${attrs.class}\">"
          out << "<span id=\"${id}\" class=\"${attrs.class}\">"
          if ( value?.icon ) {
            out << "<span class=\"select-icon ${value?.icon}\">&nbsp;</span>"
          }
          out << "<span>"
          out << attrs.val
          out << "</span>"
        }
        else {
          out << "<span id=\"${id}\" class=\"${attrs.class}\"></span>"
        }
      }
      else {
        out << "Unknown refdata category ${attrs.cat}"
      }
    }
    else {
      out << "No category for refdata"
    }
    
  }

  def singleValueFieldNote= { attrs, body ->
    out << "<p class=\"${attrs.class}\" id=\"__fieldNote_${attrs.domain}\">"
    if ( attrs.value ) {
      out << attrs.value?.owner?.content
    }
    out << "</p>"
  }

  def inPlaceEdit = { attrs, body ->
    def data_link = createLink(controller:'ajax', action: 'editableSetValue')
    out << "<span id=\"${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.id}\" class=\"xEditableValue ${attrs.class?:''}\" data-type=\"textarea\" data-pk=\"${attrs.domain}:${attrs.pk}\" data-name=\"${attrs.field}\" data-url=\"${data_link}\">"
    if ( body ) {
      out << body()
    }
    out << "</span>"
  }


  /**
   * Attributes:
   *   owner - Object
   *   field - property
   *   id [optional] - 
   *   class [optional] - additional classes
   */
  def xEditable = { attrs, body ->
    def data_link = createLink(controller:'ajax', action: 'editableSetValue')
    def oid = "${attrs.owner.class.name}:${attrs.owner.id}"
    def id = attrs.id ?: "${oid}:${attrs.field}"
    out << "<span id=\"${id}\" class=\"xEditableValue ${attrs.class?:''}\" data-type=\"textarea\" data-pk=\"${oid}\" data-name=\"${attrs.field}\" data-source=\"${data_link}\">"
    if ( body ) {
      out << body()
    }
    else {
      out << attrs.owner[attrs.field]
    }
    out << "</span>"
  }

  def xEditableRefData = { attrs, body ->
    // out << "editable many to one: <div id=\"${attrs.id}\" class=\"xEditableManyToOne\" data-type=\"select2\" data-config=\"${attrs.config}\" />"
    def data_link = createLink(controller:'ajax', action: 'sel2RefdataSearch', params:[id:attrs.config,format:'json'])
    def update_link = createLink(controller:'ajax', action: 'genericSetRel')
    def oid = "${attrs.owner.class.name}:${attrs.owner.id}"
    def id = attrs.id ?: "${oid}:${attrs.field}"

    out << "<span>"
   
    // If there is an icon class, output it
    if ( attrs.owner[attrs.field]?.icon ) {
      out << "<span class=\"select-icon ${attrs.owner[attrs.field].icon}\"></span>"
    }
    
    // Output an editable link
    out << "<span id=\"${id}\" class=\"xEditableManyToOne\" data-pk=\"${oid}\" data-type=\"select\" data-name=\"${attrs.field}\" data-source=\"${data_link}\" data-url=\"${update_link}\">"

    // If there is a value, output it
    if ( attrs.owner[attrs.field] ) {
      out << attrs.owner[attrs.field].value
    }

    out << "</span></span>"
  }
  
  def xEditableManyToOne = { attrs, body ->
    // out << "editable many to one: <div id=\"${attrs.id}\" class=\"xEditableManyToOne\" data-type=\"select2\" data-config=\"${attrs.config}\" />"
    def data_link = createLink(controller:'ajax', action: 'sel2RefdataSearch', params:[id:attrs.config,format:'json'])
    def oid = "${attrs.owner.class.name}:${attrs.owner.id}"
    def id = attrs.id ?: "${oid}:${attrs.field}"
    out << "<a href=\"#\" id=\"${id}\" class=\"xEditableManyToOne\" data-pk=\"${oid}\" data-type=\"select\" data-name=\"${attrs.field}\" data-source=\"${data_link}\">"
    out << body()
    out << "</a>";
  }

  def relation = { attrs, body ->
    out << "<span class=\"${attrs.class}\" id=\"${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.id}\">"
    if ( body ) {
      out << body()
    }
    out << "</span>"
  }

  def relationAutocomplete = { attrs, body ->
  }
  
 
  /**
   * simpleReferenceTypedown - create a hidden input control that has the value fully.qualified.class:primary_key and which is editable with the
   * user typing into the box. Takes advantage of refdataFind and refdataCreate methods on the domain class.
   */ 
  def simpleReferenceTypedown = { attrs, body ->
    out << "<input type=\"hidden\" name=\"${attrs.name}\" data-domain=\"${attrs.baseClass}\" id=\"${attrs.org}\" class=\"simpleReferenceTypedown ${attrs.class}\" />"
  }
}
