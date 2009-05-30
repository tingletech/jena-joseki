PREFIX ex: <http://example.com/>

            INSERT DATA { ex:subj ex:pred "unittest world 3" . }
            
            CREATE GRAPH <http://example.com/>
            INSERT DATA INTO <http://example.com/> {
                 ex:subj ex:pred "unittest world" .
            }
            CREATE GRAPH <http://example.com/g2>
            INSERT DATA INTO <http://example.com/g2> {
                 ex:subj ex:pred "unittest world 2" .
            }
## 
## PREFIX : <http://example/>
## 
## # Only works if the target is backed by a storage
## # system that handles named graphs as a unit (so not an immutable ad hoc in-memory dataset).
## 
## ## INSERT DATA INTO :direct { :x :p 123 }
## 
## ## INSERT DATA { :x :p :v }
##  
## ## INSERT { ?x :p [ :p 1 ] . }
## ## WHERE { ?x :p :v }
## 
## CREATE SILENT GRAPH <http://example.org/graph1>
## INSERT DATA INTO <http://example.org/graph1> { :x :p 123 }
## INSERT DATA { :x :p 456 }
## 
## #LOAD <http://moustaki.org/foaf.rdf> INTO <http://example.org/graph1>
## 
## 
