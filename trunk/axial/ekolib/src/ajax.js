/*
 * Ajax method wrappers for ekolib
 */

(function() {
    $.extend(eko,{
        json:{
            get: function(url,callback){
                return $.ajax({
                    type: 'GET',
                    url: url,
                    dataType: 'json',
                    success: callback
                });
            },
            /*  Idea:
                $.extend(data,{accent:"e"})
                var newdata = $.tojSON(data);
                var postdata = {json:newdata, eacut:"e"}

                contentType: "application/json; charset=utf-8",
                url: "WebService.asmx/WebMethodName",
                data: "{}",
            */
            post: function(url,data,callback,contentType){
                return $.ajax({
                    type: 'POST',
                    url: url,
                    data: data,
                    dataType: 'json',
                    success: callback,
                    contentType:contentType
                });
            }
        }
    });

})();
