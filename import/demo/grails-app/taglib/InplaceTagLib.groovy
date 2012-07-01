import com.k_int.kbplus.*

class InplaceTagLib {

  def refdataValue = { attrs, body ->
    if ( attrs.val && attrs.refdataCat ) {
      def refdataCat = RefdataCategory.findByDesc(attrs.refdataCat)
      def value = RefdataValue.findByOwnerAndValue(refdataCat, attrs.val)
      out << "<span id=\"${attrs.propname}\" class=\"${attrs.class}\">"
      if ( value ) {
        if ( value.icon ) {
          out << "<i class=\"${value.icon}\"></i> "
        }
        else {
          out << "<i class=\"icon-search icon-white\"></i>&nbsp;"
        }
      }
      out << attrs.val
      out << "</span>"
    }
  }

  def singleValueFieldNote= { attrs, body ->
    out << "<p class=\"${attrs.class}\" id=\"__fieldNote_${attrs.domain}\">"
    if ( attrs.value ) {
      out << attrs.value?.owner?.content
    }
    out << "</p>"
  }
}
