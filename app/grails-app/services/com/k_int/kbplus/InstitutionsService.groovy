package com.k_int.kbplus

class InstitutionsService {

    def copyLicence(params){
        def baseLicense = params.baselicense ? License.get(params.baselicense) : null;
        def org = Org.findByShortcode(params.shortcode)

        def license_type = RefdataCategory.lookupOrCreate('License Type', 'Actual')
        def license_status = RefdataCategory.lookupOrCreate('License Status', 'Current')
        def lic_name = params.lic_name?:"Copy of ${baseLicense?.reference}"
        def licenseInstance = new License(reference: lic_name,
                status: license_status,
                type: license_type,
                noticePeriod: baseLicense?.noticePeriod,
                licenseUrl: baseLicense?.licenseUrl,
                onixplLicense: baseLicense?.onixplLicense,
                startDate:baseLicense?.startDate,
                endDate:baseLicense?.endDate
        )
        if(params.copyStartEnd){
            licenseInstance.startDate = baseLicense?.startDate
            licenseInstance.endDate = baseLicense?.endDate
        }
        for(prop in baseLicense?.customProperties){
            def copiedProp = new LicenseCustomProperty(type:prop.type,owner:licenseInstance)
            copiedProp = prop.copyValueAndNote(copiedProp)
            licenseInstance.addToCustomProperties(copiedProp)
        }
        // the url will set the shortcode of the organisation that this license should be linked with.
        if (!licenseInstance.save(flush: true)) {
            log.error("Problem saving license ${licenseInstance.errors}");
            return licenseInstance
        } else {
            log.debug("Save ok");
            def licensee_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensee')
            log.debug("adding org link to new license");
            org.links.add(new OrgRole(lic: licenseInstance, org: org, roleType: licensee_role));
            if (baseLicense?.licensor) {
                def licensor_role = RefdataCategory.lookupOrCreate('Organisational Role', 'Licensor')
                org.links.add(new OrgRole(lic: licenseInstance, org: baseLicense.licensor, roleType: licensor_role));
            }

            if (org.save(flush: true)) {
            } else {
                log.error("Problem saving org links to license ${org.errors}");
            }

            // Clone documents
            baseLicense?.documents?.each { dctx ->
                Doc clonedContents = new Doc(blobContent: dctx.owner.blobContent,
                        status: dctx.owner.status,
                        type: dctx.owner.type,
                        alert: dctx.owner.alert,
                        content: dctx.owner.content,
                        uuid: dctx.owner.uuid,
                        contentType: dctx.owner.contentType,
                        title: dctx.owner.title,
                        creator: dctx.owner.creator,
                        filename: dctx.owner.filename,
                        mimeType: dctx.owner.mimeType,
                        user: dctx.owner.user,
                        migrated: dctx.owner.migrated).save()

                DocContext ndc = new DocContext(owner: clonedContents,
                        license: licenseInstance,
                        domain: dctx.domain,
                        status: dctx.status,
                        doctype: dctx.doctype).save()
            }

            // Finally, create a link
            def new_link = new Link(fromLic: baseLicense, toLic: licenseInstance,slaved: params.isSlaved).save()
        }
        return licenseInstance
    }

    /** 
     * Rules [insert, delete, update, noChange] 
    **/
    def generateComparisonMap(unionList, mapA, mapB, offset, toIndex, rules){
      def result = new TreeMap()
      def insert = rules[0]
      def delete = rules[1]
      def update = rules[2]
      def noChange = rules[3]

      for (unionTitle in unionList){
       
        def objA = mapA.get(unionTitle)
        def objB = mapB.get(unionTitle)     

        def comparison = objA?.compare(objB);
        def value = null;

        if(delete && comparison == -1 ) value = [objA,null, "danger"];
        else if(update && comparison == 1) value = [objA,objB, "warning"];
        else if(insert && comparison == null) value = [null, objB, "success"];
        else if (noChange && comparison == 0) value = [objA,objB, ""];

        if(value != null) result.put(unionTitle, value);

        if (result.size() == toIndex ) {
            break;
        }
      }

      if(result.size() <= offset ){        
        result.clear()
      }else if(result.size() > (toIndex - offset) ){ 
        def keys = result.keySet().toArray();
        result = result.tailMap(keys[offset],true)
      }
      result
    }        	
}
