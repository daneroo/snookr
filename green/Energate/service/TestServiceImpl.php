<?php

//error_reporting(0);
require_once(dirname(__FILE__) . '/service_base.php');
require_once(dirname(__FILE__) . '/CDDRNusoap.php');
require_once(dirname(__FILE__) . '/CDDRDirect.php');

class TestServiceImpl extends iM_ServiceBase {

    private function getImpl($implName) {
        if ("nusoap" == $implName) {
            return new CDDRNusoap();
        } else if ("direct" == $implName) {
            return new CDDRDirect();
        }
        return NULL;
    }

    /**
      @JsonRpcHelp("return the list of implementations to test")
     */
    public function getImplementationNames() {
        $impls = array("direct", "nusoap");
        return $impls;
    }

    /**
      @JsonRpcHelp("return the list of tests to run")
     */
    public function getTests() {
        $testMap = array(
            "GetWeatherFeed" => array("strZIP" => "K1V7P1"),
            "GetThermostats" => array("nHomeID" => 1),
            "GetThermostatDetails" => array("strMacAddr" => "001BC500B00015DB"),
            "GetUserScaleNTime" => array("nUserID" => 233),
            "SLDelEditSchedules" => array("HomeID" => 1),
            "SLGetEditScheduleDetails" => array("strMacAddr" => 1),
            "GetConsumerThermDetails" => array("strMacAddr" => "001BC500B00015DB")
        );

        return $testMap;
    }

    private function wrap($result, $start) {
        $timediff = round((microtime(true) - $start) * 1000, 1);
        return array("result" => $result,
            "millis" => $timediff,
            "pretty" => print_r($result, true)
        );
    }

    /**
      @JsonRpcHelp("count the number of docs")
     */
    public function callIt($implName, $operation, $params) {
        $start = microtime(true);
        $svc = $this->getImpl($implName);
        $result = $svc->callIt($operation, $params);
        return $this->wrap($result, $start);
    }


}

?>
