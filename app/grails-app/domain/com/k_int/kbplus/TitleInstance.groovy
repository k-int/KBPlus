package com.k_int.kbplus

import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

class TitleInstance {

  static final Pattern alphanum = Pattern.compile("\\p{Punct}|\\p{Cntrl}");

  String title
  String normTitle
  String keyTitle
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
    normTitle column:'ti_norm_title'
     keyTitle column:'ti_key_title'
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
    normTitle(nullable:true, blank:false,maxSize:1024);
    keyTitle(nullable:true, blank:false,maxSize:1024);
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
      result = new TitleInstance(title:title, impId:java.util.UUID.randomUUID().toString());
      
      result.ids=[]
      ids.each { 
        result.ids.add(new IdentifierOccurrence(identifier:it, ti:result));
      }
      if ( ! result.save() ) {
        throw new RuntimeException("Problem creating title instance : ${result.errors?.toString()}");
      }
    }
    
    return result;     

  }

  /**
   *  Caller passes in a map like {issn:'nnnn-nnnn',doi:'quyeihdj'} and expects to get back a new title
   *  or one matching any of the identifiers
   */
  static def lookupOrCreateViaIdMap(candidate_identifiers, title) {
    def result = null;
    def ids = []
    
    candidate_identifiers.each { i ->
      if ( !result ) {
        def id = Identifier.lookupOrCreateCanonicalIdentifier(i.key, i.value)
        ids.add(id);
        
        def io = IdentifierOccurrence.findByIdentifier(id)
        if ( io && io.ti ) {
          result = io.ti;
        }
      }
    }
    
    if (!result) {
      result = new TitleInstance(title:title, impId:java.util.UUID.randomUUID().toString());
      
      result.ids=[]
      ids.each { 
        result.ids.add(new IdentifierOccurrence(identifier:it, ti:result));
      }
      if ( ! result.save() ) {
        throw new RuntimeException("Problem creating title instance : ${result.errors?.toString()}");
      }
    }
    
    return result;     

  }

  def beforeInsert() {
    if ( title != null ) {
      normTitle = generateNormTitle(title)
      keyTitle = generateKeyTitle(title)
    }
  }

  def beforeUpdate() {
    if ( title != null ) {
      normTitle = generateNormTitle(title)
      keyTitle = generateKeyTitle(title)
    }
  }


  public static String generateNormTitle(String input_title) {
    return input_title;
  }


  public static String generateKeyTitle(String s) {
        s.replaceAll('&',' and ');
        s = s.trim(); // first off, remove whitespace around the string
        s = s.toLowerCase(); // then lowercase it
        s = alphanum.matcher(s).replaceAll(""); // then remove all punctuation and control chars
        String[] frags = StringUtils.split(s); // split by whitespace
        TreeSet<String> set = new TreeSet<String>();
        for (String ss : frags) {
            set.add(ss); // order fragments and dedupe
        }
        StringBuffer b = new StringBuffer();
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {  // join ordered fragments back together
            b.append(i.next());
            if ( i.hasNext() )
              b.append(' ');
        }
        return asciify(b.toString()); // find ASCII equivalent to characters 
  }

  protected static String asciify(String s) {
        char[] c = s.toCharArray();
        StringBuffer b = new StringBuffer();
        for (char element : c) {
            b.append(translate(element));
        }
        return b.toString();
  }
    
    /**
     * Translate the given unicode char in the closest ASCII representation
     * NOTE: this function deals only with latin-1 supplement and latin-1 extended code charts
     */
    private static char translate(char c) {
        switch(c) {
            case '\u00C0':
            case '\u00C1':
            case '\u00C2':
            case '\u00C3':
            case '\u00C4':
            case '\u00C5':
            case '\u00E0':
            case '\u00E1':
            case '\u00E2':
            case '\u00E3':
            case '\u00E4':
            case '\u00E5':
            case '\u0100':
            case '\u0101':
            case '\u0102':
            case '\u0103':
            case '\u0104':
            case '\u0105':
                return 'a';
            case '\u00C7':
            case '\u00E7':
            case '\u0106':
            case '\u0107':
            case '\u0108':
            case '\u0109':
            case '\u010A':
            case '\u010B':
            case '\u010C':
            case '\u010D':
                return 'c';
            case '\u00D0':
            case '\u00F0':
            case '\u010E':
            case '\u010F':
            case '\u0110':
            case '\u0111':
                return 'd';
            case '\u00C8':
            case '\u00C9':
            case '\u00CA':
            case '\u00CB':
            case '\u00E8':
            case '\u00E9':
            case '\u00EA':
            case '\u00EB':
            case '\u0112':
            case '\u0113':
            case '\u0114':
            case '\u0115':
            case '\u0116':
            case '\u0117':
            case '\u0118':
            case '\u0119':
            case '\u011A':
            case '\u011B':
                return 'e';
            case '\u011C':
            case '\u011D':
            case '\u011E':
            case '\u011F':
            case '\u0120':
            case '\u0121':
            case '\u0122':
            case '\u0123':
                return 'g';
            case '\u0124':
            case '\u0125':
            case '\u0126':
            case '\u0127':
                return 'h';
            case '\u00CC':
            case '\u00CD':
            case '\u00CE':
            case '\u00CF':
            case '\u00EC':
            case '\u00ED':
            case '\u00EE':
            case '\u00EF':
            case '\u0128':
            case '\u0129':
            case '\u012A':
            case '\u012B':
            case '\u012C':
            case '\u012D':
            case '\u012E':
            case '\u012F':
            case '\u0130':
            case '\u0131':
                return 'i';
            case '\u0134':
            case '\u0135':
                return 'j';
            case '\u0136':
            case '\u0137':
            case '\u0138':
                return 'k';
            case '\u0139':
            case '\u013A':
            case '\u013B':
            case '\u013C':
            case '\u013D':
            case '\u013E':
            case '\u013F':
            case '\u0140':
            case '\u0141':
            case '\u0142':
                return 'l';
            case '\u00D1':
            case '\u00F1':
            case '\u0143':
            case '\u0144':
            case '\u0145':
            case '\u0146':
            case '\u0147':
            case '\u0148':
            case '\u0149':
            case '\u014A':
            case '\u014B':
                return 'n';
            case '\u00D2':
            case '\u00D3':
            case '\u00D4':
            case '\u00D5':
            case '\u00D6':
            case '\u00D8':
            case '\u00F2':
            case '\u00F3':
            case '\u00F4':
            case '\u00F5':
            case '\u00F6':
            case '\u00F8':
            case '\u014C':
            case '\u014D':
            case '\u014E':
            case '\u014F':
            case '\u0150':
            case '\u0151':
                return 'o';
            case '\u0154':
            case '\u0155':
            case '\u0156':
            case '\u0157':
            case '\u0158':
            case '\u0159':
                return 'r';
            case '\u015A':
            case '\u015B':
            case '\u015C':
            case '\u015D':
            case '\u015E':
            case '\u015F':
            case '\u0160':
            case '\u0161':
            case '\u017F':
                return 's';
            case '\u0162':
            case '\u0163':
            case '\u0164':
            case '\u0165':
            case '\u0166':
            case '\u0167':
                return 't';
            case '\u00D9':
            case '\u00DA':
            case '\u00DB':
            case '\u00DC':
            case '\u00F9':
            case '\u00FA':
            case '\u00FB':
            case '\u00FC':
            case '\u0168':
            case '\u0169':
            case '\u016A':
            case '\u016B':
            case '\u016C':
            case '\u016D':
            case '\u016E':
            case '\u016F':
            case '\u0170':
            case '\u0171':
            case '\u0172':
            case '\u0173':
                return 'u';
            case '\u0174':
            case '\u0175':
                return 'w';
            case '\u00DD':
            case '\u00FD':
            case '\u00FF':
            case '\u0176':
            case '\u0177':
            case '\u0178':
                return 'y';
            case '\u0179':
            case '\u017A':
            case '\u017B':
            case '\u017C':
            case '\u017D':
            case '\u017E':
                return 'z';
        }
        return c;
    }
}
