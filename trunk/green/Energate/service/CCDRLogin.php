<?php

/**
 * CCDRLogin: this class performs a login post,
 *   first it must scrape the login page for cookies, and hidden inputs.
 *   upon a successful login post, a redirect header is received
 * i,e,: Location: /ccdr/webpages/EnergateFlex.aspx?H=1&Z=K1V7P1&P=
 *   These query parameters are meant to be injected into the main page, and injected into the flash script
 *
 *
 * @author daniel
 */
//error_reporting(0);
class CCDRLogin {
    /* this function extract <input type="hidden" /> form fields */

    private static function getHidden($dom) {
        $params = array();
        $nodes = $dom->getElementsByTagName("input");
        for ($c = 0; $c < $nodes->length; $c++) {
            $node = $nodes->item($c);
            $astext = htmlspecialchars($dom->saveXML($nodes->item($c))) . "\n";
            if ("hidden" == $node->getAttribute("type")) {
                $name = $node->getAttribute("name");
                $value = $node->getAttribute("value");
                $params[] = '' . $name . '=' . urlencode($value);
                /*
                  echo "tag: $tag has type attr:" . $node->getAttribute("type") . "\n";
                  echo "tag: $tag has id attr:" . $node->getAttribute("id") . "\n";
                  echo "tag: $tag has name attr:" . $node->getAttribute("name") . "\n";
                  echo "tag: $tag has value attr:" . $node->getAttribute("value") . "\n";
                 *
                 */
            }
        }
        $postdata = $params = implode('&', $params);
        //echo "postdata = " . $postdata;
        return $postdata;
    }

    static function login($username, $passwd) {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "http://opa.myenergate.com/ccdr/webpages/main_login.aspx");

        //$temp_file = '/tmp/cookies.txt';
        $temp_file = tempnam(sys_get_temp_dir(), 'cookies');
        // but then delete it after the second curl...        
        curl_setopt($ch, CURLOPT_COOKIEJAR, $temp_file);
        curl_setopt($ch, CURLOPT_COOKIEFILE, $temp_file);

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $dataout = curl_exec($ch);
        $dom = DOMDocument::loadHTML($dataout);
        //print "cookie file: $temp_file\n";
        $postdata = CCDRLogin::getHidden($dom);
        $postdata = implode('&', array(
                    "txtlogid=" . urlencode($username),
                    "txtpwd=" . urlencode($passwd),
                    "btnlogin=Login",
                    "ScriptManager1=UpdatePanel1%7Cbtnlogin",
                    "__EVENTTARGET=",
                    "__EVENTARGUMENT=",
                    $postdata
                ));
        curl_close($ch);

        $returnValues = NULL;
        // second curl!
        //echo "about to log in with:  " . $postdata;
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "http://opa.myenergate.com/ccdr/webpages/main_login.aspx");

        curl_setopt($ch, CURLOPT_COOKIEJAR, $temp_file);
        curl_setopt($ch, CURLOPT_COOKIEFILE, $temp_file);
        curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
        curl_setopt($ch, CURLOPT_HEADER, 1);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $dataout = curl_exec($ch);
        foreach (preg_split("/(\r?\n)/", $dataout) as $line) {
            //print "XXX:line: " . htmlspecialchars($line) . "\n";
            if (strpos($line, "Location:") === 0) {
                //looking for: Location: Location: /ccdr/webpages/EnergateFlex.aspx?H=1&Z=K1V7P1&P=
                // as an indication of success
                //print "\nLOC:line: $line\n\n";
                $url = substr($line, 9);
                //print "\n url: |$url|\n\n";
                $url = trim($url);
                //print "\n url: |$url|\n\n";
                $parsedurl = parse_url($url);
                //print_r($parsedurl);
                $query = $parsedurl['query'];
                //print "\n query: |$query|\n\n";
                $parsedquery = array();
                parse_str($query, $parsedquery);
                //print_r($parsedquery);

                // remap H->homeId Z->zipCode
                $returnValues = array('homeId' => $parsedquery['H'], 'zipCode' => $parsedquery['Z']);
            }
        }
        curl_close($ch);
        unlink($temp_file);
        return $returnValues;
    }

}

?>
