<?php
session_start();
require_once 'service/TestServiceImpl.php';
$tstSvc = new TestServiceImpl();
$testNames = array_keys($tstSvc->getTests());
$impls = $tstSvc->getImplementationNames();
?><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CCDR Tests</title>
        <!--  css and media, and dependancies -->
        <link rel="stylesheet" href="css/style.css" type="text/css" media="all" />
        <link rel="stylesheet" href="css/test.css" type="text/css" media="all" />
        <!-- jEko dependancies -->
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <!-- jEko -->
        <script type="text/javascript" src="js/jquery.json-2.2.js"></script>
        <script type="text/javascript" src="js/core.js"></script>
        <script type="text/javascript" src="js/ajax.js"></script>
        <script type="text/javascript" src="js/proxy.js"></script>
        <script type="text/javascript">
            var busy = '<img src="images/busy20trans.gif" />';
            function addTest(svc,testName,testParams,impl){
                var testId = "#"+impl+"-"+testName;
                debug("running "+testId);
                $(testId).html(busy);
                //var result = svc['callIt'](impl,testName,testParams);
                svc['callIt'](impl,testName,testParams,function(result){
                    if(undefined===result.millis || undefined===result.result){
                        $(testId).text($.toJSON(result));
                    } else {
                        var h = '<div class="time">'+
                            result.millis+" ms"+
                            '</div><div class="showresult">show/hide result<div class="result">'+
                            $.toJSON(result.result)+
                            '</div></div>';
                        $(testId).html(h);
                    }
                    $(testId).click(function(){
                        $(this).find(".result").toggle();
                    });
                });
            }
            $(function(){
                /*
                var zzz = jEko.proxy.invoke("service/TestService.php", "listMethods", [], null);
                debug(zzz);
                var zzz = jEko.proxy.invoke("service/TestService.php", "system.listMethods", [], null);
                debug(zzz);
                 */

                var testServiceURI="service/TestService.php";
                var tstSvc = jEko.proxy.generate(testServiceURI);

                debug(tstSvc);
                var about = tstSvc["system.about"]();
                debug(about);
                var methods = tstSvc["system.listMethods"]();
                debug(methods);
                var tests = tstSvc.getTests();
                debug(tests);
                var impls = tstSvc.getImplementationNames();
                debug(impls);
                for (var testName in tests) {
                    var testParams = tests[testName];
                    for (var i in impls) {
                        var impl = impls[i];
                        addTest(tstSvc,testName,testParams,impl);
                    }
                }
                $(".showresult").click(function(){
                    $(this).find(".result").toggle();
                });

            });
        </script>
        <style type="text/css">
            .result {
                display: none;
                position: relative;
                background-color: #ccc;
                overflow:scroll;
                width: 400px;
            }
            th {
                width: 20%;
            }
            td {
                width: 40%;
            }
        </style>    </head>
    <body>
        <div>Tests:</div>
        <?php
        echo "<table border=\"1\" id=\"testResults\" width=\"95%\">";
        echo "<tr>";
        echo "<th>Test/Implentation</th>";
        foreach ($impls as $impl) {
            $testId = "{$impl} {$testName}";
            echo "<th>Invocation Service: {$impl}</th>";
        }
        echo "</tr>";
        foreach ($testNames as $testName) {
            echo "<tr>";
            echo "<th>{$testName}</th>";
            foreach ($impls as $impl) {
                $testId = "{$impl}-{$testName}";
                echo "<td id=\"{$testId}\">...</td>";
            }
            echo "</tr>";
        }
        echo "</table>";
        echo"\n";
        ?>
    </body>
</html>