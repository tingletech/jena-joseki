package Joseki::ResultSet ;

require Joseki::ResultBinding ;
require RDF::Core ;
#require RDF::Core::Model ;
require RDF::Core::Resource ;
require RDF::Core::Literal ;

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my ($model) = @_ ;

    my $self = {};
    my $r = bless $self, $class; 
    $self->_model2rs($model) ;
    $self->reset() ;
    return $r ;
}

sub vars {
    my($self) = @_ ;
    return $self->{vars} ;
}


our $RS='http://jena.hpl.hp.com/2003/03/result-set#' ;

our $rResultVar = new RDF::Core::Resource($RS.'resultVariable') ;
our $rSolution  = new RDF::Core::Resource($RS.'solution') ;

our $rBinding   = new RDF::Core::Resource($RS.'binding') ;
our $rVariable  = new RDF::Core::Resource($RS.'variable') ;
our $rValue     = new RDF::Core::Resource($RS.'value') ;

# Create the array of solutions
sub _model2rs
{
    my ($self, $model) = @_ ;
    $self->{vars} = [] ;
    $self->{bindings} = [] ;

    my $sIter = $model->getStmts(undef, $rResultVar, , undef) ;
    my $stmt = $sIter->getFirst; 
    for(; defined $stmt ; $stmt = $sIter->getNext )
    {
	my $rv = $stmt->getObject->getValue ;
	## print "Result Variable: ",$rv,"\n" ;
	push(@{$self->{vars}}, $rv) ;

    }
    $sIter->close ;
    # print "Len: ",$#{$self->{vars}},"\n" ;

    my $sIter = $model->getStmts(undef, $rSolution, , undef) ;
    my $stmt = $sIter->getFirst ;
    for(; defined $stmt ; $stmt = $sIter->getNext )
    {
	my $rb = $stmt->getObject ;
	my $b = new Joseki::ResultBinding($model, $rb) ;
	push (@{$self->{bindings}}, $b) ;
    }
}

# Iteration

sub iter
{
    &reset ;
}

sub hasNext
{
    my ($self) = @_ ;
    return $self->{posn} <= $#{$self->{bindings}} ;
}

sub next
{
    my($self) = @_ ;
    my $i = $self->{posn}++ ;
    # Return a ref to the ResultBinding
    return $self->{bindings}->[$i] ;
}

sub _value
{
    my($model,$subj ,$prop) = @_ ;
    my $sIter = $model->getStmts($subj, $prop, undef) ;
    my $stmt = $sIter->getFirst ;
    return $stmt->getObject ;
}

sub reset
{
    my ($self) = @_ ;
    $self->{posn} = 0 ;
}
