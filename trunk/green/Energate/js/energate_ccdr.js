/*
 * Copyright 2010 Daniel Lauzon <daniel.lauzon@gmail.com>
 */

function debug(message){
    if (window.console && window.console.firebug){
        console.log(message);
    } else if (window.console) {
        console.log(message);
    }
}

var nextJsonInvocationId=9973
function invoke(endpointurl,methodName,params,callbackFunction){
    endpointurl = endpointurl || "service/proxyCCDRService.php";
    var async=false;
    if (callbackFunction){
        async=true;
    }

    // JayRock's proxys use text/plain, we will use application/json
    //var requestContentType="text/plain; charset=utf-8";
    var requestContentType="application/json; charset=utf-8";
    var requestParamWrapper = {
        id: nextJsonInvocationId++,
        method: methodName,
        params: params
    };
    // not sure if I have to encode myself !
    var dataToPost = $.toJSON(requestParamWrapper);
    //debug("dataToPost: "+dataToPost);

    var response;
    //debug("invoking: "+dataToPost);
    $.ajax({
        type: 'POST',
        async: async,
        url: endpointurl,
        dataType: 'json',
        contentType: requestContentType,
        data: dataToPost,
        success: function(data, textStatus) {
            response=data;
            //debug("invoke success");
            //debug(data);
            if (callbackFunction) {
                callbackFunction(response.result);
            }
        },
        error:function(xhr, textStatus, errorThrown){
            debug(errorThrown);
        }
    });
    if (async) {
        return requestParamWrapper.id;
    }
    return response.result;
};

function login(username,passwd){
    if (0) return {
        "homeId":"1",
        "zipCode":"K1V7P1",
        "macAddr":"001BC500B00015DB",
        "thermName":"smckenzie tstat"
    };
    return invoke(null, "login", [username,passwd], null);
}

function getWeather(sessionVars){
    return invoke(null, "getWeatherFeed", [sessionVars.zipCode], null);

    // get weather feed:
    // {"GetWeatherFeed":{"strZIP":"K1V7P1"}
    // {"GetWeatherFeedResult":{"CurrTemp":"8","MaxCurrForcast":"10","MaxNextForecast":"10","MinCurrForecast":"3","MinNextForecast":"3"}}
    return {
        "CurrTemp":"8",
        "MaxCurrForcast":"10",
        "MaxNextForecast":"10",
        "MinCurrForecast":"3",
        "MinNextForecast":"3"
    };
}
function getThermostatDetails(sessionVars){
    return invoke(null, "getThermostatDetails", [sessionVars.macAddr], null);
    return {
        "CurrHoldName":"None",
        "DataValid":"true",
        "Details":{
            "ActiveCoolTemp":"18",
            "ActiveHeatTemp":"10",
            "ConsCoolOffset":"0",
            "ConsCoolTemp":"0",
            "ConsHeatOffset":"0",
            "ConsHeatTemp":"0",
            "ControlModeHeating":"false",
            "CoolStages":"1",
            "CurrHoldType":"None",
            "CurrIndoorTemp":"21.62",
            "DrlcCoolOffset":"0",
            "DrlcCoolTemp":"0",
            "DrlcHeatOffset":"0",
            "DrlcHeatTemp":"0",
            "DrlcInUse":"false",
            "DrlcOverridden":"false",
            "EqMode":"1",
            "EqType":"3",
            "FanMode":"true",
            "FanOn":"true",
            "FilterFault":"false",
            "HeatPumpDisabled":"false",
            "HeatPumpFault":"false",
            "HeatStages":"1",
            "HoldEndTime":"0",
            "HoldSpIndex":"0",
            "LowBattery":"false",
            "OutdoorTemp":"-46.87",
            "RemoteIndoorTemp":"0",
            "SchedCoolTemp":"17.78",
            "SchedHeatTemp":"10",
            "ShortCircuit":"false",
            "StagesEngaged":"1",
            "TempSavSpInProgress":"false",
            "TouInUse":"false",
            "TouOverridden":"false"
        },
        "InActiveDateTime":[]
    };
}

function slSetMode(sessionVars,mode,fan){
    // mode = mode-auto,mode-heat,mode-cool,mode-off
    // strEqMode-> Auto,HeatOnly,CoolOnly,Off
    // fan = fan-auto, fan-on
    // strFanMode Auto,On
    var trans = {
        "mode-auto":"Auto",
        "mode-heat":"HeatOnly",
        "mode-cool":"CoolOnly",
        "mode-off":"Off",
        "fan-auto":"Auto",
        "fan-on":"On"
    };
    var strEqMode = trans[mode];
    var strFanMode = trans[fan];
    //debug(["slSetMode:Params:",strEqMode,strFanMode,sessionVars.macAddr]);
    return invoke(null, "slSetMode", [strEqMode,strFanMode,sessionVars.macAddr], null);    
}

function slSetHold(sessionVars){

/*
    <tns:SLSetHold xmlns:tns="http://tempuri.org/">
      <tns:strVacationValue>0</tns:strVacationValue>
      <tns:strHoldType>None</tns:strHoldType>
      <tns:strMacAddr>001BC500B00015DB</tns:strMacAddr>
      <tns:nCool/>
      <tns:nHeat/>
      <tns:btStartIndex>10</tns:btStartIndex>
    </tns:SLSetHold>
    */
}

