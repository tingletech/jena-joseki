package Joseki::Response ;

require HTTP::Request ;
require LWP::UserAgent ;
require URI::Escape ;
require RDF::Core ;
require Carp;

# Inherit from HTTP::Response?

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;   
    my ($josekiRequest, $httpResponse) = @_ ;
    my $self = {};
    $self->{response} = $httpResponse ;
    $self->{request} = $josekiRequest ;
    $self->{model} = undef ;
    $self->{verbose} = 0 ;
    return bless $self, $class;
}

sub request { return $_[0]->{request} ; }

sub content { return $_[0]->{response}->content ; }

sub is_success { return $_[0]->{response}->is_success ; }


sub model
{
    my ($self) = @_ ;
    return $self->{model} if ( defined($self->{model}) ) ;
    return undef if ( !$self->is_success ) ;
    $self->{model} = _strToModel($self->content) ;
    return $self->{model} ;
}

sub _strToModel
{
    my($str) = @_ ;

    my $storage = new RDF::Core::Storage::Memory;
    my $model = new RDF::Core::Model (Storage => $storage);

    my %options = (Model => $model,
		   SourceType => 'string',
		   Source => $str,
		   BaseURI=>"urn:x-unset#"
		   ) ;

    my $parser = new RDF::Core::Model::Parser(%options);
    $parser->parse;
    return $model ;
}


1;
