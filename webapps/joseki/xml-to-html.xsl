<?xml version="1.0" encoding="iso-8859-1"?>

<!--
  Original version:

  XSLT script to format SPARQL Variable Results XML Format as xhtml

  Copyright © 2004 World Wide Web Consortium, (Massachusetts
  Institute of Technology, European Research Consortium for
  Informatics and Mathematics, Keio University). All Rights
  Reserved. This work is distributed under the W3C® Software
  License [1] in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.

  [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231

  Modified: Andy Seaborne @ hp.com
  Much help from Damian Steer / Thanks!
-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:res="http://www.w3.org/2001/sw/DataAccess/rf1/result"
  exclude-result-prefixes="res xsl">

  <!--
  <xsl:output
    method="html"
    media-type="text/html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    indent="yes"
    encoding="UTF-8"/>
  -->

  <!-- or this? -->

  <xsl:output
    method="xml" 
    indent="yes"
    encoding="UTF-8" 
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    omit-xml-declaration="no" />


  <xsl:template match="res:sparql">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
	<title>SPARQLer Results</title>
	<style>
	  <![CDATA[
          td, th { border: 1px solid black ;
                   padding-left:0.5em; padding-right: 0.5em; 
                   padding-top:0.2ex ; padding-bottom:0.2ex }
	  ]]>
	  </style>
      </head>
      <body>
	
	<h1>SPARQLer Results</h1>
    
	<table style="border-collapse: collapse ; border: 1px solid black">
	  <tr>
	    <xsl:for-each select="res:head/*">
	      <th><xsl:value-of select="@name"/></th>
	    </xsl:for-each>
	  </tr>

	  <xsl:for-each select="res:results/res:result"> 
	    <tr>
	      <xsl:call-template name="result" />
	    </tr>
	  </xsl:for-each>
    </table>
    
      </body>
    </html>
    
  </xsl:template>


  <xsl:template name="result">
    <xsl:for-each select="./*"> 
     <xsl:variable name="name" select="local-name()" />
     <xsl:text>
      </xsl:text>
     <td>
	<xsl:choose>

	  <xsl:when test="@bnodeid">
	    <!-- blank node value -->
	    <xsl:text>_:</xsl:text><xsl:value-of select="@bnodeid"/>
	  </xsl:when>

	  <xsl:when test="@uri">
	    <!-- URI value -->
	    <xsl:variable name="uri" select="@uri"/>
	    <xsl:text>&lt;</xsl:text>
	    <xsl:value-of select="$uri"/>
	    <xsl:text>&gt;</xsl:text>
	  </xsl:when>

	  <xsl:when test="@datatype">
	    <!-- datatyped literal value -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="text()"/>
	    <xsl:text>"^^</xsl:text>
	    <xsl:value-of select="@datatype"/>
	  </xsl:when>

	  <xsl:when test="@xml:lang">
	    <!-- lang-string -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="text()"/>
	    <xsl:text>"@</xsl:text><xsl:value-of select="@xml:lang"/>
	  </xsl:when>

	  <xsl:when test="@bound='false'">
	    - - - -
	  </xsl:when>

	  <xsl:otherwise>
	    <!-- Literal - no lang tag or datatype URI -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="text()"/>
	    <xsl:text>"</xsl:text>
	  </xsl:otherwise>

       </xsl:choose>
      </td>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
