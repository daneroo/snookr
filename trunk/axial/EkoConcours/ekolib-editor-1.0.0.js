/* 
 * Editor part depends on ekolib, constants
 */


function renderEditor(contest,divselector){
    var rCtx = $(divselector);

    var contestheader = $('<h3><i>Contest: </i></h3>');
    contestheader.append($('<span></span>').text(contest.name));

    var stepsElt = $('<div class="steps"></div>');
    stepsElt.data('parentarray',contest.steps);
    for (var s=0;s<contest.steps.length;s++ ){
        var step = contest.steps[s];
        var stepElt = EkoMakeStep(step,s);
        stepsElt.append(stepElt);
    }

    var accordionOpts = {
        'header': "h4",
        'active':0,
        'collapsible':true
    };
    if (!$.browser.msie){
        // haven't looked at fillSpace'
        /* clearStyle,!autoHeight works, but breaks IE */
        accordionOpts['autoHeight'] = false;
        accordionOpts['clearStyle'] = true;
    }

    //var addStepBtn = $('<a href="#" class="eko-btn ui-state-default ui-corner-all"><span class="ui-icon ui-icon-plus"></span>Step</a>');
    var addStepBtn = EkoButton('Add Step');
    addStepBtn.click(function(){
        var nuentry = {
            "intro":"Texte Intro Etape "+(contest.steps.length+1),
            "fields":[{
                "type":"EKO",
                "subtype":"couriel",
                "label":"E-Mail",
                "validation":"required",
                "options": []
            },{
                "type":"CHOICE",
                "subtype":"radio",
                "label":"Select One",
                "validation":"none",
                "options": [
                {
                    "name":"gr-french",
                    "label":""
                },

                {
                    "name":"gr-english",
                    "label":"English"
                }
                ]
            }]
        };
        contest.steps.push(nuentry);
        var stepElt = EkoMakeStep(nuentry,contest.steps.length-1);
        stepsElt.append(stepElt);
        stepsElt.accordion('destroy');
        accordionOpts.active=contest.steps.length-1;
        stepsElt.accordion(accordionOpts);
        //stepsElt.accordion('activate',contest.steps.length-1);
        return false;
    });

    rCtx.empty();
    rCtx.append(contestheader);
    rCtx.append(stepsElt);
    rCtx.append($('<p />').append(addStepBtn));

    // call accordion affter adding, fixes content heights...
    stepsElt.accordion(accordionOpts);
}

function EkoMakeStep(step,s){
    var stepElt = $('<div class="step brother"/>');
    stepElt.append($('<h4 />').append('<a href="#" >Step '+(s+1)+'</a>'));

    var upArrowElt=EkoActionIcon('ui-icon-arrowthick-1-n');
    var downArrowElt=EkoActionIcon('ui-icon-arrowthick-1-s');
    var deleteRowElt=EkoActionIcon('ui-icon-closethick');

    var ctrlElt = $('<span class=accordioncontrol>Step: </span>');
    ctrlElt.append(upArrowElt);
    ctrlElt.append(downArrowElt);
    ctrlElt.append(deleteRowElt);
    upArrowElt.click(function(){
        EkoMoveInDOM($(this),-1);
        return false;
    });
    downArrowElt.click(function(){
        EkoMoveInDOM($(this),1);
        return false;
    });
    deleteRowElt.click(function(){
        EkoRemoveInDOM($(this));
        return false;
    });

    var introTextAreaElt = EkoMakeBoundTextArea(step,'intro');

    var contentElt = $('<div/>')
    .append(ctrlElt)
    .append($('<span>Step Intro Text: </span><br>'))
    .append(introTextAreaElt)

    var fieldsEditorElt = EkoMakeFieldsEditorGroup(step.fields);

    contentElt.append(fieldsEditorElt)

    stepElt.append(contentElt);
    return stepElt;

}

function EkoMakeGSLGroup(boundarray){
    var choiceditorelt = $('<div class="choiceeditor"></div')
    choiceditorelt.data('parentarray', boundarray);

    var choiceditorlistelt = $('<div class="choiceeditorlist"></div')
    var addGSLBtn = EkoButton('Add Choice').click(function() {
        var nuentry = {
            "name":"gr-new",
            "label":"Nu Label"
        };
        boundarray.push(nuentry);
        choiceditorlistelt.append(EkoGroupSelectAndLabel(nuentry));
        return false;
    });
    choiceditorelt.append(choiceditorlistelt);
    choiceditorelt.append($('<p />').append(addGSLBtn));
    //alert($.toJSON(boundarray));
    var parentarray = choiceditorelt.data('parentarray');
    for (var z=0;z<parentarray.length;z++){
        choiceditorlistelt.append(EkoGroupSelectAndLabel(parentarray[z]));
    }
    return choiceditorelt;
}

function EkoMakeFieldsEditorGroup(boundarray){
    var fieldeditorelt = $('<div class="fieldeditor"></div')

    var fieldsElt = $('<div class="fields"></div>');
    fieldsElt.data('parentarray',boundarray);
    for (var f=0;f<boundarray.length;f++ ){
        var field = boundarray[f];
        var fieldElt = EkoFieldBase(field);
        fieldsElt.append(fieldElt);
    }

    fieldeditorelt.append(fieldsElt)

    var addFieldCtrlElt = $('<div class="fieldcontrol" />');
    var addFieldEKOBtn = EkoButton('EKO Field');
    var addFieldTAGBtn = EkoButton('TAG Field');
    var addFieldCHOICEBtn = EkoButton('Choice Field');

    addFieldCtrlElt.append(addFieldEKOBtn);
    addFieldCtrlElt.append(addFieldTAGBtn);
    addFieldCtrlElt.append(addFieldCHOICEBtn);
    addFieldEKOBtn.click(function(){
        var nuentry = {
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        return false;
    });
    addFieldTAGBtn.click(function(){
        var nuentry = {
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Nu Label",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        return false;
    });
    addFieldCHOICEBtn.click(function(){
        var nuentry = {
            "type":"CHOICE",
            "subtype":"dropdown",
            "label":"Nu Label",
            "validation":"none",
            "options": [
            {
                "name":"gr-french",
                "label":""
            },

            {
                "name":"gr-english",
                "label":"English"
            }
            ]
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        return false;
    });
    fieldeditorelt.append(addFieldCtrlElt);
    return fieldeditorelt;
}

//*********************************************
// GSL : Group Select and Label Section
// may need to be refactored to be generalised..

// TODO, make the group constant be injected or pulled (relation to AJAX ?
function EkoGroupSelect(selname){
    //eko.groups = [
    // {name:"gr-french", label:"Francais"},
    var selElt = $('<select></select>');
    for (var g=0; g<eko.groups.length; g++) {
        var gr = eko.groups[g];
        var optElt = $('<option value="'+gr.name+'">'+gr.name+'</option>');
        if (gr.name==selname){
            optElt.attr('selected', 'selected');
        }
        selElt.append(optElt);
    }
    return selElt;
}

function EkoFieldBase(bounddict){
    // bound parts, for diferent fied type, with a hidden for type
    // drop down for choice, etc.
    bounddict = bounddict || {
        "type":"EKO",
        "subtype":"nom",
        "label":"Nom",
        "validation":"none"
    };
    var typeLabelElt=$('<div class="fieldtypelabel">'+bounddict.type+':</div>');
    //typeLabelElt.text(bounddict.type+':');

    // subtype elt
    var subtypeSelElt = EkoMakeBoundSelect(eko.contest.FieldTypes[bounddict.type],bounddict,'subtype');

    // label elt
    var labelInputElt=EkoMakeBoundTextInput(bounddict,'label');
    var validationSelElt = EkoMakeBoundSelect(eko.validation,bounddict,'validation');

    var upArrowElt=EkoActionIcon('ui-icon-arrowthick-1-n');
    var downArrowElt=EkoActionIcon('ui-icon-arrowthick-1-s');
    var deleteRowElt=EkoActionIcon('ui-icon-closethick');


    // not here...
    var choicegrpelt;
    if (bounddict.type=='CHOICE'){
        choicegrpelt = EkoMakeGSLGroup(bounddict.options);
    }


    // Temporary....
    // hapens to be the <select/> element directly...
    var jsonElt=$('<pre class="jsondbg"></pre>');
    jsonElt.text($.toJSON(bounddict));
    subtypeSelElt.change(function(){
        jsonElt.text($.toJSON(bounddict));
    });
    labelInputElt.change(function(){
        jsonElt.text($.toJSON(bounddict));
    });
    validationSelElt.change(function(){
        jsonElt.text($.toJSON(bounddict));
    });

    upArrowElt.click(function(){
        EkoMoveInDOM($(this),-1);
        return false;
    });
    downArrowElt.click(function(){
        EkoMoveInDOM($(this),1);
        return false;
    });
    deleteRowElt.click(function(){
        EkoRemoveInDOM($(this),1);
        return false;
    });


    var combined=$('<div class="field"/>');
    combined.append(typeLabelElt);
    combined.append(subtypeSelElt);
    combined.append(labelInputElt);
    combined.append(validationSelElt);

    combined.append(upArrowElt);
    combined.append(downArrowElt);
    combined.append(deleteRowElt);
    // debug off
    //combined.append(jsonElt);
    //may be null:
    combined.append(choicegrpelt);
    combined.addClass('brother');
    return combined;

}
function EkoGroupSelectAndLabel(bounddata){
    bounddata = bounddata || {
        name:'gr-french',
        label:''
    };
    var grSelElt = EkoGroupSelect(bounddata.name);
    var grLabelElt=$('<input type="text" value=""></input>');
    grLabelElt.attr("value",bounddata.label);
    grLabelElt.css('margin-left','10px');
    var adjustDefaultLabel = function(){
        var defname=EkoGroupDefaultLabel(bounddata.name);
        if (bounddata.label==''){
            grLabelElt.attr("value",defname);
            grLabelElt.css('color','gray');
        }
    }

    var upArrowElt=EkoActionIcon('ui-icon-arrowthick-1-n');
    var downArrowElt=EkoActionIcon('ui-icon-arrowthick-1-s');
    var deleteRowElt=EkoActionIcon('ui-icon-closethick');

    var jsonElt=$('<pre class="jsondbg"></pre>');

    jsonElt.text($.toJSON(bounddata));
    grSelElt.change(function(){
        //alert($.toJSON($("option:selected", this)));
        var name = $("option:selected", this).val();
        bounddata.name = name;
        adjustDefaultLabel();
        jsonElt.text($.toJSON(bounddata));
    });
    grLabelElt.change(function(){
        var label = $(this).attr("value");
        // save
        bounddata.label=label;
        grLabelElt.css('color','');
        grSelElt.change();
    });
    upArrowElt.click(function(){
        EkoMoveInDOM($(this),-1);
        return false;
    });
    downArrowElt.click(function(){
        EkoMoveInDOM($(this),1);
        return false;
    });
    deleteRowElt.click(function(){
        EkoRemoveInDOM($(this),1);
        return false;
    });



    var combined=$('<div></div>');
    combined.append(grSelElt);
    combined.append(grLabelElt);
    combined.append(upArrowElt);
    combined.append(downArrowElt);
    combined.append(deleteRowElt);
    // debug off
    //combined.append(jsonElt);
    adjustDefaultLabel();
    combined.addClass('brother');
    return combined;
}
