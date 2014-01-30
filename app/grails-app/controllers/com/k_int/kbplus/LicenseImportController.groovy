package com.k_int.kbplus

import com.k_int.kbplus.auth.User
import grails.plugins.springsecurity.Secured
import org.mozilla.universalchardet.UniversalDetector
import org.xml.sax.SAXException

class LicenseImportController {

  def CAT_TYPE = "UsageType",
      CAT_STATUS = "UsageStatus",
      CAT_DOCTYPE = 'Document Type',
      DOCTYPE = 'ONIX-PL License',
      DEFAULT_DOC_TITLE = 'ONIX-PL Licence document',
      CAT_USER = "User",
      CAT_USEDRESOURCE = "UsedResource"

  def MAX_FILE_SIZE_MB = 10
  def MAX_FILE_SIZE    = MAX_FILE_SIZE_MB * 1024 * 1024 // 10Mb
  // These values are used in the view
  def CMD_REPLACE_OPL  = "replace"
  def CMD_CREATE_OPL   = "create"

  def springSecurityService
  def onixplPrefix = 'onixPL:'

  /**
   * Main request-handling method.
   * Review the offered import to make sure it is a valid ONIX-PL file.
   * @return
   */
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def doImport() {
    // Setup result object
    def result = [:]
    result.validationResult = [:]
    result.validationResult.messages = []
    result.validationResult.errors = []

    // Identify user
    if (!"anonymousUser".equals(springSecurityService.principal)) {
      result.user = User.get(springSecurityService.principal.id);
    }

    // Find a license if id specified
    if (params.license_id) {
      log.debug("Import requested with license id ${params.license_id}")
      result.license_id = params.license_id
      result.license    = License.findById(params.license_id)
    }

    // Now process if a form was posted
    if ( request.method == 'POST' ) {
      // Record form submission values
      result.import_file   = params.import_file?.getOriginalFilename()
      // hidden values
      result.upload_title  = DEFAULT_DOC_TITLE
      result.uploaded_file = params.uploaded_file
      result.upload_mime_type = params.upload_mime_type
      result.upload_filename = params.upload_filename
      result.existing_opl_id = params.existing_opl_id
      result.existing_opl = OnixplLicense.findById(result.existing_opl_id)

      // A file offered for upload
      def offered_multipart_file = request.getFile("import_file")

      // If a replace_opl result is specified, record it
      if (params.replace_opl) {
        //log.debug("replace_opl ${params.replace_opl}")
        result.replace_opl  = params.replace_opl
      }
      // Otherwise check upload file
      else if (!result.existing_opl) {
        // Check user-specified params
        if (!result.import_file) {
          result.validationResult.errors.add("Please specify a file to upload.")
          return result
        }
        if (request.getFile("import_file").size > MAX_FILE_SIZE) {
          result.validationResult.errors.add(
              "The file is too large - max size is ${MAX_FILE_SIZE_MB} Mb.")
          return result
        }
      }

      // Process the upload file appropriately
      try {
        // Read file if one is being offered for upload, and check if it matches
        // an existing OPL
        if (offered_multipart_file) {
          log.debug("Request to upload ONIX-PL file type: ${offered_multipart_file.contentType}" + " filename ${offered_multipart_file.originalFilename}");

          // 1. Read the file and parse it as XML - Extract properties and set them on the onix_parse_result map
          def onix_parse_result = readOnixMultipartFile(offered_multipart_file)
          if (onix_parse_result.errors) {
            result.validationResult.errors.addAll(onix_parse_result.errors)
            return result
          }
          result.offered_file = offered_multipart_file
          result.accepted_file = onix_parse_result
          result.putAll(onix_parse_result)

          result.validationResult.messages.add("Document validated: ${offered_multipart_file.originalFilename}")
          log.debug("Passed first phase validation")

          // If the specified license does not already have an OPL associated,
          // check for existing OPLs that appear to match.
          // If the license does have an OPL, it is replaced by default.
          if (!result.license?.onixplLicense) {
            // Save the upload to a temp file if the license matches an existing one
            def existingOpl = checkForExistingOpl(result.accepted_file, result.license)
            if (existingOpl) {
              log.debug("Found existing opl "+existingOpl)
              // Create a temp file to hold the uploaded file, which will be
              // automatically deleted when JVM exits
              File tmp = File.createTempFile("opl_upload", ".xml")
              tmp.deleteOnExit()
              offered_multipart_file.transferTo(tmp)
              result.uploaded_file = tmp
              result.existing_opl = existingOpl
              return result
            }
          }
        }

        // Read the ONIX-PL file stream (from temp or upload, whichever is specified in the result object
        result.usageTerms = parseOnixPl(result)
        // Process the import and combine results
        result.validationResult.putAll(processImport(result))

      } catch (SAXException e) {
        result.validationResult.errors.add(
            "Could not parse file; is this a valid ONIX-PL file?");
      } catch (IOException e) {
        result.validationResult.errors.add(
            "There was an error processing the file; please check the " +
                "file you have supplied is a valid ONIX-PL file, try again.");
      }
    }

    // Redirect to some ONIX-PL display page
    //log.debug("Redirecting...");
    //redirect controller: 'licenseDetails', action:'onixpl', id:params.licid, fragment:params.fragment
    result
  }


  /**
   * Verify an uploaded file and extract metadata.
   *
   * @param file a MultipartFile upload
   * @return a onix_parse_result object with extracted metadata
   * @throws SAXException
   * @throws IOException
   */
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def readOnixMultipartFile(file) throws SAXException, IOException {
    log.debug("Reading uploaded ONIX-PL file ${file?.originalFilename} " +
        "of type ${file?.contentType}")
    def onix_parse_result = [:]
    // Verify the character set of the offered ONIX-PL file, and read the title
    // if the file is okay.
    def charset = UploadController.checkCharset(file?.inputStream)
    if  ( ( charset != null ) && ( ! charset.equals('UTF-8') ) ) {
      onix_parse_result.errors = []
      onix_parse_result.errors.add("Detected input character stream encoding: ${charset}. Expected UTF-8.")
      return onix_parse_result
    } else {
      // Extract the description,
      onix_parse_result.description = new XmlSlurper().parse(file.inputStream).LicenseDetail.Description.text()
    }
    // Record mime type, filename
    onix_parse_result.upload_mime_type = file?.contentType
    onix_parse_result.upload_filename = file?.originalFilename
    onix_parse_result.size = file?.size
    onix_parse_result
  }

  /**
   * Check whether an OPL exists with the same title.
   * --Check whether the license already points to an OPL or if one exists with
   * the same title.--
   * @param result
   * @return
   */
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def checkForExistingOpl(acceptedFile, license) {
    log.debug("Checking for existing OPL for acceptedFile ${acceptedFile} and license ${license}")
    //def existingOpl = license?.onixplLicense
    def existingOpl
    if (!existingOpl) {
      def fileDesc = acceptedFile?.description
      log.debug("Finding OPL by title '${fileDesc}'")
      existingOpl = OnixplLicense.findByTitle(fileDesc)
      // TODO Offer a selection? Do we really want multiple OPLs with same title?
      //existingOpls = OnixplLicense.findAllByTitle(fileDesc)
    }
    existingOpl
  }

  /**
   * Parse the ONIX-PL XML from an input stream, and return a set of
   * UsageTermStatuses.
   * @param result result object
   * @return result object
   * @throws SAXException
   * @throws IOException
   */
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def parseOnixPl(result)  throws SAXException, IOException {
    //log.debug("parseOnixPl(result) "); result.each{k,v-> log.debug("  ${k} -> ${v}")}
    log.debug("Parsing ONIX-PL")
    
    def onix_file_input_stream = result.uploaded_file ? new FileInputStream(result.uploaded_file) : result.offered_file?.inputStream
    def onixpl = new XmlSlurper().parse(onix_file_input_stream);

    result.description = onixpl.LicenseDetail.Description.text()
    def usageTerms = []
    onixpl.UsageTerms.Usage.eachWithIndex { ut, n ->
      usageTerms[n] = [:]
      usageTerms[n].type = ut.UsageType.text().replace(onixplPrefix,'')
      usageTerms[n].status = ut.UsageStatus.text().replace(onixplPrefix,'')
      usageTerms[n].user = []
      ut.User.eachWithIndex { user, m ->
          usageTerms[n].user[m] = user.text().replace(onixplPrefix, '')
      }
      usageTerms[n].usedResource = []
      ut.UsedResource.eachWithIndex { ur, m ->
          usageTerms[n].usedResource[m] = ur.text().replace(onixplPrefix, '')
      }
      def licTexts = []
      ut.LicenseTextLink.each{ ltl ->
        licTexts.add(onixpl.LicenseDocumentText.TextElement.find{ it.@id == ltl.@href })
      }

      // There can be multiple LicenseTexts linked to a single UsageTerm
      usageTerms[n].licenseTexts = []
      licTexts.eachWithIndex { lt, m ->
        usageTerms[n].licenseTexts[m] = [:]
        usageTerms[n].licenseTexts[m].text = [lt.TextPreceding.text(), lt.Text.text()].join(" ")
        usageTerms[n].licenseTexts[m].elId = lt.@id.text() //lt.SortNumber.text()
        usageTerms[n].licenseTexts[m].displayNum = lt.DisplayNumber?.text()
        if (!usageTerms[n].licenseTexts[m].displayNum) {
            usageTerms[n].licenseTexts[m].displayNum = lt.SortNumber?.text()
        }
      }
    }
    log.debug("Found "+usageTerms.size()+" usage terms")

    usageTerms
  }


  /**
   * Process the uploaded license import file, creating database
   * records for the license, an associated KB+ license record, an uploaded
   * document in the docstore, and UsageTerms.
   * @param upload
   * @return a stats object about the import
   */
  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def processImport(upload) {
    //log.debug("processImport(upload) "); upload.each{k,v-> log.debug("  ${k} -> ${v}")}
    //log.debug("Processing imported ONIX-PL document");
    // A stats struct holding summary info for display to the user
    def importResult = [:],
        // Whether to replace an existing OPL
        replaceOplRecord = upload.replace_opl==CMD_REPLACE_OPL,
        // Create a new doc record for the upload only if we are not replacing an existing OPL
        createNewDocument = !replaceOplRecord,
        // Create a new license if none is specified, and we are not updating
        // an OPL (in which case we assume the existing OPL is already linked
        // to licenses where necessary)
        createNewLicense = !upload.license && !replaceOplRecord,
        // Use specified license if there is one
        license = upload.license
    log.debug("replaceOplRecord: ${replaceOplRecord} createNewDocument: ${createNewDocument} createNewLicense: ${createNewLicense} upload.replace_opl: ${upload.replace_opl} license: ${upload.license!=null}")
    importResult.replace = replaceOplRecord
    RefdataValue currentStatus = RefdataCategory.lookupOrCreate('License Status', 'Current')
    RefdataValue templateType  = RefdataCategory.lookupOrCreate('License Type', 'Template')
    // Create a new license
    if (createNewLicense) {
      license = new License(
          reference     : upload.description,
          licenseStatus : currentStatus.value,
          licenseType   : templateType.value,
          status        : currentStatus,
          type          : templateType,
          lastmod       : new Date().time
      )
      // Save it
      if (!license.save(flush: true)) {
        license.errors.each {
          log.error("License error:" + it);
        }
      }
      //log.debug("Created template KB+ license for '${upload.description}': ${license}")
      log.debug("Created template KB+ license ${license}")
    }

    def onix_file_input_stream = upload.uploaded_file ? new FileInputStream(upload.uploaded_file) : upload.offered_file?.inputStream
    def onix_file_size = upload.uploaded_file ? new File(upload.uploaded_file).size() : upload.offered_file.size

    def doctype = RefdataCategory.lookupOrCreate(CAT_DOCTYPE, DOCTYPE);
    def doc_content, doc_context

    // If we are creating a new document for the upload
    if (createNewDocument) doc_content = new Doc(contentType: 3)

    // Otherwise update the existing doc's description
    else doc_content = upload.existing_opl.doc

    // Update doc properties
    doc_content.uuid     = java.util.UUID.randomUUID().toString()
    doc_content.filename = filename
    doc_content.mimeType = upload.upload_mime_type
    doc_content.title    = upload.upload_title
    doc_content.type     = doctype
    doc_content.user     = upload.user
    doc_content.setBlobData(onix_file_input_stream, onix_file_size)
    doc_content.save(flush:true)

    log.debug("${createNewDocument?'Created new':'Updated'} document ${doc_content}")
    // Record a doc context if there is a new document or a new license.
    // We don't want duplicate doc_contexts.
    if (createNewDocument || createNewLicense) {
      doc_context = new DocContext(
        license: license,
          owner:   doc_content,
          doctype: doctype
      ).save(flush:true)
      log.debug("Created new document context ${doc_context}")
    }

    // Create an OnixplLicense and update the KB+ License
    def opl
    if (replaceOplRecord) {
      opl = upload.existing_opl
      opl.lastmod = new Date()
      opl.doc = doc_content
      opl.title = upload.description
      // Delete existing usage terms
      //opl.usageTerm.each { ut -> ut.delete() }
      opl.usageTerm.clear()
      opl.save()
    } else {
      opl = recordOnixplLicense(doc_content, upload.description)
    }
    log.debug("${replaceOplRecord?'Updated':'Created new'} ONIX-PL License ${opl}")
    // If a single license is specified, link it to the OPL
    if (license) {
      license.onixplLicense = opl
      license.save(flush:true)
      log.debug("Linked OPL ${opl.id} to LIC ${license.id}")
    }
    importResult.license = license
    importResult.onixpl_license = opl

    // Record the usage terms
    importResult.termStatuses = [:]
    if (upload.usageTerms) {
      def ts = importResult.termStatuses
      upload.usageTerms.each { ut ->
        recordOnixplUsageTerm(opl, ut);
        def n = ts.get(ut.status)!=null ? ts.get(ut.status)+1 : 1;
        //log.debug("term statuses "+ut.status+" = "+ts.get(ut.status)+" to "+n)
        ts.put(ut.status, n)
      }
    }
    // Set success flag
    importResult.success = true

    importResult
  }



  // -------------------------------------------------------------------------
  // Domain object creation methods
  // -------------------------------------------------------------------------

  /**
   * Record a new ONIX-PL License in the database, linked to the given KB+
   * license and uploaded Doc.
   *
   * @param doc an uploaded Doc
   * @return an OnixplLicense, or null
   */
  def recordOnixplLicense(doc, title) {
    def opl = null;
    try {
      opl = new OnixplLicense(
          lastmod:new Date(),
          doc: doc,
          title: title
      );
      opl.save(flush:true, failOnError: true);
      //log.debug("Created ONIX-PL License ${opl}");
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
    // Get the 'users' and 'used resources' for the usage term
    def rdvUser = [];
    usageTerm.user.each { user ->
      rdvUser.add(RefdataCategory.lookupOrCreate(CAT_USER, user));
    }
    def rdvUsedResource = [];
    usageTerm.usedResource.each { ur ->
        rdvUsedResource.add(RefdataCategory.lookupOrCreate(CAT_USEDRESOURCE, ur));
    }
    log.error("Usage term user: ${usageTerm.user}, Used resource: ${usageTerm.usedResource}");
    //log.debug("Recording usage term $rdvType : $rdvStatus")
    // Create the term
    def term = new OnixplUsageTerm(
        oplLicense:opl,
        usageType:rdvType,
        usageStatus:rdvStatus,
        user:rdvUser,
        usedResource:rdvUsedResource
    );
    term.save(flush: true);
    //log.debug("Term "+term.id);

    // License Text
    usageTerm.licenseTexts.each { lt ->
      def oplt = new OnixplLicenseText(
          text:lt.text,
          elementId:lt.elId,
          displayNum:lt.displayNum,
          oplLicense:opl
      );
      oplt.save(flush: true, insert:true);
      // Create the association object:
      def ass = new OnixplUsageTermLicenseText(
          usageTerm: term,
          licenseText: oplt
      )
      ass.save(flush: true, insert: true);
    }
  }

}

