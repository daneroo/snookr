<?php

/**
 * Description of CDDRNusoap
 *
 * @author daniel
 */
//error_reporting(0);
// Pull in the NuSOAP code
require_once(dirname(__FILE__) . '/../nusoap/nusoap-0.9.5/lib/nusoap.php');
require_once(dirname(__FILE__) . '/../nusoap/nusoap-0.9.5/lib/class.wsdlcache.php');

class CDDRNusoap {

    private static $client = null;
    private static $cacheClient = true;

    function callIt($operation, $params,$client=null) {
        if ($client == NULL) {
            $client = CDDRNusoap::enerMakeClient();
        }
        $namespace = "http://tempuri.org/";
        //$result = $client->call($operation, $params, $namespace, $soapAction, $headers, $rpcParams, $style, $use);
        $result = $client->call($operation, $params, $namespace);
        $verbose = false;
        if ($verbose) {
            // Display the request and response
            echo '<h4>Request</h4>';
            echo '<pre>' . htmlspecialchars($client->request, ENT_QUOTES) . '</pre>';
            echo '<h4>Response</h4>';
            echo '<pre>' . htmlspecialchars($client->response, ENT_QUOTES) . '</pre>';
            // Display the debug messages
            //echo '<h2>Debug</h2>';
            //echo '<pre>' . htmlspecialchars($client->debug_str, ENT_QUOTES) . '</pre>';
        }
        if ($client->fault) {
            return array('error' => "Soap failure: fault");
        }
        $err = $client->getError();
        if ($err) {
            return array('error' => $err);
        }
        return $result;
    }

    // Create the client instance
    static function enerMakeClient($forceNew=false) {
        if (!$forceNew && CDDRNusoap::$client != NULL) {
            return CDDRNusoap::$client;
        }

        //echo "\n\n Making new client\n\n";

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

        if (CDDRNusoap::$cacheClient) {
            CDDRNusoap::$client = $client;
        }

        return $client;
    }

}

?>
