<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

   <xsl:strip-space elements="*" />
    
   <xsl:template match="/">
   		<xsl:text>Title&#x9;ISSN/ISBN&#x9;Type&#x9;Status&#x9;Default Dates&#x9;Custom Date From&#x9;Custom Date To&#x9;Title Id&#x9;Publication date&#x9;Edition&#x9;Publisher&#x9;Public Note&#x9;Display Public Note&#x9;Location Note&#x9;Display Location Note&#x9;Default URL&#x9;Custom URL&#xA;</xsl:text>
   		<xsl:apply-templates select="//TitleListEntry" />
   </xsl:template>
   
   <xsl:template match="TitleListEntry">
   	  <xsl:text>"</xsl:text>
      <xsl:value-of select="./Title"/>
      <xsl:text>"</xsl:text>
      <xsl:text>&#x9;</xsl:text>
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

</xsl:stylesheet>
