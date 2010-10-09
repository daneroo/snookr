/*
 * Ajax-proxy method wrappers for jEko
 * This proxy service was developped to facilitate
 * the use of JayRock based services, it implements invocation
 * and proxy semantics for this access
 *
 * The dynamically generated proxy (generateFromMethods)
 * has no knowledg of the signature of the invoked method,
 * so it assumes that if the last parameter in the service invocation
 * is a function, that it is a callback, and that asynchronous invocation
 * is to be triggered:
 * e.g.
 *     var svc = jEko.proxy.generate(this.svcuri);
 *     svc.someMethod(p1,p2,p3); // will perform a synchronous call
 *   whereas
 *     svc.someMethod(p1,p2,p3,callback); // will perform an asynchronous call
 */

(function() {
    var nextJsonInvocationId=9973; // arbitrary invocationId starting point

    $.extend(jEko,{
        proxy:{
            // the stub method used to select the kind of proxy generator to use by default
            generate: function(endpointurl){
                //return jEko.proxy.staticproxy(endpointurl);
                //return jEko.proxy.generateFromMethods(endpointurl);
                return jEko.proxy.generateFromService(endpointurl);
            },
            // proxy is generated with generateFromMethods, but methods are fetched from the service itself.
            generateFromService: function(endpointurl){
                var methods = jEko.proxy.invoke(endpointurl,"system.listMethods",[],null);
                //debug("getproxy methods: "+$.toJSON(methods));
                return jEko.proxy.generateFromMethods(endpointurl,methods);
            },

            // proxy is generated from a list of methods (passed as param, with integrationtest as the default
            generateFromMethods: function(endpointurl,methods){
                var url=endpointurl;
                methods = methods || ["echo","zecho","describe","count","dicoClear","dicoGet","dicoSet","dicoList"];
                function dynamicInvoke(methodName,params){
                    //debug("dynproxy::"+methodName+" invoked with "+params.length+" args");
                    var callback=null;
                    if (params.length>0){
                        var lastarg = params[params.length-1];
                        if ("function"==typeof(params[params.length-1])){
                            callback = params[params.length-1];
                            params = params.slice(0, -1);
                        }
                    }
                    return jEko.proxy.invoke(url,methodName,params,callback);
                }

                var methodGenerator = function(methodName){ // wrapper to scope local iteration vars
                    return function(){
                        // copy arguments to (a real) array. it looks like, but is NOT an array.
                        var params=[];
                        for (var a=0;a<arguments.length;a++){
                            params.push(arguments[a]);
                        }
                        return dynamicInvoke(methodName,params);
                    };
                };
                var service = {};
                for (var m=0;m<methods.length;m++){
                    service[methods[m]] = methodGenerator(methods[m]);
                }
                return service;
            },
            invoke: function(endpointurl,methodName,params,callbackFunction){
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
            }
        }
    });

})();
