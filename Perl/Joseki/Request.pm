package Joseki::Request ;

require HTTP::Request ;
require LWP::UserAgent ;

require RDF::Core ;
require RDF::Core::Model::Parser ;
require RDF::Core::Storage::Memory ;

require Joseki::Response;
require Carp;


sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my ($url, $lang, %args) = @_ ;
    my $self = {};
    $self->{target} = $url ;
    $self->{lang} = $lang ;
    $self->{args} = \%args ;
    $self->{format} = 'application/rdf+xml' ;
    $self->{verbose} = 0 ;
##    $self->{response} = undef ;
    return bless $self, $class;
}


sub verbose
{
    my $self = shift;
    $self->{verbose} = shift ;
}

sub setFormat
{
    my $self = shift;
    $self->{format} = shift ;
}

# Return undef or the model

sub executeModel {
    my $resp = &execute ;
    return $resp->model
	if ( ! $resp->is_success ) ;
    return undef ;
}

# Return undef or the string

sub executeStr {
    my $resp = &execute ;
    return $resp->content
	if ( ! $resp->is_success ) ;
    return undef ;
}
    

sub execute
{
    my $self = shift;
    my $ua = LWP::UserAgent->new(env_proxy => 1) ;

    # Encode?  Or does this happen anyway?
    my $url = $self->{target} ;
    if ( defined($self->{lang} ) )
    {
	$url = $url."?lang=$self->{lang}" ;
	if ( defined($self->{args}) )
	{
	    my $args = $self->{args} ;
	    foreach $a (keys(%$args))
	    {
		$url = $url."&".$a."=".$$args{$a} ;
	    }
	}
    }
    print "URL = $url\n" if ( $self->{verbose} ) ;
	 
    my $request = HTTP::Request->new(GET => $url) ;
    $request->header( 'Accept' => $self->{format} ) ;

    my $response = 
	new Joseki::Response($request, $ua->request($request)) ;

    return $response ;

##     $self->{response} =  $ua->request($request) ;
##     my $result = undef ;
##     if ($self->{response}->is_success) {
## 	$result = $self->{response}->content;
##     } else {
## 	print "Error while getting ", $self->{response}->request->uri,
## 	" -- ", $self->{response}->status_line, "\n" 
## 	    if ( $self->{verbose} ) ;
## 	return undef ;
##     }
##     return $result ;
}


sub print
{
    my $self = shift ;
    my $target = $self->{target} ;
    my $lang = $self->{lang} ;
    my $args = $self->{args} ;
    my $ct =   $self->{format} ;

    $lang = '' if ( ! defined($lang) ) ;
    $args = {} if ( ! defined($args) ) ;
    print "Target: $target\n" ;
    print "Lang:   $lang\n" ;
    print "Format: $ct\n" ;
    print "Args:\n" ;
    foreach $a (keys(%$args))
    {
	print "    $a = $$args{$a}\n" ;
    }
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
    #print "Parsed!\n" ;
    return $model ;
}

1;
