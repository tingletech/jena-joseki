PREFIX ex: <http://example.com/>

CREATE SILENT GRAPH <http://example.com/>

INSERT DATA INTO <http://example.com/> {
 		 ex:subj ex:pred 1 .
                 ex:subj ex:pred 2 .
             }

INSERT DATA {
 		 ex:subj ex:pred 1 .
                 ex:subj ex:pred 2 .
            }
