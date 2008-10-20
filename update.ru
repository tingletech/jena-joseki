PREFIX : <http://example/>

INSERT DATA { :x :p :v }

INSERT { ?x :p [ :p 1 ] . }
WHERE { ?x :p :v }
