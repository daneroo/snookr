/* 
 * Editor part depends on ekolib, constants
 */

function renderEditorPreviewJSON(contest,divselector){
    var tabsElt = $('<div class="eko-editor-tabs"/>');
    var ulElt = $('<ul />');
    tabsElt.append(ulElt);

    var oid;
    
    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">General</a></li>'));
    var generalContentElt = $('<div>General</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(generalContentElt));

    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Steps</a></li>'));
    var stepsContentElt = $('<div>Steps</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(stepsContentElt));

    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Translations</a></li>'));
    var translationsContentElt = $('<div>Translations</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(translationsContentElt));
    
    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Pricing</a></li>'));
    var pricingContentElt = $('<div>Pricing</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(pricingContentElt));
    
    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Preview</a></li>'));
    var previewContentElt = $('<div>Preview</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(previewContentElt));

    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Json</a></li>'));
    var jsonContentElt = $('<div>Votes</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(jsonContentElt));
    
    oid = getOid();
    ulElt.append($('<li><a href="#tabs-'+oid+'">Participants</a></li>'));
    var votesContentElt = $('<div>Votes</div>');
    tabsElt.append( $('<div id="tabs-'+oid+'"></div>').append(votesContentElt));
    

    $(divselector).append(tabsElt);

    tabsElt.tabs({
        show: function(event, ui) {
            // the new tab being shown is
            //   $(this).tabs('option', 'selected'))
            //   or ui['index']
            //alert($.toJSON(ui));
            if (0==ui.index){
                renderGeneral(contest,generalContentElt);
            } else if (1==ui.index) {
                renderSteps(contest, stepsContentElt);
            } else if (2==ui.index) {
                renderTranslations(contest,translationsContentElt);
            } else if (3==ui.index) {
                renderPricing(contest,pricingContentElt);
            } else if (4==ui.index) {
                renderPreview(contest,previewContentElt);
            } else if (5==ui.index) {
                renderJson(contest,jsonContentElt);
            } else if (6==ui.index) {
                renderVotes(contest,votesContentElt);
            } 
            
        //$('#status').html('tab: '+$(this).tabs('option', 'selected'));
        //$('#status').html('tab: '+ui.index);
        }
    });
}

function renderTranslations(contest, divselector){
}

function renderPricing(contest, divselector){
}

function renderSteps(contest, divselector){
    var rCtx = $(divselector);
    var stepsElt = $('<div class="steps"></div>');
    stepsElt.data('parentarray',contest.steps);
    if(contest.steps){
        for (var s=0;s<contest.steps.length;s++ ){
            var step = contest.steps[s];
            var stepElt = EkoMakeStep(step,s);
            stepsElt.append(stepElt);
        }
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
        if(!contest.steps) contest.steps = new Array();
        var nuentry = {
            "intro":"Texte Intro Etape "+contest.steps.length+1,
            "fields":[]
        };
        contest.steps.push(nuentry);
        var stepElt = EkoMakeStep(nuentry,contest.steps.length-1);
        stepsElt.append(stepElt);
        stepsElt.accordion('destroy');
        accordionOpts.active=contest.steps.length-1;
        stepsElt.accordion(accordionOpts);
        //stepsElt.accordion('activate',contest.steps.length-1);
        triggerChange();

        return false;
    });
    rCtx.empty();
    rCtx.append(stepsElt);
    rCtx.append($('<p />').append(addStepBtn));
    stepsElt.accordion(accordionOpts);
}

function renderGeneral(contest,divselector){
    var rCtx = $(divselector);

    var contestheader = $('<h3><i>Contest: </i></h3>');
    //contestheader.append($('<span></span>').text(contest.name));
    var inputElt = $('<input class="field-shorttext" size="35" type="hidden" value=""></input>');
    inputElt.attr("value",contest.ctt_id);
    var inputElt = $('<input class="field-shorttext" size="35" type="hidden" value=""></input>');
    inputElt.attr("value",contest.accn_id);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['accn_id']=propertyVal;
    });
    var inputElt = $('<input class="field-shorttext" size="35" type="text" value=""></input>');
    inputElt.attr("value",contest.name);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['name']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt);
    
    
    
    contestheader.append($('<h3><i>Start Date: </i></h3>'));
    var inputElt = $('<input id="startdate" type="text" readonly="readonly"></input>');
    inputElt.attr("value",contest.start_date);
    inputElt.click(function(){
        $("#startdate").datepicker({
            dateFormat: 'yy-mm-dd',
            buttonImage: '/images/datepicker.gif'
        });
        contest['start_date']= $(this).attr("value");
    });
    inputElt.change(function(){
        contest['start_date']= $(this).attr("value");
    });
    observe(inputElt);
    contestheader.append(inputElt);
     
    contestheader.append($('<h3><i>End Date: </i></h3>'));
    var inputElt = $('<input id="enddate" readonly="readonly" type="text"></input>');
    inputElt.attr("value",contest.end_date);
    inputElt.click(function(){
        $("#enddate").datepicker({ 
            dateFormat: 'yy-mm-dd'
        });
 
    });
    inputElt.change(function(){
        contest['end_date']= $(this).attr("value");
    });
    observe(inputElt);
    contestheader.append(inputElt);
   
    
    /*DESCRIPTION*/
    contestheader.append($('<h3><i>Description: </i></h3>'));
    var chkElt = $('<input class="field-shorttext" size="35" type="checkbox" value="1"></input>');
    chkElt.attr("checked", (contest.description_included?"checked":""));
    chkElt.change(function(){
        contest['description_included'] = ($(this).attr('checked')?1:0)
    });
    observe(chkElt);
    contestheader.append(chkElt);
    contestheader.append($('<span>include automatically in the contest</span><br>'));
    var inputElt = $('<textarea class="field-shorttext" cols="70" rows="10"></textarea>');
    inputElt.attr("value",contest.description);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['description']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt);
    
    /*RULES*/
    contestheader.append($('<h3><i>Rules: </i></h3>'));
    var inputElt = $('<textarea class="field-shorttext" cols="70" rows="10"></textarea>');
    inputElt.attr("value",contest.rules);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['rules']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt);
    
    contestheader.append($('<h3><i>Notify: </i></h3>'));
    var inputElt = $('<input class="field-shorttext" size="35" type="text" value=""></input>');
    inputElt.attr("value",contest.notify);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['notify']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt);
    
    contestheader.append($('<h3><i>Steps CSS: </i></h3>'));
    var inputElt = $('<input class="field-shorttext" size="35" type="text" value=""></input>');
    inputElt.attr("value",contest.css_url);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['css_url']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt);
   
    contestheader.append($('<h3><i>Traduction Submit</i></h3>'));
    var inputElt = $('<input class="field-shorttext" size="35" type="text" value=""></input>');
    inputElt.attr("value",contest.submit_translation);
    inputElt.change(function(){
        var propertyVal = $(this).attr("value");
        contest['submit_translation']=propertyVal;
    });
    observe(inputElt);
    contestheader.append(inputElt); 
     
    contestheader.append($('<h3><i>Gabarits: </i></h3>'));
    var selElt = $('<select></select>');
    for (var t=0; t<eko.templates.length; t++) {
        var tpl = eko.templates[t];
        var optElt = $('<option value="'+tpl.name+'">'+EkoTemplateDefaultLabel(tpl.name)+'</option>');
        if (tpl.name==contest.tpl_id){
            optElt.attr('selected', 'selected');
        }
        selElt.append(optElt);
    }
    selElt.change(function(){
        var name = $("option:selected", this).val();
        contest["tpl_id"] = name;
    });
    observe(selElt);
    contestheader.append(selElt);
    
    contestheader.append($('<h3><i>Vote Frequency: </i></h3>'));
    var selElt = $('<select></select>');
    for (var t=0; t<eko.voteFrequency.length; t++) {
        var vote = eko.voteFrequency[t];
        var optElt = $('<option value="'+vote.name+'">'+vote.label+'</option>');
        if (vote.name==contest.vote_frequency){
            optElt.attr('selected', 'selected');
        }
        selElt.append(optElt);
    }
    selElt.change(function(){
        var name = $("option:selected", this).val();
        contest["vote_frequency"] = name;
    });
    observe(selElt);
    contestheader.append(selElt);
    
    contestheader.append($('<h3><i>Groupes dans lesquels les inscriptions iront: </i></h3>'));
    for (var g=0; g<eko.groups.length; g++) {
        var gr = eko.groups[g];
        var chkElt = $('<input type="checkbox" value="'+gr.name+'">'+EkoGroupDefaultLabel(gr.name)+'</input>');
        /*for (var s=0;s<contest.grp_ids.length;s++ ){
            chkElt.attr('checked', 'checked');
        }*/
        
        chkElt.change(function(){
        
            var propertyVal=[];
            chkElt.find('input:checkbox:checked').each(function() {
            
                propertyVal.push($(this).val());
            });
            contest["grp_ids"]=propertyVal;
        });
        contestheader.append(chkElt);
        observe(chkElt);
    }
    
    
    contestheader.append($('<h3><i>Langues: </i></h3>'));
    var chkFrElt = $('<input type="radio" value="fr">Francais</input>');
    if (contest.lang == "fr"){
        chkFrElt.attr('checked', 'checked');
    }
    contestheader.append(chkFrElt);
    chkFrElt.change(function(){
        var name = $(this).val();
        contest["lang"] = name;
    });
    observe(chkFrElt);
    
    var chkEnElt = $('<input type="radio" value="en">Anglais</input>');
    if (contest.lang == "fr"){
        chkEnElt.attr('checked', 'checked');
    }
    contestheader.append(chkEnElt);
    chkEnElt.change(function(){
        var name = $(this).val();
        contest["lang"] = name;
    });
    observe(chkEnElt);

    

    rCtx.empty();
    rCtx.append(contestheader);
//  rCtx.append(stepsElt);
// rCtx.append($('<p />').append(addStepBtn));

// call accordion affter adding, fixes content heights...
//stepsElt.accordion(accordionOpts);
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
        triggerChange();
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
    var addFieldCHOICESETBtn = EkoButton('Choice Set Field');
    var addFieldLABELBtn = EkoButton('Label Field');

    addFieldCtrlElt.append(addFieldEKOBtn);
    addFieldCtrlElt.append(addFieldTAGBtn);
    addFieldCtrlElt.append(addFieldCHOICEBtn);
    addFieldCtrlElt.append(addFieldCHOICESETBtn);
    addFieldCtrlElt.append(addFieldLABELBtn);
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
        triggerChange();
        return false;
    });
    addFieldTAGBtn.click(function(){
        var nuentry = {
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Tag Label",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        triggerChange();
        return false;
    });
    addFieldCHOICEBtn.click(function(){
        var nuentry = {
            "type":"CHOICE",
            "subtype":"dropdown",
            "label":"Choice Label",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        triggerChange();
        return false;
    });
    addFieldCHOICESETBtn.click(function(){
        var nuentry = {
            "type":"CHOICESET",
            "subtype":"nested",
            "label":"Choice Set Label",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        triggerChange();
        return false;
    });
    addFieldLABELBtn.click(function(){
        var nuentry = {
            "type":"LABEL",
            "subtype":"",
            "label":"Nu Label",
            "validation":"none",
            "options": []
        };
        boundarray.push(nuentry);
        fieldsElt.append(EkoFieldBase(nuentry));
        triggerChange();
        return false;
    });
    fieldeditorelt.append(addFieldCtrlElt);
    return fieldeditorelt;
}

//*********************************************
// GSL : Group Select and Label Section
// may need to be refactored to be generalised..
// NEW Optimisation for speed:
//  the slect/option for groups is made once, then simply cloned,
//  the selected attribute is then set on the clone.
var cloningGrSelElt = null;
function EkoGroupSelect(selname){
    //eko.groups = [
    // {name:"gr-french", label:"Francais"},
    if (cloningGrSelElt==null) {
        cloningGrSelElt = $('<select></select>');
        for (var g=0; g<eko.groups.length; g++) {
            var gr = eko.groups[g];
            var optElt = $('<option value="'+gr.name+'">'+EkoGroupDefaultLabel(gr.name)+'</option>');
            cloningGrSelElt.append(optElt);
        }
    }
    var selElt = cloningGrSelElt.clone();
    // set selected on appropriate option
    selElt.find('option[value="'+selname+'"]').attr('selected', 'selected');
    return selElt;
}

function EkoFieldBase(bounddict){
    // bound parts, for diferent fied type, with a hidden for type
    // drop down for choice, etc.
    bounddict = bounddict || {
        "type":"EKO",
        "subtype":"subtype",
        "label":"label",
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


    var choicegrpelt;
    if (bounddict.type=='CHOICE'){
        choicegrpelt = EkoMakeGSLGroup(bounddict.options);
    }

    // for dirtyness tracking
    observe(subtypeSelElt);
    observe(labelInputElt);
    observe(validationSelElt);

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
        if (bounddata.label=='' || bounddata.label=='Nu Label'){
            grLabelElt.attr("value",defname);
        //grLabelElt.css('color','gray');
        }
    }

    var upArrowElt=EkoActionIcon('ui-icon-arrowthick-1-n');
    var downArrowElt=EkoActionIcon('ui-icon-arrowthick-1-s');
    var deleteRowElt=EkoActionIcon('ui-icon-closethick');

    grSelElt.change(function(){
        var name = $("option:selected", this).val();
        var chg=(bounddata.name!=name);
        bounddata.name = name;
        adjustDefaultLabel();
        if(!chg)
            bounddata.label = grLabelElt.attr("value");
    });
    observe(grSelElt);
    grLabelElt.change(function(){
        
        var label = $(this).attr("value");
        bounddata.label=label;
        grLabelElt.css('color','');
        grSelElt.change();
    });
    observe(grLabelElt);
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
    adjustDefaultLabel();
    combined.addClass('brother');
    return combined;
}
