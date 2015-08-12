<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="utf-8"/>
    <xsl:strip-space elements="*" />
  <xsl:template match="/">
    <xsl:call-template name="csv_header" />
    <xsl:apply-templates select="//TitleListEntry" />
  </xsl:template>
   <xsl:template match="TitleListEntry">
    <!-- licence_id -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="ancestor::Licence/LicenceID" />
    </xsl:call-template>
    <!-- licence_ref -->
    <xsl:call-template name="csventry">
          <xsl:with-param name="txt" select="ancestor::Licence/LicenceReference" />
    </xsl:call-template>
    <!--subscription_id -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/SubscriptionID" />
    </xsl:call-template>
    <!-- subscription_name -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/SubscriptionName" />
    </xsl:call-template>
    <!-- subscription_start_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="ancestor::Subscription/SubTermStartDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="ancestor::Subscription/SubTermStartDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
    <!-- subscription_end_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="ancestor::Subscription/SubTermEndDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="ancestor::Subscription/SubTermEndDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
    <!-- subscription_renewal_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="ancestor::Subscription/ManualRenewalDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="ancestor::Subscription/ManualRenewalDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
    <!-- package_id -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="PackageID" />
    </xsl:call-template>
    <!-- package_name -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="PackageName" />
    </xsl:call-template>
      <!-- publication_title -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./Title" />
      </xsl:call-template>
      <!-- print_identifier -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='ISSN' or @namespace='issn']" />
      </xsl:call-template>
      <!-- online_identifier -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='eISSN' or @namespace='eissn']" />
      </xsl:call-template>
      <!-- date_first_issue_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/StartDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      <!-- num_first_vol_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/StartVolume" />
      </xsl:call-template>
      <!-- num_first_issue_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/StartIssue" />
      </xsl:call-template>
      <!-- date_last_issue_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/EndDate != ''">
	    <xsl:call-template name="formats_date">
	      <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
	    </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      <!-- num_last_vol_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/EndVolume" />
      </xsl:call-template>
      <!-- num_last_issue_online -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/EndIssue" />
      </xsl:call-template>
      <!-- KBART ID -->
      <xsl:call-template name="csventry"></xsl:call-template>
      <!-- Embargo Info -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/Embargo" />
      </xsl:call-template>
      <!-- coverage_depth -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/Coverage" />
      </xsl:call-template>
      <!-- coverage_notes -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/CoverageNote" />
      </xsl:call-template>
      <xsl:text>&#xA;</xsl:text>
   </xsl:template>
   
   <xsl:template name="formats_date"><xsl:param name="date"/><xsl:value-of select="concat(substring($date,9,2),'/',substring($date,6,2),'/',substring($date,1,4))"/></xsl:template>

  <xsl:template name="csventry"><xsl:param name="txt"/><xsl:text>"</xsl:text><xsl:value-of select="normalize-space($txt)"/><xsl:text>"</xsl:text>,</xsl:template>
  <xsl:template name="plainentry"><xsl:param name="txt"/><xsl:value-of select="$txt"/></xsl:template>

  <xsl:template name="csv_header">licence_id,licence_ref,subscription_id,subscription_name,subscription_start_date,subscription_end_date,subscription_renewal_date,package_id,package_name,publication_title,ID.issn,ID.eissn,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,ID.kbart_title_id,embargo_info,coverage_depth,coverage_notes<xsl:text>&#xA;</xsl:text></xsl:template>

</xsl:stylesheet>
