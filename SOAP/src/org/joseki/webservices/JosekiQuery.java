package org.joseki.webservices ;

public interface JosekiQuery
{
	public void query(String queryString, 
					  String defaultGraphURI, 
					  String[] namedGraphs) ;
}
