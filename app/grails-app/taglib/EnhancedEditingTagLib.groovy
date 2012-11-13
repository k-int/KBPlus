import com.k_int.kbplus.*

class EnhancedEditingTagLib {

  def enhancedSelect = { attrs, body ->
    log.debug("Owner = ${attrs.owner} ${attrs.owner?.id}");
    out << "<a href=\"#\" class=\"enhancedSelect\" data-toggle=\"modal\" data-target=\"#enhanced_select_content_wrapper\" >enhancedSelectTagLib</a>"
  }
}
