<?php
require_once 'facebook.php';

$now = time();
$stamp = date('Y-m-d',$now).'T'.date('H:i:s',$now).'Z';
$fbml = "Last Updated at ".$stamp;
echo "Update Facebook iMetrical refHandle:myhandle to:\n";
echo "  ".$fbml."\n";

// fb.fbml.setRefHandle('myhandle','Last Updated at %s'%stamp)

$appapikey = 'bd100b49340effa90332cdfbe6e85659';
$appsecret = 'b150ebbad9cdff51269248dbe4b9683d';
$facebook = new Facebook($appapikey, $appsecret);

$facebook->api_client->call_method('facebook.Fbml.setRefHandle', array( 'handle' => 'myhandle', 'fbml' => $fbml,) ); 

echo "Done\n"
?>


