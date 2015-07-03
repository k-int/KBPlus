<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>
   <xsl:strip-space elements="*" />
   <xsl:template match="/">
   <xsl:apply-templates select="//Package" />
   <xsl:apply-templates select="//TitleList" />
	<xsl:apply-templates select="//TitleListEntry" />
   </xsl:template>
   <xsl:template match="Package">Provider,<xsl:for-each select="./RelatedOrg">
    <xsl:if test="OrgRole = 'Content Provider'">
      <xsl:call-template name="plainentry"><xsl:with-param name="txt" select="OrgName" /></xsl:call-template>
    </xsl:if>
  </xsl:for-each>
Package Identifier,<xsl:call-template name="plainentry"><xsl:with-param name="txt" select="translate(./PackageName,':','_')" /></xsl:call-template>
Package Name,<xsl:call-template name="plainentry"><xsl:with-param name="txt" select="./PackageName" /></xsl:call-template>
Agreement Term Start Year,<xsl:call-template name="formats_date"><xsl:with-param name="date" select="./PackageTermStartDate" /></xsl:call-template>
Agreement Term End Year,<xsl:call-template name="formats_date"><xsl:with-param name="date" select="./PackageTermEndDate" /></xsl:call-template>
Consortium,
</xsl:template>
  <xsl:template match="TitleList">publication_title,ID.issn,ID.eissn,date_first_issue_online,num_first_vol_online,num_first_issue_online,date_last_issue_online,num_last_vol_online,num_last_issue_online,ID.kbart_title_id,embargo_info,coverage_depth,coverage_notes,publisher_name,ID.doi,platform.host.name,platform.host.url,platform.administrative.name,platform.administrative.url,hybrid_oa,access_start_date,access_end_date<xsl:if test="parent::Package">,tipp.status</xsl:if><xsl:if test="parent::Subscription">,core.medium,core.earliest,core.latest</xsl:if><xsl:text>&#xA;</xsl:text>
   </xsl:template>
   
   <xsl:template match="TitleListEntry">
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
      <!-- Publisher -->
      <xsl:call-template name="csventry"></xsl:call-template>
      <!-- DOI -->
      <xsl:call-template name="csventry"></xsl:call-template>
      <!-- host_platform_names -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/HostPlatformName" />
      </xsl:call-template>
       <!-- host_platform_names -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/HostPlatformURL" />
      </xsl:call-template>
      <!-- platform.administrative.name -->
      <xsl:call-template name="csventry"></xsl:call-template>
      <!-- platform.administrative.url -->
      <xsl:call-template name="csventry"></xsl:call-template>
      <!-- hybrid_oa -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt" select="./CoverageStatement/HybridOA" />
      </xsl:call-template>
      <!-- access_start_date -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/AccessFrom != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/AccessFrom" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      <!-- access_end_date -->
      <xsl:call-template name="csventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/AccessTo != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/AccessTo" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      <!-- status -->
      <xsl:if test="ancestor::Package">
        <xsl:call-template name="csventry">
          <xsl:with-param name="txt" select="./CoverageStatement/TIPPStatus" />
        </xsl:call-template>
      </xsl:if>
      <!-- Core medium -->
      <xsl:if test="ancestor::Subscription">
        <xsl:call-template name="csventry">
          <xsl:with-param name="txt" select="./CoverageStatement/CoreMedium" />
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="ancestor::Subscription">
        <!-- core_start_date -->
        <xsl:variable name="core_earliest">
          <xsl:for-each select="./CoverageStatement/CoreDateList/CoreDate">
            <xsl:sort select="CoreStart" order="ascending" />
              <xsl:if test="position() = 1">
                <xsl:value-of select="CoreStart" />
              </xsl:if>
          </xsl:for-each>
        </xsl:variable>
        <xsl:call-template name="csventry">
          <xsl:with-param name="txt">
            <xsl:if test="$core_earliest != ''">
              <xsl:call-template name="formats_date">
                <xsl:with-param name="date" select="$core_earliest" />
              </xsl:call-template>
            </xsl:if>
          </xsl:with-param>
        </xsl:call-template>
        <!-- core_end_date -->
        <xsl:variable name="core_latest">
          <xsl:for-each select="./CoverageStatement/CoreDateList/CoreDate">
            <xsl:sort select="CoreEnd" order="descending" />
              <xsl:if test="position() = 1">
                <xsl:value-of select="CoreEnd" />
              </xsl:if>
          </xsl:for-each>
        </xsl:variable>
        <xsl:call-template name="csventry">
          <xsl:with-param name="txt">
            <xsl:if test="$core_latest != ''">
              <xsl:call-template name="formats_date">
                <xsl:with-param name="date" select="$core_latest" />
              </xsl:call-template>
            </xsl:if>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:if>
      <xsl:text>&#xA;</xsl:text>
   </xsl:template>
   
   <xsl:template name="formats_date"><xsl:param name="date"/><xsl:value-of select="concat(substring($date,9,2),'/',substring($date,6,2),'/',substring($date,1,4))"/></xsl:template>

  <xsl:template name="csventry"><xsl:param name="txt"/><xsl:text>"</xsl:text><xsl:value-of select="normalize-space($txt)"/><xsl:text>"</xsl:text>,</xsl:template>
  <xsl:template name="plainentry"><xsl:param name="txt"/><xsl:value-of select="$txt"/></xsl:template>

</xsl:stylesheet>
