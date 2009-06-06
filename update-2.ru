PREFIX : <http://example.com/>

CLEAR

INSERT DATA
{
  :x :p 1 . 
  :x :p 2 . 
  :x :p 3 . 
  :x :p 4 . 
}

INSERT { ?x :q ?v } WHERE { ?x :p ?v }

MODIFY
DELETE  { ?x :p ?v }
INSERT  { ?x :pp ?v }
WHERE   { ?x :p ?v }

DELETE { ?x :q ?v } WHERE { ?x :q ?v }
DELETE { ?x :pp ?v } WHERE { ?x :pp ?v . FILTER(?v != 1) }

## ---- Named graph

DROP SILENT GRAPH <http://example.com/g>
CREATE GRAPH <http://example.com/g>

INSERT INTO <http://example.com/g>
{
  :x :p 1 . 
  :x :p 2 . 
} WHERE {}

MODIFY GRAPH <http://example.com/g>
DELETE  { ?x :p ?v }
INSERT  { ?x :pp ?v }
WHERE   { ?x :p ?v }

CREATE GRAPH <http://example.com/g2>

INSERT DATA INTO <http://example.com/g2>
{
  :x :p 1 . 
  :x :p 2 . 
}

CLEAR GRAPH <http://example.com/g2>
