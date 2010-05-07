/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function debug(message){
    if (window.console && window.console.firebug){
        console.log(message);
    }
}

// Here is the syntax for Choice sets.
//   [  [label1,..,labelDepth], // first row determines depth and default labels
//      [ entry1.1,..., entry1.Depth ],
//      [ entry2.1,..., entry2.Depth ],
//      [ entryn.1,..., entryn.Depth ]
//   ]
//   
//   each entry can be either a string representing both value and label
//     or a two-ple [value,label]
//   repeated entries of higher values may be omitted by a null or "" value.
//
// e.g.
//   one-level with id.
//   [ [["One Level With ID"]],
//     [["value1", "Label 1" ]],
//     [["value2", "Label 2" ]],
//     [["value3", "Label 3" ]]
//   ]
//
//   two-level with id.
//   [ [["Two Levels With ID"],["Second Level"]],
//     [["grvalue1", "Group Label 1" ],["value1", "Label 1" ]],
//     [null,["value2", "Label 2" ]],
//     [null,["value3", "Label 3" ]],
//     [["grvalue2", "Group Label 2" ],["value4", "Label 4" ]],
//     [null,["value5", "Label 5" ]],
//     [null,["value6", "Label 6" ]],
//   ]
//
//Numéro de région	Régions	√âtablissements
function makeChainedSelect(cs){
    var tr = CSmakeTree(cs);
    //CSprintTree(tr);
    var divElt = $('<div class="choiceset"></div>');
    //var ll = tr.c;
    var selElts=[];
    for (var d=0;d<tr.titles.length;d++){
        var selElt = $('<select class="choicesetselect" />');
        selElts.push(selElt);
    }
    for (var d=0;d<tr.titles.length;d++){
        divElt.append($('<span class="choicesetlabel" />').text(tr.titles[d]+':'));
        var selElt = selElts[d];
        divElt.append(selElt);
        selElt.change(function(){
            var v = $(this).val();
            debug("value changed to:"+v);
        });
        if (d<tr.titles.length-1){
            function genCB(tree,sels,myd) {
                return function(){
                    var v = $(this).val();
                    //debug("Should cascade to children of:"+v);

                    var children = tree.c;
                    for (var dd=0;dd<tree.titles.length;dd++){
                        var idx = sels[dd].attr("selectedIndex");
                        var me=(dd==myd)?"<--":"";
                        //debug(['sels',dd,sels[dd].val(),idx,me]);
                        if (dd==myd+1){
                            CSreplaceOptions(sels[dd], children);
                            sels[dd].change();
                            break;
                        }
                        children = children[idx].c;
                    }
                }
            }
            selElt.change(genCB(tr,selElts,d));
        }
    //CSreplaceOptions(selElt,ll);
    //ll = ll[0].c;
    }
    CSreplaceOptions(selElts[0],tr.c);
    selElts[0].change();
    return divElt;
}
function CSnullOrEmtpy(aValue){
    if (undefined==aValue) return true;
    if (aValue==null) return true;
    if (""==aValue) return true;
    return false;
}

// set value and label (if string use for both)
function CSsetVL(anObj,anEntry){  // set obect: {}.v, {}.l  -> value and label
    if (CSnullOrEmtpy(anEntry)) return;
    var v = null;
    var l = null;
    if (typeof(anEntry)=="string"){
        anObj.v=anEntry;
        anObj.l=anEntry;
    } else if (typeof(anEntry)=="object" && anEntry.length>=2) {
        anObj.v=anEntry[0];
        anObj.l=anEntry[1];
    } else {
        debug('bad entry in nested list '+anEntry);
        debug('type : '+typeof(anEntry));
        debug('length : '+anEntry.length);
    }
}

/**
* CSvalidate invariants of the list
* @returns true if everything is ok
*/
function CSvalidate(aList) {
    var titles = aList[0];
    var depth = titles.length;

    var depthok=true;
    var leafnotnullok=true;
    for (var i=1;i<aList.length;i++){  // skip title row

        var row = aList[i];
        if (row.length!=depth){
            debug("row["+i+"] has bad depth:"+row.length+" !=depth("+depth+")");
            depthok=false;
        }
        if (CSnullOrEmtpy(row[depth-1])) {
            leafnotnullok = fasle;
        }
    }
    debug('  depth check is: '+(depthok?"OK":"NOT OK"));
    debug('  leaf not null check is: '+(leafnotnullok?"OK":"NOT OK"));
    return depthok && leafnotnullok;
}

function CSmakeTree(aList) {
    var root = {
        v:'Root',
        titles:aList[0],
        c:[]
    };
    var depth = root.titles.length;

    //CSvalidate(aList);

    var prev = [];
    var prevNode = [];
    for (var d=0;d<depth;d++){
        prev.push(null);
        prevNode.push(null);
    }
    for (var i=1;i<aList.length;i++){  // skip title row
        var row = aList[i];

        // detect the break
        var isBreak=false;
        for (var d=0;d<depth;d++){
            var curr = row[d];
            // break condition, I'm sure there a re other combinations!
            if (!CSnullOrEmtpy(curr) && curr!=prev[d] ){
                // check for curr[v,l]==prev[v,l]
                var eqArrays = prev[d]!=null && typeof(curr)=='object' && curr[0]==prev[d][0] && curr[1]==prev[d][1];
                if (!eqArrays){
                    isBreak=true;
                }
            }
            // sanity: if d=depth-1 break must be true!
            if (d==depth-1){
                if (!isBreak){
                    debug("should always break on deepest level");
                    debug({
                        'i':i,
                        'd':d,
                        'prev':prev,
                        'row':row
                    });
                }
            }
            if (isBreak) {
                var node = {};
                CSsetVL(node,curr);
                prev[d]=curr;
                prevNode[d]=node;
                var parent = root;
                if (d>0) {
                    parent=prevNode[d-1];
                }
                if (undefined==parent.c){
                    parent.c = [];
                }
                parent.c.push(node);
            }
        }
    }

    return root;
}

function CSprintTree(node,indent) {
    indent=indent||'';
    debug(indent+node.v);
    if (node.c) {
        for (var i=0;i<node.c.length;i++){
            var leaf = node.c[i];
            CSprintTree(leaf,indent+'  ');
        }
    }
}
function CSreplaceOptions(selectElt,list){
    selectElt.html("");
    for (var i=0;i<list.length;i++){
        var n = list[i];
        var o = $('<option></option>').val(n.v).text(n.l);
        $(selectElt).append(o);
    }
}

function CStestArray(depth,perLevel,perLevelIncrement){
    perLevelIncrement = perLevelIncrement || 0;
    if (depth==0){
        var titles=['Title '+depth];
        var tarray = [titles];
        for (var i=0;i<perLevel;i++){
            tarray.push([''+i]);
        }
        return tarray;
    } else {
        var tam1 = CStestArray(depth-1,perLevel+perLevelIncrement,perLevelIncrement);
        var tarray = [tam1[0].concat(['Title '+depth])];
        for (var i=0;i<perLevel;i++){
            for (var j=1;j<tam1.length;j++){
                var row = [''+i].concat(tam1[j]);
                for (var k=1;k<row.length;k++){
                    row[k]=''+i+'.'+row[k];
                }
                tarray.push(row);
            }
        }
        return tarray;
    }
}

function CStestArrayWLabel(depth,perLevel,perLevelIncrement){
    var tarray = CStestArray(depth,perLevel,perLevelIncrement);
    for (var i=1;i<tarray.length;i++){
        for (var j=0;j<tarray[i].length;j++){
            var v=tarray[i][j];
            tarray[i][j] = ['v'+v,'Label '+v];
        }
    }
    return tarray;
}