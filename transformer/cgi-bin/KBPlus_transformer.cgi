#!/home/cpan/bin/perl

use strict;
use warnings;                                                                                                                    
use diagnostics;

use XML::LibXML;
use XML::LibXSLT;
use Data::Dumper;
use CGI;
use CGI::Carp qw(warningsToBrowser fatalsToBrowser); # it is possible to make non-fatal errors appear as HTML comments embedded in the output of your program.

$CGI::POST_MAX = 1024 * 1024 * 20; #Limit post to 20MB
#warn "cpan XSLT lib version: ". XML::LibXSLT::LIBXSLT_DOTTED_VERSION();
#warn "LibXSLT version: ". XML::LibXSLT::LIBXSLT_RUNTIME_VERSION();

my $q = CGI->new;

print $q->header(
        -type    => 'text/plain',
	-charset => 'utf-8',
        );
my $params = $q->Vars;
my $xslt_path;
eval{ $xslt_path = $q->param('path');};
die "The path for the xslt file is not defined!" unless defined $xslt_path;

my $xml_received;
eval{ $xml_received = $q->upload( 'xml');};
die "The xml file is too big (the limit is 20MB) or its path is not defined!" unless defined $xml_received;

my $data;
{
	# consider all the file content as unique line
	local $/;
	$data = <$xml_received>;
}

my $parse = XML::LibXML->new(); 
my $xslt = XML::LibXSLT->new();
my $xml;
eval {$xml = $parse->parse_string($data);};
die "Error parsing the content of the xml -> $@" if $@;

my $input;
eval {$input = $parse->parse_file ("$xslt_path");};
die "Error parsing the xslt file (not valid XML) -> $@" if $@;

my $stylesheet;
eval{$stylesheet = $xslt->parse_stylesheet($input);};
die "Error parsing the stylesheet -> $@" if $@;

my $result;
my $output;
if (defined $stylesheet){ 
		$result = $stylesheet->transform($xml); 
		$output = $stylesheet->output_string($result);  
}


my $header = "Title\tISSN/ISBN\tType\tStatus\tDefault Dates\tCustom Date From\tCustom Date To\tTitle Id\tPublication date\tEdition\tPublisher\tPublic Note\tDisplay Public Note\tLocation Note\tDisplay Location Note\tDefault URL\tCustom URL\n";

print $header;
print $output; 

