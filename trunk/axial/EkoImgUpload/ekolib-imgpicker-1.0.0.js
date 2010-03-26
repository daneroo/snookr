/* 
 * eko-imgpicker - Image Picker-upload
 */

function debug(message){
    if (window.console && window.console.firebug){
        console.log(message);
    }
}

EkoImagePicker.prototype = {
    getListURL: null, // getListURL, to get json struct og images
    uploadURL: null, // uploadURL, to post image to, remove upload button if not present
    dialogElt:null,  // jQ element which is resizeable draggable
    statusElt: null, // the status div
    statusIconElt: null,
    statusTxtElt: null,
    fileimglistElt:null, // the ul for the list of files
    info: function(message,timeoutMS){
        this.statusHelper(message, false, timeoutMS)
    },
    error: function(message,timeoutMS){
        timeoutMS = timeoutMS || 4000;
        this.statusHelper(message, true, timeoutMS)
    },
    statusHelper: function (message,isError,timeoutMS){
        var ekoIP = this; // alias for this in callbacks!
        var fadeinMS=200;
        var fadeoutMS=200;    
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
        this.statusTxtElt.text(message);
        // animation flow: fadeIn: fadeInMS
        //             if timeoutMS{
        //                 wait: timeoutMS
        //                 fadeOut: fadeOutMS
        //             }
        this.statusElt.fadeIn(fadeinMS,function(){
            if (timeoutMS) { // if !timeoutMS, dont wait and fade
                setTimeout(function(){
                    ekoIP.statusElt.fadeOut(fadeoutMS);
                },timeoutMS);
            }
        });
    },
    // if no divselector; appen to body.
    render: function (divselector){
        divselector  = divselector || $('body')
        var ekoIP = this; // alias for this in callbacks!
        this.dialogElt = $('<div></div>');
        this.dialogElt.attr('title','Choose (or upload) an Image');
        this.dialogElt.addClass('eko-imgpicker-dialog');
        // status bar
        this.statusElt = $('<div></div>')
        .addClass('ui-corner-all')
        .addClass('ui-state-highlight')
        .css('padding','.5em .7em')
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
        this.dialogElt.append($('<div></div>').append(this.uploadBtn)/*.css('padding','.5em .7em')*/);
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

        var ajaxopts = {
            action: this.uploadURL,
            name: 'uploadfile',
            onSubmit: function(file, ext){
                if (! (ext && /^(jpg|png|jpeg|gif)$/.test(ext.toLowerCase()))){
                    // extension is not allowed
                    ekoIP.error('Only JPG, PNG or GIF files are allowed');
                }
                ekoIP.info('Uploading...');
            },
            onComplete: function(file, response){
                //On completion clear the status
                if(response==="success"){
                    ekoIP.addIMG('./uploads/'+file);
                    ekoIP.info('Uploaded sussecsfully',1000);
                } else{
                    ekoIP.error('Failed to upload: '+response+' - '+file);
                }
                ekoIP.getList();
                this.enable();
            }
        };
        new AjaxUpload(this.uploadBtn, ajaxopts);
    },
    addIMG: function (img){ // object with url, thumb and name props
        var ekoIP = this; // alias for this in callbacks!
        $('<li></li>').appendTo(this.fileimglistElt)
        .html('<img src="'+img.thumb+'" alt="'+img.name+'" />')
        .click(function(){
            ekoIP.dialogElt.dialog("close");
            ekoIP.selectedHandler(img);
        });
    },
    replaceIMGList: function (images){
        // clear then add
        this.fileimglistElt.html('');
        for (var i=0;i<images.length;i++){
            this.addIMG(images[i]);
        }
    },
    getList: function(){
        var ekoIP = this; // alias for this in callbacks!
        //debug('getting: '+this.getListURL);
        $.ajax({
            url: this.getListURL, // url from name ??
            dataType: 'jsonp', // html,xml,json,jsonp
            jsonp:'jsonp',
            async: true,
            success: function(data) {
                ekoIP.replaceIMGList(data.images);
            }
        });
    },
    selectedHandler: function(img){
        alert('selected: '+img.url);
    },
    open: function(){
        // make sure status is reset...(how about pending uploads ?)
        this.statusTxtElt.text('');
        this.statusElt.hide();
        this.getList();
        this.dialogElt.dialog("open");
    }
};

function EkoImagePicker(getListURL,uploadURL,baseURL) {
    // params: getListURL, to get json struct of images
    // params: uploadURL, to post image to, remove upload button if not present
    // baseURL to prepend to both URLs (if present
    baseURL = baseURL || '';
    getListURL = getListURL || 'imglist.php'
    this.getListURL = baseURL+getListURL;
    uploadURL = uploadURL || 'upload-file.php';
    this.uploadURL = baseURL+uploadURL;
    debug('get list from: '+this.getListURL);
    debug('upload to: '+this.uploadURL);
}
