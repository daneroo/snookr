<?php

/**
 * Description of CDDRDirect
 *
 * @author daniel
 */
require_once(dirname(__FILE__) . '/../nusoap/HackedBadgerfish.php');

class CDDRDirect {

    function callIt($operation, $params) {
        $xmlparams = "";
        foreach ($params as $tag => $value) {
            $xmlparams .= "<$tag>$value</$tag>";
        }
        $postdata = <<<EOD
    <$operation xmlns="http://tempuri.org/">
      $xmlparams
    </$operation>
EOD;
        $soapaction = '"http://tempuri.org/ICCDRService/' . $operation . '"';
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "http://opa.myenergate.com/Svc/Energate.Core2.Reporting.Core2ReportService.svc/basic");
// No Cookie yet...
//$sessioncookie = "ASP.NET_SessionId=qfvs2mcelh1kfcmjnykunimy";
//curl_setopt($ch, CURLOPT_COOKIE, $sessioncookie);

        $wrapped = <<<EOD
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:tns="http://tempuri.org/">
  <SOAP-ENV:Body>
$postdata
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
EOD;

//echo "\nwrapped: ", htmlspecialchars($wrapped), PHP_EOL;

        curl_setopt($ch, CURLOPT_VERBOSE, true); // Display communication with server
        curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
            'Content-Type: text/xml; charset=utf-8',
            //'SOAPAction: "http://tempuri.org/ICCDRService/GetThermostatDetails"',
            "SOAPAction: $soapaction",
            'Content-Length: ' . strlen($wrapped)
        ));

        curl_setopt($ch, CURLOPT_POSTFIELDS, $wrapped);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);

        $dataoutAsStr = curl_exec($ch);
        if ($dataoutAsStr === false) {
            $dataAsPHP = array('error' => 'Curl error: ' . curl_error($ch));
        } else {
// everything is ok!
            $xmlresponse = $dataoutAsStr;
            $dom = DOMDocument::loadXML($xmlresponse);
            $dataAsPHP = HackedBadgerFish::map($dom);
        }
        curl_close($ch);
        return $dataAsPHP;
    }

}

?>
