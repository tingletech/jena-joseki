#!/bin/env python

# (c) Copyright 2003, Hewlett-Packard Development Company, LP
# [See http://www.joseki.org/license.html]

# Result Set.
#

from sys import stderr
from types import *

__version__ = "0.6"
debug=0

# A better name for the "wildcard" in a triple match
ANY = None

from rdflib.URIRef import URIRef
from rdflib.BNode import BNode
from rdflib.Literal import Literal

from rdflib.BNode import BNode
from rdflib.Namespace import Namespace
from rdflib.constants import TYPE
from rdflib.TripleStore import TripleStore

RS=Namespace("http://jena.hpl.hp.com/2003/03/result-set#")


class ResultBinding:

    def __init__(self):
        self.map = {}

    def __getitem__(self, key):

        if self.map.has_key(key):
            return self.map[key]
        
        if isinstance(key, StringType):
            key = unicode(key)
            if self.map.has_key(key):
                return self.map[key]

        if isinstance(key, UnicodeType):
            key=key.encode()
            if self.map.has_key(key):
                return self.map[key]

        return None

        
    
    def __setitem__(self, key, val):
        self.map[key] = val

    def __str__(self):
        tmp=""
        for k in self.map.keys():
            tmp = tmp+" "+k+"="+self.map[k]
        return tmp
    def vars(self):
        return self.map.keys()


class ResultSet:

    def __init__(self, model):
        self.bindings = []
        self.myVars = [] 

        for s, p, o in model.triples((ANY,RS['resultVariable'],ANY)):
            self.myVars.append(o)

        for s, p, o in model.triples((ANY,RS['solution'],ANY)):
            rb = ResultBinding()
            i = 0
            for s1, p1, o1 in model.triples((o,RS['binding'],ANY)):
                i = i+1

                # RDFlib v2
                # Must be a neater way ... 
                for o2 in model.objects(o1, RS['value']):
                    val = o2
                for o2 in model.objects(o1, RS['variable']):
                    var = o2

                # RDFLib v1.
##                 x = model[o1]
##                 val=x[RS['value']]
##                 var=x[RS['variable']]
                if not var in self.myVars:
                    print >>stderr,"Warning: undeclared variable '"+var+"'"
                rb[var]=val
            if i != len(self.myVars):
                print >>stderr, "Wrong number of vars (was "+\
                                 str(i)+" wanted "+str(len(self.myVars))+")"
            self.add(rb)

    def add(self,b):
        self.bindings.append(b)
        
    def iter(self):
        for x in self.bindings:
            yield x

    def __str__(self):
        tmp = ""
        for x in self.bindings:
            tmp = tmp+str(x)+" "
        return tmp

    def size(self):
        return len(self.bindings)

    def vars(self):
        return self.myVars
