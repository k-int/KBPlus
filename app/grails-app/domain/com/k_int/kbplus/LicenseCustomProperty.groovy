package com.k_int.kbplus

import com.k_int.custprops.PropertyDefinition
import com.k_int.kbplus.abstract_domain.CustomProperty
import javax.persistence.Transient

class LicenseCustomProperty extends CustomProperty {
	@Transient
    def grailsApplication

	static auditable = true
    static belongsTo = [
            type : PropertyDefinition,
            owner: License
    ]

 	@Transient
  	def onChange = { oldMap,newMap ->
    	log.debug("onChange LicenseCustomProperty")
	    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")
        controlledProperties.each{ cp->
            if ( oldMap[cp] != newMap[cp] ) {
            changeNotificationService.notifyChangeEvent([
                             OID:"${this.class.name}:${this.id}",
                             event:'LicenseCustomProperty.updated',
                             prop:cp,
                             name: type.name,
                             type:this."${cp}".getClass().toString(),  
                             old:oldMap[cp] instanceof RefdataValue? oldMap[cp].toString() : oldMap[cp] ,
                             new:newMap[cp] instanceof RefdataValue? newMap[cp].toString() : newMap[cp]
                            ])
            }
        }
	}


    @Transient
  	def notifyDependencies(changeDocument) {
	    log.debug("notifyDependencies(${changeDocument})");

	    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")
	    changeNotificationService.broadcastEvent("com.k_int.kbplus.License:${owner.id}", changeDocument);
        def derived_licenses = License.executeQuery('select l from License as l where exists ( select link from Link as link where link.toLic=l and link.fromLic=? )',owner)
        derived_licenses.each { dl ->
          log.debug("Send pending change to ${dl.id}");
          def locale = org.springframework.context.i18n.LocaleContextHolder.getLocale()
          ContentItem contentItemDesc = ContentItem.findByKeyAndLocale("kbplus.change.license."+changeDocument.prop,locale.toString())
          def description = "Accept this change to make the same update to your license"
          if(contentItemDesc){
              description = contentItemDesc.content
          }else{
              def defaultMsg = ContentItem.findByKeyAndLocale("kbplus.change.license.default",locale.toString());
              if( defaultMsg)
                  description = defaultMsg.content
          }
          changeNotificationService
          .registerPendingChange('license',
                                dl,
                                "<b>${changeDocument.name}</b> changed from <b>\"${changeDocument.oldLabel?:changeDocument.old}\"</b> to <b>\"${changeDocument.newLabel?:changeDocument.new}\"</b> on the template license."+description,
                                dl.getLicensee(),
                                [
                                  changeTarget:"com.k_int.kbplus.License:${dl.id}",
                                  changeType:'CustomPropertyChange',
                                  changeDoc:changeDocument
                                ])

        }
    }
}