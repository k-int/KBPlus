package com.k_int.kbplus

class Identifier {

  IdentifierNamespace ns
  String value
  IdentifierGroup ig

  static hasMany = [ occurrences:IdentifierOccurrence]
  static mappedBy = [ occurrences:'identifier']

  static constraints = {
    value validator: {val,obj ->
      if (obj.ns.validationRegex){
        def pattern = ~/${obj.ns.validationRegex}/
        return pattern.matcher(val).matches() 
      }
    }
    ig(nullable:true, blank:false)
  }

  static mapping = {
       id column:'id_id'
    value column:'id_value', index:'id_value_idx'
       ns column:'id_ns_fk', index:'id_value_idx'
       ig column:'id_ig_fk', index:'id_ig_idx'
  }

  def beforeUpdate() {
    value = value?.trim()
  }

  static def lookupOrCreateCanonicalIdentifier(ns, value) {
    value = value?.trim()
    ns = ns?.trim()
    // println ("lookupOrCreateCanonicalIdentifier(${ns},${value})");
    def namespace = IdentifierNamespace.findByNsIlike(ns) ?: new IdentifierNamespace(ns:ns.toLowerCase()).save();
    Identifier.findByNsAndValue(namespace,value) ?: new Identifier(ns:namespace, value:value).save();
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    if ( params.q.contains(':') ) {
      def qp=params.q.split(':');
      // println("Search by namspace identifier: ${qp}");
      def namespace = IdentifierNamespace.findByNsIlike(qp[0]);
      if ( namespace && qp.size() == 2) {
        ql = Identifier.findAllByNsAndValueIlike(namespace,"${qp[1]}%")
      }
    }
    else {
      ql = Identifier.findAllByValueIlike("${params.q}%",params)
    }

    if ( ql ) {
      ql.each { id ->
        result.add([id:"${id.class.name}:${id.id}",text:"${id.ns.ns}:${id.value}"])
      }
    }

    result
  }

  static def refdataCreate(value) {
    // value is String[] arising from  value.split(':');
    if ( ( value.length == 4 ) && ( value[2] != '' ) && ( value[3] != '' ) )
      return lookupOrCreateCanonicalIdentifier(value[2],value[3]);

    return null;
  }

}
