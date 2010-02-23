/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
Just to isolate the rendering code
 */

//$('#tabs').tabs({

var globalPreviewOid=222;
function getOid(){
    globalPreviewOid+=1;
    return globalPreviewOid;
}

function renderPreview(divselector,contest){
    $(divselector).html('');
    $(divselector).append(EkoMakePreview(contest1));
    $(divselector+' .steps').tabs();
}
function EkoMakePreview(contest){

    var contestElt = $('<div class="contest preview"/>');
    // make some tabs for the steps
    var contestheaderElt = $('<h3><i>Contest: </i></h3>');
    contestheaderElt.append($('<span></span>').text(contest.name));
    contestElt.append(contestheaderElt);

    var tabsElt = $('<ul ></ul>');
    var stepsElt = $('<div class="steps"></div>');
    stepsElt.append(tabsElt);
    for (var s=0;s<contest.steps.length;s++ ){
        var step = contest.steps[s];

        //				<li><a href="#tabs-1">First</a></li>
        var tabId = 'tab-'+getOid();
        var lnkElt = $('<a/>');
        lnkElt.attr('href', '#'+tabId);
        lnkElt.text('Step '+(s+1));
        var tabElt = $('<li/>').append($(lnkElt));
        tabsElt.append(tabElt);

        var stepElt = $('<div id="'+tabId+'" class="step"/>');

        var introElt = $('<div class="intro"/>');
        introElt.text(step.intro);
        stepElt.append(introElt);

        //each field
        var fieldsElt = $('<ul class="fields"/>');
        //for in step.fields:
        for (var f=0;f<step.fields.length;f++ ){
            var field = step.fields[f];
            //var fieldElt = EkoFieldBase(field);
            var fieldElt = $('<div/>');
            //fieldElt.text($.toJSON(field));
            var labelElt = $('<span class="fieldLabel"/>');
            labelElt.text(field.label);
            //var inputElt = $('<input type="text" value=""></input>');
            //inputElt.attr("value","initial value");
            var inputElt = EkoMakeInput(field);

            fieldElt.append($('<div class="field">').append(labelElt).append(inputElt));
            fieldsElt.append(fieldElt);
        }
        stepElt.append(fieldsElt);

        stepsElt.append(stepElt);
    }

    //GGGGG = stepsElt;
    //$(stepsElt).tabs()

    contestElt.append(stepsElt);
    return contestElt;
}

function EkoMakeInput(field){
    if (field.type=='EKO') {
        var inputElt = $('<input class="field-shorttext" type="text" value=""></input>');
        return inputElt;
    } else if (field.type=='TAG') {
        if (field.subtype=='shorttext') {
            var inputElt = $('<input class="field-shorttext" type="text" value=""></input>');
            return inputElt;
        } else if (field.subtype=='longtext') {
            var inputElt = $('<textarea class="field-longtext"/>');
            return inputElt;
        } else if (field.subtype=='hidden') {
            var inputElt = $('<input class="field-shorttext" type="hidden" value=""></input>');
            return inputElt;
        }
    } else if (field.type=='CHOICE') {
        if (field.subtype=='dropdown') {
        } else if (field.subtype=='radio') {
        } else if (field.subtype=='check') {
    }

    }
    var selElt = $('<select class="field-dropdown"></select>');
    for (var g=0; g<field.options.length; g++) {
        var gr = field.options[g];
        var optElt = $('<option value="'+gr.name+'">'+gr.name+'</option>');
        if (gr.label) {
            optElt.text(gr.label);
        } else {
            //optElt.text('Dflt4: '+gr.name);
            optElt.text(EkoGroupDefaultLabel(gr.name));
        }
        selElt.append(optElt);
    }
    return selElt;

}