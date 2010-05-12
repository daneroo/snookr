<?php
/*
 * Call this test page to unit test
 *   ../src/*.js :          src directory
 *   ../dist/ekolib.js:     concatenated library or
 *   ../dist/ekolib.min.js: minified library
 * You may also specify page encoding:
 *     Content-type header, and meta
 */
// default values
$lib = "src"; // || concat || min
$charset = "default"; // || UTF-8 || utf-8 || ISO-8859-1

if ( $_REQUEST['lib'] ) {
    $lib = $_REQUEST['lib'];
}

function inc($dir, $file){
    return '<script type="text/javascript" src="'.$dir.'/'.$file.'"></script>';
}
$includes = array();
if ($lib=="src"){
    $srcinc = array(
        "<!-- required plugins -->",
        inc('../src','jquery.json-2.2.js'),
        inc('../src','jquery.timer-0.1.js'),
        inc('../src','jquery.dataTables-1.6.2.js'),
        inc('../src','jshash-2.2-md5.js'),
        inc('../src','jshash-2.2-sha1.js'),
        "<!-- actual library includes -->",
        inc('../src','core.js'),
        inc('../src','ajax.js'),
        "<!-- legacy code -->",
        inc('../src','ekolib-core-1.0.0.js'),
        inc('../src','ekolib-constants-1.0.0.js'),
        inc('../src','ekolib-data-1.0.0.js'),
        inc('../src','ekolib-editor-1.0.0.js'),
        inc('../src','ekolib-chained-1.0.0.js'),
        inc('../src','ekolib-render-1.0.0.js'),
        inc('../src','ekolib-lifecycle-1.0.0.js')
    );
    $includes = array_merge($includes,$srcinc);
} else if ($lib=="concat"){
    $concatinc = array(
        "<!-- concatenated library -->",
        inc('../dist','ekolib.js')
    );
    $includes = array_merge($includes,$concatinc);
} else if ($lib=="min"){
    $mininc = array(
        "<!-- minified concatenated library -->",
        inc('../dist','ekolib.min.js')
    );
    $includes = array_merge($includes,$mininc);
}
$harnessandtestsinc = array(
    "<!-- qunit test harness -->",
    inc('qunit/qunit','qunit.js'),
   "<!-- actual unit tests -->",
    inc('unit','coreTest.js'),
    inc('unit','ajaxTest.js')
);
$includes = array_merge($includes,$harnessandtestsinc);

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr" id="html">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>ekolib Test Suite</title>
        <link rel="Stylesheet" media="screen" href="qunit/qunit/qunit.css" />
        <link rel="Stylesheet" media="screen" href="data/testsuite.css" />
        <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/start/jquery-ui.css" type="text/css" media="all" />
        <!-- jQuery Includes -->
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
        <?php
        echo "\n";
        foreach ($includes as $inc){
            echo "        ".$inc."\n";
        }
        echo "\n";
        ?>
        <script type="text/javascript">
            $(function(){
                // select current settings
                $('#lib option[value=<?= $lib ?>]').attr("selected","selected");

                $('#lib').change(function(){
                    //$(this).form.submit();
                    debug("libselect change");
                    $('#harness').submit();
                });
            });
        </script>
    </head>

    <body id="body">
        <form id="harness" action="index.php" method="get">
            <div>
                Test Harness Variants:
                <select id="lib" name="lib">
                    <option value="src">Source</option>
                    <option value="concat">Concat</option>
                    <option value="min">Minified Concat</option>
                </select>
            </div>
        </form>

        <h1 id="qunit-header">jQuery Test Suite</h1>
        <h2 id="qunit-banner"></h2>
        <div id="qunit-testrunner-toolbar"></div>
        <h2 id="qunit-userAgent"></h2>
        <ol id="qunit-tests"></ol>
    </body>
</html>
