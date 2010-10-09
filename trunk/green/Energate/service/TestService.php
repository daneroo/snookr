<?php

//error_reporting(0);
require_once('jsonRPCServer.php');
require_once 'TestServiceImpl.php';

$svc = new TestServiceImpl();
jsonRPCServer::handle($svc)
        or print 'no request';
?>
