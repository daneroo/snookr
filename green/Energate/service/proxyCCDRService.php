<?php

//error_reporting(0);
require_once(dirname(__FILE__) . '/service_base.php');
require_once(dirname(__FILE__) . '/jsonRPCServer.php');
require_once(dirname(__FILE__) . '/CCDRNusoap.php');
require_once(dirname(__FILE__) . '/CCDRDirect.php');
require_once(dirname(__FILE__) . '/CCDRLogin.php');

class proxyCCDRService extends iM_ServiceBase {

    private function getSvc() {
        //return new CCDRNusoap();
        return new CCDRDirect();
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
      @JsonRpcHelp("get thermostat details")
     *
     */
    public function getThermostatDetails($macAddr) {
        return $this->callIt("GetThermostatDetails", array("strMacAddr" => $macAddr));
    }

    /**
      @JsonRpcHelp("get weather feed")
     *
     */
    public function getWeatherFeed($zipCode) {
        return $this->callIt("GetWeatherFeed", array("strZIP" => $zipCode));
    }

    /**
      @JsonRpcHelp("set mode and fan")
     *
     */
    public function slSetMode($strEqMode, $strFanMode, $macAddr) {
        return $this->callIt("SLSetMode", array("strEqMode" => $strEqMode, "strFanMode" => $strFanMode, "strMacAddr" => $macAddr));
        /*
          <tns:SLSetMode xmlns:tns="http://tempuri.org/">
          <tns:strEqMode>HeatOnly</tns:strEqMode>
          <tns:strFanMode>On</tns:strFanMode>
          <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
          </tns:SLSetMode>
         */
    }

    /**
      @JsonRpcHelp("set mode and fan")
     *
     */
    public function slSetHold($cool, $heat, $macAddr) {
        $xmlcool='<tns:int xmlns:tns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">'.$cool.'</tns:int>';
        $xmlheat='<tns:int xmlns:tns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">'.$heat.'</tns:int>';
        return $this->callIt("SLSetHold", array(
            "strVacationValue" => 0,
            "strHoldType" => 'Permanent',
            "strMacAddr" => $macAddr,
            "nCool" => $xmlcool,
            "nHeat" => $xmlheat,
            "btStartIndex" => 7
        ));
        /*
          <tns:SLSetHold xmlns:tns="http://tempuri.org/">
          <tns:strVacationValue>0</tns:strVacationValue>
          <tns:strHoldType>Permanent</tns:strHoldType>
          <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
          <tns:nCool>
          <tns:int xmlns:tns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">37</tns:int>
          </tns:nCool>
          <tns:nHeat>
          <tns:int xmlns:tns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">45</tns:int>
          </tns:nHeat>
          <tns:btStartIndex>7</tns:btStartIndex>
          </tns:SLSetHold>
         */
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
