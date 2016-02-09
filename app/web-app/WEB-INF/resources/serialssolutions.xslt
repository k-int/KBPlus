<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

   <xsl:strip-space elements="*" />
    
   <xsl:template match="/">
      <xsl:call-template name="serialsolns_header" />
   	<xsl:apply-templates select="//TitleListEntry" />
   </xsl:template>
   
   <xsl:template match="TitleListEntry">
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt" select="./Title" />
      </xsl:call-template>
      <xsl:choose>
        <xsl:when test="./TitleIDs/ID[@namespace='ISSN' or @namespace='issn'] != ''">
          <xsl:call-template name="tsventry">
            <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='ISSN' or @namespace='issn'][1]" />
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
         <xsl:choose>
            <xsl:when test="./TitleIDs/ID[@namespace='eISSN' or @namespace='eissn'] != ''">
                  <xsl:call-template name="tsventry">
                    <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='eISSN' or @namespace='eissn'][1]" />
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="tsventry">
                 <xsl:with-param name="txt" select="./TitleIDs/ID[@namespace='ISBN' or @namespace='isbn'][1]"/>
                    </xsl:call-template>
            </xsl:otherwise>
         </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">Journal</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">Subscribed</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/StartDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt">
          <xsl:if test="./CoverageStatement/EndDate != ''">
            <xsl:call-template name="formats_date">
              <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
            </xsl:call-template>
          </xsl:if>
        </xsl:with-param>
      </xsl:call-template>
      
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="tsventry">
        <xsl:with-param name="txt"></xsl:with-param>
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

    <xsl:template name="serialsolns_header"><xsl:text>Title&#x9;ISSN/ISBN&#x9;Type&#x9;Status&#x9;Default Dates&#x9;Custom Date From&#x9;Custom Date To&#x9;Title Id&#x9;Publication date&#x9;Edition&#x9;Publisher&#x9;Public Note&#x9;Display Public Note&#x9;Location Note&#x9;Display Location Note&#x9;Default URL&#x9;Custom URL&#xA;</xsl:text></xsl:template>


</xsl:stylesheet>
