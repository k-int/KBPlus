import com.k_int.kbplus.*

class InplaceTagLib {

  def refdataValue = { attrs, body ->
    log.debug("refdataValue ${attrs}");
    if ( attrs.cat ) {
      def category = RefdataCategory.findByDesc(attrs.cat)
      if ( category ) {
        def value = RefdataValue.findByOwnerAndValue(category, attrs.val)
        def id = "${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.cat}:${attrs.id}"

        out << "<span id=\"${id}\" class=\"${attrs.class}\">"
        if ( value ) {
          if ( value.icon ) {
            out << "<i class=\"${value.icon}\"></i> "
          }
          else {
            out << "<i class=\"icon-search icon-white\"></i>&nbsp;"
          }
        }
        else {
          out << "Invalid Value"
        }
        out << attrs.val
        out << "</span>"
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
    out << "<span class=\"${attrs.class}\" id=\"${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.id}\">"
    if ( body ) {
      out << body()
    }
    out << "</span>"
  }

  def relation = { attrs, body ->
    out << "<span class=\"${attrs.class}\" id=\"${attrs.domain}:${attrs.pk}:${attrs.field}:${attrs.id}\">"
    if ( body ) {
      out << body()
    }
    out << "</span>"
  }
}
