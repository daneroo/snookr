<?php
//error_reporting(0);
require_once('service_base.php');
// from http://jsonrpcphp.org/
require_once 'jsonRPCServer.php';
//session_start();
require_once 'Energate.php';

/**
 @author daniel.lauzon
 @JsonRpcHelp("This is the description of the service")
 @JsonRpcHelp("This is another description of the service")
 */

class proxyService extends iM_ServiceBase {


    /**
     @JsonRpcHelp("Login return the session cookie")
     */
    public function login($username,$passwd) {
        //return "cocorico";
        //return "".$username."=".$passwd;
        $enrgate = new Energate();
        $sessioncookie = $enrgate->login($username,$passwd);
        return $sessioncookie;
    }

    /**
     @JsonRpcHelp("Get the data")
     */
    public function getit($cookie,$username) {
        if (is_null($cookie)||""==$cookie) {
            return "Authorization Required";
        }
        $enrgate = new Energate();
        $dataAsString = $enrgate->getit($cookie,$username);
        $decoded = json_decode($dataAsString,TRUE);
        //return "username:$username::$dataAsString";
        return $decoded;
    }


}


$svc = new proxyService();

jsonRPCServer::handle($svc)
        or print 'no request';

?>