import com.k_int.kbplus.*

class EditTwoTagLib {

  def hasManyThrough = { attrs, body ->
    log.debug("hasManyThrough = ${attrs}");
    // out << "<a href=\"#\" class=\"enhancedSelect\" data-toggle=\"modal\" data-target=\"#enhanced_select_content_wrapper\" data-profile=\""+attrs.refdataProfile+"\" data-owner=\"${attrs.owner.class.name}:${attrs.owner.id}:${attrs.ownerProperty}\"autocomplete=\"off\">"
    // out << body()
    // out << render(template:"/taglibTemplates/iconText", model:modelMap)
    out << render(template:"/templates/hasManyThrough")
    out << "EditTwo::hasManyThrough"
  }
}
