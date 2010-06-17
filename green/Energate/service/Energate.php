<?php
class Energate {
    // login with https, sniff the cookie coming back, and return it.
    public function login($username,$passwd) {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'https://firstenergy-staging.getgreenbox.com/accounts/login/');
        $postdata="username=$username&password=$passwd&commit=Sign+in&next=";

        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);

        curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
        curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
        curl_setopt($ch, CURLOPT_HEADER, 1);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $dataout = curl_exec($ch);
        $sessionCookie = "CookieNotFound";
        $foundLoggedInRedirect=FALSE;
        foreach(preg_split("/(\r?\n)/", $dataout) as $line) {
            //print "XXX:line: $line\n";
            if (strpos($line, "Location:") === 0) {
                //looking for: Location: http://firstenergy-staging.getgreenbox.com/accounts/logged-in/
                // as an indicatio of success
                //print "LOC:line: $line\n";
                //print_r(preg_match('/logged-in\/$/', trim($line)));
                if (preg_match('/logged-in\/$/', trim($line))) {
                    $foundLoggedInRedirect=TRUE;
                }
            }
            if (strpos($line, "Set-Cookie:") === 0) {
                list($headerName, $sessionCookie, $rest) = split('[:;]', $line);
                $sessionCookie = trim($sessionCookie);
            }
        }
        curl_close($ch);
        if ($foundLoggedInRedirect) {
            return $sessionCookie;
        } else {
            return "Login Failed";
        }
        return $sessionCookie;
    }

    public function getit($sessioncookie,$username) {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'http://firstenergy-staging.getgreenbox.com/db/query/');
        curl_setopt($ch,CURLOPT_COOKIE,$sessioncookie);

        $postdata = array(
                array(
                        "method"=>"query",
                        "url"=>"$username/helpers/tstat_helper?test=false",
                        //"url"=>"$username/helpers/snapshot_helper?days=7;message=messageHelper;hvac=tstat_helper",
                        "id"=>1
                )
        );
        $encoded = json_encode($postdata);
        // this is not technically correct, but required...
        // json should escape foward slashes...
        $encoded = str_replace("\/", "/", $encoded);

        curl_setopt($ch, CURLOPT_POST, true); // Tell curl that we are posting data
        curl_setopt($ch, CURLOPT_POSTFIELDS, $encoded);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $dataoutAsStr = curl_exec($ch);
        curl_close($ch);

        return $dataoutAsStr;
    }

}
?>
