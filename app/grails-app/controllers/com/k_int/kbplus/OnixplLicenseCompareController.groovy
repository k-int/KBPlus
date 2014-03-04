package com.k_int.kbplus

import com.k_int.kbplus.onixpl.OnixPLService
import com.k_int.xml.XMLDoc
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

class OnixplLicenseCompareController {

  OnixPLService onixPLService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    ArrayList<OnixplLicense> oplList = OnixplLicense.list();
    [list: oplList, termList: onixPLService.tsComparisonPoints]
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def matrix() {
    
    // Get the main licence.
    Long license_id = params.long("license1")
    OnixplLicense main_license = OnixplLicense.get(license_id)
    
    // All licenses need to be compared.
    boolean allLicenses = false
    
    // Cast each element in the list to a Long.
    List<Long> licenses =  params.list("license2").collect { String param ->
      if (param.toLowerCase() == "all") {
        allLicenses = true
      } else {
        return param as Long
      }
    }
    
    // Get the list of licenses we are comparing with.
    List<OnixplLicense> compare_to =
      allLicenses ? OnixplLicense.findAllByIdNotEqual( license_id ) : OnixplLicense.findAllByIdInList( licenses )
    
    // The list to limit to.
    ArrayList<String> comparison_points = params.list("sections")
    
    // Should we compare all?
    if (comparison_points.remove("_:PublicationsLicenseExpression") == true) {
      comparison_points = onixPLService.allComparisonPoints
    }
    
    // Go through each of the available or requested comparison points and examine them to determine equality.
    def compared_data = [:]    
    
    comparison_points.each { xpath_expr ->
      
      // Query the main license for xpath results.
      def results = main_license.getXML().XPath(xpath_expr)
      
      results.each { org.w3c.dom.Node node ->
        
        // Create our new XML element of the segment.        
        compared_data[xpath_expr] = new XMLDoc (node).toMap()
      }
      
    }
    
    render compared_data as JSON
    
//    main_license.xml.transform(response.outputStream)
    
//    Integer max = checkParam(params.max, 5);
//    ArrayList<RefdataValue> termList = RefdataValue.findAllByOwner(RefdataCategory.findByDesc("UsageType")).sort { it.id };
//    Integer id1 = checkParam(params.license1, null);
//    OnixplLicense opl1 = OnixplLicense.get(id1);
//    ArrayList<OnixplLicense> licenseList;
//    if (!"all".equals(params.license2)) {
//      if (params.license2 instanceof Object[]) {
//        licenseList = new ArrayList<OnixplLicense>();
//        for (item in params.license2) {
//          def opl2 = OnixplLicense.get(item);
//          licenseList.add(opl2);
//        }
//      } else {
//        def id2 = checkParam(params.license2, null);
//        licenseList = new ArrayList<OnixplLicense>() {
//              {
//                add(OnixplLicense.get(id2));
//              }
//            };
//      }
//    } else {
//      licenseList = OnixplLicense.list();
//    }
//    ArrayList<OnixplLicense> filteredLicenseList = filterLicenses(licenseList, opl1, params.match, params.section);
//    Integer total = filteredLicenseList.size();
//    Integer end = offset + max > total ? total : offset + max;
//    if (offset > end) {
//      offset = end;
//    }
//    if (filteredLicenseList.size() > 0 && filteredLicenseList.get(0) != null) {
//      filteredLicenseList = filteredLicenseList.subList(offset, end);
//    }
//
//    [termList: termList, license: opl1, list: filteredLicenseList, total: total, section: params.section, match: params.match]
  }

  //    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  //    def export() {
  //        StringBuilder sb = new StringBuilder();
  //        OnixplLicense opl1 = OnixplLicense.get(checkParam(params.license1, null));
  //        if (opl1) {
  //            sb.append("\t\t${opl1.title}");
  //            if (!params.license2 instanceof Object[] && !"all".equals(params.license2)) {
  //                ArrayList<OnixplLicense> opl2 = new ArrayList<OnixplLicense>();
  //                opl2.add(OnixplLicense.get(checkParam(params.license2, null)));
  //                ArrayList<OnixplLicense> filteredLicenseList = filterLicenses(opl2, opl1, params.match, params.section);
  //                sb.append(buildExportTable(opl1, filteredLicenseList));
  //            } else {
  //                ArrayList<OnixplLicense> licenseList;
  //                if (params.license2 instanceof Object[]) {
  //                    licenseList = new ArrayList<OnixplLicense>();
  //                    for (String licenseId in params.license2) {
  //                        licenseList.add(OnixplLicense.get(checkParam(licenseId, null)));
  //                    }
  //                } else {
  //                    licenseList = OnixplLicense.list();
  //                }
  //                ArrayList<OnixplLicense> filteredLicenseList = filterLicenses(licenseList, opl1, params.match, params.section);
  //                sb.append(buildExportTable(opl1, filteredLicenseList));
  //            }
  //            response.setHeader("Content-disposition", "attachment; filename=export.tsv");
  //            response.contentType = "text/tab-separated-values";
  //            response.outputStream << sb.toString();
  //            response.outputStream.flush();
  //        } else {
  //            flash.message = "No data to export!";
  //            redirect(controller: "OnixplLicenseCompare", action: "matrix", params: params);
  //        }
  //    }
  //
  //    /**
  //     * Builds a tab-separated table of licenses based on the list of licenses provided.
  //     * @param opl
  //     * @param licenseList
  //     * @return
  //     */
  //    private static String buildExportTable(OnixplLicense opl, ArrayList<OnixplLicense> licenseList) {
  //        StringBuilder sb = new StringBuilder();
  //        ArrayList<RefdataValue> termList = RefdataValue.findAllByOwner(RefdataCategory.findByDesc("UsageType")).sort { it.id };
  //        for (OnixplLicense license : licenseList) {
  //            sb.append("\t${license.title}");
  //        }
  //        sb.append("\n");
  //        sb.append("\tMatch?\t\t");
  //        for (OnixplLicense license : licenseList) {
  //            sb.append(opl.compare(license, null).toString() + "\t");
  //        }
  //        sb.append("\n");
  //        for (RefdataValue rdv : termList) {
  //            sb.append("\t${rdv.value}\t");
  //            ArrayList<OnixplUsageTerm> ut1List = OnixplUsageTerm.findAllByOplLicenseAndUsageType(opl, rdv);
  //            sb.append(buildUsageTerm(ut1List));
  //            for (OnixplLicense license : licenseList) {
  //                ArrayList<OnixplUsageTerm> utList = OnixplUsageTerm.findAllByOplLicenseAndUsageType(license, rdv);
  //                sb.append("\t");
  //                sb.append(buildUsageTerm(utList));
  //            }
  //            sb.append("\n");
  //        }
  //        return sb.toString();
  //    }
  //
  //    /**
  //     * Builds the cell that reflects the content of each usage term
  //     * @param utList
  //     * @return
  //     */
  //    private static String buildUsageTerm(ArrayList<OnixplUsageTerm> utList) {
  //        StringBuilder sb = new StringBuilder();
  //        if (utList.size() > 0) {
  //            for (OnixplUsageTerm ut1 : utList) {
  //                sb.append(ut1.usageStatus.value + ", ");
  //            }
  //        } else {
  //            sb.append("No value");
  //        }
  //        return sb.toString();
  //    }
  //
  //    /**
  //     * Filters out licenses that don't match the criteria given by match and section. If section is not given then the
  //     * entire license will be taken into account. If there is more than one result then the license that is being
  //     * compared is removed to make the table less cluttered. If the license only matches itself then that license is
  //     * shown. This may be confusing and can be easily changed if necessary.
  //     * @param licenseList
  //     * @param license
  //     * @param match
  //     * @param section
  //     * @return
  //     */
  //    private static ArrayList<OnixplLicense> filterLicenses(ArrayList<OnixplLicense> licenseList, OnixplLicense license, String match, String section) {
  //        if (match == null || match.isEmpty()) {
  //            if (licenseList.size() > 1) {
  //                licenseList.remove(license);
  //            }
  //            return licenseList;
  //        }
  //        ArrayList<OnixplLicense> filteredList = new ArrayList<OnixplLicense>();
  //        for (OnixplLicense opl : licenseList) {
  //            if (opl.compare(license, checkParam(section, 0)) == Boolean.valueOf(match)) {
  //                filteredList.add(opl);
  //            }
  //        }
  //        if (filteredList.size() > 1) {
  //            filteredList.remove(license);
  //        }
  //        return filteredList;
  //    }
  //
  //    /**
  //     * Method to convert params to integers and catch any values that can't be converted. In the case of a failure the
  //     * default value is assigned.
  //     * @param param
  //     * @param defaultValue
  //     * @return
  //     */
  //    private static Integer checkParam(String param, Integer defaultValue) {
  //        Integer value;
  //        try {
  //            value = Integer.valueOf(param);
  //        } catch (NumberFormatException nfe) {
  //            value = defaultValue;
  //        }
  //        return value;
  //    }
  //
  //    /**
  //     * Display method that takes all of the license text associated with a usage term and formats it for display. Not
  //     * all license texts have a display number and so, in their absence, the elementId is used.
  //     * @param oput
  //     * @return
  //     */
  //    public static String getLicenseText(OnixplUsageTerm oput) {
  //        StringBuilder sb = new StringBuilder();
  //        for (OnixplUsageTermLicenseText utlt : oput.usageTermLicenseText.sort { it.licenseText.text }) {
  //            if (utlt.licenseText.displayNum) {
  //                sb.append(utlt.licenseText.displayNum + " ");
  //            } else {
  //                sb.append(utlt.licenseText.elementId + " ");
  //            }
  //            sb.append(utlt.licenseText.text);
  //            sb.append("\n");
  //        }
  //        return sb.toString().trim();
  //    }
  //
  //    /**
  //     * If the license results are filtered then this method returns a message stating which section has been used for
  //     * matching and whether the requirement is a positive or negative match.
  //     * @param match
  //     * @param section
  //     * @return
  //     */
  //    public static String getSectionMessage(String match, String section) {
  //        RefdataValue rdv = RefdataValue.get(section);
  //        if (Boolean.valueOf(match)) {
  //            return "Showing results that match section ${rdv.value}";
  //        } else {
  //            return "Showing results that do not match section ${rdv.value}";
  //        }
  //    }
  //
  //    /**
  //     * Produce a sorted list of usage terms for a particular ONIX-PL licence and refdata value. Sort by usage type,
  //     * licence text, user and usedresource.
  //     * @param opl
  //     * @param rdv
  //     * @return
  //     */
  //    public static List<OnixplUsageTerm> getUsageTermList(OnixplLicense opl, RefdataValue rdv) {
  //        List<OnixplUsageTerm> outList = OnixplUsageTerm.findAllByOplLicenseAndUsageType(opl, rdv);
  //        return outList.sort {a,b ->
  //                def aString = a.usageType.value.toString() + a.usageTermLicenseText.licenseText.sort {it.displayNum}.text.toString() + a.user.sort {it.value}.value.toString() + a.usedResource.sort {it.value}.value.toString();
  //                def bString = b.usageType.value.toString() + b.usageTermLicenseText.licenseText.sort {it.displayNum}.text.toString() + b.user.sort {it.value}.value.toString() + b.usedResource.sort {it.value}.value.toString();
  //        aString <=> bString};
  //    }
}
