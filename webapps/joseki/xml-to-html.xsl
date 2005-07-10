<?xml version="1.0" encoding="utf-8"?>

<!-- Derived from ... -->
<!--

  XSLT script to format SPARQL Query Results XML Format as xhtml

  Copyright &copy; 2004 World Wide Web Consortium, (Massachusetts
  Institute of Technology, European Research Consortium for
  Informatics and Mathematics, Keio University). All Rights
  Reserved. This work is distributed under the W3C&reg; Software
  License [1] in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.

  [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231

-->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:res="http://www.w3.org/2005/06/sparqlResults"
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
    <title>SPARQLer Query Results</title>
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

      <xsl:text>
</xsl:text>
    <table style="border-collapse: collapse ; border: 1px solid black">
      <tr>
	<xsl:for-each select="res:head/*">
	  <th><xsl:value-of select="@name"/></th>
	</xsl:for-each>
      </tr>
      <xsl:text>
      </xsl:text>

  <xsl:for-each select="res:results/res:result"> 
<xsl:text>
    </xsl:text>
<tr>
<xsl:text>
    </xsl:text>
    <xsl:call-template name="result" />
</tr>
<xsl:text>
    </xsl:text>
  </xsl:for-each>

    </table>


  </body>
</html>

  </xsl:template>


  <xsl:template name="result">
    <xsl:for-each select="res:binding"> 
     <xsl:variable name="name" select="@name" />
     <xsl:text>
      </xsl:text>
     <td>
        <xsl:comment>
          <xsl:text> </xsl:text>
          <xsl:value-of select="$name" />
          <xsl:text> </xsl:text>
	</xsl:comment>
          <xsl:text> </xsl:text>

	<xsl:choose>

	  <xsl:when test="res:bnode/text()">
	    <!-- blank node value -->
	    <xsl:text>_:</xsl:text>
	    <xsl:value-of select="res:bnode/text()"/>
	  </xsl:when>

	  <xsl:when test="res:uri">
	    <xsl:variable name="uri" select="res:uri/text()"/>
	    <xsl:text>&lt;</xsl:text>
	    <xsl:value-of select="$uri"/>
	    <xsl:text>&gt;</xsl:text>
	  </xsl:when>

	  <xsl:when test="res:literal/@datatype">
	    <!-- datatyped literal value -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="res:literal/text()"/>
	    <xsl:text>"^^&lt;</xsl:text>
	    <xsl:value-of select="res:literal/@datatype"/>
	    <xsl:text>&gt;</xsl:text>
	  </xsl:when>

	  <xsl:when test="res:literal/@xml:lang">
	    <!-- lang-string -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="res:literal/text()"/>
	    <xsl:text>"@</xsl:text>
	    <xsl:value-of select="res:literal/@xml:lang"/>
	  </xsl:when>

	  <xsl:when test="res:unbound">
	    <!-- unbound -->
	  </xsl:when>

	  <!-- Assumes string does not contain "-->

	  <!-- Plain literal -->
	  <xsl:when test="string-length(res:literal/text()) != 0">
	    <!-- present and not empty -->
	    <xsl:text>"</xsl:text>
	    <xsl:value-of select="res:literal/text()"/>
	    <xsl:text>"</xsl:text>
	  </xsl:when>

	  <xsl:when test="string-length(res:literal/text()) = 0">
	    <!-- present and empty -->
	    <xsl:text>""</xsl:text>
	  </xsl:when>

	  <xsl:otherwise>
	    [[unknown]]
	  </xsl:otherwise>

	</xsl:choose>
     </td>
    <xsl:text>
</xsl:text>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
