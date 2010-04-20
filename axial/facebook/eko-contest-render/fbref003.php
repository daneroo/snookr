<?php
require_once 'log4php/Logger.php';
Logger::configure('log4php.properties');

$logger = Logger::getLogger("fbref::render");
$logger->info(print_r($_SERVER['REQUEST_URI'],TRUE));

?>

<div>This is fbref003.</div>

<a href="#" id="hello" onclick="return false">Hello World!</a>
<div id="caca">Before</div>
<script> <!--
    function random_int(lo, hi) {
        return Math.floor((Math.random() * (hi - lo)) + lo);
    }
    function hello_world(obj) {
        var r = random_int(0, 255),
        b = random_int(0, 25),
        g = random_int(0, 25);
        var color = r+', '+g+', '+b;
        obj.setStyle('color', 'rgb('+color+')');
        //document.getElementById('hello').setInnerXHTML('caca');
        document.getElementById("caca").setTextValue("After@"+new Date());
    }
    function test() {
        var obj = document.getElementById('hello');
        obj.addEventListener('click', function(e){
            hello_world(obj);
            e.stopPropagation();
            e.preventDefault();
            return false;
        }, false);

    }
    test(); //-->
</script> 
<!-- Stamp the bottom (for cache control monitoring -->
<div>stamp: <fb:application-name /> @ <fb:time t='<?= time() ?>'/></div>
