package org.codehaus.groovy.grails.plugins.orm.auditable

import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogListener

class CustomAuditLogListener extends AuditLogListener{




  /**
   * Now we get fancy. Here we want to log changes...
   * specifically we want to know what property changed,
   * when it changed. And what the differences were.
   *
   * This works better from the onPreUpdate event handler
   * but for some reason it won't execute right for me.
   * Instead I'm doing a rather complex mapping to build
   * a pair of state HashMaps that represent the old and
   * new states of the object.
   *
   * The old and new states are passed to the object's
   * 'onChange' event handler. So that a user can work
   * with both sets of values.
   *
   * Needs complex type testing BTW.
   */
  @Override
  protected void onPreUpdate(event) {
    def domain = event.entityObject
    try {
      print "+++++++++++++++++++++++++++++++++++++++++onPreUpdate: ${domain}"
       log.error "+++++++++++++++++++++++++++++++++++++++++onPreUpdate: ${domain}"
      def entity = getDomainClass(domain)
      print "+++++++++++++++++++++++++++++++++++++++++onPreUpdate-entity: ${entity}"
       log.error "+++++++++++++++++++++++++++++++++++++++++onPreUpdate-entity: ${entity}"

      // Get all the dirty properties
      Set<String> dirtyProperties = getDirtyPropertyNames(domain, entity)
      if (dirtyProperties) {
        // Get the prior values for everything that is dirty
        Map oldMap = dirtyProperties.collectEntries { String property ->
          [property, getPersistentValue(domain, property, entity)]
        }

        // Get the current values for everything that is dirty
        Map newMap = makeMap(dirtyProperties, domain)

        if (!significantChange(domain, oldMap, newMap)) {
          return
        }

        // Allow user to override whether you do auditing for them
        if (!callHandlersOnly(domain)) {
          logChanges(domain, newMap, oldMap, getEntityId(domain), getEventName(event), getClassName(entity))
        }

        executeHandler(domain, 'onChange', oldMap, newMap)
      }
    }
    catch (e) {
      log.error "Audit plugin unable to process update event for ${domain.class.name}", e
    }
  }
  @Override
  private makeMap(Set<String> propertyNames, domain) {
    propertyNames.collectEntries { [it+"CUSTOM", domain."${it}"] }
  }
}