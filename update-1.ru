## Management operations

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
