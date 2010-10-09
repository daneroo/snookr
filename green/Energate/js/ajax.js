/*
 * Ajax method wrappers for jEko
 */

(function() {
    $.extend(jEko,{
        json:{
            get: function(url,callback){
                return $.ajax({
                    type: 'GET',
                    url: url,
                    dataType: 'json',
                    success: callback
                });
            },
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
