<?php
error_reporting(0);
$mime = "default";
$cs = "default";
$ct = null;
if ( $_REQUEST['mime'] ) {
    $mime = $_REQUEST['mime'];
}
if ( $_REQUEST['cs'] ) {
    $cs = $_REQUEST['cs'];
}
if ($mime!="default"){
    if ($cs=="default"){
        $ct = $mime;
    } else {
        $ct = $mime."; charset=".$cs;
    }
} else { // default mime
    if ($cs!="default"){
        $ct = "application/json; charset=".$cs;
    }
}
if ($ct){
    header("Content-type: ".$ct);

}
$eacuteISO = html_entity_decode("&eacute;",ENT_NOQUOTES,"ISO-8859-1");
$eacuteUTF = html_entity_decode("&eacute;",ENT_NOQUOTES,"UTF-8");
$eacuteOK = $eacuteUTF;
if ($cs=="ISO-8859-1"){
    $eacuteOK = $eacuteISO;
}

function kv($key, $val){
    return '"'.$key.'":"'.$val.'"';
}
$dico = array(
    kv('key','value'),
    kv('mime',$mime),
    kv('charset',$cs),
    kv('eacuteOK',$eacuteOK),
    kv('eacuteISO',$eacuteISO),
    kv('eacuteUTF',$eacuteUTF)
);
if ( $_REQUEST['input'] ) {
    $in = $_REQUEST['input'];
    array_push($dico,kv('input',$in));
    $hex = array();
    foreach (str_split($in) as $chr) {
        $hex[] = sprintf("%02X", ord($chr));
    }
    array_push($dico,kv('inputHex',implode('',$hex)));
}

$json = '{'.implode(",", $dico).'}';
echo $json;

?>
