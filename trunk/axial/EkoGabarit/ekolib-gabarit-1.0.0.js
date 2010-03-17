/*
 * Utiliser un gabarit EKO, et en faire l'edition en place
 * avec un editeur riche (CKEditor)
 */

EkoGabarit.prototype = {
    gabaritOid:111, // set in contructor through setOidas a field, prototype is also the global counter.
    previewJQ : null,
    currentEditingElt:null,
    debugelt:null,
    debug:function(message){
        if (this.debugelt) {
            $(this.debugelt).text('DBG:'+' '+message);
        }
    },
    render: function(){

    },
    save: function(){
        var ekoG=this;
        if(ekoG.currentEditingElt){
            //alert(editor.getData());
            ekoG.currentEditingElt.html(ekoG.ckElt.val());
            ekoG.currentEditingElt.show();
            ekoG.dialogElt.hide();
        }
        // add an ajax/update hook here...
    },
    inject:function(){
        var ekoG = this; // alias for this in callbacks!

        this.previewJQ.find('ekko-placeholder').each(function(index){
            $(this).addClass("editable");
            $(this).hover(  function () {
                $(this).addClass("editablehover");
            },
            function () {
                $(this).removeClass("editablehover");
            });
            $(this).dblclick(function(){
                ekoG.currentEditingElt = $(this);
                var celtoff = ekoG.currentEditingElt.offset();
                var celtwidth = ekoG.currentEditingElt.width();
                //alert('pos: '+$.toJSON(celtoff)+' '+ekoG.currentEditingElt.width()+'x'+ekoG.currentEditingElt.height());

                ekoG.ckElt.val( ekoG.currentEditingElt.html() );
                //ekoG.currentEditingElt.hide();
                //ekoG.dialogElt.insertAfter(ekoG.currentEditingElt);
                ekoG.dialogElt.show();
                var dialogEltOffset = ekoG.dialogElt.offset();
                var contentElt = ekoG.dialogElt.find('.cke_contents');
                var contentEltOffset = contentElt.offset();
                var compensate = {
                    left : dialogEltOffset.left-contentEltOffset.left,
                    top: dialogEltOffset.top-contentEltOffset.top,
                    width: ekoG.dialogElt.width()-contentElt.width()
                };
                //$('#status').text('w:'+celtwidth+' -> '+$.toJSON(compensate));
                ekoG.dialogElt.css({
                    'left':celtoff.left+compensate.left,
                    'top':celtoff.top+compensate.top
                });
                var nuwidth = celtwidth+compensate.width;
                if (nuwidth<200) nuwidth=200;
                ekoG.dialogElt.width(nuwidth);
            });
        });
    },
    load: function(gabaritURL){
        var ekoG = this; // alias for this in callback!
        $.ajax({
            url: gabaritURL, // url from name ??
            //data: data,
            dataType: 'html', // html,xml,json,jsonp
            async: false,
            success: function(data) {
                var htmlString = data;
                //ckElt.val( htmlString );
                ekoG.previewJQ.html( htmlString );
            }
        });
    },
    getOid: function(){ // can be used for class-wide (static) counter
        return this.gabaritOid;
    },
    setOid: function(){
        this.gabaritOid = this.getOid();
    },
    genOid: function(){ // static methid! can be used for class-wide (static) counter
        EkoGabarit.prototype.gabaritOid+=1;
        return EkoGabarit.prototype.gabaritOid;
    }
};


function EkoGabarit(previewSelector) {
    this.setOid();
    this.previewJQ = $(previewSelector);
}


