package com.k_int.kbplus

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This service should contain the methods required to build the different exported files.
 * CSV methods will stream out the content of the file to a given output.
 * XML methods are provided to build the XML document
 * JSON methods build a Map object which can then be converted into Json.
 * 
 * @author wpetit
 */
class ExportService {
	def formatter = new java.text.SimpleDateFormat("yyyy-MM-dd")
	
	/* *************
	 *  CSV Exports 
	 */
	def StreamOutSubsCSV(out, sub, entitlements, header){
		def jc_id = sub.getSubscriber()?.getIdentifierByType('JC')?.value
		out.withWriter { writer ->
			def tsdate = sub.startDate ? formatter.format(sub.startDate) : ''
			def tedate = sub.endDate ? formatter.format(sub.endDate) : ''
			if ( header ) {
				writer.write("FileType,SpecVersion,JC_ID,TermStartDate,TermEndDate,SubURI,SystemIdentifier\n")
				writer.write("${sub.type.value},\"2.0\",${jc_id?:''},${tsdate},${tedate},\"uri://kbplus/sub/${sub.identifier}\",${sub.impId}\n")
			}
	 
			// Output the body text
			// writer.write("publication_title,print_identifier,online_identifier,date_first_issue_subscribed,num_first_vol_subscribed,num_first_issue_subscribed,date_last_issue_subscribed,num_last_vol_subscribed,num_last_issue_subscribed,embargo_info,title_url,first_author,title_id,coverage_note,coverage_depth,publisher_name\n");
			writer.write("publication_title,print_identifier,online_identifier,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,title_url,first_author,title_id,embargo_info,coverage_depth,coverage_notes,publisher_name\n");
	 
			entitlements.each { e ->
	 
				def start_date = e.startDate ? formatter.format(e.startDate) : '';
				def end_date = e.endDate ? formatter.format(e.endDate) : '';
				def title_doi = (e.tipp?.title?.getIdentifierValue('DOI'))?:''
				def publisher = e.tipp?.title?.publisher
	 
				writer.write("\"${e.tipp.title.title}\",\"${e.tipp?.title?.getIdentifierValue('ISSN')?:''}\",\"${e.tipp?.title?.getIdentifierValue('eISSN')?:''}\",${start_date},${e.startVolume?:''},${e.startIssue?:''},${end_date},${e.endVolume?:''},${e.endIssue?:''},\"${e.tipp?.hostPlatformURL?:''}\",,\"${title_doi}\",\"${e.embargo?:''}\",\"${e.tipp?.coverageDepth?:''}\",\"${e.tipp?.coverageNote?:''}\",\"${publisher?.name?:''}\"\n");
			}
			writer.flush()
			writer.close()
		}
	}
	
	/**
	 * This function will stream out the list of titles in a CSV format.
	 * 
	 * @param out - the {@link #java.io.OutputStream OutputStream}
	 * @param entitlements - the list of {@link #com.k_int.kbplus.IssueEntitlement IssueEntitlement}
	 */
	def StreamOutTitlesCSV(out, entitlements){
		def starttime = printStart("Get Namespaces and max IE")
		// Get distinct ID.Namespace and the maximum of entitlements for one title
		def namespaces = []
		def current_title_id = -1
		def current_nb_ie = 0
		def max_nb_ie = 1
		entitlements.each(){ ie ->
			def ti = ie.tipp.title
			if(ti.id != current_title_id){
				current_title_id = ti.id
				if(max_nb_ie<current_nb_ie) max_nb_ie = current_nb_ie
				current_nb_ie = 1
				//Add namespace
				ti.ids.each(){ id -> namespaces.add(id.identifier.ns.ns) }
			}else{
				current_nb_ie ++
			}
		}
		namespaces.unique()
		printDuration(starttime, "Get Namespaces and max IE=${max_nb_ie}")
		
		out.withWriter { writer ->
			// Output the header
			writer.write("Title,")
			namespaces.each(){ ns -> writer.write("${ns},") }
			writer.write("Earliest date,Latest date")
			(1..max_nb_ie).each(){
				writer.write(",IE.${it}.Subscription name,")
				writer.write("IE.${it}.Start date,")
				writer.write("IE.${it}.Start Volume,")
				writer.write("IE.${it}.Start Issue,")
				writer.write("IE.${it}.End date,")
				writer.write("IE.${it}.End Volume,")
				writer.write("IE.${it}.End Issue,")
				writer.write("IE.${it}.Embargo,")
				writer.write("IE.${it}.Coverage,")
				writer.write("IE.${it}.Coverage note,")
				writer.write("IE.${it}.platform.host.name,")
				writer.write("IE.${it}.platform.host.url,")
				writer.write("IE.${it}.platform.admin.name,")
				writer.write("IE.${it}.Core status,")
				writer.write("IE.${it}.Core start,")
				writer.write("IE.${it}.Core end")
			}
			writer.write("\n")
			
//              result.titles.each { title ->
//				  def ti = title[0]
			current_title_id = -1
			String entitlements_str
			def earliest_date
			def latest_date
			entitlements.each { e ->
				if(e.tipp.title.id != current_title_id){
					if(current_title_id != -1){
						//Write earliest and latest dates
						writer.write("\"${earliest_date?formatter.format(earliest_date):''}\",");
						writer.write("\"${latest_date?formatter.format(latest_date):''}\"");
						//Write entitlements
						writer.write("${entitlements_str}");
						writer.write("\n");
					}
					
					//Start a new title
					current_title_id = e.tipp.title.id
					def ti = e.tipp.title
					entitlements_str = ""
					
					writer.write("\"${ti.title}\",");
					namespaces.each(){ ns ->
						writer.write("\"${ti.getIdentifierValue(ns)?:''}\",");
					}
					earliest_date = e.startDate?:null
					latest_date = e.endDate?:null
				}
				
				if(e.startDate && (!earliest_date || earliest_date>e.startDate)) earliest_date = e.startDate
				if(e.endDate && (!latest_date || latest_date<e.endDate)) latest_date = e.endDate
				
//                    grouped_ies[ti[0].id].each(){ ie ->
				entitlements_str += ",\"${e.subscription.name}\","
				entitlements_str += "${e.startDate?formatter.format(e.startDate):''},"
				entitlements_str += "\"${e.startVolume?:''}\","
				entitlements_str += "\"${e.startIssue?:''}\","
				entitlements_str += "${e.endDate?formatter.format(e.endDate):''},"
				entitlements_str += "\"${e.endVolume?:''}\","
				entitlements_str += "\"${e.endIssue?:''}\","
				entitlements_str += "\"${e.embargo?:''}\","
				entitlements_str += "\"${e.coverageDepth?:''}\","
				entitlements_str += "\"${e.coverageNote?:''}\","
				entitlements_str += "\"${e.tipp?.platform?.name?:''}\","
				entitlements_str += "\"${e.tipp?.hostPlatformURL?:''}\","
				entitlements_str += "\""
				e.tipp?.additionalPlatforms.eachWithIndex(){ ap, i ->
					if(i>0) entitlements_str += ", "
					entitlements_str += "${ap.platform.name}"
				}
				entitlements_str += "\","
				entitlements_str += "\"${e.coreStatus?.value?:''}\","
				entitlements_str += "\"${e.coreStatusStart?formatter.format(e.coreStatusStart):''}\","
				entitlements_str += "\"${e.coreStatusEnd?formatter.format(e.coreStatusEnd):''}\""
//                    }
			}
			
			//Write earliest and latest dates for last title
			writer.write("\"${earliest_date?formatter.format(earliest_date):''}\",");
			writer.write("\"${latest_date?formatter.format(latest_date):''}\"");
			//Write entitlements for last title
			writer.write("${entitlements_str}");
			writer.write("\n");
			
			writer.flush()
			writer.close()
		}
	}
	
	/* ************
	 * XML Exports
	 */
	
	/**
	 * Create the document and with the root Element of the XML file
	 * 
	 * @param root - the name of the root {@link #org.w3c.dom.Element Element}
	 * @return the {@link #org.w3c.dom.Document Document} created
	 */
	def buildDocXML(root) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(root);
		doc.appendChild(rootElement);
		
		return doc
	}
	
	/**
	 * Add a list of titles from a given entitlement list into a given Element
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document}
	 * @param into_elem - the {@link #org.w3c.dom.Element Element} into which we want to insert the list of titles
	 * @param entries - the list of {@link com.k_int.kbplus.IssueEntitlement} or {@link com.k_int.kbplus.TitleInstancePackagePlatform}
	 * @param type -  either "TIPP" or default "Issue Entitlement"
	 */
    def addTitleListXML(Document doc, Element into_elem, List entries, String type = "Issue Entitlement") {
		def current_title_id = -1
		
		Element titlelistentry
		entries.each { e ->
			// There is a few distinction between TIPP and IE objects, they are handled here
			def tipp = (type=="Issue Entitlement")?e.tipp:e
			def sub  = (type=="Issue Entitlement")?e.subscription:e.sub
			def status  = (type=="Issue Entitlement")?e.coreStatus:e.status
			
			if(tipp.title.id != current_title_id){
				current_title_id = tipp.title.id
				def ti = tipp.title
				
				// TitleListEntry elements
				titlelistentry = addXMLElementInto(doc, into_elem, "TitleListEntry", null)
				// Title elements
				Element title = addXMLElementInto(doc, titlelistentry, "Title", ti.title)
				// TitleIDs elements
				Element titleids = addXMLElementInto(doc, titlelistentry, "TitleIDs", null)
				
				ti.ids.each(){ id ->
					def value = id.identifier.value
					def ns = id.identifier.ns.ns
					Element titleid = addXMLElementInto(doc, titleids, "ID", value)
					// set attribute to titleid element
					addXMLAttr(doc, titleid, "namespace", ns)
				}
			}
			
			// CoverageStatement elements
			Element coveragestatement = addXMLElementInto(doc, titlelistentry, "CoverageStatement", null)
			addXMLAttr(doc, coveragestatement, "type", type)
			
			addXMLElementInto(doc, coveragestatement, "SubscriptionID", sub?.id?:'')
			addXMLElementInto(doc, coveragestatement, "SubscriptionName", sub?.name?:'')
			addXMLElementInto(doc, coveragestatement, "StartDate", e.startDate?formatter.format(e.startDate):'')
			addXMLElementInto(doc, coveragestatement, "StartVolume", e.startVolume?:'')
			addXMLElementInto(doc, coveragestatement, "StartIssue", e.startIssue?:'')
			addXMLElementInto(doc, coveragestatement, "EndDate", e.endDate?formatter.format(e.endDate):'')
			addXMLElementInto(doc, coveragestatement, "EndVolume", e.endVolume?:'')
			addXMLElementInto(doc, coveragestatement, "EndIssue", e.endIssue?:'')
			addXMLElementInto(doc, coveragestatement, "Embargo", e.embargo?:'')
			addXMLElementInto(doc, coveragestatement, "Coverage", e.coverageDepth?:'')
			addXMLElementInto(doc, coveragestatement, "CoverageNote", e.coverageNote?:'')
			addXMLElementInto(doc, coveragestatement, "HostPlatformName", tipp?.platform?.name?:'')
			addXMLElementInto(doc, coveragestatement, "HostPlatformURL", tipp?.hostPlatformURL?:'')
			
			tipp.additionalPlatforms.each(){ ap ->
				def platform = addXMLElementInto(doc, coveragestatement, "Platform", null)
				addXMLElementInto(doc, platform, "PlatformName", ap.platform?.name?:'')
				addXMLElementInto(doc, platform, "PlatformRole", ap.rel?:'')
				addXMLElementInto(doc, platform, "PlatformURL", ap.platform?.primaryUrl?:'')
			}
			
			addXMLElementInto(doc, coveragestatement, "CoreStatus", status?.value?:'')
			addXMLElementInto(doc, coveragestatement, "CoreStart", e.coreStatusStart?formatter.format(e.coreStatusStart):'')
			addXMLElementInto(doc, coveragestatement, "CoreEnd", e.coreStatusEnd?formatter.format(e.coreStatusEnd):'')
			addXMLElementInto(doc, coveragestatement, "PackageID", tipp?.pkg?.id?:'')
			addXMLElementInto(doc, coveragestatement, "PackageName", tipp?.pkg?.name?:'')
					
		}
    }
	
	/**
	 * Add the licences of a given list into a given XML element.
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document} to update
	 * @param into_elem - the {@link #org.w3c.dom.Element Element} we want to put the list of licence(s) in.
	 * @param lics - the {@link com.k_int.kbplus.License} list
	 */
	def addLicencesIntoXML(Document doc, Element into_elem, List lics) {
		lics.each() { licence ->
			def licElem = addXMLElementInto(doc, into_elem, "Licence", null)
			addXMLElementInto(doc, licElem, "LicenceReference", licence.reference)
			addXMLElementInto(doc, licElem, "NoticePeriod", licence.noticePeriod)
			addXMLElementInto(doc, licElem, "LicenceURL", licence.licenseUrl)
			addXMLElementInto(doc, licElem, "LicensorRef", licence.licensorRef)
			addXMLElementInto(doc, licElem, "LicenseeRef", licence.licenseeRef)
			
			addRelatedOrgsIntoXML(doc, licElem, licence.orgLinks)
			
			def licPropElem = addXMLElementInto(doc, licElem, "LicenceProperties", null)
			
			def concurrentAccessElem = addXMLElementInto(doc, licPropElem, "ConcurrentAccess", null)
			addXMLElementInto(doc, concurrentAccessElem, "Status", licence.concurrentUsers?.value)
			addXMLElementInto(doc, concurrentAccessElem, "UserCount", licence.concurrentUserCount)
			addXMLElementInto(doc, concurrentAccessElem, "Notes", licence.getNote("concurrentUsers")?.owner?.content?:"")
			
			def remoteAccessElem = addXMLElementInto(doc, licPropElem, "RemoteAccess", null)
			addXMLElementInto(doc, remoteAccessElem, "Status", licence.remoteAccess?.value)
			addXMLElementInto(doc, remoteAccessElem, "Notes", licence.getNote("remoteAccess")?.owner?.content?:"")
			
			def walkingAccessElem = addXMLElementInto(doc, licPropElem, "WalkingAccess", null)
			addXMLElementInto(doc, walkingAccessElem, "Status", licence.walkinAccess?.value)
			addXMLElementInto(doc, walkingAccessElem, "Notes", licence.getNote("walkinAccess")?.owner?.content?:"")
			
			def multisiteAccessElem = addXMLElementInto(doc, licPropElem, "MultisiteAccess", null)
			addXMLElementInto(doc, multisiteAccessElem, "Status", licence.multisiteAccess?.value)
			addXMLElementInto(doc, multisiteAccessElem, "Notes", licence.getNote("multisiteAccess")?.owner?.content?:"")
			
			def partnersAccessElem = addXMLElementInto(doc, licPropElem, "PartnersAccess", null)
			addXMLElementInto(doc, partnersAccessElem, "Status", licence.partnersAccess?.value)
			addXMLElementInto(doc, partnersAccessElem, "Notes", licence.getNote("partnersAccess")?.owner?.content?:"")
			
			def alumniAccessElem = addXMLElementInto(doc, licPropElem, "AlumniAccess", null)
			addXMLElementInto(doc, alumniAccessElem, "Status", licence.alumniAccess?.value)
			addXMLElementInto(doc, alumniAccessElem, "Notes", licence.getNote("alumniAccess")?.owner?.content?:"")
			
			def interLibraryLoansElem = addXMLElementInto(doc, licPropElem, "InterLibraryLoans", null)
			addXMLElementInto(doc, interLibraryLoansElem, "Status", licence.ill?.value)
			addXMLElementInto(doc, interLibraryLoansElem, "Notes", licence.getNote("ill")?.owner?.content?:"")
			
			def includeinCoursepacksElem = addXMLElementInto(doc, licPropElem, "IncludeinCoursepacks", null)
			addXMLElementInto(doc, concurrentAccessElem, "Status", licence.coursepack?.value)
			addXMLElementInto(doc, concurrentAccessElem, "Notes", licence.getNote("coursepack")?.owner?.content?:"")
			
			def includeinVLEElem = addXMLElementInto(doc, licPropElem, "IncludeinVLE", null)
			addXMLElementInto(doc, includeinVLEElem, "Status", licence.vle?.value)
			addXMLElementInto(doc, includeinVLEElem, "Notes", licence.getNote("vle")?.owner?.content?:"")
			
			def entrepriseAccessElem = addXMLElementInto(doc, licPropElem, "EntrepriseAccess", null)
			addXMLElementInto(doc, entrepriseAccessElem, "Status", licence.enterprise?.value)
			addXMLElementInto(doc, entrepriseAccessElem, "Notes", licence.getNote("enterprise")?.owner?.content?:"")
			
			def pcaEntitlementElem = addXMLElementInto(doc, licPropElem, "PostCancellationAccessEntitlement", null)
			addXMLElementInto(doc, pcaEntitlementElem, "Status", licence.pca?.value)
			addXMLElementInto(doc, pcaEntitlementElem, "Notes", licence.getNote("pca")?.owner?.content?:"")
		}
	}
	
	/**
	 * Add a subscription into a XML file
	 * It will also add the Licence (owner) and Titles of that subscription
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document} to update
	 * @param into_elem - the {@link #org.w3c.dom.Element Element} we want to put the list of licence(s) in.
	 * @param sub - the {@link com.k_int.kbplus.Subscription}
	 * @param entitlements - the list of {@link com.k_int.kbplus.IssueEntitlement}
	 */
	def addSubIntoXML(Document doc, Element into_elem, sub, entitlements) {
		def subElem = addXMLElementInto(doc, into_elem, "Subscription", null)
		addXMLElementInto(doc, subElem, "SubscriptionID", sub.id.toString())
		addXMLElementInto(doc, subElem, "SubscriptionName", sub.name)
		addXMLElementInto(doc, subElem, "SubTermStartDate", sub.startDate?formatter.format(sub.startDate):'')
		addXMLElementInto(doc, subElem, "SubTermEndDate", sub.endDate?formatter.format(sub.endDate):'')
		
		addRelatedOrgsIntoXML(doc, subElem, sub.orgRelations)
		
		if(sub.owner) addLicencesIntoXML(doc, subElem, [sub.owner])
		
		def titlesElem = addXMLElementInto(doc, subElem, "TitleList", null)
		addTitleListXML(doc, titlesElem, entitlements)
	}
	
	/**
	 * Add a package into a XML file
	 * It will also add the Licence and the Titles of that subscription
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document} to update
	 * @param into_elem - the {@link #org.w3c.dom.Element Element} we want to put the list of licence(s) in.
	 * @param pck - the {@link com.k_int.kbplus.Package}
	 * @param tipps - the list of {@link com.k_int.kbplus.TitleInstancePackagePlatform}
	 */
	def addPackageIntoXML(Document doc, Element into_elem, pck, tipps) {
		def subElem = addXMLElementInto(doc, into_elem, "Package", null)
		addXMLElementInto(doc, subElem, "PackageID", pck.id.toString())
		addXMLElementInto(doc, subElem, "PackageName", pck.name)
		addXMLElementInto(doc, subElem, "PackageTermStartDate", pck.startDate?formatter.format(pck.startDate):'')
		addXMLElementInto(doc, subElem, "PackageTermEndDate", pck.endDate?formatter.format(pck.endDate):'')
		
		addRelatedOrgsIntoXML(doc, subElem, pck.orgs)
		
		if(pck.license) addLicencesIntoXML(doc, subElem, [pck.license])
		
		def titlesElem = addXMLElementInto(doc, subElem, "TitleList", null)
		addTitleListXML(doc, titlesElem, tipps, "TIPP")
	}
	
	/**
	 * Add Organisation into a given Element.
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document} to update
	 * @param into_elem - the {@link #org.w3c.dom.Element Element} we want to put the list of licence(s) in.
	 * @param orgs - list of {@link com.k_int.kbplus.Organisations}
	 */
	private addRelatedOrgsIntoXML(Document doc, Element into_elem, orgs){
		orgs.each { or ->
			def orgElem = addXMLElementInto(doc, into_elem, "RelatedOrg", null)
			addXMLAttr(doc, orgElem, "id", or.org.id.toString())
			addXMLElementInto(doc, orgElem, "OrgName", or.org.name)
			addXMLElementInto(doc, orgElem, "OrgRole", or.roleType.value)
			
			def orgIDsElem = addXMLElementInto(doc, orgElem, "OrgIDs", null)
			or.org.ids.each(){ id ->
				def value = id.identifier.value
				def ns = id.identifier.ns.ns
				def idElem = addXMLElementInto(doc, orgIDsElem, "ID", value)
				addXMLAttr(doc, idElem, "namespace", ns)
			}
		}
	}
	
	/**
	 * Stream out a given Document into a given output.
	 * This function is using TransformerFactory to create the XML output.
	 * It will use UTF-8 and add line break and space to get a readable XML architecture.
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document} to stream
	 * @param out - the {@link java.io.OutputStream}
	 * @return - the {@link javax.xml.transform.stream.StreamResult} created
	 */
	def streamOutXML(doc, out) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //add line break
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1"); //add spaces for xml architecture
		DOMSource source = new DOMSource(doc);
		
		StreamResult streamout = new StreamResult(out);
		transformer.transform(source, streamout);
		
		return streamout
	}
	
	/* 
	 * A few useful method to build XML document 
	 */
	
	/**
	 * Add an attribute into a given Element
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document}
	 * @param e - the {@link #org.w3c.dom.Element Element} to update
	 * @param name - name of the attribute
	 * @param val - value of the attribute
	 */
	private Element addXMLAttr(Document doc, Element e, String name, String val){
		Attr attr = doc.createAttribute(name);
		attr.setValue(val);
		e.setAttributeNode(attr);
	}
	
	/**
	 * Add XML Element into another given Element
	 * 
	 * @param doc - the {@link #org.w3c.dom.Document Document}
	 * @param p - parent {@link #org.w3c.dom.Element Element}
	 * @param name - name of the element
	 * @param content - text content of the element 
	 * @return the {@link #org.w3c.dom.Element Element} created
	 */
	private Element addXMLElementInto(def doc, Element p, String name, def content){
		Element e = doc.createElement(name);
		if(content)
			e.appendChild(doc.createTextNode("${content}"));
		p.appendChild(e)
		return e
	}
	
	/* *************
	 * JSON EXPORTS
	 */
	
	/**
	 * Add a list of titles from a given entitlement list into a given Map.
	 * The Map created with this function has the purpose to be transformed into JSON.
	 * 
	 * @param into_map - Map which will contain the list
	 * @param ie_list - list of {@link com.k_int.kbplus.IssueEntitlement}
	 */
	def addTitlesToMap(into_map, ie_list, String type = "Issue Entitlement"){
		def current_title_id = -1
		def titles = []
		def title
		def entitlements
		ie_list.each { e ->
			// There is a few distinction between TIPP and IE objects, they are handled here
			def tipp = (type=="Issue Entitlement")?e.tipp:e
			def sub  = (type=="Issue Entitlement")?e.subscription:e.sub
			def status  = (type=="Issue Entitlement")?e.coreStatus:e.status
			
			if(tipp.title.id != current_title_id){
				//start new title
				if(current_title_id!=-1) titles.add(title) // not the first time
				title = [:]
				
				current_title_id = tipp.title.id
				def ti = tipp.title
				
				title."Title" = ti.title
			
				def ids = [:]
				ti.ids.each(){ id ->
					def value = id.identifier.value
					def ns = id.identifier.ns.ns
					if(ids.containsKey(ns)){
						def current = ids[ns]
						def newval = []
						newval << current
						newval << value
						ids[ns] = newval
					} else {
						ids[ns]=value
					}
				}
				title."TitleIDs" = ids
				entitlements = title."CoverageStatements" = []
			}
			
			def ie = [:]
			ie."CoverageStatementType" = type
			ie."SubscriptionID" = sub?.id
			ie."SubscriptionName" = sub?.name
			ie."StartDate" = e.startDate?formatter.format(e.startDate):''
			ie."StartVolume" = e.startVolume?:''
			ie."StartIssue" = e.startIssue?:''
			ie."EndDate" = e.endDate?formatter.format(e.endDate):''
			ie."EndVolume" = e.endVolume?:''
			ie."EndIssue" = e.endIssue?:''
			ie."Embargo" = e.embargo?:''
			ie."Coverage" = e.coverageDepth?:''
			ie."CoverageNote" = e.coverageNote?:''
			ie."HostPlatformName" = tipp?.platform?.name?:''
			ie."HostPlatformURL" = tipp?.hostPlatformURL?:''
			ie."AdditionalPlatforms" = []
			tipp?.additionalPlatforms.each(){ ap ->
				def platform = [:]
				platform.PlatformName = ap.platform?.name?:''
				platform.PlatformRole = ap.rel?:''
				platform.PlatformURL = ap.platform?.primaryUrl?:''
				ie."AdditionalPlatforms" << platform
			}
			ie."CoreStatus" = status?.value?:''
			ie."CoreStart" = e.coreStatusStart?formatter.format(e.coreStatusStart):''
			ie."CoreEnd" = e.coreStatusEnd?formatter.format(e.coreStatusEnd):''
			ie."PackageID" = tipp?.pkg?.id?:''
			ie."PackageName" = tipp?.pkg?.name?:''
			
			entitlements.add(ie)
		}
		titles.add(title) // add last title
		
		into_map."TitleList" = titles
	}
	
	/**
	 * Add Organisations into a given Map.
	 * The Map created with this function has the purpose to be transformed into JSON.
	 * 
	 * @param into_map - map which will contain the list of organisation
	 * @param orgs - list of {@link com.k_int.kbplus.Org}
	 */
	def addOrgMap(into_map, orgs){
		orgs.each { or ->
			def org = [:]
			org."OrgID" = or.org.id
			org."OrgName" = or.org.name
			org."OrgRole" = or.roleType.value
			
			def ids = [:]
			or.org.ids.each(){ id ->
				def value = id.identifier.value
				def ns = id.identifier.ns.ns
				if(ids.containsKey(ns)){
					def current = ids[ns]
					def newval = []
					newval << current
					newval << value
					ids[ns] = newval
				} else {
					ids[ns]=value
				}
			}
			org."OrgIDs" = ids
			
			into_map."RelatedOrgs" << org
		}
	}
	
	/**
	 * Add Licences into a given Map.
	 * The Map created with this function has the purpose to be transformed into JSON.
	 * 
	 * @param into_map - map which will contain the list of licences
	 * @param lics - list of {@link com.k_int.kbplus.License}
	 * @return the Map created
	 */
	def addLicensesToMap(into_map, lics){
		def licences = []
		
		lics.each { licence ->
			def lic = [:]
			
			lic."LicenceReference" = licence.reference
			lic."NoticePeriod" = licence.noticePeriod
			lic."LicenceURL" = licence.licenseUrl
			lic."LicensorRef" = licence.licensorRef
			lic."LicenseeRef" = licence.licenseeRef
				
			lic."RelatedOrgs" = []
			addOrgMap(lic, licence.orgLinks)
			
			def prop = lic."LicenceProperties" = [:]
			def ca = prop."ConcurrentAccess" = [:]
			ca."Status" = licence.concurrentUsers?.value
			ca."UserCount" = licence.concurrentUserCount
			ca."Notes" = licence.getNote("concurrentUsers")?.owner?.content?:""
			def ra = prop."RemoteAccess" = [:]
			ra."Status" = licence.remoteAccess?.value
			ra."Notes" = licence.getNote("remoteAccess")?.owner?.content?:""
			def wa = prop."WalkingAccess" = [:]
			wa."Status" = licence.walkinAccess?.value
			wa."Notes" = licence.getNote("walkinAccess")?.owner?.content?:""
			def ma = prop."MultisiteAccess" = [:]
			ma."Status" = licence.multisiteAccess?.value
			ma."Notes" = licence.getNote("multisiteAccess")?.owner?.content?:""
			def pa = prop."PartnersAccess" = [:]
			pa."Status" = licence.partnersAccess?.value
			pa."Notes" = licence.getNote("partnersAccess")?.owner?.content?:""
			def aa = prop."AlumniAccess" = [:]
			aa."Status" = licence.alumniAccess?.value
			aa."Notes" = licence.getNote("alumniAccess")?.owner?.content?:""
			def ill = prop."InterLibraryLoans" = [:]
			ill."Status" = licence.ill?.value
			ill."Notes" = licence.getNote("ill")?.owner?.content?:""
			def cp = prop."IncludeinCoursepacks" = [:]
			cp."Status" = licence.coursepack?.value
			cp."Notes" = licence.getNote("coursepack")?.owner?.content?:""
			def vle = prop."IncludeinVLE" = [:]
			vle."Status" = licence.vle?.value
			vle."Notes" = licence.getNote("vle")?.owner?.content?:""
			def ea = prop."EntrepriseAccess" = [:]
			ea."Status" = licence.enterprise?.value
			ea."Notes" = licence.getNote("enterprise")?.owner?.content?:""
			def pca = prop."PostCancellationAccessEntitlement" = [:]
			pca."Status" = licence.pca?.value
			pca."Notes" = licence.getNote("pca")?.owner?.content?:""
			
			licences << lic
		}
		into_map."Licences" = licences
		
		return into_map
	}
	
	
	/**
	 * Create a Subscription Map which has the purpose to be transformed into JSON.
	 * 
	 * @param sub - the {@link com.k_int.kbplus.Subscription}
	 * @param entitlements - list of {@link com.k_int.kbplus.IssueEntitlement}
	 * @return the Map created
	 */
	def getSubscriptionMap(sub, entitlements){
		def map = [:]
		def subscriptions = []
		
		def subscription = [:]
		subscription."SubscriptionID" = sub.id
		subscription."SubscriptionName" = sub.name
		subscription."SubTermStartDate" = sub.startDate?formatter.format(sub.startDate):''
		subscription."SubTermEndDate" = sub.endDate?formatter.format(sub.endDate):''
		
		subscription."RelatedOrgs" = []
		
		addOrgMap(subscription, sub.orgRelations)
		
		if(sub.owner) addLicensesToMap(subscription, [sub.owner])
		
		addTitlesToMap(subscription, entitlements)
					
		subscriptions.add(subscription)
		
		map."Subscriptions" = subscriptions
		
		return map
	}
	
	/**
	 * Create a Package Map which has the purpose to be transformed into JSON.
	 * 
	 * @param pck - the {@link com.k_int.kbplus.Package}
	 * @param tipps - the list of {@link com.k_int.kbplus.TitleInstancePackagePlatform}
	 * @return the Map created
	 */
	def getPackageMap(pck, tipps){
		def map = [:]
		def packages = []
		
		def pckage = [:]
		pckage."PackageID" = pck.id
		pckage."PackageName" = pck.name
		pckage."PackageTermStartDate" = pck.startDate?formatter.format(pck.startDate):''
		pckage."PackageTermEndDate" = pck.endDate?formatter.format(pck.endDate):''
				
		pckage."RelatedOrgs" = []
		
		addOrgMap(pckage, pck.orgs)
		
		if(pck.license) addLicensesToMap(pckage, [pck.license])
		
		addTitlesToMap(pckage, tipps, "TIPP")
					
		packages.add(pckage)
		
		map."Packages" = packages
		
		return map
	}
	
	/* **************
	 * OTHER METHODS
	 */
	
	/**
	 * This function has been created to track the time taken by the different methods provided by this service
	 * It's suppose to be run at the start of an event and it will catch the time and display it.
	 * 
	 * @param event - text which will be print out, describing the event
	 * @return time when the method is called
	 */
	def printStart(event){
		def starttime = new Date();
		log.debug("******* Start ${event}: ${starttime} *******")
		return starttime
	}
	
	/**
	 * This function has been created to track the time taken by the different methods provided by this service.
	 * It's suppose to be run at the end of an event.
	 * It will print the duration between the given time and the current time.
	 * 
	 * @param starttime - the time when the event started
	 * @param event - text which will be print out, describing the event
	 */
	def printDuration(starttime, event){
		use(groovy.time.TimeCategory) {
			def duration = new Date() - starttime
			log.debug("******* End ${event}: ${new Date()} *******")
			log.debug("Duration: ${(duration.hours*60)+duration.minutes}m ${duration.seconds}s")
		}
	}
	
	
}
