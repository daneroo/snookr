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
$query = 'SELECT unix_timestamp(stamp),watt FROM tedlive order by stamp desc limit 100';
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