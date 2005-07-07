package org.joseki.webservices ;

public interface JosekiQuery
{
	public String query(String queryString, 
						String defaultGraphURI, 
						String[] namedGraphs) ;
}
