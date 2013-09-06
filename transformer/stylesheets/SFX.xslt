<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="text" encoding="utf-8"/>

	<xsl:strip-space elements="*" />

   <xsl:template match="TitleListEntry">
      <xsl:choose>
      	<xsl:when test="./TitleIDs/ID[@namespace='ISSN'] != ''">
        	<xsl:value-of select="./TitleIDs/ID[@namespace='ISSN']"/>
        </xsl:when>
        <xsl:otherwise>
        	<xsl:choose>
	        	<xsl:when test="./TitleIDs/ID[@namespace='eISSN'] != ''">
	        		<xsl:value-of select="./TitleIDs/ID[@namespace='eISSN']"/>
	        	</xsl:when>
	        	<xsl:otherwise>
	        		<xsl:value-of select="./TitleIDs/ID[@namespace='ISBN']"/>
	        	</xsl:otherwise>
        	</xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>&#x9;</xsl:text>
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
      <xsl:text>&#xA;</xsl:text>
   </xsl:template>

   
   <xsl:template name="extract_year">
   	  <xsl:param name="date"/>
      <xsl:value-of select="substring($date,1,4)"/>
   </xsl:template>
    
    <xsl:template name="only_end">
    	<xsl:text disable-output-escaping="yes"><![CDATA[$obj->parsedDate(“>=”,]]></xsl:text>
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
	    	<xsl:when test="not(./CoverageStatement/EndVolume = '')">
	    		<xsl:value-of select="./CoverageStatement/EndVolume"></xsl:value-of>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:text>undef</xsl:text>
			</xsl:otherwise>
    	</xsl:choose>
    	<xsl:text>,</xsl:text>
    	<xsl:choose>
	    	<xsl:when test="not(./CoverageStatement/EndIssue = '')">
	    		<xsl:value-of select="./CoverageStatement/EndIssue"></xsl:value-of>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:text>undef</xsl:text>
			</xsl:otherwise>
    	</xsl:choose>
    	<xsl:text>)</xsl:text>
    </xsl:template>
    
    
    <xsl:template name="only_start">
    	<xsl:text disable-output-escaping="yes"><![CDATA[$obj->parsedDate(“>=”,]]></xsl:text>
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
	    	<xsl:when test="not(./CoverageStatement/StartVolume = '')">
	    		<xsl:value-of select="./CoverageStatement/StartVolume"></xsl:value-of>
	    	</xsl:when>
	    	<xsl:otherwise>
	    		<xsl:text>undef</xsl:text>
			</xsl:otherwise>
    	</xsl:choose>
    	<xsl:text>,</xsl:text>
    	<xsl:choose>
	    	<xsl:when test="not(./CoverageStatement/StartIssue = '')">
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

</xsl:stylesheet>