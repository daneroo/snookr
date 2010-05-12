/* 
 * core library components for eko lib
 */

(function() {
    /* should eko be an instance or a prototyped class ?
     *  it will be an instance for now.
     */

    var eko={};
    eko.version = "@VERSION";
    
    $.extend(eko,{
        debug: function(message){
            if (window.console && window.console.firebug){
                console.log(message);
            }
        },
        isEmpty: function ( value ) {
            if (!value) return true;
            if ($.trim(String(value)).length==0) return true;
            return false;
        }
    });

    // Expose eko to the global object
    window.eko = eko;

})();

