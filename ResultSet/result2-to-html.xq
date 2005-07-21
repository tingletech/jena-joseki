xquery version "1.0";
(:

  XQuery script to format SPARQL Variable Results XML Format as xhtml

  Copyright © 2004 World Wide Web Consortium, (Massachusetts
  Institute of Technology, European Research Consortium for
  Informatics and Mathematics, Keio University). All Rights
  Reserved. This work is distributed under the W3C® Software
  License [1] in the hope that it will be useful, but WITHOUT ANY
  WARRANTY; without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.

  [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231

:)


declare namespace res="http://www.w3.org/2005/06/sparqlResults";
declare namespace default="http://www.w3.org/1999/xhtml";


(: How to set serialization parameters? :)

(: doctype-system = "-//W3C//DTD XHTML 1.0 Transitional//EN" :)
(: doctype-public = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> :)


declare variable $variableNames {
   for $element in //res:sparql/res:head/res:variable
     return string($element/@name)
};


document {
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>SPARQL Query Results to XHTML table (XQuery)</title>
  </head>
  <body>
    <h1>SPARQL Query Results to XHTML table (XQuery)</h1>

    <p>Ordered: { let $v := //res:results/@ordered return text { $v } } </p>
    <p>Distinct: { let $v := //res:results/@distinct return text { $v } } </p>

    <table border="1">
<tr> {
  for $name in $variableNames
    return <th>{$name}</th>
} </tr>


{ for $result in //res:sparql/res:results/*
    return
<tr> {
  for $name in $variableNames 
    let $item := $result/res:binding[@name = $name]
    return  
	<td> {
	  if ($item/res:bnode) then
	     (: blank node value :)
	     text { "nodeID", string($item/res:bnode/text()) }
	  else if ($item/res:uri) then 
	     (: URI value :)
	     text { "URI", string($item/res:uri/text()) }
	  else if ($item/res:literal/@datatype) then 
	     (: datatyped literal value :)
	     text { $item/res:literal/text(), "(datatype", string($item/res:literal/@datatype), ")" }
	  else if ($item/res:literal/@xml:lang) then 
	     (: lang-string :)
	     text { $item/res:literal/text(), "@", string($item/res:literal/@xml:lang) }
	  else if ($item/res:literal/res:unbound) then 
	     (: unbound variable - empty cell :)
	     "[unbound]"
	  else if ( exists($item/res:literal/text()) ) then
	     (: present and not empty :)
	     $item/res:literal/text()
	  else if ( exists($item/res:literal) ) then
	     (: present and empty :)
	     "[empty literal]" 
	  else
	     (: unbound variable - empty cell :)
	     "[unbound]"
	 } </td>
  } </tr>

}

    </table>

  </body>
</html>
}    
