## To Do:
## Return a Result object with 
##   Model
##   String result
##   Return code
##   Return message


package Joseki;

require Joseki::Request;
require Joseki::Response;


# Functions
# Change to two kinds: fetch (returns a model) and fetchStr (returns a string)

sub rdql
{
    # Call as Joseki::rdql or Joseki->rdql
    my $pkg = shift if ( @_[0] eq 'Joseki' ) ;
    my ($target, $query) = @_ ;
    my $req = new Joseki::Request($target, 'RDQL', query=>$query  ) ;
    return _doRequest($req) ;
}

sub getModel
{
    my $pkg = shift if ( @_[0] eq 'Joseki' ) ;
    my ($target) = @_ ;
    my $req = new Joseki::Request($target) ;
    $req->setFormat($format) if defined($format) ;
    return _doRequest($req) ;
}

sub getStr
{
    my $pkg = shift if ( @_[0] eq 'Joseki' ) ;
    my ($target, $format) = @_ ;
    my $req = new Joseki::Request($target) ;
    $req->setFormat($format) if defined($format) ;
    my $resp = $req->execute() ;
    return $resp->content() ;
}

sub fetch
{
    my $pkg = shift if ( @_[0] eq 'Joseki' ) ;
    my ($target, $resourceURI ) = @_ ;

    $resourceURI = $resourceURI->getURI
	if ( $resourceURI->isa(RDF::Core::Resource) ) ;
    my $req = new Joseki::Request($target, 'fetch', r=>$resourceURI ) ;
    return _doRequest($req) ;
}

## sub fetchStr
## {
##     my ($pkg, $target, $resourceURI ) = @_ ;
## 
##     $resourceURI = $resourceURI->getURI
## 	if ( $resourceURI->isa(RDF::Core::Resource) ) ;
##     my $req = new Joseki::Request($target, 'fetch', r=>$resourceURI ) ;
##     $req->setFormat('application/n3') ;
##     my $resp = $req->execute() ;
##     print "Warning - problems"
## 	if ( ! $resp->is_success ) ;
##     print $resp->content ;
##     return $req->executeStr() ;
## }

sub triples
{
    ## Call as Joseki::triples or Joseki->triples
    my $pkg = shift if ( @_[0] eq 'Joseki' ) ;
    my ($target, $subj, $pred, $obj) = @_ ;

    my %a ;

    $a{s} = $subj->getURI
	if ( defined $subj ) ;

    $a{p} = $pred->getURI
	if ( defined $pred ) ;

    # obj can be:
    # Literal
    # (no) String (literal)
    # Resource

    $a{o} = $obj->getURI
	if ( defined($obj) && ref($obj) eq 'RDF::Core::Resource' ) ;

    $a{v} = $obj->getValue
	if ( defined($obj) && ref($obj) eq 'RDF::Core::Literal' ) ;

    $a{v} = $obj 
	if ( defined($obj) && ! ref($obj) ) ;

##     print "Triple debug\n" ;
##     for $k (keys(%a))
##     {
## 	print "triple arg: $k - ",$a{$k},"\n" ;
##     }

    
    my $req = new Joseki::Request($target, 'Triples', %a ) ;
    return _doRequest($req) ;
}     

sub _doRequest
{
    my ($req) = @_ ;
    ## $req->print ;
    my $resp = $req->execute() ;

    if ( ! $resp->is_success )
    {
	Carp::carp("Request to $req->{$url} failed\n") ;
	return undef ;
    }
    return $resp->model() ;
}

1;
