<?php

//error_reporting(0);
require_once 'service_base.php';
require_once('./service/CDDRNusoap.php');
require_once('./service/CDDRDirect.php');

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
        $impl = $this->getImpl($implName);
        $result = $svc->callIt($operation, $params);
        return $this->wrap($result, $start);
    }

    /**
      @JsonRpcHelp("count the number of docs")
     */
    public function test01CountAllDocs($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        // search sort page project
        $result = $impl->countDocs();
        return $this->wrap($result, $start);
    }

    /**
      @JsonRpcHelp("fetch all and count")
     */
    public function test02FetchAllAndCountDocsUnlessBig($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        // search sort page project
        $cheatcount = $impl->countDocs();
        if ($cheatcount > 10000) {
            $result = $cheatcount;
        } else {
            $docs = $impl->findDocs();
            $result = count($docs);
        }
        return $this->wrap($result, $start);
    }

    /**
      @JsonRpcHelp("fetch the first doc")
     */
    public function test03FirstDoc($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        // search sort page project
        $docs = $impl->findDocs(NULL, NULL, NULL, array(1, 1));
        $result = $docs[0]["entreprise"];
        return $this->wrap($result, $start);
    }

    /**
      @JsonRpcHelp("fetch the first doc")
     */
    public function test04FirstAfterSortByCity($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        $docs = $impl->findDocs(NULL, NULL, array("ville"), array(1, 1));
        $result = $docs[0]["ville"];
        return $this->wrap($result, $start);
    }

    /**
      @JsonRpcHelp("unique titre")
     */
    public function test05UniqueTitre($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        $list = $impl->unique("titre");
        $result = "" . $list[0] . "..." . $list[count($list) - 1] . " (" . count($list) . ")";
        return $this->wrap($result, $start);
    }

    public function test06UniqueVille($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        $list = $impl->unique("ville");
        $result = "" . $list[0] . "..." . $list[count($list) - 1] . " (" . count($list) . ")";
        return $this->wrap($result, $start);
    }

    public function test07CountSherbrooke($implName) {
        $start = microtime(true);
        $impl = $this->getImpl($implName);
        if (NULL == $impl) {
            return -1;
        }
        $result = $impl->countDocs(array("ville" => "Sherbrooke"), NULL, NULL, array(1, -1));
        return $this->wrap($result, $start);
    }

}

?>
