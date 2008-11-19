<?php
header("Content-type: text/plain");
$dbhost = 'localhost';
$dbuser = 'aviso';
$dbpass = '';

$conn = mysql_connect($dbhost, $dbuser, $dbpass) or die                      ('Error connecting to mysql');

$dbname = 'ted';
mysql_select_db($dbname);

$xmldecl = "<?xml version=\"1.0\"?>\n"; 
$xmldecl .= "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n";
echo $xmldecl;

echo "<plist version=\"1.0\"><array>\n";
# old live query
#$query = 'SELECT unix_timestamp(stamp),watt FROM tedlive order by stamp desc limit 30';
//print "<!-- Scope param : ".htmlspecialchars($_GET["scope"])."  --> \n"; 
$scope=0;
$scope=intval(htmlspecialchars($_GET["scope"]));
if ($scope>3) $scope = fmod($scope,4);
if ($scope<0) $scope = fmod(intval(time()/10),4);
//print "<!-- Scope value : $scope  --> \n"; 

if ($scope == 0) {
    $len = 18;   $rightpad = '0';           $secondsPerSample=10;    $samples=30;  // tensec
 } elseif ($scope == 1) {
     $len = 16;   $rightpad = ':00';         $secondsPerSample=60;    $samples=60;  // minute
 } elseif ($scope == 2) {
     $len = 13;   $rightpad = ':00:00';      $secondsPerSample=3600;  $samples=24;  // hour
 } elseif ($scope == 3) {
     $len = 10;   $rightpad = ' 00:00:00';   $secondsPerSample=86400; $samples=31;  // day
 }

$secondsInWhere = $secondsPerSample * (1+$samples);
$query = "select ";
$query .= " unix_timestamp(str_to_date(concat(left(stamp,$len),'$rightpad'),'%Y-%m-%d %H:%i:%s')) as g, round(avg(watt)) ";
$query .= " from tedlive ";
$query .= " where stamp>date_sub(now(), interval $secondsInWhere second) ";
$query .= " group by g  order by g desc";
$query .= " limit $samples";

//print "<!-- Query: ".$query."  --> \n"; 

$result = mysql_query($query) or die('Query failed: ' . mysql_error());

while ($row = mysql_fetch_array($result, MYSQL_NUM)) {
    $tt = $row[0];
    $watt = $row[1];
    $stamp = date('Y-m-d',$tt).'T'.date('H:i:s',$tt).'Z';
    echo "<dict><key>stamp</key><date>$stamp</date><key>value</key><integer>$watt</integer></dict>\n";
}

// Free resultset
mysql_free_result($result);

echo "</array></plist>\n";

mysql_close($conn);
?>