import com.k_int.kbplus.*

class InplaceTagLib {

  def editInPlace = { attrs, body ->
    def rows = attrs.rows ? attrs.rows : 0;
    def cols = attrs.cols ? attrs.cols : 0;
    def id = attrs.remove('id')
    out << "<span id='${id}'>"
    out << body()
    out << "</span>"
    out << "<script type='text/javascript'>"
    out << "new Ajax.InPlaceEditor('${id}', '"
    out << createLink(attrs)
    out << "',{"

    if(rows)
      out << "rows:${rows},"

    if(cols)
      out << "cols:${cols},"

    if(attrs.paramName) {
      out <<  """callback: function(form, value) {
                  return '${attrs.paramName}=' + escape(value) }"""
    }

    out << "});"
    out << "</script>"
  }

  def refdataValue = { attrs, body ->
    if ( attrs.val && attrs.refdataCat ) {
      def refdataCat = RefdataCategory.findByDesc(attrs.refdataCat)
      def value = RefdataValue.findByOwnerAndValue(refdataCat, attrs.val)
      out << "<p id=\"${attrs.propname}\" class=\"refdataedit\">"
      if ( value ) {
        if ( value.icon ) {
          out << "<i class=\"${value.icon}\"></i> "
        }
        else {
          out << "<i class=\"icon-search icon-white\"></i>&nbsp;"
        }
      }
      out << attrs.val
      out << "</p>"
    }
  }
}
