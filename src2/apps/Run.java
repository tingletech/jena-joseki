
package apps;
//import joseki.rdfserver;
import java.net.* ;

import org.joseki.server.http.HttpContentType;

public class Run
{

    public static void main(String[] args)
    {
        Run_rdfserver.main(null) ;
        System.exit(0) ;
        
        return ;
        
        //String a[] = {} ;
        //rdfserver.main(a) ;

//        ct("  RDF/XML; charset=UTF-8" ) ;
//        ct("  RDF/XML" ) ;
//        ct("  RDF/XML;p=57;charset =UTF-8" ) ;
        
        
        //dwim() ;
    }
    
    public static void ct(String s)
    {
        HttpContentType ct = new HttpContentType(s) ;
        System.out.println() ;
        System.out.println("Input: "+s) ;
        System.out.println("Media type = "+ct.getMediaType()) ;
        System.out.println("Charset    = "+ct.getCharset());
        System.out.println("::"+ct.toString()+"::") ;
    }
    
    public static void dwim()
    {
        try {
        String s1 = "SELECT%20?x%20WHERE%20(?x,%20%3Chttp://www.w3.org/2001/vcard-rdf/3.0#FN%3E,%20%22John%20Smith%22)" ;
        System.out.println(s1) ;
        String t = URLDecoder.decode(s1, "UTF-8") ;
        String s2 = URLEncoder.encode(t, "UTF-8") ;
        System.out.println(t) ;
        System.out.println(s2) ;
        } catch(Exception e)
        {
            System.err.println(e) ;
        }
    }
}
