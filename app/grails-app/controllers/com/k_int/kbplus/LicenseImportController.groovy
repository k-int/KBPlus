package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured
import org.mozilla.universalchardet.UniversalDetector
import org.xml.sax.SAXException

class LicenseImportController {

  def CAT_TYPE = "UsageType", CAT_STATUS = "UsageStatus", CAT_DOCTYPE = 'Document Type', DOCTYPE = 'ONIX-PL License';

  // NOTE The spring security is not implemented properly in this class
  // See UploadController
  def springSecurityService
  def docstoreService
  def onixplPrefix = 'onixPL:'

  /**
   * Review the offered import to make sure it is a valid ONIX-PL file.
   * @return
   */
  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def doImport() {

    def result = [:]
    result.validationResult = [:]
    result.validationResult.messages = []
    result.validationResult.errors = []
    if (!"anonymousUser".equals(springSecurityService.principal)) {
      result.user = User.get(springSecurityService.principal.id);
    }

    // Try and read the file, and if it is okay, process the import
    if ( request.method == 'POST' ) {
      result.validationResult = readOfferedOnixPl(request)
      result.validationResult.success = false
      if ( result.validationResult.processFile == true ) {
        result.validationResult.messages.add("Document validated")
        //if (result.validationResult.messages) result.validationResult.messages.addAll(result.validationResult.messages)
        log.debug("Passed first phase validation, continue...")
        def importResults = processImport(result.validationResult)
        result.validationResult.putAll(importResults)
        if (importResults.termStatuses) {
          result.validationResult.termStatuses = importResults.termStatuses
          result.validationResult.success = true
          //result.validationResult.messages.add("Document processed") i
        }
        // TODO show licenses to link to
      } else {

      }
    }
    else {   }

    return result

    // Redirect to some ONIX-PL display page
    //log.debug("Redirecting...");
    //redirect controller: 'licenseDetails', action:'index', id:params.licid, fragment:params.fragment
  }


  /**
   * Read the offered ONIX-PL file.
   * @param request
   * @return result object
   */
  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def readOfferedOnixPl(request) {
    def result = [:]
    def file = request.getFile("importFile");
    result.file = file;
    def charset = checkCharset(file?.inputStream)
    log.debug("Request to upload ONIX-PL file type: ${file?.contentType} filename ${file?.getOriginalFilename()}");

    // ------------------------------------------------------------------------
    // Parse the XML doc for usage terms
    // ------------------------------------------------------------------------
    // File level messages
    result.messages=[]
    result.errors=[]

    //log.debug("Reading Stream");
    if  ( ( charset != null ) && ( ! charset.equals('UTF-8') ) ) {
      result.messages.add("WARNING: Detected input character stream encoding: ${charset}. Expected UTF-8");
    }

    try {
      def onixpl = new XmlSlurper().parse(file.inputStream);
      result.usageTerms = []
      onixpl.UsageTerms.Usage.eachWithIndex { ut, n ->
        result.usageTerms[n] = [:]
        result.usageTerms[n].type = ut.UsageType.text().replace(onixplPrefix,'')
        result.usageTerms[n].status = ut.UsageStatus.text().replace(onixplPrefix,'')
        //def licText = onixpl.LicenseDocumentText.TextElement.find{ it.@id==ut.LicenseTextLink.@href }
        def licTexts = []
        ut.LicenseTextLink.each{ ltl ->
          licTexts.add(onixpl.LicenseDocumentText.TextElement.find{ it.@id == ltl.@href })
        }

        // There can be multiple LicenseTexts linked to a single UsageTerm
        result.usageTerms[n].licenseTexts = []
        licTexts.eachWithIndex { lt, m ->
          result.usageTerms[n].licenseTexts[m] = [:]
          result.usageTerms[n].licenseTexts[m].text = [lt.TextPreceding.text(), lt.Text.text()].join(" ")
          result.usageTerms[n].licenseTexts[m].elId = lt.SortNumber.text()
        }
      }
      log.debug("Found "+result.usageTerms.size()+" usage terms")

      // Set to true if the file was read successfully
      result.processFile=true
    } catch (SAXException e) {
      result.processFile=false
      result.errors.add("ERROR: Could not parse file; is this a valid ONIX-PL file?");
    } catch (IOException e) {
      result.processFile=false
      result.errors.add("ERROR: There was an error processing the file; " +
          "please check the file you have supplied is a valid ONIX-PL file, try again.");
    }

    return result;
  }


  /**
   * Process the uploaded license import file, creating database
   * records for the license, an associated KB+ license record, an uploaded
   * document in the docstore, and UsageTerms.
   * @param upload
   * @return a stats object about the import
   */
  def processImport(upload) {

    // Create or find a license
    //def l = License.get(params.licid);
     def license = null;
     /*license = new License();
    if (!license.save(flush: true)) {
        license.errors.each {
            log.error("License error:" + it);
        }
    }
    log.debug("Created empty license "+license.id);*/

    def file = request.getFile("importFile");
    log.debug("Processing imported ONIX-PL document "+file);

    def upload_mime_type = file?.contentType
    def upload_filename = file?.getOriginalFilename()
    def input_stream = file?.inputStream

    // A stats struct holding summary info for display to the user
    def results = [:]
    results.messages=[]
    results.filename = upload_filename
    results.contentType = upload_mime_type

    if ( /*license &&*/ input_stream ) {

      // stats update
      results.termStatuses = [:]
      results.license = license

      // ------------------------------------------------------------------------
      // Upload the doc to docstore
      // ------------------------------------------------------------------------
      log.debug("Uploading ONIX-PL document to docstore: "+params.upload_title);
      def docstore_uuid = docstoreService.uploadStream(input_stream, upload_filename, params.upload_title)
      log.debug("Docstore uuid is ${docstore_uuid}");

      def d = new Doc(lastmod:new Date());
      d.save(flush: true);
      log.debug("Created new doc "+d.id);

      // ------------------------------------------------------------------------
      // Create doc records
      // ------------------------------------------------------------------------
      if ( docstore_uuid ) {
        //log.debug("Docstore uuid present (${docstore_uuid}) Saving info");
        def doctype = RefdataCategory.lookupOrCreate(CAT_DOCTYPE, DOCTYPE);
        log.debug('Saving doctype '+doctype);
        def doc_content = new Doc(
            contentType:1,
            uuid: docstore_uuid,
            filename: upload_filename,
            mimeType: upload_mime_type,
            title: params.upload_title,
            type:doctype,
            user: upload.user)
            .save()

        def doc_context = new DocContext(
            license:license,
            owner:doc_content,
            doctype:doctype).save(flush:true);

        def opl = recordOnixplLicense(license, doc_content);
        //results.messages.add("ONIX-PL License with id "+opl.id)

        if (upload.usageTerms) {
          def ts = results.termStatuses
          upload.usageTerms.each { ut ->
            recordOnixplUsageTerm(opl, ut);
            def n = ts.get(ut.status)!=null ? ts.get(ut.status)+1 : 0;
            //log.debug("term statuses "+ut.status+" = "+ts.get(ut.status)+" to "+n)
            ts.put(ut.status, n)
          }
        }

      } else {

      }
    }
    return results;
  }


  /**
   * Record a new ONIX-PL License in the database, linked to the given KB+
   * license and uploaded Doc.
   *
   * @param license a KB+ License
   * @param doc an uploaded Doc
   * @return an OnixplLicense, or null
   */
  def recordOnixplLicense(license, doc) {
    def opl = null;
      log.error("License2: " + license.id);
    try {
      opl = new OnixplLicense(
          lastmod:new Date(),
          license: license,
          doc: doc
      ).save(flush:true, failOnError: true);
      log.debug("Created ONIX-PL License "+opl.id);
    } catch (Exception e) {
      log.debug("Exception saving ONIX-PL License");
      e.printStackTrace();
    }
    return opl;
  }


  /**
   * Record an ONIX-PL UsageTerm in the database, linked to the given ONIX-PL
   * license.
   *
   * @param opl the OnixplLicense to attach the term to
   * @param usageTerm a struct representing the UsageTerm
   */
  def recordOnixplUsageTerm(opl, usageTerm) {
    // Retrieve the type and status
    def rdvType = RefdataCategory.lookupOrCreate(CAT_TYPE, usageTerm.type);
    def rdvStatus = RefdataCategory.lookupOrCreate(CAT_STATUS, usageTerm.status);
    //log.debug("Recording usage term $rdvType : $rdvStatus")
    // Create the term
    def term = new OnixplUsageTerm(
        oplLicense:opl,
        usageType:rdvType,
        usageStatus:rdvStatus
    );
    term.save(flush: true);
    //log.debug("Term "+term.id);

    // License Text
    usageTerm.licenseTexts.each { lt ->
      def oplt = new OnixplLicenseText(
          text:lt.text,
          elementId:lt.elId,
          oplLicense:opl
      );
      oplt.save(validate:false, flush: true, insert:true);
      // Create the association object:
      def ass = new OnixplUsageTermLicenseText(
          usageTerm: term,
          licenseText: oplt
      )
      ass.save(flush: true);
      //log.debug("LicenseText "+oplt.id);
        def oputlt = new OnixplUsageTermLicenseText(
                usageTerm: term,
                licenseText: oplt
        );
        oputlt.save(validate: false, flush: true, insert: true);
    }
  }



  // -------------------------------------------------------------------------
  // Validation methods
  // -------------------------------------------------------------------------

  // Copied from UploadController
  def checkCharset(file_input_stream) {

    def result = null;

    byte[] buf = new byte[4096];

    // (1)
    UniversalDetector detector = new UniversalDetector(null);

    // (2)
    int nread;
    while ((nread = file_input_stream.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    // (3)
    detector.dataEnd();

    // (4)
    String encoding = detector.getDetectedCharset();
    if (encoding != null) {
      result = encoding;
      System.out.println("Detected encoding = " + encoding);
      if ( encoding.equals('WINDOWS-1252') ) {
      }
    } else {
      System.out.println("No encoding detected.");
    }

    // (5)
    detector.reset();

    result
  }


}
