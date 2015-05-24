package com.k_int.kbplus

/**
 * An association class for the many-to-many relationship between OnixplUsageTerm
 * and OnixPlLicenseText.
 */
class OnixplUsageTermLicenseText {

  static hasOne= [
      usageTerm:OnixplUsageTerm,
      licenseText:OnixplLicenseText
  ]

  static belongsTo = [
      usageTerm:OnixplUsageTerm
  ]

  static mapping = {
    usageTerm column:   'opul_oput_fk', index:'opul_entry_idx'
    licenseText column: 'opul_oplt_fk', index:'opul_entry_idx'
  }

    static constraints = {
      usageTerm(nullable:false, blank: false)
      licenseText(nullable:false, blank: false)
    }

  @Override
  public java.lang.String toString() {
    return "OnixplUsageTermLicenseText{" +
        "id=" + id +
        ", version=" + version +
        ", usageTerm=" + usageTerm +
        ", licenseText=" + licenseText +
        '}';
  }
}
