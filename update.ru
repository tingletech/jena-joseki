PREFIX : <http://example/>

# Only works if the target is backed by a storage
# system that handles named graphs as a unit (so not an immutable ad hoc in-memory dataset).

## INSERT DATA INTO :direct { :x :p 123 }

INSERT DATA { :x :p :v }

INSERT { ?x :p [ :p 1 ] . }
WHERE { ?x :p :v }
