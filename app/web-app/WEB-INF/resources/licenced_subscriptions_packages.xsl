<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="utf-8"/>
    <xsl:strip-space elements="*" />
  <xsl:template match="/">
    <xsl:call-template name="csv_header" />
    <xsl:apply-templates select="//Packages" />
  </xsl:template>
  <xsl:template match="Packages">
    <xsl:choose>
      <xsl:when test="child::node()">
        <xsl:for-each select="Package">
          <xsl:apply-templates select="ancestor::Subscription" />
          <xsl:call-template name="csventry">
            <xsl:with-param name="txt" select="PackageID" />
          </xsl:call-template>
          <xsl:call-template name="csventry">
            <xsl:with-param name="txt" select="PackageName" />
          </xsl:call-template>
          <xsl:call-template name="csventry">
            <xsl:with-param name="txt" select="PackageContentProvider" />
          </xsl:call-template>
          <xsl:text>&#xA;</xsl:text>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="parent::Subscription" />
        <xsl:text>&#xA;</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
   <xsl:template match="Subscription">
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
        <xsl:with-param name="txt" select="SubscriptionID" />
    </xsl:call-template>
    <!-- subscription_name -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="SubscriptionName" />
    </xsl:call-template>
    <!-- subscription_start_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="SubTermStartDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="SubTermStartDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
    <!-- subscription_end_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="SubTermEndDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="SubTermEndDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
    <!-- subscription_renewal_date -->
    <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="ManualRenewalDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="ManualRenewalDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
   </xsl:template>
   
   <xsl:template name="formats_date"><xsl:param name="date"/><xsl:value-of select="concat(substring($date,9,2),'/',substring($date,6,2),'/',substring($date,1,4))"/></xsl:template>

  <xsl:template name="csventry"><xsl:param name="txt"/><xsl:text>"</xsl:text><xsl:value-of select="normalize-space($txt)"/><xsl:text>"</xsl:text>,</xsl:template>
  <xsl:template name="plainentry"><xsl:param name="txt"/><xsl:value-of select="$txt"/></xsl:template>

  <xsl:template name="csv_header">licence_id,licence_ref,subscription_id,subscription_name,subscription_start_date,subscription_end_date,subscription_renewal_date,package_id,package_name,package_provider<xsl:text>&#xA;</xsl:text></xsl:template>

</xsl:stylesheet>
