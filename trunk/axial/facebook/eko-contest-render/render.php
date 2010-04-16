<?php
/*
 * variables of interest:
 *  added to EkoCLientOne 'Page":  http://www.facebook.com/pages/EkoClientOne/112544522101543#

INFO render - Request [GET]: /admin
    [fb_page_id] => 112544522101543
    [0252e289262d8a3cf06f8ed06c53f871] => 60aed6b95c8e6fc7f82a8fd793b5bf6c
    [__utma] => 10627500.1591860979.1263244940.1263244940.1269534508.2
    [__utmz] => 10627500.1263244940.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)

INFO render - Request [POST]: /post-auth
    [fb_sig_authorize] => 1
    [fb_sig_locale] => en_US
    [fb_sig_in_new_facebook] => 1
    [fb_sig_time] => 1271175826.34
    [fb_sig_added] => 0
    [fb_sig_page_id] => 112544522101543
    [fb_sig_page_added] => 1
    [fb_sig_api_key] => 0252e289262d8a3cf06f8ed06c53f871
    [fb_sig_app_id] => 112976262057899
    [fb_sig] => 13613191074b5a9d4fff601f757d877e

 * Remove from page
INFO render - Request [POST]: /post-remove
    [fb_sig_uninstall] => 1
    [fb_sig_added] => 0
    [fb_sig_page_id] => 112544522101543
    [fb_sig_page_added] => 0
    [fb_sig_api_key] => 0252e289262d8a3cf06f8ed06c53f871
    [fb_sig_app_id] => 112976262057899

* canvas as iframe / FBML - no difference
INFO render - Request [POST]: /profile-tab
    [fb_sig_in_canvas] => 1
    [fb_sig_request_method] => GET
    [fb_sig_is_admin] => 1
    [fb_sig_type] => LOCAL_TECHNOLOGY_TELECOMMUNICATIONS_SERVICES
    [fb_sig_is_fan] => 0
    [fb_sig_added] => 0
    [fb_sig_page_id] => 112544522101543
    [fb_sig_page_added] => 1
    [fb_sig_profile_user] => 112544522101543
    [fb_sig_profile_id] => 112544522101543
    [fb_sig_in_profile_tab] => 1
* canvas as FBML
    [fb_sig_in_canvas] => 1
    [fb_sig_request_method] => GET
    [fb_sig_is_admin] => 1
    [fb_sig_type] => LOCAL_TECHNOLOGY_TELECOMMUNICATIONS_SERVICES
    [fb_sig_is_fan] => 0
    [fb_sig_added] => 0
    [fb_sig_page_id] => 112544522101543
    [fb_sig_page_added] => 1
    [fb_sig_profile_user] => 112544522101543
    [fb_sig_profile_id] => 112544522101543
    [fb_sig_in_profile_tab] => 1
 */
require_once 'log4php/Logger.php';
Logger::configure('log4php.properties');

$logger = Logger::getLogger("render");
$logger->info("Request [${_SERVER['REQUEST_METHOD']}]: ${_SERVER['PATH_INFO']}\n".print_r($_REQUEST,TRUE));

require_once 'facebook.php';

$appapikey = '0252e289262d8a3cf06f8ed06c53f871';
$appsecret = '32e8887964ea6a986c01f1c7ab5a3986';
$facebook = new Facebook($appapikey, $appsecret);

$appProps = $facebook->api_client->admin_getAppProperties(array('application_name','use_iframe'));
$use_iframe = $appProps['use_iframe'];
$use_fbml = ! $use_iframe;
$is_admin = $_REQUEST['fb_sig_is_admin'];
$is_canvas = $_REQUEST['fb_sig_in_canvas'];
$is_canvas_url = ('/'==$_SERVER['PATH_INFO'])?true:false;
$logger->info("is_canvas_url:".(($is_canvas_url)?'true-eroo':'false-eroo'));

echo '<h2>Eko Contest Render</h2>';
echo '<img src="http://axial.imetrical.com/facebook/eko-contest-render/becomeFan-520x88.jpg" />';

echo '<div> go to: <fb:application-name /> and "Add to Page"</div>';
echo '<div> add to profile: <fb:add-section-button section="profile" /> </div>';
echo '<div> add to info: <fb:add-section-button section="info" /> </div>';

echo "<pre>props: ".print_r($appProps,TRUE)."</pre>";

if (array_key_exists('fb_sig_page_id', $_REQUEST)) {
$page_id = $_REQUEST['fb_sig_page_id'];
$profile_boxes = "<div><fb:application-name /> in box @ <fb:time t='".time()."'/></div>";
$profile_wallinfo = "<div><fb:application-name /> in wall/info @ <fb:time t='".time()."'/></div>";
$facebook->api_client->profile_setFBML(NULL, $page_id, $profile_boxes, NULL, NULL, $profile_wallinfo);
}

//echo "<pre>application properties.: ".print_r($appProps,TRUE)."</pre>";

$extRefUrl = 'http://axial.imetrical.com/facebook/eko-contest-render/fbref001.php';
$facebook->api_client->fbml_refreshRefUrl($extRefUrl);
echo "<fb:ref url=\"$extRefUrl\">";

/*
echo "<p>Hello, <fb:name uid=\"$user_id\" useyou=\"false\" />!</p>";
echo "<p>Friends:";
$friends = $facebook->api_client->friends_get();
$friends = array_slice($friends, 0, 25);
foreach ($friends as $friend) {
  echo "<br>$friend";
}
echo "</p>";
*/
?>



