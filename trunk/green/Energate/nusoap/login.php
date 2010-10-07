<?php
require_once '../service/CCDRLogin.php';
echo "<pre>";
$badValues = CCDRLogin::login('nobody', 'badpassword');
echo "bad:";var_dump($badValues); echo "\n";
$goodValues = CCDRLogin::login('smckenzie', 'test-123');
echo "good:";var_dump($goodValues); echo "\n";
echo "</pre>";
?>

