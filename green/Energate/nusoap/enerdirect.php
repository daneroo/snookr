<pre><?php
//error_reporting(0);
require_once 'HackedBadgerfish.php';

function callIt($soapaction, $postdata) {
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic");
    // No Cookie yet...
    //$sessioncookie = "ASP.NET_SessionId=qfvs2mcelh1kfcmjnykunimy";
    //curl_setopt($ch, CURLOPT_COOKIE, $sessioncookie);

    $encoded = $postdata;

    curl_setopt($ch, CURLOPT_VERBOSE, true); // Display communication with server
    curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
        'Content-Type: text/xml; charset=utf-8',
        //'SOAPAction: "http://tempuri.org/ICCDRService/GetThermostatDetails"',
        "SOAPAction: $soapaction",
        'Content-Length: ' . strlen($encoded)
    ));

    curl_setopt($ch, CURLOPT_POSTFIELDS, $encoded);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

    $dataoutAsStr = curl_exec($ch);
    if ($dataoutAsStr === false) {
        $dataoutAsStr = 'Curl error: ' . curl_error($ch);
    }
    curl_close($ch);
    return $dataoutAsStr;
}

function getT() {
    $homeId = 1;
    $ONE_LINER_postdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"http://tempuri.org/\"><SOAP-ENV:Body><tns:GetThermostatDetails xmlns:tns=\"http://tempuri.org/\"><tns:strMacAddr>$macAddr</tns:strMacAddr></tns:GetThermostatDetails></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    // < ? xml version="1.0" encoding="utf-8"  ? >
    $postdata = <<<EOD
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://tempuri.org/">
  <SOAP-ENV:Body>
    <tns:GetThermostats xmlns:tns="http://tempuri.org/">
      <tns:nHomeID>$homeId</tns:nHomeID>
    </tns:GetThermostats>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOD;
    $soapaction = '"http://tempuri.org/ICCDRService/GetThermostats"';

    return callIt($soapaction, $postdata);
}

function getTD() {
    $macAddr = "001BC500B00015DB";
    $ONE_LINER_postdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"http://tempuri.org/\"><SOAP-ENV:Body><tns:GetThermostatDetails xmlns:tns=\"http://tempuri.org/\"><tns:strMacAddr>$macAddr</tns:strMacAddr></tns:GetThermostatDetails></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    // < ? xml version="1.0" encoding="utf-8"  ? >
    $postdata = <<<EOD
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://tempuri.org/">
  <SOAP-ENV:Body>
    <tns:GetThermostatDetails xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>$macAddr</tns:strMacAddr>
    </tns:GetThermostatDetails>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOD;
    $soapaction = '"http://tempuri.org/ICCDRService/GetThermostatDetails"';

    return callIt($soapaction, $postdata);
}

function getSFP() {
    $macAddr = "001BC500B00015DB";
    $ONE_LINER_postdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"http://tempuri.org/\"><SOAP-ENV:Body><tns:GetThermostatDetails xmlns:tns=\"http://tempuri.org/\"><tns:strMacAddr>$macAddr</tns:strMacAddr></tns:GetThermostatDetails></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    // < ? xml version="1.0" encoding="utf-8"  ? >
    $postdata = <<<EOD
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://tempuri.org/">
  <SOAP-ENV:Body>
    <tns:GetFixedSetPoint xmlns:tns="http://tempuri.org/">
      <tns:strMacAddr>$macAddr</tns:strMacAddr>
      <tns:nSpIndex>0</tns:nSpIndex>
    </tns:GetFixedSetPoint>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOD;
    $soapaction = '"http://tempuri.org/ICCDRService/GetFixedSetPoint"';
    return callIt($soapaction, $postdata);
}

function getWF() {
    $zipcode = "K1V7P1";
    $ONE_LINER_postdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"http://tempuri.org/\"><SOAP-ENV:Body><tns:GetThermostatDetails xmlns:tns=\"http://tempuri.org/\"><tns:strMacAddr>$macAddr</tns:strMacAddr></tns:GetThermostatDetails></SOAP-ENV:Body></SOAP-ENV:Envelope>";
    // < ? xml version="1.0" encoding="utf-8"  ? >
    $postdata = <<<EOD
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://tempuri.org/">
  <SOAP-ENV:Body>
    <tns:GetWeatherFeed xmlns:tns="http://tempuri.org/">
      <tns:strZIP>$zipcode</tns:strZIP>
    </tns:GetWeatherFeed>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOD;
    $soapaction = '"http://tempuri.org/ICCDRService/GetWeatherFeed"';
    return callIt($soapaction, $postdata);
}

function report($name, $xmlresponse, $time) {
    echo "\n($time s.) $name: " . htmlspecialchars($xmlresponse) . "\n";

    // IDEA use sed to remove namespaces <(xmlns):=""...
    // remove start tag name spaces
    /*
    $xmlresponse = preg_replace ( '/<(\w+:)(\w+)/', '<$2', $xmlresponse );
    echo " xns " . htmlspecialchars($xmlresponse) . "\n";
    $xmlresponse = preg_replace ( '/<\/(\w+:)(\w+)/', '</$2', $xmlresponse );
    echo " xns " . htmlspecialchars($xmlresponse) . "\n";
    $xmlresponse = preg_replace ( '/(xmlns(:\w+)?)="[^"]+"/', '', $xmlresponse );
    echo " xns " . htmlspecialchars($xmlresponse) . "\n";
    */
    if (1) {

        $dom = DOMDocument::loadXML($xmlresponse);
        $asphp = HackedBadgerFish::map($dom);
        $json = json_encode($asphp);
        echo "\n  json: " . $json, PHP_EOL;
        //removeStuff($asphp);
        //var_dump($asphp);
    }
}

$iterations = 0;
$start = microtime(TRUE);
for ($it = 0; $it < $iterations; $it++) {
    report("getT", getT(), ( microtime(TRUE) - $start) / 1.0);
    $start = microtime(TRUE);
}
$start = microtime(TRUE);
for ($it = 0; $it < $iterations; $it++) {
    report("getSFP", getSFP(), ( microtime(TRUE) - $start) / 1.0);
    $start = microtime(TRUE);
}
$start = microtime(TRUE);
for ($it = 0; $it < $iterations+10; $it++) {
    report("getWF", getWF(), ( microtime(TRUE) - $start) / 1.0);
    $start = microtime(TRUE);
}
$start = microtime(TRUE);
for ($it = 0; $it < $iterations; $it++) {
    report("getTD", getTD(), ( microtime(TRUE) - $start) / 1.0);
    $start = microtime(TRUE);
}
?>
</pre>
