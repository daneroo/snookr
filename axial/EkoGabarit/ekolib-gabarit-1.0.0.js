/*
 * Utiliser un gabarit EKO, et en faire l'edition en place
 * avec un editeur riche (CKEditor)
 */

function debug(message){
    if (window.console && window.console.firebug){
        console.log(message);
    }
}

EkoGabarit.prototype = {
    gabaritOid:121, // set in contructor through setOidas a field, prototype is also the global counter.
    previewJQ : null, // the jQ Object into which the preview is rendered
    valueDict: {}, // this is the JS Object literal into which replaced values are stored.
    // Theese are the four jQ/CK element hooks.
    currentEditingElt:null, // maintains state (which div is being edited!
    dialogElt:null,  // jQ element which is resizeable draggable
    ckElt:null,    // the jQ Object that the CKEditor was replaced into
    ckeditor:null, // the CKEditor object itself
    ekoIP:null, // eko-image-picker
    currentPickingImgElt:null, // picking image for image picker
    clearValues: function(){
        var prevEntries = 0;
        for(var key in this.valueDict){
            prevEntries++;
        }
        //debug('Clearing value dict: '+prevEntries+' previous entries');
        this.valueDict = {};
    },
    saveValue: function(key,value){ // Save to valueDict: (on save from different placeholders)
        // track dirtyness for saving (JSON) back to server.
        var dirty=true;
        if (this.valueDict ) {
            dirty = value != this.valueDict[key];
        }
        this.valueDict[key] = value;

        if (value.length && value.length>23) {
            value = value.substring(0,20)+'...';
        }
    //debug('Save ('+(dirty?'':'not ')+'dirty) ['+key+']='+value);
    },
    render: function (divselector){
        var ekoG = this; // alias for this in callbacks!
        this.dialogElt = $('<div></div>');
        this.dialogElt.css('position', 'absolute');

        this.dialogElt.addClass('eko-editor-dialog');
        var closeBtnElt=EkoActionIcon('ui-icon-closethick','Close');
        closeBtnElt.addClass('eko-editor-closebtn');
        closeBtnElt.click(function(){
            ekoG.discardWithDialog();
        });
        this.dialogElt.append(closeBtnElt);

        this.ckElt = $('<textarea class="dlg_ckeditor" cols="80" id="editor1" name="editor1" rows="10">CONTENT</textarea>');
        this.dialogElt.append(this.ckElt);
        $(divselector).append(this.dialogElt);

        this.dialogElt.resizable().draggable();
        this.dialogElt.hide();

        this.confirmElt = $('<div id="dialog-confirm" title="Save changes ?">You have unsaved edits in this text!</div>');
        $(divselector).append(this.confirmElt);
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
            ['ekodiscard', 'ekosave','Preview','Source', ,'-', 'Bold', 'Italic' ]
            ],
            extraPlugins:'ekosave,ekodiscard',
            ZZtoolbar: [
            ['Bold', 'Italic', '-', 'NumberedList', 'BulletedList', '-', 'Link', 'Unlink'],
            ['UIColor']
            ],
            resize_enabled : false,
            height: ck_editor_height
        };

        var ck_initCallback = function(){
            ekoG.ckeditor = ekoG.ckElt.ckeditorGet();
            ekoG.ckeditor.ekoGabarit = ekoG;
        }
        this.ckElt.ckeditor(ck_initCallback,ck_config);

        var baseURL = 'http://axial.imetrical.com/EkoImgUpload/';
        this.ekoIP = new EkoImagePicker(null,null,baseURL);
        this.ekoIP.selectedHandler = function(img) {
            debug('IP:Saving to eko-placehoder:');
            debug(img)
            debug(ekoG.currentPickingImgElt);
            var key = ekoG.currentPickingImgElt.attr('id');
            ekoG.saveValue(key,img.url);
            ekoG.currentPickingImgElt.find('img').attr('src',img.url);
            ekoG.currentPickingImgElt = null;
        };
        this.ekoIP.render(); // append to body
    },
    save: function(){
        var ekoG = this; // alias for this in callbacks!
        if(ekoG.currentEditingElt){
            debug('CK:Saving to eko-placehoder:');
            debug(ekoG.currentEditingElt);
            var key = ekoG.currentEditingElt.attr('id');
            var value = ekoG.ckElt.val();
            ekoG.saveValue(key,value);
            ekoG.currentEditingElt.html(value);
            ekoG.currentEditingElt = null; // not editing state
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
    pencilMark:function(pointedElt){
        var pencilElt = $('<div class="pencilmark ui-state-default ui-corner-all" title="Editable"><span class="ui-icon ui-icon-pencil"></span></div>');
        pencilElt.hover(function() {
            pencilElt.addClass('ui-state-hover');
        },function() {
            pencilElt.removeClass('ui-state-hover');
        })
        var type = $(pointedElt).attr('type')||'none';
        debug("pencil-type: "+type);
        if ('text'==type || 'html'==type){
            pencilElt.click(function(){
                $(pointedElt).dblclick();
            });
        } else if ('image'==type) {
            pencilElt.click(function(){
                $(pointedElt).find('img').click();
            });
        }

        var pencilholderElt = $('<div class="pencilmarkholder"></div>');
        pencilholderElt.append(pencilElt);

        $(pointedElt).before(pencilholderElt);
    },
    inject:function(){
        var ekoG = this; // alias for this in callbacks!
        ekoG.clearValues(); // clear the value dictionary.
        this.previewJQ.find('ekko-placeholder').each(function(index){
            //debug(this);
            var type = $(this).attr('type') || 'none';
            var key = $(this).attr('id') || 'none';
            // pencil mark
            ekoG.pencilMark(this);

            if ('text'==type){
                // populate dict with initial value
                ekoG.saveValue(key,$(this).text());

                var jEditableSubmitCallback = function(value,settings){
                    debug('JE:Saving to eko-placehoder:');
                    debug(this);
                    ekoG.saveValue(key,value);
                    return(value);
                };

                $(this).editable(jEditableSubmitCallback, {
                    //indicator : '<img src="images/indicator.gif">',
                    indicator : 'Saving...',
                    width: 300,
                    height:'2.5em',
                    type      : 'text',
                    event : 'dblclick',
                    cancel    : 'Cancel',
                    submit    : 'OK',
                    tooltip   : 'Double Click to edit...'
                });
            } else if ('html'==type) {
                // populate dict with initial value
                ekoG.saveValue(key,$(this).html());

                $(this).addClass("editable");
                $(this).hover(  function () {
                    $(this).addClass("editablehover");
                },
                function () {
                    $(this).removeClass("editablehover");
                });
                $(this).dblclick(function(){
                    //debug($.toJSON(ekoG.ckeditor.checkDirty()));
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
                    //debug('w:'+celtwidth+' -> '+$.toJSON(compensate));
                    ekoG.dialogElt.css({
                        'left':celtoff.left+compensate.left,
                        'top':celtoff.top+compensate.top
                    });
                    var nuwidth = celtwidth+compensate.width;
                    if (nuwidth<200) nuwidth=200;
                    ekoG.dialogElt.width(nuwidth);

                });
            } else if ('image'==type) {
                ekoG.saveValue(key,$(this).find('img').attr('src'));

                $(this).addClass("editable");
                $(this).hover(  function () {
                    $(this).addClass("editablehover");
                },
                function () {
                    $(this).removeClass("editablehover");
                });
                var ekoParentOfImg = $(this); // alias
                $(this).find('img').click(function(){
                    ekoG.currentPickingImgElt = ekoParentOfImg;
                    ekoG.ekoIP.open();
                });
            } else {
                // populate dict with initial value
                ekoG.saveValue(key,'unknown value for type: '+type);
                debug(this);
                debug('eko-tag type: '+type+' not in (text|html|image)');
            }
        });
    },
    load: function(gabaritURL){
        // TODO : prevent simultaneous loading ?
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
                ekoG.inject();
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


/* Theese were modified from EkoCOncours ekolib-1.0.0.js */
function EkoActionIcon(iconClass,hint) {
    // calls EkoIconWrapper...
    return EkoIconWrapper('',iconClass,'jeko-action-icon',hint);
}

function EkoButton(label,iconClass) {
    // calls EkoIconWrapper...
    label = label || "Add";
    return EkoIconWrapper(label,iconClass,'jeko-btn');
}

function EkoIconWrapper(label,iconClass,cssClass,hint) {
    /* return a new element for insertion into DOM
     * icon class : e.g. ui-icon-plus, ui-icon-arrowthick-1-n, etc
     * cssClass: eko-btn, eko-action-icon (arrown,..)
     */
    iconClass = iconClass || "ui-icon-plus";
    cssClass = cssClass || "jeko-btn";
    hint = hint||iconClass; // icon only has to have text set inthe button, used as hint and calculate height!
    var hasText=(''!=label);
    var btnElt = $('<button></button>').text(hasText?label:hint);
    btnElt.addClass(cssClass);
    btnElt.button({
        icons: {
            primary: iconClass
        },
        text: hasText
    });
    return btnElt;
}

