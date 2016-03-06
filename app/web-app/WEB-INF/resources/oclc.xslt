<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

   <xsl:strip-space elements="*" />
    
   <xsl:template match="/">
    <xsl:call-template name="oclc_header" />
   		<xsl:apply-templates select="//TitleListEntry" />
   </xsl:template>
   
   <xsl:template match="TitleListEntry">

      <!-- publication_title -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./Title" />
      </xsl:call-template>

      <!-- print_identifier -->
      <xsl:choose>
        <xsl:when test="./TitleIDs/ID[@namespace='ISSN' or @namespace='issn']">
          <xsl:call-template name="tsventry">
            <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='ISSN' or @namespace='issn'][1]" />
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="./TitleIDs/ID[@namespace='ISBN' or @namespace='isbn']">
          <xsl:call-template name="tsventry">
            <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='ISBN' or @namespace='isbn'][1]" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>""&#x9;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>

      <!-- online_identifier -->
      <xsl:choose>
        <xsl:when test="./TitleIDs/ID[@namespace='eISSN' or @namespace='eissn']">
          <xsl:call-template name="tsventry">
            <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='eISSN' or @namespace='eissn'][1]" />
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="./TitleIDs/ID[@namespace='eISBN' or @namespace='eisbn']">
          <xsl:call-template name="tsventry">
            <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='eISBN' or @namespace='eisbn'][1]" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>""&#x9;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>

      <!-- date_first_issue_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/StartDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>

      <!-- num_first_vol_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/StartVolume" />
      </xsl:call-template>

      <!-- num_first_issue_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/StartIssue" />
      </xsl:call-template>

      <!-- date_last_issue_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/EndDate != ''">
	    <xsl:call-template name="formats_date">
	      <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
	    </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>

      <!-- num_last_vol_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/EndVolume" />
      </xsl:call-template>

      <!-- num_last_issue_online -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/EndIssue" />
      </xsl:call-template>

      <!-- title_url -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/HostPlatformURL" />
      </xsl:call-template>

      <!-- first_author -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- title_id -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='kbart_title_id']" />
      </xsl:call-template>

      <!-- coverage_depth -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/CoverageDepth" />
      </xsl:call-template>

      <!-- coverage_notes -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/CoverageNote" />
      </xsl:call-template>

      <!-- publisher_name -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- location -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- title_notes -->
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./CoverageStatement/CoverageNote" />
      </xsl:call-template>

      <!-- oclc_collection_name -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- oclc_collection_id -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- oclc_entry_id -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- oclc_linkscheme -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <!-- oclc_number -->
      <xsl:call-template name="tsventry">
      </xsl:call-template>

      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">raw</xsl:with-param>
      </xsl:call-template>

      <xsl:text>&#xA;</xsl:text>

   </xsl:template>
   
   <xsl:template name="formats_date">
   	  <xsl:param name="date"/>
      <xsl:value-of select=
		  "concat(
		     substring($date,6,2),
		     '-',
		     substring($date,9,2),
		     '-',
		     substring($date,1,4)
			)"/>
    </xsl:template>

  <xsl:template name="tsventry"><xsl:param name="txt"/><xsl:text>"</xsl:text><xsl:value-of select="normalize-space($txt)"/><xsl:text>"</xsl:text><xsl:text>&#x9;</xsl:text></xsl:template>

  <xsl:template name="oclc_header"><xsl:text>publication_title&#x9;print_identifier&#x9;online_identifier&#x9;date_first_issue_online&#x9;num_first_vol_online&#x9;num_first_issue_online&#x9;date_last_issue_online&#x9;num_last_vol_online&#x9;num_last_issue_online&#x9;title_url&#x9;first_author&#x9;title_id&#x9;coverage_depth&#x9;coverage_notes&#x9;publisher_name&#x9;location&#x9;title_notes&#x9;oclc_collection_name&#x9;oclc_collection_id&#x9;oclc_entry_id&#x9;oclc_linkscheme&#x9;oclc_number&#x9;ACTION&#x9;</xsl:text><xsl:text>&#xA;</xsl:text></xsl:template>

</xsl:stylesheet>
