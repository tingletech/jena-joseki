Joseki: The Jena RDF Server
===========================

    http://www.joseki.org/

	Andy Seaborne
	andy.seaborne@hp.com
	May 2004
    
This is the version 3.0 release of Joseki - a server for publishing RDF
models on the web. Models have URLs and they can be accessed by HTTP GET.

Joseki is part of the Jena RDF framework.  Joseki is open source under a
BSD-style license.

Joseki3 implements the protocol recommendations of W3C RDF Data Access
Working Group (DAWG).  It contains an implementation of SPARQL.

Working Group home page:
http://www.w3.org/2001/sw/DataAccess/

Documents:
<to be added>

Joseki3 provides SPARQL as the protocol and the main query
langauge. Joseki3 is not compatible with Joseki2. 

The differences in architecture aren't huge but there are many minor
differences but the objective of Joseki3 is to provide all the new features
of SPARQL.

The principle query lanaguage Joseki3 is 





Porting from Joseki2
====================

Client Libraries
----------------


Direct use of the HTTP protocol
-------------------------------






------------------------------------------------
OLD TEXT


Query Languages
---------------

As well as plain HTTP GET, the Joseki distribution provides RDQL, "Fetch",
and a minimal query language "SPO".

RDQL: RDQL is an SQL-like query language that provides matching a graph
pattern, with named variables, against an RDF graph.  The result of all
Joseki operations is an RDF graph - typically this is the subgraph matched
by the query (i.e. exeuting the query locally on the result subgraph gives
the same varioable bindings).

    See: http://www.w3.org/Submission/2004/SUBM-RDQL-20040109/
         http://jena.sourceforge.net/tutorial/RDQL/

Fetch: The "fetch" query operation provides a "tell me about <uri>"
operation.  It is a way for applications to get data objects (small RDF
graphs) of RDF statements that are considered to be about the named
resource.  The exact form of the returned graph is determined by the server
through a data-specific module.  For example, if a resource has Dublin Core
information about it, the server would return all the statements with the
resource as subject.

    See: http://www.joseki.org/RDF_Data_Objects.html

Inference Models
----------------

Joseki hosts models provided by Jena2, including inferencing models: data
can be combined with an OWL ontology or RDFS vocabulary description to
produce an RDF model that has both ground and inferred statements.  Query
is then used to access the model.

Extensibility
-------------

New query languages and new adapters to data sources can be written and
configured without needing to change the source code. Data sources are not
restricted to being Jena models.

    http://www.joseki.org/processors.html

Client Libraries
----------------

The distribution includes client libraries for Java, Python (using
RDFlib http://rdflib.net) and Perl (using RDF::Core)

The download
------------

The main directory is Joseki-<version>.  Servers run in this directory to
find auxiliary files.

The main sub-directories are:

    + Examples/  - example server and client scripts
    + lib/       - JARs needed
    + bin/       - scripts (check before use)
    + etc/       - configuration and various support files
    + doc/       - a copy of the online documentation
    + webapps/   - Joseki is a web application
    + Python/    - The Python client library
    + Perl/      - The Perl client library


Running the Examples
--------------------

A simple server can be run by:

1/ Add all the JARs in lib/ to the CLASSPATH
2/ Run
   java -cp <classpath> joseki.rdfserver Examples/joseki-examples.n3

There are scripts in bin/ and Examples/ to help.

3/ Try the examples: see Examples/
   and also the scripts in the
   Python/ and Perl/ directories.


For more details on configuring and running a server, see:

Quick Guide:
http://www.joseki.org/publishing.html

Server configuration:
http://www.joseki.org/running.html

Details of the configuration file:
http://www.joseki.org/configuration.html


Documentation
-------------

The website http://www.joseki.org/ describes Joseki and provides
documentation on server configuration and on the client library.  It also
includes examples of access using common, unmodified applications like
wget.  Details about the use of HTTP GET, how to create query language
processors and how to add modules for data-specific fetch operations can
also be found on the web site.  A copy of the web site is in the doc/
directory of the download.


Support
-------

Please send questions to jena-dev@groups.yahoo.com


Development
-----------

The Joseki development project is hosted by SourceForge:

Project:   http://sourceforge.net/projects/joseki
CVS:       http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joseki/

It relies on other open source software: 

    RDFLib, Jetty, Jena (which uses Xerces,
    Log4J, ORO, util.concurrent), JUnit

