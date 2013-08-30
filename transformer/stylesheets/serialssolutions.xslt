<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text"/>

	<xsl:strip-space elements="*" />

   <xsl:template match="TitleListEntry">
      <xsl:value-of select="./Title"/>
      <xsl:text>&#x9;</xsl:text>
      <xsl:choose>
      	<xsl:when test="./TitleIDs/ID[@namespace='ISSN'] != ''">
        	<xsl:value-of select="./TitleIDs/ID[@namespace='ISSN']"/>
        </xsl:when>
        <xsl:otherwise>
        	<xsl:value-of select="./TitleIDs/ID[@namespace='eISSN']"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>Journal</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>Subscribed</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:text>""</xsl:text>
      <xsl:text>&#x9;</xsl:text>
      <xsl:call-template name="formats_date">
        <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
      </xsl:call-template>
      <xsl:text>&#x9;</xsl:text>
      <xsl:call-template name="formats_date">
        <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
      </xsl:call-template>
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

</xsl:stylesheet>
