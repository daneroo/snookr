/*
 * Utiliser un gabarit EKO, et en faire l'edition en place
 * avec un editeur riche (CKEditor)
 */

EkoGabarit.prototype = {
    gabaritOid:111, // set in contructor through setOidas a field, prototype is also the global counter.
    previewJQ : null,
    // Theese are the three jQ element hooks.
    currentEditingElt:null,
    dialogElt:null,
    ckElt:null,    // j\the jQ Object that the CKEditor was replaced into
    ckeditor:null, // the CKEditor object itself
    debugelt:null,
    debug:function(message){
        if (this.debugelt) {
            $(this.debugelt).text('DBG:'+' '+message);
        }
    },
    render: function (divselector){
        this.ckElt = $('<textarea class="dlg_ckeditor" cols="80" id="editor1" name="editor1" rows="10">CONTENT</textarea>');
        this.dialogElt = $('<div style="position:absolute;">').append(this.ckElt);
        $(divselector).append(this.dialogElt);
        this.dialogElt.resizable().draggable();
        this.dialogElt.hide();

        this.confirmElt = $('<div id="dialog-confirm" title="Save changes ?">You have unsaved edits in this text!</div>');
        $(divselector).append(this.confirmElt);
        var ekoG = this; // alias for this in callbacks!
        this.confirmElt.dialog({
            resizable: false,
            //height:180,
            modal: true,
            autoOpen: false,
            buttons: {
                'Cancel': function() {
                    $(this).dialog('close');
                },
                'Save': function() {
                    $(this).dialog('close');
                    ekoG.save();
                },
                'Discard': function() {
                    $(this).dialog('close');
                    var invokeDialogIfDirty = false
                    ekoG.discard(invokeDialogIfDirty);
                }
            }
        });

        var ck_editor_height=300;
        var ck_config = {
            BSCtoolbar:'Basic',
            toolbar: [
            ['ekodiscard', 'ekosave','Preview' ,'-', 'Bold', 'Italic' ]
            ],
            extraPlugins:'ekosave,ekodiscard',
            ZZtoolbar: [
            ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink'],
            ['UIColor']
            ],
            resize_enabled : false,
            height: ck_editor_height
        };

        var ekoG = this; // alias for this in callbacks!
        var ck_initCallback = function(){
            ekoG.ckeditor = ekoG.ckElt.ckeditorGet();
            ekoG.ckeditor.ekoGabarit = ekoG;
        }
        this.ckElt.ckeditor(ck_initCallback,ck_config);

    },
    save: function(){
        var ekoG = this; // alias for this in callbacks!
        if(ekoG.currentEditingElt){
            ekoG.currentEditingElt.html(ekoG.ckElt.val());
            ekoG.currentEditingElt = null;
            ekoG.dialogElt.hide();
        }
    // add an ajax/update hook here...
    },
    discardWithDialog: function(){ // willcause save or discard on dialog click!
        if (this.isDirty()) {
            this.confirmElt.dialog('open');
        } else {
            this.discard();
        }
    },
    discard: function(){ // juste discard, no dialog
        if(this.currentEditingElt){
            this.currentEditingElt = null;
        }
        this.dialogElt.hide();
    },
    isDirty: function() {
        // cannot count on ckeditor.checkDirty()!
        if (!this.currentEditingElt) {
            return false;
        }
        return this.currentEditingElt.html()!=this.ckElt.val();
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
                //ekoG.debug($.toJSON(ekoG.ckeditor.checkDirty()));
                if (ekoG.isDirty()){
                    ekoG.confirmElt.dialog('open');
                    return;
                }
                ekoG.currentEditingElt = $(this);
                var celtoff = ekoG.currentEditingElt.offset();
                var celtwidth = ekoG.currentEditingElt.width();

                ekoG.ckElt.val( ekoG.currentEditingElt.html() );
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

                ekoG.ckeditor.resetDirty();
                ekoG.debug('XXX:'+$.toJSON(ekoG.ckeditor.checkDirty()));
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


