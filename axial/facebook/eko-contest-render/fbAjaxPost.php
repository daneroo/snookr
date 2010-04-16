<?php

require_once 'log4php/Logger.php';
Logger::configure('log4php.properties');

$logger = Logger::getLogger("ajax-post");
$logger->info("Request [${_SERVER['REQUEST_METHOD']}]: ${_SERVER['PATH_INFO']}\n".print_r($_REQUEST,TRUE));

echo "Your string in POST method is: ".$_POST['othertest']."</br>Your number POST method is: ".$_POST['otherval']."</br>";
$user = isset($_POST['fb_sig_user']) ? $_POST['fb_sig_user'] : null;
if ($_GET['t'] == 0) { // Ajax.RAW tested with GET
    echo 'This is a raw string. The current time is: '.date('r').', and you are '.($user ? 'uid: #'.$user : 'anonymous').'.';
}
else if ($_GET['t'] == 1) { // Ajax.JSON tested with GET
    echo '{"message": "This is a JSON object.", "time": "'.date('r').'", "fbml_test": "Hello, '.($user ? '<fb:name uid='.$user.' useyou=false />' : 'anonymous').'."}';
}
else if ($_GET['t'] == 2) { // Ajax.FBML tested with GET
    echo 'This is an FBML string. The current time is: '.date('r').', and you are '.($user ? '<fb:name uid='.$user.' useyou=false />' : 'anonymous').'.';
}
?>
