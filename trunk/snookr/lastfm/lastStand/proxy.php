<?php

$track_url = $_GET["url"];

//echo "<pre>\n";
//echo $track_url."\n";


$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $track_url);

//curl_setopt($ch, CURLOPT_COOKIEJAR, $temp_file);
//curl_setopt($ch, CURLOPT_COOKIEFILE, $temp_file);
//curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
//curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
curl_setopt($ch, CURLOPT_HEADER, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$dataout = curl_exec($ch);
foreach (preg_split("/(\r?\n)/", $dataout) as $line) {
    //print "XXX:line: " . htmlspecialchars($line) . "\n";
    if (strpos($line, "Location:") === 0) {
        //looking for: Location: Location: /ccdr/webpages/EnergateFlex.aspx?H=1&Z=K1V7P1&P=
        // as an indication of success
        //print "\nLOC:line: $line\n\n";
        $track_url = substr($line, 9);
        //print "\n url: |$track_url|\n\n";
        $track_url = trim($track_url);
        //print "\n new url: |$track_url|\n\n";
    }
}
curl_close($ch);
error_log("url:: ".$track_url);
echo json_encode(array("url"=>$track_url));
//echo "</pre>";
?>