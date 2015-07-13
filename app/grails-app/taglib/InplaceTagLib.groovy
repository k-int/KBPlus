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
   *   type - type of input
   *   id [optional] - 
   *   class [optional] - additional classes
   */
  def xEditable = { attrs, body ->
    
    boolean editable = request.getAttribute('editable')
    
    if ( editable == true ) {
      def oid = "${attrs.owner.class.name}:${attrs.owner.id}"
      def id = attrs.id ?: "${oid}:${attrs.field}"

      out << "<span id=\"${id}\" class=\"xEditableValue ${attrs.class?:''}\""
      out << " data-type=\"${attrs.type?:'textarea'}\" data-pk=\"${oid}\""
      out << " data-name=\"${attrs.field}\""

      def data_link = null
      switch ( attrs.type ) {
        case 'date':
          data_link = createLink(controller:'ajax', action: 'editableSetValue', params:[type:'date',format:'yyyy/MM/dd']).encodeAsHTML()
          break;
        case 'string':
        default:
          data_link = createLink(controller:'ajax', action: 'editableSetValue').encodeAsHTML()
          break;
      }

      out << " data-url=\"${data_link}\""
      out << ">"

      if ( body ) {
        out << body()
      }
      else {
        if ( attrs.owner[attrs.field] && attrs.type=='date' ) {
          def sdf = new java.text.SimpleDateFormat(attrs.format?:'yyyy-MM-dd')
          out << sdf.format(attrs.owner[attrs.field])
        }
        else {
          if ( ( attrs.owner[attrs.field] == null ) || ( attrs.owner[attrs.field].toString().length()==0 ) ) {
          }
          else
            out << attrs.owner[attrs.field]
        }
      }
      out << "</span>"
    }
    else {
      if ( body ) {
        out << body()
      }
      else {
        if ( attrs.owner[attrs.field] && attrs.type=='date' ) {
          def sdf = new java.text.SimpleDateFormat(attrs.format?:'yyyy-MM-dd')
          out << sdf.format(attrs.owner[attrs.field])
        }
        else {
          if ( ( attrs.owner[attrs.field] == null ) || ( attrs.owner[attrs.field].toString().length()==0 ) ) {
          }
          else
            out << attrs.owner[attrs.field]
        }
      }
    }
  }


  def xEditableRefData = { attrs, body ->
    // log.debug("xEditableRefData ${attrs}");
    try {
      boolean editable = request.getAttribute('editable')
     
      if ( editable == true ) {

        def oid = "${attrs.owner.class.name}:${attrs.owner.id}"
        def dataController = attrs.dataController ?: 'ajax'
        def dataAction = attrs.dataAction ?: 'sel2RefdataSearch'
        def data_link = createLink(controller:dataController, action: dataAction, params:[id:attrs.config,format:'json',oid:oid]).encodeAsHTML()
        def update_link = createLink(controller:'ajax', action: 'genericSetRel').encodeAsHTML()
        def id = attrs.id ?: "${oid}:${attrs.field}"
   
        out << "<span>"
   
        // Output an editable link
        out << "<span id=\"${id}\" class=\"xEditableManyToOne\" data-pk=\"${oid}\" data-type=\"select\" data-name=\"${attrs.field}\" data-source=\"${data_link}\" data-url=\"${update_link}\">"

        // Here we can register different ways of presenting object references. The most pressing need to be
        // outputting a span containing an icon for refdata fields.
        out << renderObjectValue(attrs.owner[attrs.field])

        out << "</span></span>"
      }
      else {
        out << renderObjectValue(attrs.owner[attrs.field])
      }
    }
    catch ( Throwable e ) {
      log.error("Problem processing editable refdata ${attrs}",e)
    }
  }

  /**
   * ToDo: This function is a duplicate of the one found in AjaxController, both should be moved to a shared static utility
   */
  def renderObjectValue(value) {
    def result=''
    if ( value ) {
      switch ( value.class ) {
        case com.k_int.kbplus.RefdataValue.class:
          if ( value.icon != null ) {
            result="<span class=\"select-icon ${value.icon}\"></span>${value.value?:'Not set'}"
          }
          else {
            result=value.value?:'Not set'
          }
          break;
        default:
          result=value.toString();
      }
    }
    result;
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
  
  def xEditableFieldNote = { attrs, body ->
   
    boolean editable = request.getAttribute('editable')
     
    def org = ""
    if (attrs.owner.getNote("${attrs.field}")){
      org =attrs.owner.getNote("${attrs.field}").owner.content
    }
    else{
      org = attrs.owner.getNote("${attrs.field}")
    }
    
    if ( editable == true ) {
      def data_link = createLink(controller:'ajax', action: 'setFieldTableNote')
      data_link = data_link +"/"+attrs.owner.id +"?type=License"
      def oid = "${attrs.owner.class.name}:${attrs.owner.id}S"
      def id = attrs.id ?: "${oid}:${attrs.field}"
      out << "<span id=\"${id}\" class=\"xEditableValue ${attrs.class?:''}\" data-type=\"textarea\" data-pk=\"${oid}\" data-name=\"${attrs.field}\" data-url=\"${data_link}\"  data-original-title=\"${org}\">"
      if ( body ) {
        out << body()
      }
      else {
        out << org
      }
      out << "</span>"
    }
    else {
      if ( body ) {
        out << body()
      }
      else {
        out << org
      }
    }
  }


  /**
   * simpleReferenceTypedown - create a hidden input control that has the value fully.qualified.class:primary_key and which is editable with the
   * user typing into the box. Takes advantage of refdataFind and refdataCreate methods on the domain class.
   */ 
  def simpleReferenceTypedown = { attrs, body ->
    out << "<input type=\"hidden\" name=\"${attrs.name}\" data-domain=\"${attrs.baseClass}\" "
    if ( attrs.id ) {
      out << "id=\"${attrs.id}\" "
    }
    if ( attrs.style ) {
      out << "style=\"${attrs.style}\" "
    }

    attrs.each { att ->
      if ( att.key.startsWith("data-") ) {
        out << "${att.key}=\"${att.value}\" "
      }
    }

    out << "class=\"simpleReferenceTypedown ${attrs.class}\" />"
  }


  def simpleHiddenRefdata = { attrs, body ->
    def data_link = createLink(controller:'ajax', action: 'sel2RefdataSearch', params:[id:attrs.refdataCategory,format:'json'])
    // out << "<input type=\"hidden\" id=\"${attrs.id}\" name=\"${attrs.name}\" value=\"${params[attrs.name]}\"/>"
    out << "<input type=\"hidden\" id=\"${attrs.id}\" name=\"${attrs.name}\" />"
    out << "<a href=\"#\" class=\"simpleHiddenRefdata\" data-type=\"select\" data-source=\"${data_link}\" data-hidden-id=\"${attrs.name}\">"
    out << body()
    out << "</a>";
  }

  def simpleHiddenValue = { attrs, body ->
    out << "<a href=\"#\" class=\"simpleHiddenRefdata ${attrs.class?:''}\" data-type=\"${attrs.type?:'textarea'}\" data-hidden-id=\"${attrs.name}\">${attrs.value?:''}</a>"
    out << "<input type=\"hidden\" id=\"${attrs.id}\" name=\"${attrs.name}\" value=\"${attrs.value?:''}\"/>"
  }
}
