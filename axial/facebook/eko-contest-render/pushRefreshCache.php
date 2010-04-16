<!--
http://axial.imetrical.com/facebook/eko-contest-render/pushRefreshCache.php
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <?php
        require_once 'log4php/Logger.php';
        Logger::configure('log4php.properties');

        $b = 'http://axial.imetrical.com/facebook/eko-contest-render';
        $extRefUrls = array("$b/fbref001.html","$b/fbref001.php","$b/fbref002.php","$b/fbref001.php?pid=112544522101543","$b/fbref001.php?pid=111211942246289");

        $logger = Logger::getLogger("cache");
        $logger->info("update cache: ".$extRefUrl);

        require_once 'facebook.php';

        $appapikey = '0252e289262d8a3cf06f8ed06c53f871';
        $appsecret = '32e8887964ea6a986c01f1c7ab5a3986';
        $facebook = new Facebook($appapikey, $appsecret);
        // put your code here

        foreach ($extRefUrls as $u) {
            $ans = $facebook->api_client->fbml_refreshRefUrl($u);
            $logger->info("refreshRefUrl: $ans <- $u");
            echo "<pre>refreshRefUrl: $ans <- $u</pre>";
        }

        /* This does not work
        $page_ids = array(112544522101543,111211942246289);
        foreach ($page_ids as $p) {
            $ans = $facebook->api_client->profile_getinfo($p);
            $logger->info("profile_info: $ans <- $p");
            echo "<pre>profile_info: $ans <- $p</pre>";
        }*/

        ?>
    </body>
</html>
