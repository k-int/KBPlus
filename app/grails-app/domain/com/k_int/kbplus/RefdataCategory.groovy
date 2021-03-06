package com.k_int.kbplus
import javax.persistence.Transient

class RefdataCategory {

  String desc

  static mapping = {
         id column:'rdc_id'
    version column:'rdc_version'
       desc column:'rdc_description', index:'rdc_description_idx'
  }

  static constraints = {
  }

  static def lookupOrCreate(category_name, value) {
    def cat = RefdataCategory.findByDesc(category_name);
    if ( !cat ) {
      cat = new RefdataCategory(desc:category_name).save();
    }

    def result = RefdataValue.findByOwnerAndValue(cat, value)

    if ( !result ) {
      new RefdataValue(owner:cat, value:value).save(flush:true);
      result = RefdataValue.findByOwnerAndValue(cat, value);
    }

    result
  }

  static def lookupOrCreate(category_name, icon, value) {
    def result = lookupOrCreate(category_name, value)
    result.icon = icon
    result
  }
  static def refdataFind(params) {
      def result = []
      def ql = null

      ql = RefdataCategory.findAllByDescIlike("${params.q}%",params)
      if ( ql ) {
          ql.each { id ->
              result.add([id:"${id.id}",text:"${id.desc}"])
          }
      }
      result
  }

  @Transient
  public static final PKG_SCOPE = "Package.Scope"
  @Transient
  public static final PKG_LIST_STAT = "Package.ListStatus"
  @Transient
  public static final PKG_FIXED = "Package.Fixed"
  @Transient
  public static final PKG_BREAKABLE = "Package.Breakable"
  @Transient
  public static final PKG_CONSISTENT = 'Package.Consistent'
  @Transient
  public static final PKG_TYPE = 'Package.Type'
  @Transient
  public static final TI_STATUS = 'TitleInstanceStatus'
  @Transient
  public static final LIC_STATUTS= 'License Status'
  @Transient
  public static final LIC_TYPE = 'License Type'
  @Transient
  public static final TIPP_STATUS = 'TIPP Status'
   
}
