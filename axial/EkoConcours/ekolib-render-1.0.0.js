/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Just to isolate the rendering code
 * Navigation through the step panels accumulates a response object, and a validation state
 *
 * response = [
 *   { '1.1:EKO:nom:Nom':'Lauzon'},
 *   { '1.2:TAG:shorttext:Occupation':'Menuisier'}
 * ];
 *
 * validation:
 *    includes only none, and require for now, they are both single-field client-side validations
 * Spec: we want to provide visual feedback of validation on Step navigation,
 *   also preventing navigation, if not validated.
 *   --Preventing Navigation on ivalid might require hooking in to the tabs events
 *   we will only do the next/previous/post, (actually previous should aways be fine.)
 *  So validation needs the 'Step' concept.
 *  the basic field validation, should indicate success/failure, and errorcode/message on failure
 */

function renderPreview(divselector,contest){
    // replaces
    $(divselector).html('');
    $(divselector).append(EkoMakePreview(contest1));
    $(divselector+' .steps').tabs();
}

// Fatory of DOM Elements
function EkoMakePreview(contest){
    var response = {}; // map of key/value objects:
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

        var validatorsForStep = []; // accumulate field validators
        //each field
        var fieldsElt = $('<ul class="fields"/>');
        //for in step.fields:
        for (var f=0;f<step.fields.length;f++ ){
            var field = step.fields[f];
            // this is how we mangle the stet/field/type info into a key
            var fieldResponseKey = ''+s+'.'+f+':'+field.type+':'+field.subtype+':'+field.label;
            var fieldElt = $('<div/>');
            var labelElt = $('<span class="fieldLabel"/>');
            labelElt.text(field.label);
            var validatorElt = $('<span class="validatorLabel"/>');
            var validationCB = eko.validators[field.validation];
            var validatorForField = validatorGenerator(validationCB,response,fieldResponseKey,validatorElt);
            var inputElt = EkoMakeInput(field,response,fieldResponseKey,validatorForField);
            validatorsForStep.push(validatorForField);

            fieldElt.append($('<div class="field">').append(labelElt).append(inputElt).append(validatorElt));

            fieldsElt.append(fieldElt);
        }
        stepElt.append(fieldsElt);

        // calback generator
        function genGoto(stepsElt,stepidx,validatorsForStep){
            return function(){
                // handles negatives, and wraparound....
                var ss = (stepidx+2*contest.steps.length)%contest.steps.length;
                var previouslySelected = stepsElt.tabs('option', 'selected');
                showResponse(response,'Step');
                // if valid
                var okstep = true;
                for (var v=0;v<validatorsForStep.length;v++){
                    var okfield = validatorsForStep[v]();
                    if (!okfield) okstep=false;
                }
                //alert('step ok?: '+okstep+' validators: '+validatorsForStep.length);
                if (okstep) stepsElt.tabs('select',ss);
            }
        }

        if (s>0){
            var prvBtn = EkoButton('Prev Step');
            prvBtn.click(genGoto(stepsElt,s-1,validatorsForStep));
            stepElt.append(prvBtn);
        }
        if (s<(contest.steps.length-1)){
            var nxtBtn = EkoButton('Next Step');
            nxtBtn.click(genGoto(stepsElt,s+1,validatorsForStep));
            stepElt.append(nxtBtn);
        } else if (s==(contest.steps.length-1)) {
            var postBtn = EkoButton('Submit');
            // temporary to get validation
            postBtn.click(genGoto(stepsElt,s,validatorsForStep));
            postBtn.click(function(){
                showResponse(response,'Form');
            });
            stepElt.append(postBtn);
        }
        stepsElt.append(stepElt);
    }

    //GGGGG = stepsElt;
    //$(stepsElt).tabs()

    contestElt.append(stepsElt);
    return contestElt;
}
function showResponse(boundDict,scope){
    var jsonText = $.toJSON(boundDict);
    jsonText = jsonText.replace(/,/g,',<br />');
    $('#previewpost').html('The '+scope+' updated: <div>'+jsonText+'</div>');
}

function validatorGenerator(validationCB,response,fieldResponseKey,validatorElt) {
    var validatorForField = function (){
        //alert('field: '+fieldResponseKey);
        if (validationCB) {
            var propertyVal = response[fieldResponseKey];
            var returnCode = validationCB(propertyVal);
            // make error text conditional on !valid
            if (!returnCode[0]) {
              validatorElt.text(returnCode[1]);  
            } else {
                // clear the text
                validatorElt.text('');
            }
            return returnCode[0];
        }
        return true;
    }
    return validatorForField;
}

function EkoMakeInput(field,boundDict,propertyName,validatorFeedbackCallback){
    // protect against bad/null callback param by wrapping it up
    var validatorFBCB = function() {
        if (!validatorFeedbackCallback) return;
        if (typeof(validatorFeedbackCallback)!='function') return;
        validatorFeedbackCallback();
    }
    var onchangeTextcallback=function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
        showResponse(boundDict,'Field');
        validatorFBCB();
    };
    // initial value for response array
    // aka: response[fieldResponseKey]=null;
    boundDict[propertyName]=null;

    if (field.type=='EKO') {
        var inputElt = $('<input class="field-shorttext" type="text" value=""></input>');
        inputElt.change(onchangeTextcallback);
        return inputElt;
    } else if (field.type=='TAG') {
        if (field.subtype=='shorttext') {
            var inputElt = $('<input class="field-shorttext" type="text" value=""></input>');
            inputElt.change(onchangeTextcallback);
            return inputElt;
        } else if (field.subtype=='longtext') {
            var inputElt = $('<textarea class="field-longtext"/>');
            inputElt.change(onchangeTextcallback);
            return inputElt;
        } else if (field.subtype=='hidden') {
            var inputElt = $('<input class="field-shorttext" type="hidden" value=""></input>');
            inputElt.change(onchangeTextcallback);
            return inputElt;
        }

    } else if (field.type=='CHOICE') {
        if (field.subtype=='dropdown') {
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
                if (g==0) {
                    // override default initial  value in response
                    boundDict[propertyName]=gr.name;
                }
                selElt.append(optElt);
            }
            selElt.change(function(){
                var propertyVal = $("option:selected", this).val();
                boundDict[propertyName]=propertyVal;
                showResponse(boundDict,'DD Field');
                validatorFBCB();
            });
            return selElt;
        } else if (field.subtype=='radio') {
            var grpId = 'radiogroup-'+getOid();
            var radioGrpElt = $('<div class="field-radio-group"></div>');
            var onchangeRadioCallback = function(){
                //var propertyVal = $(this).attr("value");
                var propertyVal = radioGrpElt.find('input:radio:checked').val();
                boundDict[propertyName]=propertyVal;
                showResponse(boundDict,'Radio Field');
                validatorFBCB();
            };
            for (var g=0; g<field.options.length; g++) {
                var gr = field.options[g];
                var lbl = gr.name;
                if (gr.label) {
                    lbl=gr.label;
                } else {
                    //optElt.text('Dflt4: '+gr.name);
                    lbl=EkoGroupDefaultLabel(gr.name);
                }
                var checked = '';
                if (g==0) {
                    checked='checked="checked"';
                    // override default initial  value in response
                    boundDict[propertyName]=gr.name;
                }
                var radioElt = $('<span><input type="radio" class="field-radio" '+checked+' name="'+grpId+'" value="'+gr.name+'" /> '+lbl+'</span>');
                radioElt.find('input:radio').change(onchangeRadioCallback);
                radioGrpElt.append(radioElt);
            }
            radioGrpElt.find('input:radio').change(); // trigger a fisr change.
            return radioGrpElt;
        } else if (field.subtype=='check') {
            var grpId2 = 'checkgroup-'+getOid();
            var checkGrpElt = $('<div class="field-check-group"></div>');
            var onchangeCheckCallback = function(){
                //var propertyVal = $(this).attr("value");
                //var propertyVal = checkGrpElt.find('input:checkbox:checked').val();
                var propertyVal=[];
                checkGrpElt.find('input:checkbox:checked').each(function() {
                    propertyVal.push($(this).val());
                });

                boundDict[propertyName]=propertyVal;
                showResponse(boundDict,'Check Field');
                validatorFBCB();
            };
            for (var g2=0; g2<field.options.length; g2++) {
                var gr2 = field.options[g2];
                var lbl2 = gr2.name;
                if (gr2.label) {
                    lbl2=gr2.label;
                } else {
                    //optElt.text('Dflt4: '+gr.name);
                    lbl2=EkoGroupDefaultLabel(gr2.name);
                }
                var checkElt = $('<span><input type="checkbox" class="field-check" name="'+grpId2+'" value="'+gr2.name+'" /> '+lbl2+'</span>');
                checkElt.find('input:checkbox').change(onchangeCheckCallback);
                checkGrpElt.append(checkElt);
            }
            // override default initial  value in response
            boundDict[propertyName]=[];
            return checkGrpElt;
        }
    }
}

