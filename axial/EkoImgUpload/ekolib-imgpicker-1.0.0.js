/* 
 * eko-imgpicker - Image Picker-upload
 */


EkoImagePicker.prototype = {
    dialogElt:null,  // jQ element which is resizeable draggable
    statusElt: null, // the status div
    statusIconElt: null,
    statusTxtElt: null,
    fileimglistElt:null, // the ul for the list of files
    status: function (message,isError,timeoutMS){
        var ekoIP = this; // alias for this in callbacks!
        var fadeinMS=1000;
        var fadeoutMS=1000;
        timeoutMS = timeoutMS || 5000;
    
        // icon and alert/highligh toggle
        if (isError){
            this.statusElt.addClass('ui-state-error');
            this.statusElt.removeClass('ui-state-highlight');
            this.statusIconElt.addClass('ui-icon-alert');
            this.statusIconElt.removeClass('ui-icon-info');
        } else {
            this.statusElt.addClass('ui-state-highlight');
            this.statusElt.removeClass('ui-state-error');
            this.statusIconElt.addClass('ui-icon-info');
            this.statusIconElt.removeClass('ui-icon-alert');
        }
        // the text
        this.statusTxtElt.text(message);
        //this.statusElt.show('fade',null,20000,fadeBackOut);
        this.statusElt.fadeIn(fadeinMS,function(){
            setTimeout(function(){
                ekoIP.statusElt.fadeOut(fadeoutMS);
            },timeoutMS);
        });
    },
    render: function (divselector){
        var ekoIP = this; // alias for this in callbacks!
        this.dialogElt = $('<div></div>');
        this.dialogElt.addClass('eko-imgpicker-dialog');
        // status bar
        this.statusElt = $('<div></div>')
        .addClass('ui-corner-all')
        .addClass('ui-state-highlight')
        .css('padding','0 .7em')
        .hide();
        this.statusIconElt = $('<span style="float: left; margin-right: .3em;"></span>')
        .addClass('ui-icon')
        .addClass('ui-icon-alert')
        .css({
            "float": "left",
            "margin-right": ".3em"
        });
        this.statusTxtElt = $('<span>Status goes here</span>');
        this.statusElt.append(this.statusIconElt).append(this.statusTxtElt);
        this.dialogElt.append(this.statusElt);

        // the upload button
        this.uploadBtn = $('<a href="#">Upload Image</a>');
        this.uploadBtn.addClass('eko-button ui-state-default ui-corner-all');
        this.dialogElt.append($('<div></div>').append(this.uploadBtn));
        // The file list of images
        this.fileimglistElt = $('<ul></ul>').addClass('eko-fileimglist');
        this.dialogElt.append(this.fileimglistElt);

        $(divselector).append(this.dialogElt);

        this.dialogElt.dialog({
            autoOpen: false,
            width: 500,
            height:400,
            modal: false,
            buttons: {
                'Cancel': function() {
                    $(this).dialog('close');
                }
            }
        });

        //var baseURL = '';
        var baseURL = 'http://axial.imetrical.com/EkoImgUpload/';

        var ajaxopts = {
            action: baseURL+'upload-file.php',
            name: 'uploadfile',
            onSubmit: function(file, ext){
                if (! (ext && /^(jpg|png|jpeg|gif)$/.test(ext))){
                    // extension is not allowed
                    ekoIP.status('Only JPG, PNG or GIF files are allowed',true);
                    return false;
                }
                ekoIP.status('Uploading...',false);
                // what is this diabling ?
                this.disable();
            },
            onComplete: function(file, response){
                //On completion clear the status
                ekoIP.status('Empty',false);
                //Add uploaded file to list
                if(response==="success"){
                    addIMG('./uploads/'+file);
                } else{
                    ekoIP.status('failed: '+response+' - '+file,true);
                //addERR(file);
                }
                //getList();
                this.enable();
            }
        };
        new AjaxUpload(this.uploadBtn, ajaxopts);


    }
};

function EkoImagePicker() {
}
