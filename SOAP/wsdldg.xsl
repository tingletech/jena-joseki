<xsl:transform version="1.0"
	       xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

	       xmlns:wsd2="http://www.w3.org/2004/08/wsdl"
	       xmlns:whttp="http://www.w3.org/2004/08/wsdl/http"

	       xmlns:wsd1="http://schemas.xmlsoap.org/wsdl/"
	       xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
	       >

  <xsl:output method="xml" indent="yes" />

  <xsl:template match="wsd2:description">
    <wsd1:definitions>
      <!-- copy attributes such as targetNamespace over -->
      <xsl:for-each select="@*">
	<xsl:attribute name='{name()}'>
	  <xsl:value-of select='.'/>
	</xsl:attribute>
      </xsl:for-each>

      <!-- KLUDGE: copy namespaces over -->
      <xsl:for-each select="namespace::*">
	<xsl:variable name="nsname" select="." />
	<xsl:variable name="pfx" select="name(.)" />
	<xsl:if test='$pfx and $pfx != "xml"'>
	  <xsl:message>@@found ns: <xsl:value-of select="$pfx" /> -> <xsl:value-of select="$nsname" /></xsl:message>
	  <xsl:attribute name="{$pfx}:kludge" namespace="{$nsname}">kludge</xsl:attribute>
	</xsl:if>
      </xsl:for-each>

      <xsl:apply-templates />

    </wsd1:definitions>
  </xsl:template>

  <xsl:template match="wsd2:interface">
    <wsd1:portType name="{@name}">
      <xsl:apply-templates />
    </wsd1:portType>
  </xsl:template>

  <xsl:template match="wsd2:interface/wsd2:operation">
    <wsd1:operation name="{@name}">
      <xsl:if test="@pattern">
	<xsl:message>ignoring pattern <xsl:value-of select="@pattern" />
	on operation <xsl:value-of select="@name" />
	</xsl:message>
      </xsl:if>
      <xsl:if test="@style">
	<xsl:message>ignoring style <xsl:value-of select="@style" />
	on operation <xsl:value-of select="@name" />
	</xsl:message>
      </xsl:if>
      
      <xsl:apply-templates />
    </wsd1:operation>
  </xsl:template>

  <xsl:template match="wsd2:types">
    <wsd1:types>
      <xsl:for-each select="*">
	<xsl:copy-of select="." />
      </xsl:for-each>
    </wsd1:types>
  </xsl:template>
  

  <xsl:template match="wsd2:interface/wsd2:operation/wsd2:input">
    <xsl:variable name="elty" select="@element" />
    <xsl:if test="@messageLabel">
      <xsl:message>ignoring messageLabel <xsl:value-of select="@messageLabel" />
      on operation <xsl:value-of select="../@name" />
      </xsl:message>
    </xsl:if>
    <wsd1:input message="{$elty}" />
  </xsl:template>

  <xsl:template match="wsd2:interface/wsd2:operation/wsd2:output">
    <xsl:variable name="elty" select="@element" />
    <xsl:if test="@messageLabel">
      <xsl:message>ignoring messageLabel <xsl:value-of select="@messageLabel" />
      on operation <xsl:value-of select="../@name" />
      </xsl:message>
    </xsl:if>
    <wsd1:output message="{$elty}" />
  </xsl:template>


  <xsl:template match="wsd2:binding">
    <xsl:choose>
      <xsl:when test='@type = "http://www.w3.org/2004/08/wsdl/http"'>
	<!-- ok -->
      </xsl:when>
      <xsl:when test='@type = "http://www.w3.org/2004/08/wsdl/soap"'>
	<!-- ok -->
      </xsl:when>
      <xsl:otherwise>
	<xsl:message>unknown binding type <xsl:value-of select="@type" />
	</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
    
    <xsl:variable name="iface" select="@interface" />

    <wsd1:binding name="{@name}"
		  type="{$iface}">
      
      <xsl:if test="wsd2:operation/@whttp:method">
	<http:binding verb="{wsd2:operation/@whttp:method}" />
      </xsl:if>
      
      <xsl:for-each select="wsd2:operation">
	<!-- @@collapse all namespaces -->
	<xsl:variable name="op" select='substring-after(@ref, ":")' />

	<xsl:message>@@looking for op <xsl:value-of select="$op" /> on interface <xsl:value-of select="$iface" />.
	</xsl:message>


	<wsd1:operation name="{$op}">
	  <xsl:apply-templates />
	  
	  <xsl:if test='/wsd2:description/wsd2:interface[@name=$iface]/wsd2:operation[@name=$op]/@style = "http://www.w3.org/2004/08/wsdl/style/uri"'>
	    <wsd1:input>
	      <http:urlEncoded/>
	    </wsd1:input>
	  </xsl:if>
	</wsd1:operation>
      </xsl:for-each>
    </wsd1:binding>
  </xsl:template>

  <xsl:template match="*">
    <xsl:message>@@not matched: <xsl:value-of select="name()"/> in <xsl:value-of select="name(..)"/>
    </xsl:message>
  </xsl:template>

  <!-- don't pass text thru -->
  <xsl:template match="text()|@*">
  </xsl:template>


</xsl:transform>