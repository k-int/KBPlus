package com.k_int.kbplus

class OrgRole {

  static belongsTo = [
    org:Org
  ]

  RefdataValue roleType

  // For polymorphic joins based on "Target Context"
  Package pkg
  Subscription sub
  License lic
  TitleInstance title
  Date startDate
  Date endDate

  static mapping = {
          id column:'or_id'
     version column:'or_version'
         org column:'or_org_fk', index:'or_org_rt_idx'
    roleType column:'or_roletype_fk', index:'or_org_rt_idx'
         pkg column:'or_pkg_fk'
         sub column:'or_sub_fk'
         lic column:'or_lic_fk'
       title column:'or_title_fk'
   startDate column:'or_start_date'
     endDate column:'or_end_date'
  }

  static constraints = {
    roleType(nullable:true, blank:false)
    pkg(nullable:true, blank:false)
    sub(nullable:true, blank:false)
    lic(nullable:true, blank:false)
    title(nullable:true, blank:false)
    startDate(nullable:true, blank:false)
    endDate(nullable:true, blank:false)
  }
  
  
  static def assertOrgTitleLink(porg, ptitle, prole) {
    // def link = OrgRole.findByTitleAndOrgAndRoleType(ptitle, porg, prole) ?: new OrgRole(title:ptitle, org:porg, roleType:prole).save();

    if ( porg && ptitle && prole ) {

      def link = OrgRole.find{ title==ptitle && org==porg && roleType==prole }
      if ( ! link ) {
        link = new OrgRole(title:ptitle, org:porg, roleType:prole)
        if ( !porg.links )
          porg.links = [link]
        else
          porg.links.add(link)
  
        porg.save(flush:true, failOnError:true);
      }
    }
  }

  static def assertOrgPackageLink(porg, ppkg, prole) {

    if ( porg && ppkg && prole ) {
      def link = OrgRole.find{ org==porg && pkg==ppkg && roleType==prole }
      if ( ! link ) {
        link = new OrgRole(pkg:ppkg, org:porg, roleType:prole)
        if ( !porg.links )
          porg.links = [link]
        else
          porg.links.add(link)

        porg.save();
      }
    }
  }
  
}
