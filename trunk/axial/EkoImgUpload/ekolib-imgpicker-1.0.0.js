/* 
 * eko-imgpicker - Image Picker-upload
 */


EkoImagePicker.prototype = {
    dialogElt:null,  // jQ element which is resizeable draggable
    status: function (message,isError,timeoutMS){

    },
    render: function (divselector){
        var ekoIP = this; // alias for this in callbacks!
        this.dialogElt = $('<div style="display:none;"></div>');


        this.dialogElt.addClass('eko-imgpicker-dialog');
        this.dialogElt.append(closeBtnElt);

        $(divselector).append(this.dialogElt);

    }
};
