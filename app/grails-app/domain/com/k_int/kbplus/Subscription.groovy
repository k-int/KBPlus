package com.k_int.kbplus

import com.k_int.kbplus.auth.*;
import javax.persistence.Transient



class Subscription {

  static auditable = true

  @Transient
  def grailsApplication

  RefdataValue status
  RefdataValue type

  String name
  String identifier
  String impId
  Date startDate
  Date endDate
  Date manualRenewalDate
  Subscription instanceOf

  // If a subscription is slaved then any changes to instanceOf will automatically be applied to this subscription
  Boolean slaved

  String noticePeriod
  Date dateCreated
  Date lastUpdated
  // Org vendor

  License owner
  SortedSet issueEntitlements
  RefdataValue isPublic

  static transients = [ 'subscriber', 'provider', 'consortia' ]

  static hasMany = [ packages : SubscriptionPackage, 
                     issueEntitlements: IssueEntitlement,
                     documents:DocContext,
                     orgRelations: OrgRole,
                     derivedSubscriptions: Subscription,
                     pendingChanges:PendingChange,
                     customProperties:SubscriptionCustomProperty  ]

  static mappedBy = [ packages : 'subscription', 
                      issueEntitlements: 'subscription',
                      documents: 'subscription',
                      orgRelations: 'sub',
                      derivedSubscriptions: 'instanceOf',
                      pendingChanges:'subscription' ]

  static mapping = {
                  id column:'sub_id'
             version column:'sub_version'
              status column:'sub_status_rv_fk'
                type column:'sub_type_rv_fk'
               owner column:'sub_owner_license_fk'
                name column:'sub_name'
          identifier column:'sub_identifier'
               impId column:'sub_imp_id', index:'sub_imp_id_idx'
           startDate column:'sub_start_date'
             endDate column:'sub_end_date'
   manualRenewalDate column:'sub_manual_renewal_date'
          instanceOf column:'sub_parent_sub_fk'
              slaved column:'sub_is_slaved'
        noticePeriod column:'sub_notice_period'
            isPublic column:'sub_is_public'
  }

  static constraints = {
    status(nullable:true, blank:false)
    type(nullable:true, blank:false)
    owner(nullable:true, blank:false)
    impId(nullable:true, blank:false)
    startDate(nullable:true, blank:false)
    endDate(nullable:true, blank:false)
    manualRenewalDate(nullable:true, blank:false)
    instanceOf(nullable:true, blank:false)
    slaved(nullable:true, blank:false)
    noticePeriod(nullable:true, blank:true)
    isPublic(nullable:true, blank:true)
    customProperties(nullable:true)
    // vendor(nullable:true, blank:false)
  }

  def getSubscriber() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Subscriber' )
        result = or.org;
    }
    result
  }

  def getProvider() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Content Provider' )
        result = or.org;
    }
    result
  }

  def getConsortia() {
    def result = null;
    orgRelations.each { or ->
      if ( or?.roleType?.value=='Subscription Consortia' )
        result = or.org;
    }
    result
  }

  // determin if a user can edit this subscription
  def isEditableBy(user) {
    hasPerm("edit",user);
  }

  def hasPerm(perm, user) {
    def result = false

    if ( perm=='view' && this.isPublic?.value=='Yes' ) {
      result = true;
    }

    if (!result) {
      // If user is a member of admin role, they can do anything.
      def admin_role = Role.findByAuthority('ROLE_ADMIN');
      if ( admin_role ) {
        if ( user.getAuthorities().contains(admin_role) ) {
          result = true;
        }
      }
    }

    if ( !result ) {
      result = checkPermissions(perm,user);
    }

    result;
  }

  def checkPermissions(perm, user) {
    def result = false
    def principles = user.listPrincipalsGrantingPermission(perm);   // This will list all the orgs and people granted the given perm
    log.debug("The target list of principles : ${principles}");

    // Now we need to see if we can find a path from this object to any of those resources... Any of these orgs can edit

    // If this is a concrete license, the owner is the 
    // If it's a template, the owner is the consortia that negoited
    // def owning org list
    // We're looking for all org links that grant a role with the corresponding edit property.
    Set object_orgs = new HashSet();
    orgRelations.each { ol ->
      def perm_exists=false
      ol.roleType?.sharedPermissions.each { sp ->
        if ( sp.perm.code==perm )
          perm_exists=true;
      }
      if ( perm_exists ) {
        log.debug("Looks like org ${ol.org} has perm ${perm} shared with it.. so add to list")
        object_orgs.add("${ol.org.id}:${perm}")
      }
    }

    log.debug("After analysis, the following relevant org_permissions were located ${object_orgs}, user has the following orgs for that perm ${principles}")

    // Now find the intersection
    def intersection = principles.retainAll(object_orgs)

    log.debug("intersection is ${principles}")

    if ( principles.size() > 0 )
      result = true

    result
  }

  def onChange = { oldMap,newMap ->

    log.debug("onChange")

    def changeNotificationService = grailsApplication.mainContext.getBean("changeNotificationService")
    def controlledProperties = ['name','startDate','endDate']

    controlledProperties.each { cp ->
      if ( oldMap[cp] != newMap[cp] ) {
        //changeNotificationService.notifySubscriptionChange(this.id, cp, oldMap[cp], newMap[cp], null, 'S');
        changeNotificationService.notifyChangeEvent([
                                                     OID:"${this.class.name}:${this.id}",
                                                     event:'Subscription.updated',
                                                     prop:cp,
                                                     old:oldMap[cp],
                                                     new:newMap[cp]
                                                    ])
      }
    }
  }

  def beforeInsert() {
    if (impId == null) {
      impId = java.util.UUID.randomUUID().toString();
    }
  }

  @Transient
  def notifyDependencies(changeDocument) {
    log.debug("notifyDependencies(${changeDocument})");
  }

  public String toString() {
    "Subscription ${id} - ${name}".toString();
  }

  // JSON definition of the subscription object
  // see http://manbuildswebsite.com/2010/02/15/rendering-json-in-grails-part-3-customise-your-json-with-object-marshallers/
  // Also http://jwicz.wordpress.com/2011/07/11/grails-custom-xml-marshaller/
  // Also http://lucy-michael.klenk.ch/index.php/informatik/grails/c/
  static {
    grails.converters.JSON.registerObjectMarshaller(User) {
      // you can filter here the key-value pairs to output:
      return it.properties.findAll {k,v -> k != 'passwd'}
    }
  }

  // XML.registerObjectMarshaller Facility, { facility, xml ->
  //    xml.attribute 'id', facility.id
  //               xml.build {
  //      name(facility.name)  
  //    }
  //  }

  public Date getDerivedAccessStartDate() {
    startDate ? startDate : null
  }

  public Date getDerivedAccessEndDate() {
    endDate ? endDate : null
  }

  public Date getRenewalDate() {
    manualRenewalDate ? manualRenewalDate : null
  }
  /**
  * OPTIONS: startDate, endDate, hideIdent, inclSubStartDate, hideDeleted, accessibleToUser,inst_shortcode
  **/
  @Transient
  static def refdataFind(params) {
    def result = [];   

    def hqlString = "select sub from Subscription sub where sub.name like ? "
    def hqlParams = [params.q + "%"]
    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd")
    
    if(params.hasDate ){

      def startDate = params.startDate.length() > 1 ? sdf.parse(params.startDate) : null
      def endDate = params.endDate.length() > 1 ? sdf.parse(params.endDate)  : null

      if(startDate){
          hqlString += " AND sub.startDate >= ? "
          hqlParams += startDate
      }
      if(endDate){
        hqlString += " AND sub.endDate <= ? "
        hqlParams += endDate
        }       
    }

    if(params.hideDeleted == 'true'){
      hqlString += " AND sub.status.value != 'Deleted' "
    }

    if(params.inst_shortcode && params.inst_shortcode.length() > 1){
      hqlString += " AND exists ( select orgs from sub.orgRelations orgs where orgs.org.shortcode = ? AND orgs.roleType.value = 'Subscriber' ) "
      hqlParams += params.inst_shortcode
    }


    def results = Subscription.executeQuery(hqlString,hqlParams)
    
    if(params.accessibleToUser){
      for(int i=0;i<results.size();i++){
        if(! results.get(i).checkPermissions("view",User.get(params.accessibleToUser))){
          results.remove(i)
        }
      }
    }

    results?.each { t ->
      def resultText = t.name
      def date = t.startDate ? " (${sdf.format(t.startDate)})" : ""
      resultText = params.inclSubStartDate == "true"? resultText + date : resultText
      resultText = params.hideIdent == "true"? resultText : resultText + " (${t.identifier})"
      result.add([id:"${t.class.name}:${t.id}",text:resultText])
    } 

    result
  }

}

