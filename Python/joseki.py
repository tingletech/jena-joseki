# (c) Copyright 2003, Hewlett-Packard Development Company, LP
# [See http://www.joseki.org/license.html]

import types
import urllib
import urllib2

from rdflib.URIRef import URIRef
from rdflib.BNode import BNode
from rdflib.Literal import Literal

## from rdflib.BNode import BNode
## from rdflib.Namespace import Namespace
## from rdflib.constants import TYPE
from rdflib.TripleStore import TripleStore

__version__ = "0.6"

debug=0

# Convenience operations: need to control the syntax format

def queryRDQL(target, queryStr):
    if debug:
        print "RDQL: "+queryStr
    return query(target, "RDQL", q=queryStr)


def get(target):
    request = JosekiRequest(target)
    return request.execute()


def fetch(target, resource):
    return query(target, "fetch", r=resource )


def triples(target, s, p, o):
    args = {}
    if s and not isinstance(s, BNode):
        args['s']=s
    if p and not isinstance(p, BNode):
        args['p']=p
    if o:
        if isinstance(o, URIRef):
            args['o'] = o
        elif isinstance(o, (Literal, str)):
            args['v'] = o
    return query(target, "Triples", **args)


# Main function ...

def query(target, lang, **args):
    op = JosekiRequest(target, lang=lang, **args)
    return op.execute()

# A request object - can be passed around.

class JosekiRequest:
    """Joseki objects are a single request (query) of a remote Joseki server
    """

    def __init__(self, url, lang=None, **args):
        self.remoteModel = url
        self.queryLang = lang
        self.queryArgs = args          # Map of name/value pairs
        self.httpHeaders = {}          # Any additional HTTP stuff

    def execute(self):
        if self.queryLang :
            url = self.remoteModel+'?lang='+self.queryLang
        else:
            url = self.remoteModel
            
        if self.queryArgs:
            for a in self.queryArgs.keys():
                ## Need to hex-encode ...
                url = url+'&'+a+'='+urllib.quote(self.queryArgs[a])
        model = TripleStore()
        if debug:
            print "URL:", url
        model.load(url)
        return model
