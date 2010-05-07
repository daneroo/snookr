/*
 * Lifecycle part (New/Edit/Duplicate - Dirtyness - Save/Close,etc)
 * Contest lifecycle section
 *  Spec:
 *    - separate New Edit Duplicate
 *    - Save/Close moves inside Editor widget - inside Editor Session Anyways
 *    - State Machine:
 *      we are editing or not.
 *      not editing: new/edit/duplicate -> editing (hide new/edit/dup controls
 *      if editing: dirty or not:
 *        dirty: Save / Discard|Cancel / Save&Close
 *        not dirty: Close
 *
 *        contest: is the current contest | null
 *          setContest: resets hash for dirtyness
 *           -- close set contest to null
 *           
 */

EkoLifeCycle.prototype = {
    STATE_NODOC:0,
    STATE_CLEAN:1,
    STATE_DIRTY:2,
    contest:null,
    savedHash:null,
    state:0,
    spinner:0,
    spinTxt : ['-','\\','|','/'],
    dirtydebugelt:null,
    statusElt:null,
    debug:function(message){
        if (this.dirtydebugelt) {
            $(this.dirtydebugelt).text('DBG:'+' '+message);
        }
    },
    checkStateOK : function(){
        // return true/false if the current state is OK.
        return this.checkState()==this.state;
    },
    checkState : function(){
        // return the state the Lifecyle shoud be in.
        var tt = new Date().getTime();
        var hash = this.hash();
        var dirty = this.savedHash != hash;
        var elapsed = new Date().getTime()-tt;


        this.spin();
        this.debug('state:'+this.state+' '+this.savedHash+' '+(dirty?'!=':'==')+' '+hash+' '+elapsed+' ms. dirty:'+dirty+' '+this.spinTxt[this.spinner]);

        var desiredStated=this.STATE_NODOC;
        if (this.contest) {
            if (!this.contest.name) { // contest without a name ??
                desiredStated = this.STATE_NODOC;
            } else if (dirty) {
                desiredStated = this.STATE_DIRTY;
            } else {
                desiredStated = this.STATE_CLEAN;
            }
        } else { // not edit
            desiredStated = this.STATE_NODOC;
        }
        return desiredStated;
    },
    dirtynessCallBack : function(){
        var nustate = this.checkState();
        if ( nustate!=this.state){
            this.setState(nustate);
        }
    },
    spin: function(){
        this.spinner = (this.spinner+1)%this.spinTxt.length;
    },
    hash: function(){
        if (this.contest){
            var jsonText = $.toJSON(this.contest);
            var sha1sum = hex_sha1(jsonText);
            return sha1sum;
        }
        return null;
    },
    start: function(interval){
        this.savedHash = this.hash();
        interval = interval || 2000;
        var thing = this;
        var cb = function(timer){
            thing.dirtynessCallBack();
        }
        $.timer(interval,cb);
    },
    render: function (divselector,contests, accn_id){
        var lifeCycleElt = $('<div class="eko-lifecycle" ></div>');
        var nuBtn = EkoButton('New Contest','ui-icon-plusthick');
        var editBtn = EkoButton('Edit Contest','ui-icon-plusthick');
        var dupBtn = EkoButton('Duplicate Contest','ui-icon-copy');
        var saveBtn = EkoButton('Save Contest','ui-icon-plusthick');
        var cancelBtn = EkoButton('Discard Changes','ui-icon-closethick');
        var closeBtn = EkoButton('Close','ui-icon-closethick');
        var viewBtn = EkoButton('View','ui-icon-play');
        var expBtn = EkoButton('Export','ui-icon-extlink');
        var delBtn = EkoButton('Delete','ui-icon-trash');
        var contestSellElt = EkoMakeBoundSelect(contests);
        var contestsTbl = EkoMakeBoundTable(contests);

        var ekoEditorPreviewTabsElt = $('<div class="eko-editor-preview-tabs"></div>');

        // not editing: NODOC
        this.stateNODOCElt = $('<div class="eko-NODOC"></div>');
        $(this.stateNODOCElt).append(nuBtn);
        $(this.stateNODOCElt).append(contestSellElt);
        
        $(this.stateNODOCElt).append(editBtn);
        $(this.stateNODOCElt).append(viewBtn);
        $(this.stateNODOCElt).append(dupBtn);
        $(this.stateNODOCElt).append(delBtn);
        //$(this.stateNODOCElt).append(expBtn);
        $(this.stateNODOCElt).append(contestsTbl);
        
        // editing: CLEAN
        this.stateCLEANElt = $('<div class="eko-CLEAN"></div>');
        $(this.stateCLEANElt).append(closeBtn);
        $(this.stateCLEANElt).append(viewBtn);
        $(this.stateCLEANElt).append(delBtn);
        // editing: dirty
        this.stateDIRTYElt = $('<div class="eko-DIRTY"></div>');
        $(this.stateDIRTYElt).append(saveBtn);
        $(this.stateDIRTYElt).append(cancelBtn);
        $(this.stateCLEANElt).append(viewBtn);
        $(this.stateCLEANElt).append(delBtn);
        //$(this.stateCLEANElt).append(expBtn);

        $(lifeCycleElt).append(this.stateNODOCElt);
        $(lifeCycleElt).append(this.stateCLEANElt);
        $(lifeCycleElt).append(this.stateDIRTYElt);
        //this.stateCLEANElt.hide();
        //this.stateDIRTYElt.hide();

        this.statusElt = $('<div class="eko-lifecycle-status">Status</div>');
        $(lifeCycleElt).append(this.statusElt);
        $(divselector).append(lifeCycleElt);
        this.setState(this.STATE_NODOC);

        // should remove this later
        this.dirtydebugelt = $('<div style="margin-top:1em;">DBG</div>');
        $(lifeCycleElt).append(this.dirtydebugelt);
        $(lifeCycleElt).append(ekoEditorPreviewTabsElt);

        // setup callbacks
        var thing = this;
       /* contestsTbl.click(function(){
            thing.contest = thing.load(30,"Concours des résidents");
            renderEditorPreviewJSON(thing.contest,ekoEditorPreviewTabsElt);
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_DIRTY);
            thing.dirtynessCallBack();
        });*/
        nuBtn.click(function(){
            thing.contest = {"ctt_id":"0", "accn_id":accn_id};
            renderEditorPreviewJSON(thing.contest,ekoEditorPreviewTabsElt);
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_CLEAN);
            thing.dirtynessCallBack();
        });
        editBtn.click(function(){
            var name = $("option:selected", contestSellElt).val();
            var label = $("option:selected", contestSellElt).text();
            thing.contest = thing.load(name,label);
            renderEditorPreviewJSON(thing.contest,ekoEditorPreviewTabsElt);
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_DIRTY);
            thing.dirtynessCallBack();
        });
        delBtn.click(function(){
            var name = $("option:selected", contestSellElt).val();
            var label = $("option:selected", contestSellElt).text();
            thing.del(name);
            thing.setState(this.STATE_CLEAN);
            thing.dirtynessCallBack();
            return;
        });
        dupBtn.click(function(){
            var name = $("option:selected", contestSellElt).val();
            var label = $("option:selected", contestSellElt).text();
            thing.contest = thing.load(name,label);
            renderEditorPreviewJSON(thing.contest,ekoEditorPreviewTabsElt);
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_CLEAN);
            thing.dirtynessCallBack();
        });
        viewBtn.click(function(){
            var name = $("option:selected", contestSellElt).val();
            window.open("accountcontestview.aspx?u5=" + name); 
            return;
        });
        expBtn.click(function(){
            var name = $("option:selected", contestSellElt).val();
            thing.exp(name);
            //window.open("accountmembers.aspx?ui=xls&u5=30"); 
            return;
        });
        closeBtn.click(function(){
            if (!thing.checkStateOK()) { // prevents the write hole on lose focus
                return;
            }
            thing.contest = null;
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_NODOC);
            ekoEditorPreviewTabsElt.html('CLOSE: No Contest Selected: Choose/Create a contest');
            thing.dirtynessCallBack();
        });
        cancelBtn.click(function(){
            if (!thing.checkStateOK()) { // prevents the write hole on lose focus
                return;
            }
            thing.contest = null;
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_NODOC);
            ekoEditorPreviewTabsElt.html('Contest modifications Discarded: Choose/Create a contest');
            thing.dirtynessCallBack();
        });
        saveBtn.click(function(){
            if (!thing.checkStateOK()) { // prevents the write hole on lose focus
                return;
            }
            //alert('saving...');
            thing.save();
            thing.savedHash = thing.hash();
            thing.setState(this.STATE_CLEAN);
            thing.dirtynessCallBack();
        });

        // replace global change Hook
        eko.globalEditorChangeHook = function(){
            //$('#hook').text(' '+getOid()+'changed at: '+(new Date()));
            thing.dirtynessCallBack();
        };

    },
    setState: function(nustate){
        if (nustate==undefined || nustate==null) return;
        //nustate = nustate || (this.state+1)%3;
        this.state = nustate;

        var showOrHide = function(domElt,condition){
            if (condition) domElt.show(); else domElt.hide();
        };
        showOrHide(this.stateNODOCElt,this.isNODOC());
        showOrHide(this.stateCLEANElt,this.isCLEAN());
        showOrHide(this.stateDIRTYElt,this.isDIRTY());
        this.showStatus();
    },
    showStatus:function(){
        var message = 'Error';
        try {
            if (this.isNODOC()) {
                message = 'No Current Document';
            } else if (this.isCLEAN()) {
                message = 'Contest: '+this.contest.name+' does not need saving';
            } else if (this.isDIRTY()) {
                message = 'Contest: '+this.contest.name+' needs saving';
            }
            this.statusElt.text('Status: '+message);
        } catch(err){
            this.statusElt.text(err);
        }
    },
    isNODOC:function(){
        if(this.STATE_NODOC==this.state) return true; return false;
    },
    isCLEAN:function(){
        if(this.STATE_CLEAN==this.state) return true; return false;
    },
    isDIRTY:function(){
        if(this.STATE_DIRTY==this.state) return true; return false;
    },
    save: function(){
        $.ajax({
          type:"POST",    
          url: "accountcontestservices.asmx/SaveContest",          
          data: {"json":$.toJSON(this.contest)},                 
          //contentType: "application/json; charset=utf-8", 
          dataType: "html", 
          success: function(data) {
          },
          error: function(data) {
          }
          });
    },
    exp: function(){
        alert(this.contest.ctt_id);
        $.ajax({
          type:"POST",    
          url: "accountcontestservices.asmx/LoaVotes",          
          data: {"ctt_id":this.contest.ctt_id},                 
            dataType: "xml", 
          success: function(data) {
            var votes = data.getElementsByTagName("string")[0].firstChild.data;
            alert(data.getElementsByTagName("string")[0].firstChild.data );
          },
          error: function(data) {
          }
          });
    },
    del: function(contestName){
        $.ajax({
          type:"POST",    
          url: "accountcontestservices.asmx/DeleteContest",          
          data: {"ctt_id":contestName},                 
          dataType: "html", 
          success: function(data) {
          },
          error: function(data) {
          }
             });
    },
    load: function(contestName,contestLabel){
        var nucontest=null;
        $.ajax({
            type:"POST",   
            url: "accountcontestservices.asmx/LoadContest",       
            data: {"ctt_id":contestName},      
            dataType: 'html', 
            async: false,
            success: function(data) {
                data = data.substring(76);
                data = data.substring(0, data.length-9);
                var loadedContest = $.evalJSON(data);
                nucontest = loadedContest;
                nucontest.name = contestLabel;
                contest1 = loadedContest;
            },
            error: function(data) {
                alert("Load contest failed");
                nucontest = loadedContest;
                nucontest.name = contestLabel;
            }
        });
        return nucontest;
    }
};

function EkoLifeCycle() {
}

