This directory contains some examples of common HTTP client programs
accessing a Joseki server.

Run the server
==============

The server configuration file is Examples/joseki-examples.n3

Either run the script ex-run-server or run:


    cd <joseki installation diretory>
    java -cp <all joseki lib/*.jar> joseki.rdfserver \
        Examples/joseki-examples.n3



Examples
========

ex-wget   Get the whole example model with a plain HTTP GET (uses wget)

ex-wget2  As above, except asking for a particular RDF syntax

ex-wget3  Ask the server about "book3"
          Returns server decided RDF statements

ex-cwm    Print the titles of two of the books
          Run with "http_proxy= cwm ex-cwm --think --strings"

ex-rdql   Ask an RDQL query using the Joseki command line RDQL application

ex-fetch  Fetch RDF using the Joseki command line fetch application
