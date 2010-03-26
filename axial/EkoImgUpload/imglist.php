<?php
header('Content-type: text/javascript');
$rex = "/uploads\/.*\.$i(jpg|jpeg|png|gif)/i";
$imglist = array();
$baseURL="http://axial.imetrical.com/EkoImgUpload/";
foreach (glob("uploads/*.*") as $filename) {
    if ($rex!="" && !preg_match($rex, $filename)) {
        //echo "REMOVE $filename size " . filesize($filename) . "<br />\n";
        continue;
    }
    $url = $baseURL . $filename;
    $imgStruct = array ('url'=>$url, 'thumb'=>$url."#thumb", 'name'=>basename($filename) );
    array_push($imglist, $imgStruct);
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