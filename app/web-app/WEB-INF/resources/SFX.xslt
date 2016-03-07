<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

	<xsl:strip-space elements="*" />

<xsl:template match="/">
	<xsl:apply-templates select="//TitleListEntry" />
</xsl:template>

   <xsl:template match="TitleListEntry">
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
      <xsl:variable name="coverage">
          <xsl:choose>
    	  <!-- all the values StartDate, StartVolume and StartIssue are empty -->
    	      <xsl:when test="((./CoverageStatement/StartDate = '') and (./CoverageStatement/StartVolume = '') and (./CoverageStatement/StartIssue = '')) and
    						(not(./CoverageStatement/EndDate = '') or not(./CoverageStatement/EndVolume = '') or not(./CoverageStatement/EndIssue = ''))">
    			<xsl:call-template name="only_end"/>
    	      </xsl:when>
    	      <!-- all the values EndDate, EndVolume and EndIssue are empty -->
    	      <xsl:when test="(not(./CoverageStatement/StartDate = '') or not(./CoverageStatement/StartVolume = '') or not(./CoverageStatement/StartIssue = '')) and
    						((./CoverageStatement/EndDate = '') and (./CoverageStatement/EndVolume = '') and (./CoverageStatement/EndIssue = ''))">			
    			<xsl:call-template name="only_start"/>
    	      </xsl:when>
    	      <!-- all the values are empty -->
    	      <xsl:when test="((./CoverageStatement/StartDate = '') and (./CoverageStatement/StartVolume = '') and (./CoverageStatement/StartIssue = '')) and
    						((./CoverageStatement/EndDate = '')  and (./CoverageStatement/EndVolume = '') and (./CoverageStatement/EndIssue = ''))">
    			<xsl:call-template name="without_both"/>
    	      </xsl:when>
    	      <!-- all the values are NOT empty -->
    	      <xsl:otherwise>
    			<xsl:call-template name="with_both"/>	
    	      </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="tsventry">
          <xsl:with-param name="txt" select="$coverage" />
        </xsl:call-template>
        <xsl:call-template name="tsventry">
          <xsl:with-param name="txt" select="'ACTIVE'" />
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>

   
   <xsl:template name="extract_year">
   	  <xsl:param name="date"/>
      <xsl:value-of select="substring($date,1,4)"/>
   </xsl:template>
    
    <xsl:template name="only_end">
	<xsl:text disable-output-escaping="yes"><![CDATA[$obj->parsedDate("<=",]]></xsl:text>
	<xsl:choose>
		<xsl:when test="not(./CoverageStatement/EndDate = '')">
			<xsl:call-template name="extract_year">
			        <xsl:with-param name="date" select="./CoverageStatement/EndDate" />
			    </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
	<xsl:text>,</xsl:text>
	<xsl:choose>
		<xsl:when test="number(./CoverageStatement/EndVolume)">
			<xsl:value-of select="./CoverageStatement/EndVolume"></xsl:value-of>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
			</xsl:otherwise>
	</xsl:choose>
	<xsl:text>,</xsl:text>
	<xsl:choose>
		<xsl:when test="number(./CoverageStatement/EndIssue)">
			<xsl:value-of select="./CoverageStatement/EndIssue"></xsl:value-of>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
			</xsl:otherwise>
	</xsl:choose>
	<xsl:text>)</xsl:text>
    </xsl:template>
    
    
    <xsl:template name="only_start">
	<xsl:text disable-output-escaping="yes"><![CDATA[$obj->parsedDate(">=",]]></xsl:text>
	<xsl:choose>
		<xsl:when test="not(./CoverageStatement/StartDate = '')">
			<xsl:call-template name="extract_year">
			        <xsl:with-param name="date" select="./CoverageStatement/StartDate" />
			    </xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
			</xsl:otherwise>
	</xsl:choose>
	<xsl:text>,</xsl:text>
	<xsl:choose>
		<xsl:when test="number(./CoverageStatement/StartVolume)">
			<xsl:value-of select="./CoverageStatement/StartVolume"></xsl:value-of>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
			</xsl:otherwise>
	</xsl:choose>
	<xsl:text>,</xsl:text>
	<xsl:choose>
		<xsl:when test="number(./CoverageStatement/StartIssue)">
			<xsl:value-of select="./CoverageStatement/StartIssue"></xsl:value-of>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>undef</xsl:text>
			</xsl:otherwise>
	</xsl:choose>
	<xsl:text>)</xsl:text>
    </xsl:template>
    
    <xsl:template name="with_both">
    	<xsl:call-template name="only_start"/>
    	<xsl:text disable-output-escaping="yes"> <![CDATA[&&]]> </xsl:text>
  		<xsl:call-template name="only_end"/>
    </xsl:template>
    
    <xsl:template name="without_both">
    	<xsl:text></xsl:text>
    </xsl:template>

    <xsl:template name="tsventry"><xsl:param name="txt"/><xsl:value-of select="normalize-space($txt)"/><xsl:text>&#x9;</xsl:text></xsl:template>
    <xsl:template name="plainentry"><xsl:param name="txt"/><xsl:value-of select="$txt"/></xsl:template>

</xsl:stylesheet>