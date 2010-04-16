<?php
require_once 'log4php/Logger.php';
Logger::configure('log4php.properties');

$logger = Logger::getLogger("fbref::render");
$logger->info(print_r($_SERVER['REQUEST_URI'],TRUE));

?>

<!-- Gabarit Eko -->
<div align="center" style="Zdisplay:none;">
    <table width="520" border="0" cellspacing="0" cellpadding="0" style="background:#423d32; font-family:Arial,Verdana,Sans-serif; font-size:11px;">

        <tr>
            <td width="10"></td>
            <td width="500"><img src="http://axial.imetrical.com/EkoGabarit/images/header-logo.gif"></td>

            <td width="10"></td>
        </tr>


        <tr>
            <td width="10"></td>
            <td width="500"><img src="http://axial.imetrical.com/EkoGabarit/images/header-img.gif"></td>
            <td width="10"></td>
        </tr>

        <tr>
            <td width="10"></td>
            <td width="500" style="background:#ffffff;">

                <table width="500" border="0" cellspacing="0" cellpadding="0" style="background:#ffffff; font-family:Arial,Verdana,Sans-serif; font-size:11px;">
                    <tr>

                        <td width="320" valign="top" align="left" style="padding:10px; background-color:#ffffff; background-image:url('http://www.axialdev.com/emailing/Axialdev/general-hiver/images/maincontent-bg.gif'); background-repeat:no-repeat; background-position:top left;">
                            <span style="color:#666666;"><div id="date" name="Date" type="text">24 décembre 2007</div></span>

                            <h1 style="font-size:26px; font-weight:normal; color:#006699;">
                                <div id="titre" name="Titre de la nouvelle" type="text">
                                    Inscrivez-vous maintenant!
                                </div>
                            </h1>

                            <div id="soustitre" name="Sous titre et intro" type="html">
                                <h2 style="font-size:14px; font-weight:bold; color:#006699;"><i>pour</i> EkoClientTwo</h2>

                                <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.</p>


                                <style type="text/css">
                                    .stepdiv {
                                        width:250px; height:100px;
                                        /*float: left;*/
                                        padding:10px;
                                        /*background-color: red; */
                                        display:none;
                                    }
                                    .buttons  { Zwidth:100%; clear:both;}
                                    input.left {float:left; width:50px;}
                                    input.right {float:right; width:50px;}
                                </style>

                                <div id="slideshow_inner" Zstyle="position: relative; width: 2450px">
                                    <div id="slide00" class="stepdiv" style="display:block">
                                        <div>
                                            <div>Step 0</div>
                                            <ul >
                                                <li id="li_1" >
                                                    <label class="description">Name </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>		<li id="li_2" >
                                                    <label class="description">Email </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                        <div class="buttons">
                                            <input class="left" onclick="sviz(0); return false;" value="Left"type="submit" />
                                            <input class="right" onclick="sviz(1); return false;" value="Right"type="submit" />
                                        </div>
                                    </div>
                                    <div id="slide01" class="stepdiv">
                                        <div>
                                            <div>Step 1</div>
                                            <ul >
                                                <li id="li_1" >
                                                    <label class="description">OtherFiels </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>		<li id="li_2" >
                                                    <label class="description">Extra Info </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                        <div class="buttons">
                                            <input class="left" onclick="sviz(0); return false;" value="Left"type="submit" />
                                            <input class="right" onclick="sviz(2); return false;" value="Right"type="submit" />
                                        </div>
                                    </div>
                                    <div id="slide02" class="stepdiv">
                                        <div>
                                            <div>Step 2</div>
                                            <ul >
                                                <li id="li_1" >
                                                    <label class="description">Name </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>		<li id="li_2" >
                                                    <label class="description">Email </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                        <div class="buttons">
                                            <input class="left" onclick="sviz(1); return false;" value="Left"type="submit" />
                                            <input class="right" onclick="sviz(3); return false;" value="Right"type="submit" />
                                        </div>
                                    </div>
                                    <div id="slide03" class="stepdiv">
                                        <div>
                                            <div>Step 3</div>
                                            <ul >
                                                <li id="li_1" >
                                                    <label class="description">OtherFiels </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>		<li id="li_2" >
                                                    <label class="description">Extra Info </label>
                                                    <div>
                                                        <input class="element text medium" type="text" maxlength="255" value=""/>
                                                    </div>
                                                </li>
                                            </ul>
                                        </div>
                                        <div class="buttons">
                                            <input class="left" onclick="sviz(2); return false;" value="Left"type="submit" />
                                            <input class="right" onclick="sviz(3); return false;" value="Right"type="submit" />
                                        </div>
                                    </div>
                                </div>

                                <div style="clear:both;"></div>

                                <p>Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.</p>
                            </div>


                            <div id="highlight" name="nouvelle Highlight" type="html">
                                <table width="100%" cellspacing="0" cellpadding="0" border="0" style="background:#00b5d9; color:#ffffff; font-family:Arial,Verdana,Sans-serif; font-size:11px;">
                                    <tr>
                                        <td width="80" valign="top" align="right" style="padding:10px; font-weight:bold; font-size:14px;">Titre</td>
                                        <td valign="top" align="left" style="padding:10px 10px 20px 0px;">Texte</td>
                                    </tr>
                                </table>
                            </div>


                            <div id="texte" name="Sous titre" type="html">
                                <p>n enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend te llus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, </p>
                            </div>

                            <div id="listeexemple" name="Liste d'exemple" type="html">
                                <h2 style="font-size:14px; font-weight:bold; color:#006699;">Liste d'exemple</h2>

                                <ul style="margin:0px; padding:0px 0px 0px 15px; list-style-type:none; list-style-image:url('http://www.axialdev.com/emailing/Axialdev/general-hiver/images/puce-bleu.gif'); color:#666666;">
                                    <li><font color="#006699"><b>Brin de jasette</b></font><br>

                                        Nous avons développé pour eux toute l'interface permettant les échanges entre membres (messagerie privée, blogue), nous avons redéfini le visuel su site.
                                    </li>
                                    <li><font color="#006699"><b>Chambre de commerce de Sherbrooke</b></font><br>
                                        Développé en collaboration avec Groupe Itek, le site utilise notre système de gestion de contenu (CMS) : Axone avec le module calendrier.
                                    </li>
                                </ul>
                            </div>

                        </td>

                        <td width="150" valign="top" align="left" style="padding:30px 10px 20px 0px; color:#666666; font-size:10px;">

                            <span style="color:#423d32; font-size:14px; font-weight:bold;">Le service avant tout</span>

                            <p>Toujours dans le but de vous offrir le meilleur service, nous vous invitons à communiquer avec nous via 3 adresses de courriels :</p>

                            <p><a href="mailto:support@axialdev.com" style="color:#006699; text-decoration:none;"><b>support@axialdev.com</b></a><br>
                            pour tous vos problèmes de courriels, problèmes techniques, etc…</p>

                            <p><a href="mailto:ventes@axialdev.com" style="color:#006699; text-decoration:none;"><b>ventes@axialdev.com</b></a><br>
                            pour toutes vos demandes d’informations ou soumissions.</p>


                            <p><a href="mailto:prod@axialdev.com" style="color:#006699; text-decoration:none;"><b>prod@axialdev.com</b></a><br>
                            pour toutes demandes d'ajouts, envoi de votre matériel et pour vos projets en cours.</p>

                            <p>Vous pouvez également nous joindre via 4 numéros de téléphone :</p>

                            <p style="color:#423d32; font-weight:bold;">Sherbrooke : 819-564-2417<br>
                                Gatineau : 819-772-9396<br>
                                Montréal : 514-373-8104<br>

                            Sans-frais : 1 866 52-AXIAL</p>
                            <p>&nbsp;</p>
                            <table width="150" cellspacing="0" cellpadding="0" border="0" style="font-family:Arial,Verdana,Sans-serif; font-size:11px;">
                                <tr>
                                    <td height="20" style="padding:0px 10px; background:#938b78;"><img src="http://axial.imetrical.com/EkoGabarit/images/title-liens.gif"></td>
                                </tr>

                                <tr>
                                    <td valign="top" align="left" style="padding:10px; font-size:10px; background-color:#e8e4db; background-image:url('http://www.axialdev.com/emailing/Axialdev/general-hiver/images/liens-bg.gif'); background-repeat:no-repeat; background-position:top left;">

                                        <div id="liens" name="Liens" type="html">
                                            <ul style="margin:0px 0px 0px 0px; padding:0px 0px 0px 0px; line-height:12px; list-style-type:none;">
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px; border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>

                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                                <li style="margin:0px 0px 0px 0px; padding:5px 0px 5px 0px;  border-bottom:1px solid #aaa18b;">N’oubliez pas de consulter nos présentations interactives pour avoir un aperçu de nos solutions .</li>
                                            </ul>
                                        </div>
                                    </td>
                                </tr>
                            </table>

                        </td>

                    </tr>
                </table>

            </td>
            <td width="10"></td>
        </tr>

        <tr>
            <td width="10"></td>
            <td width="500" height="47" style="color:#cccccc; text-align:right; ">
                <p id="liensaxial"><div number="4">Envoyer à un ami</div>  |  <div lang="fr" >Se désabonner</div></p>

                <p style="font-size:10px;">Téléphones : Gatineau : 819 772-9396     Montréal : 514 373-8104     Sherbrooke : 819 564-2417</p>
            </td>
            <td width="10"></td>
        </tr>

    </table>
    <div style="text-align:center; font-size:11px; color:#666666; font-family:Arial,Verdana,Sans-serif; padding:10px 0px;">Si le courriel ne s'affiche pas correctement, une <div lang="fr" >version en ligne</div> est disponible.</div>

</div>

<!-- Ajax example -->

<div id="ajax_exameple"  style="display:none;" >
    <h1>Example Page</h1>
    <h3>You Are:</h3>
    <fb:name uid="<?=$user_id?>" useyou="false"/><br/>
    <?php
    $callbackurl='http://axial.imetrical.com/facebook/eko-contest-render/fbAjaxPost.php';
    $initcounter=0; //perhaps retrieve value from database
    echo "<h3>setTimeout Counter increased in the script: <span id='counterSpan'>".$initcounter."s</span></h3><br/>";
    ?>
    <h2>Div where test links:</h2>
    <div id="actionDiv">[[Nothing happening in this div yet.]]</div><br/>

    <h3>Select <a href="#" onclick="do_ajax('actionDiv',0);return false;">Option 1 (AJAX)</a></h3>
    <h3>Select <a href="#" onclick="do_ajax('actionDiv',1);return false;">Option 2 (AJAX)</a></h3>
    <h3>Select <a href="#" onclick="do_ajax('actionDiv',2);return false;">Option 3 (AJAX)</a></h3>

</div>
<script type="text/javascript">
    <!--
    function do_ajax(div,val) {
        dlg = new Dialog();

        dlg.showChoice('Confirm Request', "Instructions, ipsum lorem,..." , 'Yes', 'No');
        dlg.onconfirm = function() {
            alert('OK');
        }
        var ajax = new Ajax();
        ajax.responseType = Ajax.FBML;
        ajax.ondone = function(data) {
            document.getElementById(div).setInnerFBML(data);
        }
        var params={"action":'select',"option":val,"othertest":'anystring',"otherval":100}; //add parameters as comma separated "param":value
        ajax.post('<?=$callbackurl?>?t='+val,params);  //GET values sended with "val" and POST values sended with "params"
    }

    //setTimeout counter:
    var counterValue=<?=$initcounter?>;
    function incCounter(units) {
        counterValue++;
        document.getElementById('counterSpan').setTextValue(counterValue+units);
        setTimeout(function() {incCounter("s")},1000);
    }

    // comment this out...
    //  setTimeout(function() {incCounter("s")},1000); //1000 = 1 sec
    //it is possible to simply call incCounter,
    //but that would increment the counter immediately upon page load

    //-->
</script>

<!-- Slideshow becomes step-form -->
<!-- move this up....
<link href="http://www.webdigi.co.uk/css/fb.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.stepdiv {
width:400px; height:200px;
/*float: left;*/
padding:10px;
/*background-color: red; */
display:none;
}
.buttons  { width:100%; clear:both;}
input.left {float:left; width:50px;}
input.right {float:right; width:50px;}
</style>

<div id="slideshow_wrapper" style="width:550px; clear: both; margin-bottom: 20px">
<div id="slideshow" style="overflow: hidden; width: 435px; float: left; position:relative; margin-right: 5px">
<div id="slideshow_inner" Zstyle="position: relative; width: 2450px">
<div id="slide00" class="stepdiv" style="display:block">
<div>
<div>Step 0</div>
<ul >
<li id="li_1" >
<label class="description">Name </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>		<li id="li_2" >
<label class="description">Email </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>
</ul>
</div>
<div class="buttons">
<input class="left" onclick="sviz(0); return false;" value="Left"type="submit" />
<input class="right" onclick="sviz(1); return false;" value="Right"type="submit" />
</div>
</div>
<div id="slide01" class="stepdiv">
<div>
<div>Step 1</div>
<ul >
<li id="li_1" >
<label class="description">OtherFiels </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>		<li id="li_2" >
<label class="description">Extra Info </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>
</ul>
</div>
<div class="buttons">
<input class="left" onclick="sviz(0); return false;" value="Left"type="submit" />
<input class="right" onclick="sviz(2); return false;" value="Right"type="submit" />
</div>
</div>
<div id="slide02" class="stepdiv">
<div>
<div>Step 2</div>
<ul >
<li id="li_1" >
<label class="description">Name </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>		<li id="li_2" >
<label class="description">Email </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>
</ul>
</div>
<div class="buttons">
<input class="left" onclick="sviz(1); return false;" value="Left"type="submit" />
<input class="right" onclick="sviz(3); return false;" value="Right"type="submit" />
</div>
</div>
<div id="slide03" class="stepdiv">
<div>
<div>Step 3</div>
<ul >
<li id="li_1" >
<label class="description">OtherFiels </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>		<li id="li_2" >
<label class="description">Extra Info </label>
<div>
<input class="element text medium" type="text" maxlength="255" value=""/>
</div>
</li>
</ul>
</div>
<div class="buttons">
<input class="left" onclick="sviz(2); return false;" value="Left"type="submit" />
<input class="right" onclick="sviz(3); return false;" value="Right"type="submit" />
</div>
</div>
</div>
</div>
</div>
-->
<script>
    //Script for slideshow
    var numslides = 6;
    function sviz(slidenbr) {
        //alert('slide: '+slidenbr);
        for(var i = 0;i < numslides; i++) {
            var viz = "none";
            if (i==slidenbr) viz="block";
            var slid = "slide0"+i;
            //alert('slide: '+slidenbr+' == '+slid+' -> '+viz);
            document.getElementById(slid).setStyle("display",viz);
        }

    }
</script>

<!-- Stamp the bottom (for cache control monitoring -->
<div>stamp: <fb:application-name /> @ <fb:time t='<?= time() ?>'/></div>
