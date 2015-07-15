package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import grails.converters.*
import org.springframework.transaction.TransactionStatus
import com.k_int.custprops.PropertyDefinition

class PendingChangeService {

def genericOIDService
def grailsApplication

def performAccept(change,httpRequest) {
    def result = true
    PendingChange.withNewTransaction { TransactionStatus status ->
      change = PendingChange.get(change)

      try {
        def parsed_change_info = JSON.parse(change.changeDoc)
        log.debug("Process change ${parsed_change_info}");
        switch ( parsed_change_info.changeType ) {
          case 'TIPPDeleted' :
            // "changeType":"TIPPDeleted","tippId":"com.k_int.kbplus.TitleInstancePackagePlatform:6482"}
            def sub_to_change = change.subscription
            def tipp = genericOIDService.resolveOID(parsed_change_info.tippId)
            def ie_to_update = IssueEntitlement.findBySubscriptionAndTipp(sub_to_change,tipp)
            if ( ie_to_update != null ) {
              ie_to_update.status = RefdataCategory.lookupOrCreate('Entitlement Issue Status','Deleted');
              ie_to_update.save();
            }
            break;
          case 'PropertyChange' :  // Generic property change
            if ( ( parsed_change_info.changeTarget != null ) && ( parsed_change_info.changeTarget.length() > 0 ) ) {
              def target_object = genericOIDService.resolveOID(parsed_change_info.changeTarget);
              target_object.refresh()
              if ( target_object ) {
                // Work out if parsed_change_info.changeDoc.prop is an association - If so we will need to resolve the OID in the value
                def domain_class = grailsApplication.getArtefact('Domain',target_object.class.name);
                def prop_info = domain_class.getPersistentProperty(parsed_change_info.changeDoc.prop)
                if(prop_info == null){
                  log.debug("We are dealing with custom properties: ${parsed_change_info}")
                  processCustomPropertyChange(parsed_change_info)
                }
                else if ( prop_info.isAssociation() ) {
                  log.debug("Setting association for ${parsed_change_info.changeDoc.prop} to ${parsed_change_info.changeDoc.new}");
                  target_object[parsed_change_info.changeDoc.prop] = genericOIDService.resolveOID(parsed_change_info.changeDoc.new)
                }
                else if ( prop_info.getType() == java.util.Date ) {
                  log.debug("Date processing.... parse \"${parsed_change_info.changeDoc.new}\"");
                  if ( ( parsed_change_info.changeDoc.new != null ) && ( parsed_change_info.changeDoc.new.toString() != 'null' ) ) {
                    //if ( ( parsed_change_info.changeDoc.new != null ) && ( parsed_change_info.changeDoc.new != 'null' ) ) {
                    def df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // yyyy-MM-dd'T'HH:mm:ss.SSSZ 2013-08-31T23:00:00Z
                    def d = df.parse(parsed_change_info.changeDoc.new)
                    target_object[parsed_change_info.changeDoc.prop] = d
                  }
                  else {
                    target_object[parsed_change_info.changeDoc.prop] = null
                  }
                }
                else {
                  log.debug("Setting value for ${parsed_change_info.changeDoc.prop} to ${parsed_change_info.changeDoc.new}");
                  target_object[parsed_change_info.changeDoc.prop] = parsed_change_info.changeDoc.new
                }
                target_object.save()
                //FIXME: is this needed anywhere?
                def change_audit_object = null
                if ( change.license ) change_audit_object = change.license;
                if ( change.subscription ) change_audit_object = change.subscription;
                if ( change.pkg ) change_audit_object = change.pkg;
                def change_audit_id = change_audit_object.id
                def change_audit_class_name = change_audit_object.class.name
              }
            }
            break;
          case 'TIPPEdit':
            // A tipp was edited, the user wants their change applied to the IE
            break;
          case 'New Object' :
             def new_domain_class = grailsApplication.getArtefact('Domain',parsed_change_info.newObjectClass);
             if ( new_domain_class != null ) {
               // def new_instance = new_domain_class.getClazz().newInstance(parsed_change_info.changeDoc).save()
               def new_instance = new_domain_class.getClazz().newInstance()
               bindData(new_instance, parsed_change_info.changeDoc)
               new_instance.save();
             }
            break;
          case 'Update Object' :
            if ( ( parsed_change_info.changeTarget != null ) && ( parsed_change_info.changeTarget.length() > 0 ) ) {
              def target_object = genericOIDService.resolveOID(parsed_change_info.changeTarget);
              if ( target_object ) {
                bindData(target_object, parsed_change_info.changeDoc)
                target_object.save();
              }
            }
            break;
          default:
            log.error("Unhandled change type : ${pc.changeDoc}");
            break;
        }
        change.pkg?.pendingChanges?.remove(change)
        change.pkg?.save();
        change.license?.pendingChanges?.remove(change)
        change.license?.save();
        change.subscription?.pendingChanges?.remove(change)
        change.subscription?.save();
        change.status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Accepted")
        change.actionDate = new Date()
        change.user = httpRequest?.user
        change.save();
        log.debug("Pending change accepted and saved")
      }
      catch ( Exception e ) {
        log.error("Problem accepting change",e);
        result = false;
      }

      return result
    }
  }

  def performReject(change,httpRequest) {
    PendingChange.withNewTransaction { TransactionStatus status ->
      change = PendingChange.get(change)
      change.license?.pendingChanges?.remove(change)
      change.license?.save();
      change.subscription?.pendingChanges?.remove(change)
      change.subscription?.save();
      change.actionDate = new Date()
      change.user = httpRequest.user
      change.status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Rejected")

      def change_audit_object = null
      if ( change.license ) change_audit_object = change.license;
      if ( change.subscription ) change_audit_object = change.subscription;
      if ( change.pkg ) change_audit_object = change.pkg;
      def change_audit_id = change_audit_object.id
      def change_audit_class_name = change_audit_object.class.name
    }
  }

  def processCustomPropertyChange(parsed_change_info){
    def changeDoc = parsed_change_info.changeDoc
     if ( ( parsed_change_info.changeTarget != null ) && ( parsed_change_info.changeTarget.length() > 0 ) ) {
      def target_object = genericOIDService.resolveOID(parsed_change_info.changeTarget);
      if ( target_object ) {
        if(!target_object.hasProperty('customProperties')){
            log.error("Custom property change, but owner doesnt have the custom props: ${parsed_change_info}")
            return
        }      
        def updateProp = target_object.customProperties.find{it.type.name == changeDoc.name}
        if(updateProp){
          switch (changeDoc.event){
            case "CustomProperty.deleted":
              log.debug("Deleting property ${updateProp.type.name} from ${parsed_change_info.changeTarget}")
              target_object.customProperties.remove(updateProp)
              updateProp.delete()
              break;
            case "CustomProperty.updated":
              log.debug("Update custom property ${updateProp.type.name}")
              if(changeDoc.type == RefdataValue.toString()){
                  def propertyDefinition = PropertyDefinition.findByName(changeDoc.name)
                  def newProp =  RefdataCategory.lookupOrCreate(propertyDefinition.refdataCategory,changeDoc.new)
                  updateProp."${changeDoc.prop}" = newProp                
              }else{
                updateProp."${changeDoc.prop}" = 
                  updateProp.parseValue("${changeDoc.new}", changeDoc.type)
              }
              log.debug("Setting value for ${changeDoc.name}.${changeDoc.prop} to ${changeDoc.new}")
              updateProp.save()          
              break;
            default:
              log.error("ChangeDoc event '${changeDoc.event}'' not recognized.")          
          }
        }else{
          if(changeDoc.propertyOID){
            def propertyType = genericOIDService.resolveOID(changeDoc.propertyOID).type
            def newProperty = PropertyDefinition.createPropertyValue(target_object,propertyType)

            if(changeDoc.type == RefdataValue.toString()){
              def originalRefdata = genericOIDService.resolveOID(changeDoc.propertyOID).refValue;
              log.debug("RefdataCategory ${propertyType.refdataCategory}")
              def copyRefdata = RefdataCategory.lookupOrCreate(propertyType.refdataCategory,changeDoc.new)
              newProperty."${changeDoc.prop}" = copyRefdata
            }else{
              newProperty."${changeDoc.prop}" = newProperty.parseValue("${changeDoc.new}", changeDoc.type)
            }
            newProperty.save()
            log.debug("New CustomProperty ${newProperty.type.name} created for ${target_object}")
            
          }else{
            log.error("Custom property change, but changedoc is missing propertyOID: ${parsed_change_info}")
          }
        }
      }
    }
  }

}