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

var nextJsonInvocationId=456
function invoke(endpointurl,methodName,params,callbackFunction){
    endpointurl = endpointurl || "service/proxyService.php";
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
    return invoke(null, "login", [username,passwd], null);
}
function getit(sessionCookie,username){
    return invoke(null, "getit", [sessionCookie,username], null);
}
function storeit(sessionCookie,username,hvac,program){
    return invoke(null, "storeit", [sessionCookie,username,hvac,program], null);
}

