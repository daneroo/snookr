
document.write("<script type=\"text/javascript\" "+"src=\"http://java.com/js/deployJava.js\"><"+"/script>\n");var browserIDs=[{id:"MSIE",varsToSearch:[navigator.userAgent],stringsToFind:["MSIE"]},{id:"Chrome",varsToSearch:[navigator.userAgent,navigator.vendor],stringsToFind:["Chrome","Google"]},{id:"Safari",varsToSearch:[navigator.userAgent,navigator.vendor],stringsToFind:["Safari","Apple Computer"]},{id:"Opera",varsToSearch:[navigator.userAgent],stringsToFind:["Opera"]},{id:"Netscape Family",varsToSearch:[navigator.appName],stringsToFind:["Netscape"]}];var OSIDs=[{id:"Windows",varsToSearch:[navigator.userAgent],stringsToFind:["Windows"]},{id:"Mac",varsToSearch:[navigator.userAgent],stringsToFind:["Mac OS X"]},{id:"Linux",varsToSearch:[navigator.userAgent],stringsToFind:["Linux"]},{id:"UNIX",varsToSearch:[navigator.userAgent],stringsToFind:["X11"]}];var browsersSupportingDirectJavaAccess=["Opera","Netscape Family"];var browsersSupportingActiveX=["MSIE"];var activeXVersionList=["1.8.0","1.7.0","1.6.0","1.5.0","1.4.2"];function findEntryInList(listToUse){var myID;for(var i=0;i<listToUse.length;i++){var match=true;for(var j=0;j<listToUse[i].varsToSearch.length;j++){if(listToUse[i].varsToSearch[j].indexOf(listToUse[i].stringsToFind[j],0)==-1){match=false;break;}}
if(match){myID=listToUse[i].id;break;}}
return myID;}
var thisBrowser;function getBrowser(){if(thisBrowser===undefined){thisBrowser=findEntryInList(browserIDs);if(thisBrowser===undefined){thisBrowser="unknown";}}
return thisBrowser;}
var thisBrowserCanAccessJava;function browserCanAccessJava(){if(thisBrowserCanAccessJava===undefined){var browser=getBrowser();thisBrowserCanAccessJava=false;for(var i=0;i<browsersSupportingDirectJavaAccess.length;i++){if(browser==browsersSupportingDirectJavaAccess[i]){thisBrowserCanAccessJava=true;break;}}}
return thisBrowserCanAccessJava;}
var thisBrowserHasActiveX;function browserHasActiveX(){if(thisBrowserHasActiveX===undefined){var browser=getBrowser();thisBrowserHasActiveX=false;if(window.ActiveXObject!==undefined){for(var i=0;i<browsersSupportingActiveX.length;i++){if(browser==browsersSupportingActiveX[i]){thisBrowserHasActiveX=true;break;}}}}
return thisBrowserHasActiveX;}
var thisJavaVersion;function getJavaVersion(){if(thisJavaVersion===undefined){if(!navigator.javaEnabled()){thisJavaVersion="0 - Java Not Enabled";}
else if(browserCanAccessJava()){thisJavaVersion=java.lang.System.getProperty("java.version");}
else if(browserHasActiveX()){for(var v=0;v<activeXVersionList.length;v++){try{var axo=new ActiveXObject("JavaWebStart.isInstalled."+
activeXVersionList[v]+".0");thisJavaVersion=activeXVersionList[v];break;}catch(exception){}}}
if(thisJavaVersion===undefined){var bestVersionSeen;for(var i=0;i<navigator.mimeTypes.length;i++){var s=navigator.mimeTypes[i].type;var m=s.match(/^application\/x-java-applet;jpi-version=(.*)$/);if(m!==null){thisJavaVersion=m[1];break;}
m=s.match(/^application\/x-java-applet;version=(.*)$/);if(m!==null){if((bestVersionSeen===undefined)||(m[1]>bestVersionSeen)){bestVersionSeen=m[1];}}}
if((thisJavaVersion===undefined)&&(bestVersionSeen!==undefined)){thisJavaVersion=bestVersionSeen;}}
if(thisJavaVersion===undefined){thisJavaVersion="0 - unknown";}}
return thisJavaVersion;}
var thisOSName;function getSystemOS(){if(thisOSName===undefined){thisOSName=findEntryInList(OSIDs);if(thisOSName===undefined){thisOSName="unknown";}}
return thisOSName;}
var thisMacOSVersion;function getMacOSVersion(){if(thisMacOSVersion===undefined){if("Mac"!=getSystemOS()){thisMacOSVersion="Not Mac";}
else{if(navigator.javaEnabled()&&browserCanAccessJava()){thisMacOSVersion=java.lang.System.getProperty("os.version");}
else{var av=navigator.appVersion;var m=av.match(/Mac OS X ([0-9_]*);/);if(null!==m){thisMacOSVersion=m[1];thisMacOSVersion=thisMacOSVersion.split("_").join(".");}}}
if(thisMacOSVersion===undefined){thisMacOSVersion="unknown";}}
return thisMacOSVersion;}
var overlayCount=0;var nameSeed;function getBogusJarFileName(){if(nameSeed===undefined){nameSeed=(new Date()).getTime();}
var uniqueNum=nameSeed++;return"emptyJarFile-"+uniqueNum;}
window.onunload=function(){};function isVersionAvailable(){var ret=true;if(("Safari"==getBrowser())&&("Mac"==getSystemOS())&&(getMacOSVersion().indexOf("10.4",0)===0)){ret=false;}
return ret;}
function fxOverlayEnabled(){return(getBrowser()!="Netscape Family"&&getBrowser()!="Opera")||getSystemOS()!="Mac";}
function fxAppletStarted(id){document.getElementById(id+"Overlay").style.visibility="hidden";document.getElementById(id).style.left="0px";}
function javafxString(launchParams,appletParams){var stringOutput="";try{deployJava.returnPage=document.location}catch(exception){alert("BadLocationCAll")};if(!navigator.javaEnabled()){alert("Java is required to run JavaFX applications.\n"+"You will now be redirected to a Java update site to get the latest version.\n");deployJava.installLatestJRE();}
if(isVersionAvailable()){var javaVersion=getJavaVersion();if(("V"+javaVersion)<"V1.5"){if("Mac"==getSystemOS()){var osVersion=getMacOSVersion();if(("V"+osVersion)<"V10.4"){alert("JavaFX requires Java 5.0 (1.5) or above.\n"+"Mac OS version "+osVersion+" does not support this Java version\n"+"Please upgrade your OS to 10.5 (Leopard) in order to run this application.");throw new Error("Invalid Java version for JavaFX");}
else{alert("JavaFX requires Java 5.0 (1.5) or above.\n"+"Please use Software Update to upgrade your Java version.");throw new Error("Invalid Java version for JavaFX");}}
else{alert("The current version of Java on this system ("+
javaVersion+") does not support JavaFX.\n"+"You will now be redirected to a Java update site to get the latest version.\n");deployJava.installLatestJRE();}}}
var standardArchives=["applet-launcher","javafx-rt","jmc"];switch(getSystemOS()){case"Mac":standardArchives.push("Decora-SSE");break;case"Windows":standardArchives.push("Decora-SSE");standardArchives.push("Decora-HW");standardArchives.push("Decora-D3D");break;case"Linux":standardArchives.push("Decora-HW");standardArchives.push("Decora-OGL");break;}
standardArchives.push(""+getBogusJarFileName());var versionNumber="1.0.1";var appletPlayer="org.jdesktop.applet.util.JNLPAppletLauncher";var tagLeadChar="<";var tagEndChar=">";var carriageReturn="\n";var appletTagParams={};appletTagParams.code=appletPlayer;var params={};params.codebase_lookup="false";params["subapplet.classname"]="com.sun.javafx.runtime.adapter.Applet";params.progressbar="false";params.classloader_cache="false";var key="";if(typeof launchParams!="string"){for(key in launchParams){switch(key.toLocaleLowerCase()){case"jnlp_href":params.jnlp_href=launchParams[key];break;case"version":versionNumber=launchParams[key];break;case"code":params.MainJavaFXScript=launchParams[key];break;case"name":params["subapplet.displayname"]=launchParams[key];break;case"draggable":params[key]=launchParams[key];break;case"displayhtml":if(launchParams[key]){tagLeadChar="&lt;";tagEndChar="&gt;";carriageReturn="<br>\n";}
break;default:appletTagParams[key]=launchParams[key];break;}}}else{appletTagParams.archive=launchParams;}
params.jnlpNumExtensions=2;params.jnlpExtension1="http://dl.javafx.com/jmc";if(versionNumber!==""){params.jnlpExtension1+="__V"+versionNumber+".jnlp";}else{params.jnlpExtension1+=".jnlp";}
params.jnlpExtension2="http://dl.javafx.com/Decora";if(versionNumber!==""){params.jnlpExtension2+="__V"+versionNumber+".jnlp";}else{params.jnlpExtension2+=".jnlp";}
if(params.jnlp_href===undefined){var loc=appletTagParams.archive.indexOf(".jar,");if(-1==loc){loc=appletTagParams.archive.lastIndexOf(".jar");}
if(-1!=loc){params.jnlp_href=appletTagParams.archive.substr(0,loc)+"_browser.jnlp";}}
for(var i=0;i<standardArchives.length;i++){appletTagParams.archive+=","+"http://dl.javafx.com/"+standardArchives[i];if(versionNumber!==""){appletTagParams.archive+="__V"+versionNumber;}
appletTagParams.archive+=".jar";}
if(fxOverlayEnabled()){var dtId="deployJavaApplet"+(++overlayCount);var width=appletTagParams.width;var height=appletTagParams.height;var imgURL;var imgWidth;var imgHeight;if(width>=100&&height>=100){imgURL='http://dl.javafx.com/javafx-loading-100x100.gif';imgWidth=100;imgHeight=100;}else{imgURL='http://dl.javafx.com/javafx-loading-25x25.gif';imgWidth=25;imgHeight=25;}
stringOutput+=tagLeadChar+'div id="'+dtId+'Overlay'+'" style="width:'+width+';height:'+height+';position:absolute;background:white"'+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'table width='+width+' height='+height+' border=0 padding=0 margin=0'+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'tr'+tagEndChar+tagLeadChar+'td align="center" valign="center"'+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'img src="'+imgURL+'" width='+imgWidth+' imgHeight='+imgHeight+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'/td'+tagEndChar+tagLeadChar+'/tr'+tagEndChar+tagLeadChar+'/table'+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'/div'+tagEndChar+carriageReturn;stringOutput+=tagLeadChar+'div id="'+dtId+'" style="position:relative;left:-10000px"'+tagEndChar+carriageReturn;}
stringOutput+=tagLeadChar+"APPLET MAYSCRIPT"+carriageReturn;for(key in appletTagParams){stringOutput+=key+"=";if(typeof appletTagParams[key]=="number"){stringOutput+=appletTagParams[key];}else{stringOutput+="\""+appletTagParams[key]+"\"";}
stringOutput+=carriageReturn;}
stringOutput+=tagEndChar+carriageReturn;if(!appletParams){appletParams={};}
appletParams.deployJavaAppletID=dtId;for(key in appletParams){params[key]=appletParams[key];}
for(key in params){stringOutput+=tagLeadChar+"param name=\""+key+"\" value=\""+params[key]+"\""+
tagEndChar+carriageReturn;}
stringOutput+=tagLeadChar+"/APPLET"+tagEndChar+carriageReturn;if(fxOverlayEnabled()){stringOutput+=tagLeadChar+"/div"+tagEndChar+carriageReturn;}
return stringOutput;}
function javafx(launchParams,appletParams){var stringOutput=javafxString(launchParams,appletParams);if(stringOutput){document.write(stringOutput);}}