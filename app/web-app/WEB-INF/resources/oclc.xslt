<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

   <xsl:strip-space elements="*" />
    
   <xsl:template match="/">
   		<xsl:text>publication_title&#x9;print_identifier&#x9;online_identifier&#x9;date_first_issue_online&#x9;num_first_vol_online&#x9;num_first_issue_online&#x9;date_last_issue_online&#x9;num_last_vol_online&#x9;num_last_issue_online&#x9;title_url&#x9;first_author&#x9;title_id&#x9;coverage_depth&#x9;coverage_notes&#x9;publisher_name&#x9;location&#x9;title_notes&#x9;oclc_collection_name&#x9;oclc_collection_id&#x9;oclc_entry_id&#x9;oclc_linkscheme&#x9;oclc_number&#x9;ACTION&#x9;</xsl:text>
   		<xsl:apply-templates select="//TitleListEntry" />
   </xsl:template>
   
   <xsl:template match="TitleListEntry">

      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./Title" />
      </xsl:call-template>

      
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">ACTION</xsl:with-param>
      </xsl:call-template>


      <!-- Old stuff -->

      <xsl:text>"</xsl:text>
      <xsl:choose>
      	<xsl:when test="./TitleIDs/ID[@namespace='ISSN'] != ''">
        	<xsl:value-of select="./TitleIDs/ID[@namespace='ISSN']"/>
        </xsl:when>
        <xsl:otherwise>
        	<xsl:value-of select="./TitleIDs/ID[@namespace='eISSN']"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>"</xsl:text>

      <xsl:text>&#x9;</xsl:text>

      <xsl:text>"Journal"</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>"Subscribed"</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>"</xsl:text>
      <xsl:if test="./CoverageStatement/StartDate != ''">
	      <xsl:call-template name="formats_date">
	        <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
	      </xsl:call-template>
      </xsl:if>
      <xsl:text>"</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>"</xsl:text>
      <xsl:if test="./CoverageStatement/EndDate != ''">
	      <xsl:call-template name="formats_date">
	        <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
	      </xsl:call-template>
      </xsl:if>
      <xsl:text>"</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
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

  <xsl:template name="tsventry">
    <xsl:param name="txt"/>
    <xsl:text>"</xsl:text>
    <xsl:value-of select="$txt"/>
    <xsl:text>"</xsl:text>
    <xsl:text>&#x9;</xsl:text>
  </xsl:template>

</xsl:stylesheet>
