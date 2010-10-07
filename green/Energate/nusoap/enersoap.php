<?php

error_reporting(0);
// Pull in the NuSOAP code
require_once('nusoap-0.9.5/lib/nusoap.php');
require_once('nusoap-0.9.5/lib/class.wsdlcache.php');

// Create the client instance
function enerMakeClient() {
    $endpoint = 'http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc?wsdl';

    $usecache = true;
    if ($usecache) {
        // directory for cache
        //$cache = new nusoap_wsdlcache('wsdl-cache', 60);
        $cache = new nusoap_wsdlcache(sys_get_temp_dir(), 3660);
        $wsdl = $cache->get($endpoint);
        if (is_null($wsdl)) {
            $wsdl = new wsdl($endpoint);
            $err = $wsdl->getError();
            if ($err) {
                echo '<h2>WSDL Constructor error (Expect - 404 Not Found)</h2><pre>' . $err . '</pre>';
                echo '<h2>Debug</h2><pre>' . htmlspecialchars($wsdl->getDebug(), ENT_QUOTES) . '</pre>';
                exit();
            }
            $cache->put($wsdl);
        } else {
            $wsdl->clearDebug();
            $wsdl->debug('Retrieved from cache: ' . sys_get_temp_dir());
        }
        $client = new nusoap_client($wsdl, 'wsdl');
    } else {
        // this works!
        $client = new nusoap_client($endpoint, 'wsdl');
    }
    $client->soap_defencoding = 'utf-8';


    // Check for an error
    $err = $client->getError();
    if ($err) {
        // Display the error
        return NULL;
        //echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
        // At this point, you know the call that follows will fail
    }
    // $client->setUseCurl($useCURL);
    //var_dump($client);
    return $client;
}

function enerSoapCall($client, $operation, $params) {
    $namespace = "http://tempuri.org/";
    //$result = $client->call($operation, $params, $namespace, $soapAction, $headers, $rpcParams, $style, $use);
    $result = $client->call($operation, $params, $namespace);
    return $result;
}

function enerSoapReport($client, $result, $showReqResp=false, $start=NULL,$operation='op') {
// Check for a fault
    echo "<hr />\n";
    if ($client->fault) {
        echo '<h2>Fault</h2><pre>';
        print_r($result);
        echo '</pre>';
    } else {
        // Check for errors
        $err = $client->getError();
        if ($err) {
            // Display the error
            echo '<h2>Error</h2><pre>' . $err . '</pre>';
        } else {
            // Display the result
            if ($start) {
                echo '(' . (microtime(true) - $start) . ' s.) '.$operation.': '.json_encode($result);
            } else {
                echo '<h4>Result ' . date('Y-m-d H:i:s') . '</h4><pre>';
                echo json_encode($result);
                echo '</pre>';
            }
            //print_r($result);
        }
    }
    if ($showReqResp) {
        // Display the request and response
        echo '<h4>Request</h4>';
        echo '<pre>' . htmlspecialchars($client->request, ENT_QUOTES) . '</pre>';
        echo '<h4>Response</h4>';
        echo '<pre>' . htmlspecialchars($client->response, ENT_QUOTES) . '</pre>';
        // Display the debug messages
        //echo '<h2>Debug</h2>';
        //echo '<pre>' . htmlspecialchars($client->debug_str, ENT_QUOTES) . '</pre>';
    }
}

$start = microtime(TRUE);
$client = enerMakeClient();
echo '<h3>Setup ' . (microtime(true) - $start) . ' s.</h3>';

$testMap = array(
    "GetWeatherFeed" => array("strZIP" => "K1V7P1")/* ,
          "GetThermostats" => array("nHomeID" => 1),
          "GetThermostatDetails" => array("strMacAddr" => "001BC500B00015DB")
         *
         */
);

foreach ($testMap as $operation => $params) {
    for ($it = 0; $it < 5; $it++) {
        $start = microtime(TRUE);
        $result = enerSoapCall($client, $operation, $params);
        enerSoapReport($client, $result, false, $start, $operation);
    }
}
if (0) {
    $start = microtime(TRUE);
    $operation = "GetThermostatDetails";
    $params = array("strMacAddr" => "001BC500B00015DB");
    $operation = "GetWeatherFeed";
    $params = array("strZIP" => "K1V7P1");
    $result = enerSoapCall($client, $operation, $params);
    enerSoapReport($client, $result, false, $start);
}
if (1) {
    for ($it = 0; $it < 10; $it++) {
        $start = microtime(TRUE);
        $result = enerSoapCall($client, $operation, $params);
        enerSoapReport($client, $result, false, $start, $operation);
    }
}

if (0) {

    $start = microtime(TRUE);
    $result = enerSoapCall($client, "GetUserScaleNTime", array("nUserID" => 233));
    enerSoapReport($client, $result, false, $start);

    $start = microtime(TRUE);
    $result = enerSoapCall($client, "SLDelEditSchedules", array("HomeID" => 1));
    enerSoapReport($client, $result, false, $start);

    $start = microtime(TRUE);
    $result = enerSoapCall($client, "SLGetEditScheduleDetails", array("strMacAddr" => 1));
    enerSoapReport($client, $result, false, $start);

    $start = microtime(TRUE);
    $result = enerSoapCall($client, "GetConsumerThermDetails", array("strMacAddr" => "001BC500B00015DB"));
    enerSoapReport($client, $result, false, $start);

    $start = microtime(TRUE);
    $result = enerSoapCall($client, "GetThermostats", array("nHomeID" => 1));
    enerSoapReport($client, $result, false, $start);
}
?>
