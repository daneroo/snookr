<?php
header('Content-type: text/javascript');
$rex = "/uploads\/.*\.$i(jpg|jpeg|png|gif)/i";
$imglist = array();
foreach (glob("uploads/*.*") as $filename) {
    if ($rex!="" && !preg_match($rex, $filename)) {
        //echo "REMOVE $filename size " . filesize($filename) . "<br />\n";
        continue;
    }
    array_push($imglist, $filename);
}
//print_r($imglist);
if (false) foreach ($imglist as $filename) {
    echo "$filename size " . filesize($filename) . "<br />\n";
}
/*
 * The output will JSON / JSONP
 *
 *  if param jsonp or jasoncallback or jsonpcallback THEN we return JSONP
 */
$jsonpcallback = null;
if ($_GET['callback']) {
    $jsonpcallback = $_GET['callback'];
}
if ($_GET['jsoncallback']) {
    $jsonpcallback = $_GET['jsoncallback'];
}
if ($_GET['jsonpcallback']) {
    $jsonpcallback = $_GET['jsoncallback'];
}
if ($_GET['jsonp']) {
    $jsonpcallback = $_GET['jsonp'];
}

//$json = json_encode($imglist);
$json = json_encode(array ('images'=>$imglist));

if ($jsonpcallback!=null) {
    echo $jsonpcallback . '(' . $json . ');';
} else {
    echo $json;
}
?>