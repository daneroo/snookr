/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var contest1 = {
    "name":"Win a Free VisualStudio '05 Copy!",
    "steps":[{
        "intro":"Texte Intro Etape 1",
        "fields":[{
            "type":"EKO",
            "subtype":"prenom",
            "label":"Prénom",
            "validation":"none",
            "options": []
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options": []
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"none",
            "options": []
        },{
            "type":"CHOICE",
            "subtype":"radio",
            "label":"Groups",
            "validation":"none",
            "options": [
            {
                "name":"gr-french",
                "label":""
            },

            {
                "name":"gr-english",
                "label":"English"
            },

            {
                "name":"gr-child",
                "label":""
            },

            {
                "name":"gr-adult",
                "label":""
            },

            {
                "name":"gr-green",
                "label":"Enviro"
            },

            {
                "name":"gr-techsavy",
                "label":"Techie"
            }
            ]
        }]
    },{
        "intro":"Texte Intro Etape 2",
        "fields":[{
            "type":"EKO",
            "subtype":"prenom",
            "label":"Prénom",
            "validation":"none",
            "options": []
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options": []
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"none",
            "options": []
        },{
            "type":"CHOICE",
            "subtype":"check",
            "label":"Select Many",
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
    }]
};

// namespace
var eko={}; eko.contest={};
eko.contest.FieldTypes = { // and subtypes
    EKO : [
    {
        name:"prenom",
        label:"Prénom"
    },
    {
        name:"nom",
        label:"Nom"
    },
    {
        name:"couriel",
        label:"Couriel"
    }
    ],
    TAG : [
    {
        name:"shorttext",
        label:"Short Text"
    },
    {
        name:"longtext",
        label:"Long Text"
    },
    {
        name:"hiddentext",
        label:"Injected Field"
    }
    ],
    CHOICE : [
    {
        name:"dropdown",
        label:"Drop Down (1)"
    },
    {
        name:"radio",
        label:"Radio Buttons (1)"
    },
    {
        name:"check",
        label:"CheckBoxes (mult)"
    }
    ]
};
eko.groups = [
{
    name:"gr-french",
    label:"Francais"
},
{
    name:"gr-english",
    label:"Anglais"
},
{
    name:"gr-child",
    label:"Enfant"
},
{
    name:"gr-teen",
    label:"Adolescent"
},
{
    name:"gr-adult",
    label:"Adulte"
},
{
    name:"gr-green",
    label:"Vert"
},
{
    name:"gr-techsavy",
    label:"Techno"
},
{
    name:"gr-bio",
    label:"Bio"
}
];

eko.validation = [
{
    name:"none",
    label:"None"
},
{
    name:"required",
    label:"Required"
}
];


function renderJSON(contest,divselector){
    $(divselector).html('JSON: <textarea cols="80" rows="25" name="myname">'+$.toJSON(contest)+'</textarea>');
}

function renderEditor(contest,divselector){
    var rCtx = $(divselector);

    var contestheader = $('<h3><i>Contest: </i></h3>');
    contestheader.append($('<span></span>').text(contest.name));

    var stepsElt = $('<div class="steps"></div>');
    stepsElt.data('parentarray',contest.steps);
    for (var s=0;s<contest.steps.length;s++ ){
        var step = contest.steps[s];
        var stepElt = $('<div class="step brother"/>');
        // step title in a h4
        stepElt.append($('<h4 />').append('<a href="#" >Step '+(s+1)+'</a>'));
        //html += '<h4><a href="#">Step '+(s+1)+'</a></h4>';

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
        //var introTextAreaElt = EkoMakeBoundTextInput(step,'intro');
        
        var contentElt = $('<div/>')
        .append(ctrlElt)
        .append($('<span>Step Intro Text: </span><br>'))
        .append(introTextAreaElt)

        // REMOVE:
        // var choicegrpelt = EkoMakeGSLGroup();
        //contentElt.append(choicegrpelt);

        var fieldsEditorElt = EkoMakeFieldsEditorGroup(step.fields);
            
        contentElt.append(fieldsEditorElt)

        stepElt.append(contentElt);
        stepsElt.append(stepElt);
    }
    //var addStepBtn = $('<a href="#" class="eko-btn ui-state-default ui-corner-all"><span class="ui-icon ui-icon-plus"></span>Step</a>');
    var addStepBtn = EkoButton('Add Step');
    addStepBtn.click(function(){
        contest.steps.push({
            "intro":"Texte Intro Etape "+(contest.steps.length+1),
            "fields":[{
                "type":"EKO",
                "subtype":"couriel",
                "label":"E-Mail",
                "validation":"none",
                "options": []
            },{
                "type":"CHOICE",
                "subtype":"check",
                "label":"Select Some",
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
        });
        renderEditor(contest,divselector);
        return false;
    });

    rCtx.empty();
    rCtx.append(contestheader);
    rCtx.append(stepsElt);
    rCtx.append($('<p />').append(addStepBtn));

    // call accordion affter adding, fixes content heights...
    var accordionOpts = {
        'header': "h4"
    };
    if (!$.browser.msie){
        // haven't looked at fillSpace'
        /* clearStyle,!autoHeight works, but breaks IE */
        accordionOpts['autoHeight'] = false;
        accordionOpts['clearStyle'] = true;
    }
    stepsElt.accordion(accordionOpts);
}

function EkoMakeBoundTextArea(boundDict,propertyName){
    var introTextAreaElt = $('<textarea cols="40" rows="5"/>');
    introTextAreaElt.text(boundDict[propertyName]);
    introTextAreaElt.change(function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
    //alert('boundDict['+propertyName+']='+propertyVal);
    });
    return introTextAreaElt;
}
function EkoMakeBoundTextInput(boundDict,propertyName){
    var textInputElt = $('<input type="text" value=""/>');
    textInputElt.attr("value",boundDict[propertyName]);
    textInputElt.change(function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
    //alert('boundDict['+propertyName+']='+propertyVal);
    });
    return textInputElt;
}

// A hidden field can't really change...'
function EkoMakeBoundHiddenInput(boundDict,propertyName){
    var textInputElt = $('<input type="hidden" value=""/>');
    textInputElt.attr("value",boundDict[propertyName]);
    textInputElt.change(function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
    //alert('boundDict['+propertyName+']='+propertyVal);
    });
    return textInputElt;
}

function EkoMakeBoundSelect(options,boundDict,propertyName){
    // options = [{name:blablabla},{name:blablabla}]
    // or
    // could optionally have some labelled
    //  [{name:'blabla'},{name:'blibli',label:'Bli bli'},{name:'bloblo'}]
    var selElt = $('<select></select>');
    // for styling: field-propertyname
    var cssClass = 'field-'+propertyName;
    selElt.addClass(cssClass);
    for (var g=0; g<options.length; g++) {
        var gr = options[g];
        var optElt = $('<option value="'+gr.name+'">'+gr.name+'</option>');
        if (gr.label) {
            optElt.text(gr.label)
        }
        if (gr.name==boundDict[propertyName]){
            optElt.attr('selected', 'selected');
        }
        selElt.append(optElt);
    }
    // Now bind it....
    selElt.change(function(){
        var propertyVal = $("option:selected", this).val();
        boundDict[propertyName]=propertyVal;
    });

    return selElt;
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

function EkoGroupDefaultLabel(name){
    for (var g=0; g<eko.groups.length; g++) {
        var gr = eko.groups[g];
        if (name==gr.name){
            return gr.label;
        }
    }
    return "Label";
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

function EkoRemoveInDOM(element) {
    if (element==undefined) return
    msg='delete: ';
    msg+=' | '+$(element).nodeName;
    msg+='\n';
    // climb the searching for the sibling which is us.
    var measbrother;
    var ll = element.parents();
    for (i=0;i<ll.length;i++){
        msg+=' | '+ll[i].nodeName+ '-'+$(ll[i]).hasClass('brother');
        if ($(ll[i]).hasClass('brother')) {
            measbrother = ll[i];
            break;
        }
    }

    // find my position in the list of siblings - for affecting the array
    var position=0;
    for (p = measbrother; $(p).prev('.brother').length>0; p = $(p).prev('.brother')) {
        position+=1;
    }
    msg = 'position: '+position + ' '+ msg

    // find the parent holding the bound parentarray.
    // traverse same ll = element.parents as above...
    msg+='\n';
    for (i=0;i<ll.length;i++){
        if ($(ll[i]).data('parentarray')){
            var bounddata = $(ll[i]).data('parentarray');
            // make sure not last entry: has length >1
            if(bounddata.length>1){
                EkoRemoveInArray(bounddata,position);
            }
            msg+=' arrayJSON:  [ '+$.toJSON(bounddata)+']';
            break;
        }
    }

    // make sure not last entry: has one sibling, before or after...
    nextsibling = ($(measbrother).next('.brother'))[0];
    prevsibling = ($(measbrother).prev('.brother'))[0];
    if (nextsibling || prevsibling){
        $(measbrother).remove();
    }
    $('#status').html(msg);

}
function EkoMoveInDOM(element,direction) {
    if (element==undefined) return
    direction = direction || 1;
    msg='move direction: '+direction;
    msg+=' | '+$(element).nodeName;
    msg+='\n';

    // climb the searching for the sibling which is us.
    var measbrother;
    var ll = element.parents();
    for (i=0;i<ll.length;i++){
        msg+=' | '+ll[i].nodeName+ '-'+$(ll[i]).hasClass('brother');
        if ($(ll[i]).hasClass('brother')) {
            measbrother = ll[i];
            break;
        }
    }

    // find my position in the list of siblings - for affecting the array
    var position=0;
    for (p = measbrother; $(p).prev('.brother').length>0; p = $(p).prev('.brother')) {
        position+=1;
    }
    msg = 'position: '+position + ' '+ msg

    // find the parent holding the bound parentarray.
    // traverse same ll = element.parents as above...
    msg+='\n';
    for (i=0;i<ll.length;i++){
        if ($(ll[i]).data('parentarray')){
            var bounddata = $(ll[i]).data('parentarray');
            EkoMoveInArray(bounddata,direction,position);
            msg+=' arrayJSON:  [ '+$.toJSON(bounddata)+']';
            break;
        }
    }

    if (direction>0){
        nextsibling = ($(measbrother).next('.brother'))[0];
        if (nextsibling) $(nextsibling).after(measbrother);
    } else if (direction<0){
        prevsibling = ($(measbrother).prev('.brother'))[0];
        if (prevsibling) $(prevsibling).before(measbrother);
    }
    $('#status').html(msg);
}

function EkoRemoveInArray(anarray,position){
    anarray.splice(position,1);
}

function EkoMoveInArray(anarray,direction,position){
    //alert('dir:'+direction+' position:'+position);
    if (direction>0){
        if (position<anarray.length-1) {
            var tmp1 = anarray[position+1];
            anarray[position+1] = anarray[position];
            anarray[position] = tmp1;
        }
    } else if (direction<0){
        if (position>0) {
            var tmp2 = anarray[position-1];
            anarray[position-1] = anarray[position];
            anarray[position] = tmp2;
        }
    }
}

function EkoActionIcon(iconClass) {
    // calls EkoIconWrapper...
    return EkoIconWrapper('',iconClass,'eko-action-icon');
}

function EkoButton(label,iconClass) {
    // calls EkoIconWrapper...
    label = label || "Add";
    return EkoIconWrapper(label,iconClass,'eko-btn');
}
function EkoIconWrapper(label,iconClass,cssClass) {
    /* return a new element for insertion into DOM
     * icon class : e.g. ui-icon-plus, ui-icon-arrowthick-1-n, etc
     * cssClass: eko-btn, eko-action-icon (arrown,..)
     */
    iconClass = iconClass || "ui-icon-plus";
    cssClass = cssClass || "eko-btn";
    var btnElt = $('<a href="#" class="'+cssClass+' ui-state-default ui-corner-all"><span class="ui-icon '+iconClass+'"></span>'+label+'</a>');
    btnElt.hover(function() {
        $(this).addClass('ui-state-hover');
    },function() {
        $(this).removeClass('ui-state-hover');
    })
    return btnElt;
}