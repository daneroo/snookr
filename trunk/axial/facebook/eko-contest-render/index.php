<?php
// log4php
require_once 'log4php/Logger.php';
Logger::configure('log4php.properties');

$logger = Logger::getRootLogger();
$logger->debug("Hello World!");
$logger->info("request: ".print_r($_REQUEST,TRUE));

?><!--
 This also performs PHP Firebug connection
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>eko-contest-render</title>
    </head>
    <body>
        <ul>
            <li>Facebook integration</li>
            <li>Contest Render Link</li>
        </ul>
    </body>
</html>
