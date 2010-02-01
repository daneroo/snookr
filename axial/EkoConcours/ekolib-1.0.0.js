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
            "validation":"None"
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"None"
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"None"
        }]
    },{
        "intro":"Texte Intro Etape 2",
        "fields":[{
            "type":"EKO",
            "subtype":"prenom",
            "label":"Prénom",
            "validation":"None"
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"None"
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"None"
        }]
    }]
};

// namespace
var eko={}; eko.contest={};
eko.contest.FieldTypes = { // and subtypes
    EKO : {
        prenom: "Prenom",
        nom:"Nom",
        couriel:"Couriel"
    },
    TAG : {
        shorttext:"Short Text",
        longtext:"Long Text"
    },
    CHOICE : {
        dropdown:"Drop Down (1)",
        radio:"Radio Buttons",
        check:"CheckBoxes (mult)"
    }
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
},
];


function renderJSON(contest,divselector){
    $(divselector).html('JSON: <textarea cols="80" rows="25" name="myname">'+$.toJSON(contest)+'</textarea>');
}

function renderEditor(contest,divselector){
    var rCtx = $(divselector);

    var contestheader = $('<h3><i>Contest: </i></h3>');
    contestheader.append($('<span></span>').text(contest.name));

    var stepsElt = $('<div class="steps"></div>');
    for (var s=0;s<contest.steps.length;s++ ){
        var step = contest.steps[s];
        var stepElt = $('<div class="step"/>');
        // step title in a h4
        stepElt.append($('<h4/>').append('<a href="#" >Step '+(s+1)+'</a>'));
        //html += '<h4><a href="#">Step '+(s+1)+'</a></h4>';
        var contentElt = $('<div/>')
        .append($('<span>Step Intro Text: </span><br>'))
        .append($('<textarea cols="40" rows="5" name="myname" />').text(step.intro))
        for (var f=0;f<step.fields.length;f++ ){
            var field = step.fields[f];
            contentElt.append($('<div/>')
                .text('Field '+(s+1)+'.'+(f+1)+': '+field.type+' : '+field.subtype+' : '+field.label+' : '+field.validation)
                );
        }
        stepElt.append(contentElt);
        stepsElt.append(stepElt);
    }
    //var addStepBtn = $('<a href="#" class="eko-btn ui-state-default ui-corner-all"><span class="ui-icon ui-icon-plus"></span>Step</a>');
    var addStepBtn = EkoButton('Step');
    addStepBtn.click(function(){
        //alert('Would add step');
        contest.steps.push({
            "intro":"Texte Intro Etape "+(contest.steps.length+1),
            "fields":[{
                "type":"EKO",
                "subtype":"prenom",
                "label":"Prénom",
                "validation":"None"
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
    stepsElt.accordion({ 
        /*fillSpace:true,*/
        header: "h4"
    });

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
    var jsonElt=$('<pre style="display:inline"></pre>');
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
    //alert('name changed to: '+label)
    })
    var combined=$('<div></div>');
    combined.append(grSelElt);
    combined.append(grLabelElt);
    combined.append(jsonElt);
    adjustDefaultLabel();
    //grSelElt.change();
    return combined;
}
function EkoButton(label,iconclass) {
    /* return a new element for insertion into DOM
     * icon class : e.g. ui-icon-plus, ui-icon-arrowthick-1-n, etc
     */
    label = label || "Add";
    iconclass = iconclass || "ui-icon-plus";
    var btnElt = $('<a href="#" class="eko-btn ui-state-default ui-corner-all"><span class="ui-icon ui-icon-plus"></span>'+label+'</a>');
    btnElt.hover(function() {
        $(this).addClass('ui-state-hover');
    },function() {
        $(this).removeClass('ui-state-hover');
    })
    return btnElt;
}