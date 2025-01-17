<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Energate CCDR Protoype</title>
    </head>
    <body>
        <pre>
            <?php
            /*
             * Copyright 2010 Daniel Lauzon <daniel.lauzon@gmail.com>
             */
            //error_reporting(0);
            require_once('service/CCDRNusoap.php');
            require_once('service/CCDRDirect.php');
            $testMap = array(
                "GetWeatherFeed" => array("strZIP" => "K1V7P1"),
                "GetThermostats" => array("nHomeID" => 1),
                "GetThermostatDetails" => array("strMacAddr" => "001BC500B00015DB"),
                "GetUserScaleNTime" => array("nUserID" => 233),
                "SLDelEditSchedules" => array("HomeID" => 1),
                "SLGetEditScheduleDetails" => array("strMacAddr" => 1),
                "GetConsumerThermDetails" => array("strMacAddr" => "001BC500B00015DB")
            );
            /*$testMap = array(
                "GetWeatherFeed" => array("strZIP" => "K1V7P1"),
                "GetThermostatDetails" => array("strMacAddr" => "001BC500B00015DB")
            );*/

            $services = array(
                "nusoap" => new CCDRDirect(),
                "direct" => new CCDRDirect()
            );
            echo "<pre>";
            foreach ($testMap as $operation => $params) {
                for ($it = 0; $it < 4; $it++) {
                    foreach ($services as $svcName => $svc) {
                        $start = microtime(TRUE);
                        $result = $svc->callIt($operation, $params);
                        echo "$svcName (" . (microtime(true) - $start) . ' s.) ' . $operation . ': ' . json_encode($result) . PHP_EOL;
                        //print_r($result);
                    }
                }
            }
            echo "</pre>";
            ?>
        </pre>
    </body>
</html>
