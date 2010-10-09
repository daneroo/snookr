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
        <title>Mongo Qualify Tests</title>
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
            var busy = '<img src="images/busy-spinner.gif" />';
            function addTest(svc,testName,impl){
                var testId = "#"+impl+"-"+testName;
                debug("running "+testId);
                $(testId).html(busy);
                var result = svc[testName](impl);
                if(undefined===result.millis || undefined===result.result){
                    $(testId).text($.toJSON(result));
                } else {
                    var h = '<div class="time">'+
                        result.millis+" ms"+
                        '</div><div class="result">'+
                        $.toJSON(result.result)+
                        '</div>';
                    $(testId).html(h);
                }
            }
            $(function(){
                
                var testServiceURI="service/TestService.php";
                var tstSvc = jEko.proxy.generate(testServiceURI);
                //var tests = tstSvc["system.listMethods"]();
                var tests = tstSvc.getTests();
                debug(tests);
                var impls = tstSvc.getImplementationNames();
                debug(impls);
                for (var t in tests) {
                    var test = tests[t];
                    for (var i in impls) {
                        var impl = impls[i];
                        addTest(tstSvc,test,impl);
                    }
                }
            });
        </script>
    </head>
    <body>
        <div>Tests:</div>
        <?php
        echo "<table border=\"1\" id=\"testResults\">";
        echo "<tr>";
        echo "<th>Test/Implentation</th>";
        foreach ($impls as $impl) {
            $testId = "{$impl} {$testName}";
            echo "<th>{$impl}</th>";
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
        /*
          $m = new Mongo("mongodb://localhost/", array("persist" => "onlyone"));
          $db = $m->selectDB("sib");
          $collection = $db->docs;
          $cursor = $collection->find();
          $allDocs = iterator_to_array($cursor);
          var_dump($allDocs);
         *
         */
        echo "<pre>";
        if (0) {
            $s = new DocumentServiceSessionImpl();
            var_dump($s->countDocs());
            //var_dump($s->unique("titre"));
            $s->findDocs(NULL, array("ville"), array(1, -1), array("fields" => array("ville"), "unique" => true));
            //var_dump($s->unique("ville"));
        }
        if (0) {
            $s = new DocumentServiceMongoImpl("sib");
            //var_dump($s->unique("ville"));
            //var_dump($s->unique("titre"));
            var_dump($s->countDocs(array("ville" => "Sherbrooke"), NULL, array(1,10)));

        }
        echo "</pre>";
        ?>
    </body>
</html>