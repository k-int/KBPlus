package com.k_int.kbplus

import javax.persistence.Transient

import com.k_int.kbplus.auth.Role
import com.k_int.kbplus.onixpl.OnixPLHelperService
import com.k_int.kbplus.onixpl.OnixPLService
import com.k_int.xml.XMLDoc
import groovy.util.logging.Log4j

/**
 * An OnixplLicense has many OnixplUsageTerms and OnixplLicenseTexts.
 * It can be associated with many licenses.
 * The OnixplLicenseTexts relation is redundant as UsageTerms refer to the
 * LicenseTexts, but is a convenient way to access the whole license text.
 */

@Log4j
class OnixplLicense {

  Date lastmod;
  String title;

  // An ONIX-PL license relates to a a doc
  Doc doc;

  @Transient
  private XMLDoc xml

  @Transient
  private OnixPLService onixService

  @Transient
  private OnixPLHelperService onixHelperService

  @Transient
  public setOnixPLService (service) {
    onixService = service
  }

  @Transient
  public setOnixPLHelperService (service) {
    onixHelperService = service
  }


  @Transient
  public XMLDoc getXML() {
    xml = xml ?: new XMLDoc (doc.getBlobContent().binaryStream)
    xml
  }

  // One to many
  static hasMany = [
    usageTerm: OnixplUsageTerm,
    licenseText: OnixplLicenseText,
    licenses: License
  ]

  // Reference to license in the many
  static mappedBy = [
    usageTerm: 'oplLicense',
    licenseText: 'oplLicense',
    licenses: 'onixplLicense',
  ]

  static mapping = {
    id column: 'opl_id'
    version column: 'opl_version'
    doc column: 'opl_doc_fk'
    lastmod column: 'opl_lastmod'
    title column: 'opl_title'
  }

  static constraints = {
    doc(nullable: false, blank: false)
    lastmod(nullable: true, blank: true)
    title(nullable: false, blank: false)
  }

  // Only admin has permission to change ONIX-PL licenses;
  // anyone can view them.
  def hasPerm(perm, user) {
    if (perm == 'view') return true;
    // If user is a member of admin role, they can do anything.
    def admin_role = Role.findByAuthority('ROLE_ADMIN');
    if (admin_role) return user.getAuthorities().contains(admin_role);
    false;
  }


  @Override
  public java.lang.String toString() {
    return "OnixplLicense{" +
        "id=" + id +
        ", lastmod=" + lastmod +
        ", title='" + title + '\'' +
        ", doc=" + doc +
        '}';
  }

  public Map toMap (List<String> sections = null) {

    // Get all comparison points as a map.
    Map all_points = onixService.allComparisonPointsMap

    // Default to all points.
    sections = sections ?: all_points.keySet() as List

    // Go through each of the available or requested comparison points and examine them to determine equality.
    TreeMap data = [:]

    sections.each { String xpath_expr ->

      def group = all_points."${xpath_expr}"?."group"
      if (group) {
        if (data[group] == null) data[group] = new TreeMap<String, List<Map>>().withDefault {
          []
        }

        def xml = getXML()
        
        // log.debug("XPath expression: ${xpath_expr}")
        
        // Query for xpath results.
        def results = xml.XPath(xpath_expr)
        if (results.length > 0) {

          // For each of the results we need to add a map representation to the result.
          results.each { org.w3c.dom.Node node ->

            node = onixHelperService.duplicateDefinitionText(node,xml)

            def snippet = new XMLDoc (node)
            snippet = onixHelperService.replaceAllTextElements(xml, snippet)

            // Create our new XML element of the segment.
            def xml_maps = snippet.toMaps()
            
            // if we have a post processor then execute it here
            all_points."${xpath_expr}"?."processor"?.call(xml_maps)
            
            // Add the data to the result.
            data[group][xpath_expr] += xml_maps
          }
        }
      }
    }

    // Return the data.
    data
  }

  /**
   * Given an ONIX-PL license and a section of the license this method will return a boolean reflecting whether
   * the usage terms are the same in each license. The status value and the content of the license text are used in
   * this comparison.
   * @param opl
   * @param section
   * @return
   */
  private Boolean compareSection(OnixplLicense opl, RefdataValue section) {
    ArrayList<OnixplUsageTerm> utlist1 = OnixplUsageTerm.findAllByOplLicenseAndUsageType(opl, section).sort { it.usageTermLicenseText.sort { it.licenseText.text }.get(0).licenseText.text };
    ArrayList<OnixplUsageTerm> utlist2 = OnixplUsageTerm.findAllByOplLicenseAndUsageType(this, section).sort { it.usageTermLicenseText.sort { it.licenseText.text }.get(0).licenseText.text };
    if (utlist1.size() != utlist2.size()) {
      return false;
    }
    for (int i = 0; i < utlist1.size(); i++) {
      OnixplUsageTerm ut1 = utlist1.get(i);
      OnixplUsageTerm ut2 = utlist2.get(i);
      if (ut1.usageType.value != ut2.usageType.value) {
        return false;
      }
      if (ut1.usageStatus.value != ut2.usageStatus.value) {
        return false;
      }
      ArrayList<OnixplUsageTermLicenseText> ltList1 = ut1.usageTermLicenseText.sort { it.licenseText.text }.asList();
      ArrayList<OnixplUsageTermLicenseText> ltList2 = ut2.usageTermLicenseText.sort { it.licenseText.text }.asList();
      StringBuilder sb1 = new StringBuilder();
      for (OnixplUsageTermLicenseText lt : ltList1) {
        sb1.append(lt.licenseText.text);
      }
      StringBuilder sb2 = new StringBuilder();
      for (OnixplUsageTermLicenseText lt : ltList2) {
        sb2.append(lt.licenseText.text);
      }
      if (sb1.toString() != sb2.toString()) {
        return false;
      }
    }
    return true;
  }

  /**
   * If a section is specified then the licenses will be compared only taking into account the section. If no section
   * is given then both licenses will be compared in their entirety.
   * @param opl
   * @param section
   * @return
   */
  public Boolean compare(OnixplLicense opl, Integer section) {
    if (section) {
      return compareSection(opl, RefdataValue.get(section));
    } else {
      if (opl.usageTerm.size() != this.usageTerm.size()) {
        return false;
      }
      // A license can have multiple usage terms for a certain usage type and these usage terms can have multiple
      // license texts associated with them. In order to be able to compare these they have to be put in the same
      // order. They are ordered using the usageType initially and then the content of the license text.
      def utList1 = opl.usageTerm.sort {it.usageType.value};
      utList1.sort {it.usageStatus.value}
      def utList2 = this.usageTerm.sort {it.usageType.value};
      utList2.sort {it.usageStatus.value}
      for (int i = 0; i < utList1.size(); i++) {
        if (utList1.get(i).usageType?.value != utList2.get(i).usageType?.value) {
          return false;
        } else if (utList1.get(i).usageStatus.value != utList2.get(i).usageStatus.value) {
          return false;
        }
      }
      // In order to compare the license text for a given usage term the license texts are ordered and then
      // combined. If the aggregated strings are the same then it is assumed that the content of the license
      // texts was the same.
      def ltList1 = opl.licenseText.sort { it.text }.toList();
      StringBuilder sb1 = new StringBuilder();
      for (OnixplLicenseText lt1 : ltList1) {
        sb1.append(lt1.text);
      }
      def ltList2 = this.licenseText.sort { it.text }.toList();
      StringBuilder sb2 = new StringBuilder();
      for (OnixplLicenseText lt2 : ltList2) {
        sb2.append(lt2.text);
      }
      if (sb1.toString() != sb2.toString()) {
        return false;
      }
      def userList1 = opl.usageTerm.sort {it.usageType.value}.user.sort {it.value};
      def userList2 = this.usageTerm.sort {it.usageType.value}.user.sort {it.value};
      if (userList1.toString() != userList2.toString()) {
        return false;
      }
      return true;
    }
  }
  static def refdataFind(params) {
      def result = []
      def  ql = findAllByTitleIlike(params.q,params)
      if ( ql ) {
          ql.each { prop ->
              result.add([id:"${prop.title}||${prop.id}",text:"${prop.title}"])
          }
      }
      result
  }
}
