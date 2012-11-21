import com.k_int.kbplus.*

class EnhancedEditingTagLib {

  def enhancedSelect = { attrs, body ->
    log.debug("Owner = ${attrs.owner} ${attrs.owner?.id}");
    out << "<a href=\"#\" class=\"enhancedSelect\" data-toggle=\"modal\" data-target=\"#enhanced_select_content_wrapper\" data-profile=\""+attrs.refdataProfile+"\" data-owner=\"${attrs.owner.class.name}:${attrs.owner.id}:${attrs.ownerProperty}\"autocomplete=\"off\">"
    out << body()
    out << "</a>"
  }
}
