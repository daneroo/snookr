<pre><?php
error_reporting(0);
require_once ('../service/CCDRDirect.php');


$testMap = array(
    "GetWeatherFeed" => array("strZIP" => "K1V7P1"),
    "GetThermostats" => array("nHomeID" => 1),
    "GetThermostatDetails" => array("strMacAddr" => "001BC500B00015DB"),
    "GetUserScaleNTime" => array("nUserID" => 233),
    "SLDelEditSchedules" => array("HomeID" => 1),
    "SLGetEditScheduleDetails" => array("strMacAddr" => 1),
    "GetConsumerThermDetails" => array("strMacAddr" => "001BC500B00015DB")
);

echo "<pre>";
if (1)
    foreach ($testMap as $operation => $params) {
        for ($it = 0; $it < 1; $it++) {
            $start = microtime(TRUE);
            $svc = new CCDRDirect();
            $result = $svc->callIt($operation, $params);

            echo '(' . (microtime(true) - $start) . ' s.) ' . $operation . ': ' . json_encode($result) . PHP_EOL;
        }
    }
echo "</pre>";
?>
</pre>
