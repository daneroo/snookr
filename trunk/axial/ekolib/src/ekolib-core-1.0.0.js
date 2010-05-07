/* 
 * This contains common code for editor and renderer
 *   dependancy of includes is as follows:
 *   This file must not depend on any others.
 *   TODO:
 *    - ekolib-1.0.0.js
 *    - ekolib-constants-1.0.0.js
 *    - ekolib-data-1.0.0.js
 *    - ekolib-editor-1.0.0.js
 *    - ekolib-render-1.0.0.js
 */

function debug(message){
    if (window.console && window.console.firebug){
        console.log(message);
    }
}

// namespace
var eko={};
eko.contest={};


var globalPreviewOid=222;
function getOid(){
    globalPreviewOid+=1;
    return globalPreviewOid;
}

function renderJson(contest,divselector){
    $(divselector).html('JSON: <textarea cols="80" rows="25">'+$.toJSON(contest)+'</textarea>');
}

function renderVotes(contest,divselector){

    $.ajax({
        type:"POST",
        url: "accountcontestservices.asmx/LoadVotes",
        data: {
            "ctt_id":contest.ctt_id
        },
        dataType: "xml",
        success: function(data) {
          
            eval("var votes = " + data.getElementsByTagName("string")[0].firstChild.data + ";");
            if(votes == ""){
                $(divselector).html('Aucun vote');
                return;
            }
            var headers = [];
            var vote;
            for ( i=0, iLen=votes.length ; i<iLen ; i++ ){
                vote = votes[i];
                for(var field in vote){
                    if($.inArray(field, headers)==-1){
                        headers.push(field);
                    }
                }
            }
            var rows = [];
            var columns = [];
            for ( i=0, iLen=votes.length ; i<iLen ; i++ ){
                vote = votes[i];
                rows[i] = [];
                for ( j=0, jLen=headers.length ; j<jLen ; j++ ){
                    var headerSplitted = headers[j].split(":");
                    if(i==0){
                        columns[j] = new Array();
                        columns[j]["sTitle"] = (headerSplitted.length > 2 ? headerSplitted[3] : headers[j]);
                    }
                    if(headerSplitted[1] != "CHOICE")
                        rows[i].push(vote[headers[j]]);
                    else
                        rows[i].push(EkoGroupDefaultLabel(vote[headers[j]]));
                }
            }
          
            var tableElt = $('<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>');
            $(divselector).append(tableElt);
            tableElt.dataTable( {
                "aaData": rows,
                "aoColumns": columns,
                "sDom": 'T<"clear">lfrtip'
            });

        },
        error: function(data) {
        }
    });
        
//    $(divselector).html('JSON: <textarea cols="80" rows="25">'+$.toJSON(contest)+'</textarea>');
}



eko.globalEditorChangeHook = function(){
    $('#hook').text(' '+getOid()+'changed at: '+(new Date()));
};

// should dynamically monitor a property, so we could change the callback at will.
function observe(changeElt){
    changeElt.change(eko.globalEditorChangeHook);
}
function triggerChange(){
    if (eko.globalEditorChangeHook) eko.globalEditorChangeHook();
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

function EkoTemplateDefaultLabel(name){
    for (var t=0; t<eko.templates.length; t++) {
        var tpl = eko.templates[t];
        if (name==tpl.name){
            return tpl.label;
        }
    }
    return "Label";
}

function EkoMakeBoundTextArea(boundDict,propertyName,observer){
    var introTextAreaElt = $('<textarea cols="40" rows="5"/>');
    introTextAreaElt.text(boundDict[propertyName]);
    introTextAreaElt.change(function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
    });
    if (eko.globalEditorChangeHook) {
        introTextAreaElt.change(eko.globalEditorChangeHook);
    }
    return introTextAreaElt;
}
function EkoMakeBoundTextInput(boundDict,propertyName){
    var textInputElt = $('<input type="text" value=""/>');
    textInputElt.attr("value",boundDict[propertyName]);
    textInputElt.change(function(){
        var propertyVal = $(this).attr("value");
        boundDict[propertyName]=propertyVal;
    });
    if (eko.globalEditorChangeHook) {
        textInputElt.change(eko.globalEditorChangeHook);
    }
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
    if (eko.globalEditorChangeHook) {
        textInputElt.change(eko.globalEditorChangeHook);
    }
    return textInputElt;
}

function EkoMakeBoundSelect(options,boundDict,propertyName){
    // options = [{name:blablabla},{name:blablabla}]
    // or
    // could optionally have some labelled
    //  [{name:'blabla'},{name:'blibli',label:'Bli bli'},{name:'bloblo'}]
    // boundDict/propertyName is optional - no onchange if not defined.
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
        if (boundDict && gr.name==boundDict[propertyName]){
            optElt.attr('selected', 'selected');
        }
        selElt.append(optElt);
    }
    // Now bind it.... if boundDict/propertyName were passsed
    if (boundDict && propertyName){
        selElt.change(function(){
            var propertyVal = $("option:selected", this).val();
            boundDict[propertyName]=propertyVal;
        });
        if (eko.globalEditorChangeHook) {
            selElt.change(eko.globalEditorChangeHook);
        }
    }


    return selElt;
}

function EkoMakeBoundTable(options){
 
    var columns = new Array();
    var data = new Array();
    var i=0;
    for (var word in options[0]){
        columns[i]=new Array();
        columns[i]["sTitle"] = word;
        i++;
    }
  
    for(i=0;i<options.length;i++){
        data[i] = new Array();
        var j=0;
        for(j=0;j<columns.length;j++){
            data[i][j] = options[i][columns[j]["sTitle"]];
        }
    }
   
    var tableElt = $('<table cellpadding="0" cellspacing="0" border="0" class="display" id="example"></table>');
    tableElt.dataTable( {
        "aaData": data,
        "aoColumns": columns
    });
    return tableElt;
}




function EkoRemoveInDOM(element) {
    if (element==undefined) return;
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
    triggerChange();
}

function EkoMoveInArray(anarray,direction,position){
    triggerChange();
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


// Removes leading whitespaces
function LTrim( value ) {
    if (!value) return value;
    var re = /\s*((\S+\s*)*)/;
    return value.replace(re, "$1");
}

// Removes ending whitespaces
function RTrim( value ) {
    if (!value) return value;
    var re = /((\s*\S+)*)\s*/;
    return value.replace(re, "$1");
}

// Removes leading and ending whitespaces
function trim( value ) {
    return LTrim(RTrim(value));
}

function isEmpty( value ) {
    if (!value) return true;
    if (trim(String(value)).length==0) return true;
    return false;
}

function testTrimAndEmpty(){
    var str='OK'
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str='  leading'
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str='trailing   '
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str='  both '
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str='   '
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str=''
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
    str=null
    alert('str=|'+str+'| trm=|'+trim(str)+'| isEmtpy:'+isEmpty(str))
}