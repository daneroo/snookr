/*
 * Ajax related pattern
 */
module("ajax", {
    setup: function() {
        function datauri(fname){
            var base = window.location.href.replace(/[^\/]*$/, "");
            var uri = base+'data/'+fname;
            return uri;
        }
        //this.objecturi = datauri('object.json');
        this.objecturi = datauri('object-json.txt');
        //this.dynuri = datauri('json.php');
        this.dynuri = datauri('json.aspx');
    }
});


test("Basic requirements", function() {
    expect(2);
    equals(typeof(eko.json.get),"function","Check that eko.json.get is a function");
    equals(typeof(eko.json.post),"function","Check that eko.json.post is a function");
});

test("Settings", function() {
    expect(1);
    ok( true,"Implement Settings tests");
});

test("eko.json.get", function() {
    expect(1);

    stop();
    eko.json.get(this.objecturi, function(json) {
        equals( json.key, 'value', 'Check JSON: key/value' );
        start();
    });

});

test("eko.json.get mime", function() {
    var exp=4;
    expect(exp);
    stop();
    var count = 0;
    function checkdone(){
        if ( ++count == exp ) start();
    }

    var k = "mime";
    eko.json.get(this.dynuri, function(json) {
        equals( json[k], 'default', 'Content header: default' );
        checkdone();
    });

    eko.json.get(this.dynuri+"?mime=application/json", function(json) {
        equals( json[k], 'application/json', 'Content header: application/json' );
        checkdone();
    });

    eko.json.get(this.dynuri+"?mime=text/html", function(json) {
        equals( json[k], 'text/html', 'Content header: text/html' );
        checkdone();
    });

    eko.json.get(this.dynuri+"?mime=text/xml", function(json) {
        equals( json[k], 'text/xml', 'Content header: text/xml' );
        checkdone();
    });
});


test("eko.json.get charset", function() {
    var exp=3;
    expect(exp);
    stop();
    var count = 0;
    function checkdone(){
        if ( ++count == exp ) start();
    }

    var k = "charset";

    eko.json.get(this.dynuri, function(json) {
        equals( json[k], 'default', 'Content header charset: default' );
        checkdone();
    });

    //stop();
    eko.json.get(this.dynuri+"?cs=utf-8", function(json) {
        equals( json[k], 'utf-8', 'Content header charset: utf-8' );
        checkdone();
    });

    //stop();
    eko.json.get(this.dynuri+"?cs=ISO-8859-1", function(json) {
        equals( json[k], 'ISO-8859-1', 'Content header charset: ISO-8859-1' );
        checkdone();
    });

});

test("eko.json.get eacute", function() {
    var exp=3;
    expect(exp);
    stop();
    var count = 0;
    function checkdone(){
        if ( ++count == exp ) start();
    }

    var k = "eacuteOK";

    var eacute = unescape('%E9');

    eko.json.get(this.dynuri, function(json) {
        equals( json[k], eacute, 'eacute charset: default' );
        checkdone();
    });

    //stop();
    eko.json.get(this.dynuri+"?cs=utf-8", function(json) {
        equals( json[k], eacute, 'eacute charset: utf-8' );
        checkdone();
    });

    //stop();
    eko.json.get(this.dynuri+"?cs=ISO-8859-1", function(json) {
        equals( json[k], eacute, 'eacute charset: ISO-8859-1' );
        checkdone();
    });

});

test("eko.json.post", function() {
    expect(1);

    stop();
    eko.json.post(this.dynuri,{}, function(json) {
        equals( json.key, 'value', 'Check JSON: key/value' );
        start();
    });

});

test("eko.json.post - mime out", function() {
    var exp=4;
    expect(2*exp);
    stop();
    var count = 0;
    function checkdone(){
        if ( ++count == exp ) start();
    }
    var eacute = unescape('%E9');
    var data  = {
        input:"param"
    };

    var k = "mime";
    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'default', 'Content header: default' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"application/json"
    });
    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'application/json', 'Content header: application/json' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"text/html"
    });
    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'text/html', 'Content header: text/html' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"text/xml"
    });
    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'text/xml', 'Content header: text/xml' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });
});

test("eko.json.post - contentType up", function() {
    var exp=3;
    expect(2*exp);
    stop();
    var count = 0;
    function checkdone(){
        if ( ++count == exp ) start();
    }
    var eacute = unescape('%E9');
    var data  = {
        input:"param"
    };
    var k = "mime";

    $.extend(data,{
        mime:"application/json",
        input:eacute
    });
    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    });

    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/x-www-form-urlencoded; charset=UTF-8');

    eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/x-www-form-urlencoded; charset=ISO-8859-1');

/*eko.json.post(this.dynuri,data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/json; charset=utf-8');*/
});

