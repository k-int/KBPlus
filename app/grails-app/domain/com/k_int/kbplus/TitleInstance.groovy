package com.k_int.kbplus

class TitleInstance {

  String title
  String impId
  RefdataValue status
  RefdataValue type
  Date dateCreated
  Date lastUpdated

  static mappedBy = [tipps: 'title', ids: 'ti', orgs: 'title']
  static hasMany = [tipps: TitleInstancePackagePlatform, ids: IdentifierOccurrence, orgs: OrgRole]


  static mapping = {
         id column:'ti_id'
      title column:'ti_title'
    version column:'ti_version'
      impId column:'ti_imp_id', index:'ti_imp_id_idx'
     status column:'ti_status_rv_fk'
       type column:'ti_type_rv_fk'
      tipps sort:'startDate', order: 'asc'
  }

  static constraints = {
    status(nullable:true, blank:false);
    type(nullable:true, blank:false);
    title(nullable:true, blank:false,maxSize:1024);
  }

  String getIdentifierValue(idtype) {
    def result=null
    ids?.each { id ->
      if ( id.identifier?.ns?.ns == idtype )
        result = id.identifier?.value
    }
    result
  }

  Org getPublisher() {
    def result = null;
    orgs.each { o ->
      if ( o.roleType.value == 'Publisher' ) {
        result = o.org
      }
    }
    result
  }

  /**
   * Attempt to look up a title instance which has any of the listed identifiers
   * @param candidate_identifiers A list of maps containing identifiers and namespaces [ { namespace:'ISSN', value:'Xnnnn-nnnn' }, {namespace:'ISSN', value:'Xnnnn-nnnn'} ]
   */
  static def findByIdentifier(candidate_identifiers) {
    candidate_identifiers.each { i ->
      def id = Identifier.lookupOrCreateCanonicalIdentifier(i.namespace, i.value)
      def io = IdentifierOccurrence.findByIdentifier(id)
      if ( io && io.ti )
        return io.ti;
    }
    return null;     
  }
  
  static def lookupOrCreate(candidate_identifiers, title) {
    def result = null;
    def ids = []
    
    candidate_identifiers.each { i ->
      if ( !result ) {
        def id = Identifier.lookupOrCreateCanonicalIdentifier(i.namespace, i.value)
        ids.add(id);
        
        def io = IdentifierOccurrence.findByIdentifier(id)
        if ( io && io.ti ) {
          result = io.ti;
        }
      }
    }
    
    if (!result) {
      result = new TitleInstance(title:title);
      ids.each { 
        result.ids.add(new IdentifierOccurrence(identifier:it, ti:result));
      }
      if ( ! result.save() ) {
        throw new RuntimeException("Problem creating title instance : ${result.errors?.toString()}");
      }
    }
    
    return result;     

  }
}
