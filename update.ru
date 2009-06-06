PREFIX ex: <http://example.com/>

CREATE GRAPH <http://example.com/>

## Graph creation maybe a no-op
## CREATE GRAPH <http://example.com/>
CREATE SILENT GRAPH <http://example.com/>

INSERT DATA INTO <http://example.com/> {
 		 ex:subj ex:pred 1 .
                 ex:subj ex:pred 2 .
            }
INSERT DATA INTO <http://example.com/notThere> {
                 ex:subj ex:pred 1 .
                 ex:subj ex:pred 2 .
            }
DELETE DATA FROM <http://example.com/> {
                 ex:subj ex:pred 1 .
            }

CLEAR GRAPH <http://example.com/notThere>
CLEAR GRAPH <http://example.com/notThere2>

DROP GRAPH <http://example.com/>

DROP GRAPH <http://example.com/notThere3>
## -----

## LOAD <data-defaultGraph.ttl> 
# MODIFY





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
