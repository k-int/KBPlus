package com.k_int.kbplus

class Identifier {

  IdentifierNamespace ns
  String value

  static hasMany = [ occurrences:IdentifierOccurrence ]
  static mappedBy = [ occurrences:'identifier' ]

  static constraints = {
  }

  static mapping = {
       id column:'id_id'
       ns column:'id_ns_fk', index:'id_value_idx'
    value column:'id_value', index:'id_value_idx'
  }

  static def lookupOrCreateCanonicalIdentifier(ns, value) {
    // log.debug("lookupOrCreateCanonicalIdentifier(${ns},${value})");
    def namespace = IdentifierNamespace.findByNs(ns) ?: new IdentifierNamespace(ns:ns).save();
    Identifier.findByNsAndValue(namespace,value) ?: new Identifier(ns:namespace, value:value).save();
  }

  static def refdataFind(query) {
    def result = []
    if ( query.contains(':') ) {
      params=query.split(':')
      def namespace = IdentifierNamespace.findByNs(ns)
      if ( namespace ) {
        result = Identifier.findByNsAndValueLike(namespace,"${value}%")
      }
    }
    else {
      result = Identifier.findByValueLike("${value}%")
    }
    result
  }

  static def refdataCreate(value) {
    def params = value.split(':');
    if ( ( params.length == 2 ) && ( params[0] != '' ) && ( params[1] != '' ) )
      return lookupOrCreateCanonicalIdentifier(params[0],params[1]);
  }

}
