#!/usr/bin/perl
# -----------------------------------------
# Program : lfmCMD.pl (last.fm Command Line Utility)
# Version : 1.0.0 - 2009-08-12
#
# Copyright (C) 2009 Klaus Tockloth
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Contact (eMail): <Klaus.Tockloth@googlemail.com>
#
# Further information:
# - last.fm API Documentation (Last.fm Ltd.)
#
# Test:
# - Windows-XP (by Klaus Tockloth with ActivePerl)
# - Linux (by last.fm user Linuxology on Fedora 11, by Klaus Tockloth on Ubuntu 9.04)
# - OS-X (by last.fm user JRoar)
# - checked with Perl::Critic (severity 2)
# -----------------------------------------

use warnings;
use strict;

# General
use English;
use File::Basename;

# last.fm
use LWP::UserAgent;
use URI::QueryParam;
use Encode;
use XML::Simple;
use Digest::MD5 qw(md5_hex);

# Debug
# use Data::Dumper;

my $EMPTY = q{};

my $lastfm_api_key = '0996e89f272c714ec0bd463ea17faf6c';
my $lastfm_api_secret = 'aced25823414f7398e60a0323eff1741';
my $lastfm_service_root = 'http://ws.audioscrobbler.com/2.0/';

my $lastfm_response = $EMPTY;
my $lastfm_xmlref = $EMPTY;

my %params = ();
my $rc = 0;

my $logfile_request = 'last.fm.Request.txt';
my $logfile_response = 'last.fm.Response.xml';

my $ua = $EMPTY;

# configuration defaults (overwritten by read_Program_Configuration())
use constant HTTP_TIMEOUT => 53;
my $httpTimeout = HTTP_TIMEOUT;
my $httpProxy = $EMPTY;
my $display_response = 0;
my $terminal_encoding = 'utf8';

my ($appname, $appdirectory, $appsuffix) = fileparse($0, qr/\.[^.]*/);

my $cfgfile = $appname . '.cfg';
my $cfgfile_found = 0;
read_Program_Configuration();

# set STDOUT to configured terminal encoding
binmode STDOUT, ":encoding($terminal_encoding)";

printf {*STDOUT} "\n$appname - last.fm Command Line Utility, Rel. 1.0.0 - 2009-08-12\n\n";

if ($#ARGV < 0) {
  show_help();
}

# copy the key/value pairs into a hash (enforce utf8 encoding)
for (my $i = 0; $i <= $#ARGV; $i++) {
  my ($key, $value) = split /=/, $ARGV[$i], 2;

  # encode all command line input parameters to 'utf8'
  if (lc $terminal_encoding eq 'utf8') {
    # data is already utf8 encoded (nothing to do)
  }
  else {
    $key = encode_utf8($key);
    $value = encode_utf8($value);
  }

  $params{$key} = $value;
}

# create an internet user agent
$ua = LWP::UserAgent->new;
$ua->agent('lfmCMD/1.0');
$ua->timeout($httpTimeout);
if ($httpProxy ne $EMPTY) {
  $ua->proxy('http', $httpProxy);
}

lastfm_request_service(\%params);

printf {*STDOUT} "http status ...: %s\n", $lastfm_response->status_line;
printf {*STDOUT} "last.fm status : %s\n", $lastfm_xmlref->{status};
# printf {*STDOUT} "Parsed xml     :\n%s\n", Dumper($lastfm_xmlref);

$rc = 0;
if (! $lastfm_response->is_success) {
  printf {*STDOUT} "error code ....: %s\n", $lastfm_xmlref->{error}->[0]->{code};
  printf {*STDOUT} "error content .: %s\n", $lastfm_xmlref->{error}->[0]->{content};
  $rc = 2;
}

if ($display_response) {
  printf {*STDOUT} "\n%s\n", $lastfm_response->decoded_content;
}

printf {*STDOUT} "\nlast.fm request  : See logfile \"%s\" for details.\n", $logfile_request;
printf {*STDOUT} "last.fm response : See logfile \"%s\" for details.\n", $logfile_response;

# return codes: 0=successful; 1=help screen; 2=not successful
exit $rc;


# -----------------------------------------
# Request a lastfm service - send http get/post request and decode xml response.
# -----------------------------------------
sub lastfm_request_service
{
  my %lfmParams = %{(shift)};

  my $lastfm_service_uri = $EMPTY;

  # open logfile for request
  open my $LOGFILE_REQUEST, '+>', $logfile_request or die "Error opening logfile \"$logfile_request\": $!\n";

  # open logfile for response
  open my $LOGFILE_RESPONSE, '+>:utf8', $logfile_response or die "Error opening logfile \"$logfile_response\": $!\n";

  printf {$LOGFILE_REQUEST} "last.fm request:\n----------------\n";
  printf {$LOGFILE_REQUEST} "Timestamp .....: %s (localtime)\n", scalar localtime;
  printf {$LOGFILE_REQUEST} "Timestamp .....: %s (gmtime)\n", scalar gmtime;
  printf {$LOGFILE_REQUEST} "Configuration .: cfgfile = %s (%s)\n", $cfgfile, ($cfgfile_found ? 'found' : 'not_found');
  printf {$LOGFILE_REQUEST} "Configuration .: display_response = %s\n", $display_response;
  printf {$LOGFILE_REQUEST} "Configuration .: terminal_encoding = %s (expected)\n", $terminal_encoding;
  printf {$LOGFILE_REQUEST} "Configuration .: httpProxy = %s\n", $httpProxy;
  printf {$LOGFILE_REQUEST} "Configuration .: httpTimeout = %s\n", $httpTimeout;
  printf {$LOGFILE_REQUEST} "System ........: OSNAME = %s\n", $OSNAME;
  printf {$LOGFILE_REQUEST} "System ........: PERL_VERSION = %s\n", $PERL_VERSION;

  # add "api_key" to "%params"
  $lfmParams{api_key} = $lastfm_api_key;

  # write all parameters to request logfile
  foreach my $key (sort keys %lfmParams) {
    my $value = $lfmParams{$key};
    printf {$LOGFILE_REQUEST} "Parameter .....: %s = %s\n", $key, $value;
  }

  # build the hash string
  my $hashstring = $EMPTY;
  foreach my $key (sort keys %lfmParams) {
    my $value = $lfmParams{$key};
    $hashstring .= $key . $value;
  }
  # add "api_secret" to hash string
  $hashstring .= $lastfm_api_secret;

  # calculate hash value and add "api_sig" to "%lfmParams"
  $lfmParams{api_sig} = md5_hex($hashstring);

  # build URI (last.fm request) (inclusive UTF8 escaping)
  $lastfm_service_uri = URI->new($lastfm_service_root);
  foreach my $key (sort keys %lfmParams) {
    my $value = $lfmParams{$key};
    $lastfm_service_uri->query_param($key, $value);
  }
  printf {$LOGFILE_REQUEST} "Service URI ...: %s\n", $lastfm_service_uri;

  # use "post" (instead of "get") if "sk" (session key) is given
  if (defined $lfmParams{sk}) {
    # last.fm write service
    $lastfm_response = $ua->post($lastfm_service_uri);
  }
  else {
    # last.fm read service
    $lastfm_response = $ua->get($lastfm_service_uri);
  }
  printf {$LOGFILE_RESPONSE} "%s", $lastfm_response->decoded_content;

  printf {$LOGFILE_REQUEST} "\nlast.fm response:\n-----------------\n";
  printf {$LOGFILE_REQUEST} "http status ...: %s\n", $lastfm_response->status_line;
  # printf {$LOGFILE_REQUEST} "\nheaders .......: \n%s\n", $lastfm_response->headers_as_string;

  # transfer xml response to perl data structure - force everything into arrays
  $lastfm_xmlref = XMLin($lastfm_response->decoded_content, ForceArray => 1);

  printf {$LOGFILE_REQUEST} "last.fm status : %s\n", $lastfm_xmlref->{status};
  # printf {*LOGFILE_REQUEST} "Parsed xml     : %s\n", Dumper($lastfm_xmlref);

  $rc = 0;
  if (! $lastfm_response->is_success) {
    printf {$LOGFILE_REQUEST} "error code ....: %s\n", $lastfm_xmlref->{error}->[0]->{code};
    printf {$LOGFILE_REQUEST} "error content .: %s\n", $lastfm_xmlref->{error}->[0]->{content};
    $rc = 1;
  }

  # close logfiles 
  close $LOGFILE_REQUEST;
  close $LOGFILE_RESPONSE;

# 0=successful; 1=not successful
return $rc;
}


# -----------------------------------------
# Show help and exit.
# -----------------------------------------
sub show_help
{
  printf {*STDOUT}
        "Copyright (C) 2009   Klaus Tockloth   <Klaus.Tockloth\@googlemail.com>\n" .
        "This program comes with ABSOLUTELY NO WARRANTY. This is free software,\n" .
        "and you are welcome to redistribute it under certain conditions.\n" .
        "\n" .
        "Usage:\n" .
        "$appname method=\"name\" [param1=\"value\"] ... [paramN=\"value\"]\n" .
        "\n" .
        "Examples (read/get services):\n" .
        "$appname method=\"tag.getTopArtists\" tag=\"disco\"\n" .
        "$appname method=\"artist.getInfo\" artist=\"Cher\"\n" .
        "$appname method=\"album.getInfo\" artist=\"Cher\" album=\"Believe\"\n" .
        "$appname method=\"user.getPlaylists\" user=\"apple777\"\n" .
        "$appname method=\"playlist.fetch\" playlistURL=\"lastfm://playlist/album/2026126\"\n" .
        "\n" .
        "Examples (write/post services; replace \"sk\", \"playlistID\", ... with your data):\n" .
        "$appname method=\"playlist.create\" title=\"Best of Cher\" description=\"Nutrimentum spiritus.\" sk=\"3a50000000000000000000000000030b\"\n" .
        "$appname method=\"playlist.addTrack\" playlistID=\"4441934\" track=\"Runaway\" artist=\"Cher\" sk=\"3a50000000000000000000000000030b\"\n" .
        "\n" .
        "Parameters:\n" .
        "method: method name (method=\"name\")\n" .
        "param1: method parameter 1 (param1=\"value\")\n" .
        "paramN: method parameter N (paramN=\"value\")\n" .
        "\n" .
        "General information:\n" .
        "- The last.fm http request will be logged to file \"last.fm.Request.txt\".\n" .
        "- The last.fm xml response will be logged to file \"last.fm.Response.xml\".\n" .
        "- Both files are written in utf8 format.\n" .
        "- Use batch files for your convenience (e.g. user.getPlaylists.cmd).\n" .
        "- Use a good editor (e.g. microsoft xml notepad, notepad++) to visualize the outputs.\n" .
        "- See xml file \"$cfgfile\" for configuration settings.\n" .
        "\n" .
        "Input / Output configuration:\n" .
        "- It's possible to display the last.fm response additionally to STDOUT.\n" .
        "- Output (last.fm response) conversion: utf8 --> your terminal encoding\n" .
        "- Input (your parameters) conversion  : your terminal encoding --> utf8\n" .
        "- It's important to configure the correct terminal encoding !\n" .
        "  If incorrect: You probably will see funny and not your language specific characters.\n" .
        "- See configuration file \"$cfgfile\" for details.\n" .
        "\n" .
        "HTTP configuration:\n" .
        "- Proxy configuration is possible.\n" .
        "- HTTP timeout configuration ist possible.\n" .
        "- See configuration file \"$cfgfile\" for details.\n" .
        "\n" .
        "How to get a session key (sk)?\n" .
        "\n" .
        "Notes:\n" .
        "- The process to gain a sk is only required once !\n" .
        "- A sk is only necessary for API write services !\n" .
        "\n" .
        "1. Call method \"auth.getToken\":\n" .
        "   - e.g. $appname method=\"auth.getToken\"\n" .
        "   - You will get a token (60 minutes valid) in the last.fm xml response (type last.fm.Response.xml).\n" .
        "2. Authorize lfmCMD to access your last.fm account:\n" .
        "   - Open your standard browser and login to your last.fm account.\n" .
        "   - Build an URL like this: http://www.last.fm/api/auth/?api_key=<API_KEY>&token=<TOKEN>\n" .
        "     - api_key: $lastfm_api_key\n" .
        "     - token: see step 1\n" .
        "     - e.g. http://www.last.fm/api/auth/?api_key=$lastfm_api_key&token=519000000000000000000000000005c7\n" .
        "     - open URL in your browser (you will see the last.fm application permission page)\n" .
        "     - grant permission to lfmCMD / wait some seconds for last.fm server processing\n" .
        "3. Call method \"auth.getSession\":\n".
        "   - e.g. $appname method=\"auth.getSession\" token=\"519000000000000000000000000005c7\"\n" .
        "     - token: see step 1\n" .
        "   - You will get a session key (sk) (infinite valid) in the last.fm xml response (type last.fm.Response.xml).\n" .
        "4. Session key:\n" .
        "   - Keep the session key save (similar to a password).\n" .
        "   - Everybody who knows your session key has write access to your account.\n" .
        "   - You can invalidate the session key by removing this application.\n" .
        "   - Removing is done on the settings page of your last.fm profile.\n";

exit 1;
}


# -----------------------------------------
# Read program configuration (from cfg/xml file).
# ----------------------------------------- 
sub read_Program_Configuration
{
  if (! (-s $cfgfile)) {
    return;
  }
  $cfgfile_found = 1;

  # SuppressEmpty => 1 : skip undefined values (eg. <proxy></proxy>)
  my $config = XMLin($cfgfile, SuppressEmpty => 1);
  # print Dumper($config);

  if (defined $config->{display_response}) {
    $display_response = $config->{display_response};
  }

  if (defined $config->{terminal_encoding}) {
    $terminal_encoding = $config->{terminal_encoding};
  }

  if (defined $config->{http}->{timeout}) {
    $httpTimeout = $config->{http}->{timeout};
  }

  if (defined $config->{http}->{proxy}) {
    $httpProxy = $config->{http}->{proxy};
  }

return;
}
