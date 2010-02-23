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

    var contestElt = $('<div class="contest"/>');
    // make some tabs for the steps
    var contestheader = $('<h3><i>Contest: </i></h3>');
    contestheader.append($('<span></span>').text(contest.name));

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
        stepElt.text("Content of step: "+(s+1))
        stepsElt.append(stepElt);
    }

    //GGGGG = stepsElt;
    //$(stepsElt).tabs()

    contestElt.append(stepsElt);
    return contestElt;
}