package Joseki::ResultBinding ;

# The bindings of a single solution in a ResultSet

require Joseki::ResultSet ;
require RDF::Core ;
require RDF::Core::Model ;
require RDF::Core::Resource ;
require RDF::Core::Literal ;

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my ($model, $resource) = @_ ;

    my $self = {};
    my %binding = () ;
    $self->{bindings} = \%binding ;
    $self->{posn} = -1 ;
    bless $self, $class; 
    $self->_parse($model, $resource) if ( defined($resource) ) ;

    return $self ;
}

sub add {
    my($self, $var, $value) = @_ ;
    $self->{bindings}->{$var} = $value ;
}

sub get {
    my($self, $var) = @_ ;
    return $self->{bindings}->{$var} ;
}

sub vars { 
    my($self) = @_ ;
    return keys(%{$self->{bindings}}) ;
}

our $RS='http://jena.hpl.hp.com/2003/03/result-set#' ;
our $rBinding   = new RDF::Core::Resource($RS.'binding') ;
our $rVariable  = new RDF::Core::Resource($RS.'variable') ;
our $rValue     = new RDF::Core::Resource($RS.'value') ;


sub _parse {
    my($self,$model, $resource) = @_ ;

    my %binding = () ;

    my $sIter = $model->getStmts($resource, $rBinding, , undef) ;
    my $stmt = $sIter->getFirst ;
    for(; defined $stmt ; $stmt = $sIter->getNext )
    {
	my $b = $stmt->getObject ;
	my $var = _value($model, $b, $rVariable)->getValue ;
	# Value is a RDF::Core object (Resource or Literal).
	my $val = _value($model, $b, $rValue) ;
	## print "[ $var = ",$val->getLabel," ]\n" ;
	$self->add($var, $val) ;
    }
    $sIter->close ;
}

sub _value
{
    my($model,$subj ,$prop) = @_ ;
    my $sIter = $model->getStmts($subj, $prop, undef) ;
    my $stmt = $sIter->getFirst ;
    $sIter->close ;
    return $stmt->getObject ;
}

1;
