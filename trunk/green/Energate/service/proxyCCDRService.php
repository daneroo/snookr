<?php

//error_reporting(0);
require_once(dirname(__FILE__) . '/service_base.php');
require_once(dirname(__FILE__) . '/jsonRPCServer.php');
require_once(dirname(__FILE__) . '/CCDRNusoap.php');
require_once(dirname(__FILE__) . '/CCDRDirect.php');
require_once(dirname(__FILE__) . '/CCDRLogin.php');

class proxyCCDRService extends iM_ServiceBase {

    private function getSvc() {
        return new CCDRNusoap();
        //return new CCDRDirect();
    }

    private function callIt($operation, $params) {
        $svc = $this->getSvc();
        $result = $svc->callIt($operation, $params);
        $resultName = "" . $operation . "Result";
        if (isset($result[$resultName])) {
            return $result[$resultName];
        }
        // should never reach
        return $result;
    }

    /**
      @JsonRpcHelp("return the array('homeId' => HHHHH, 'zipCode' => ZZZZZ) or null")
     *
     */
    public function login($username, $passwd) {
        // get {"homeId":"HHH","zipCode":"ZZZZ"}
        $phase1 = CCDRLogin::login($username, $passwd);
        if ($phase1 == NULL) {
            return null;
        }
        // get {"macAddr":"MMM","thermName":"TTTTTT"}
        $operation = "GetThermostats";
        $params = array("nHomeID" => $phase1["homeId"]);
        $phase2 = $this->callIt($operation, $params);
        if (isset($phase2["Thermostats"])) {
            $thermostats = $phase2["Thermostats"];
            $result = $phase1;
            if (isset($thermostats["MacAddr"])) {
                $result["macAddr"] = $thermostats["MacAddr"];
            }
            if (isset($thermostats["ThermName"])) {
                $result["thermName"] = $thermostats["ThermName"];
            }
            return $result;
        }
        return $null;
    }

}

$svc = new proxyCCDRService();

jsonRPCServer::handle($svc)
        or print 'no request';
?>
