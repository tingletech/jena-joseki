Joseki/RDF::Core Library
========================

    Andy Seaborne <andy.seaborne@hp.com>

Joseki <http://www.joseki.org> is a web server that allows queries to be
made on the RDF models it hosts.  Query languages include RDQL, "fetch"
(gets RDF data object subgraphs) and "triples", a really simple language.
See the Joseki web site for details.  Other languages can be added to a
server.

This package is a client library for Joseki that integrates with RDF::Core
Perl library for RDF.

Some examples of use are given.

perl-ex-1   Use the "fetch" operation to find out
            what a knowledge base knows about a resource

perl-ex-2   Get a whole model.

perl-ex-3   Use a simple query language to find statements with a
            given subject.  This illustrates a very simple query
            language but also that it is only suitable for some
            situations.  "fetch" or an RDQL query gets more deatils.

perl-ex-4   Find the book with a given title, then retrieve, using the
            "fetch" query language, details of the book.
            This is style of locating the items of interest and then 
            getting more details about them is a common pattern.
            The initial query itself is useful for finding resources
            by some identifiying property (e.g. an OWL inverse functional
            property).

perl-ex-5   RDQL query, returning a subgraph of the original
            knowledge base graph

perl-ex-6   RDQL query, getting a result set in RDF as the result
            Reconstruct the result set for programmatic use.


Dependencies:

HTTP::Request
LWP::UserAgent
RDF::Core
and their dependences
