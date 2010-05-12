/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
module("ajax");

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

    var base = window.location.href.replace(/[^\/]*$/, "");

    stop();
    eko.json.get(base + "data/object.json", function(json) {
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

    var base = window.location.href.replace(/[^\/]*$/, "");
    var k = "mime";
    eko.json.get(base + "data/json.php", function(json) {
        equals( json[k], 'default', 'Content header: default' );
        checkdone();
    });

    eko.json.get(base + "data/json.php?mime=application/json", function(json) {
        equals( json[k], 'application/json', 'Content header: application/json' );
        checkdone();
    });

    eko.json.get(base + "data/json.php?mime=text/html", function(json) {
        equals( json[k], 'text/html', 'Content header: text/html' );
        checkdone();
    });

    eko.json.get(base + "data/json.php?mime=text/xml", function(json) {
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

    var base = window.location.href.replace(/[^\/]*$/, "");
    var k = "charset";

    eko.json.get(base + "data/json.php", function(json) {
        equals( json[k], 'default', 'Content header charset: default' );
        checkdone();
    });

    //stop();
    eko.json.get(base + "data/json.php?cs=utf-8", function(json) {
        equals( json[k], 'utf-8', 'Content header charset: utf-8' );
        checkdone();
    });

    //stop();
    eko.json.get(base + "data/json.php?cs=ISO-8859-1", function(json) {
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

    var base = window.location.href.replace(/[^\/]*$/, "");
    var k = "eacuteOK";

    var eacute = unescape('%E9');

    eko.json.get(base + "data/json.php", function(json) {
        equals( json[k], eacute, 'eacute charset: default' );
        checkdone();
    });

    //stop();
    eko.json.get(base + "data/json.php?cs=utf-8", function(json) {
        equals( json[k], eacute, 'eacute charset: utf-8' );
        checkdone();
    });

    //stop();
    eko.json.get(base + "data/json.php?cs=ISO-8859-1", function(json) {
        equals( json[k], eacute, 'eacute charset: ISO-8859-1' );
        checkdone();
    });

});

test("eko.json.post", function() {
    expect(1);

    var base = window.location.href.replace(/[^\/]*$/, "");

    stop();
    eko.json.post(base + "data/object.json",{}, function(json) {
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

    var base = window.location.href.replace(/[^\/]*$/, "");
    var k = "mime";
    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'default', 'Content header: default' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"application/json"
    });
    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'application/json', 'Content header: application/json' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"text/html"
    });
    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'text/html', 'Content header: text/html' );
        equals( json.input, 'param', 'Check input param' );
        checkdone();
    });

    $.extend(data,{
        mime:"text/xml"
    });
    eko.json.post(base + "data/json.php",data, function(json) {
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
    var base = window.location.href.replace(/[^\/]*$/, "");
    var k = "mime";

    $.extend(data,{
        mime:"application/json",
        input:eacute
    });
    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    });

    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/x-www-form-urlencoded; charset=UTF-8');

    eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/x-www-form-urlencoded; charset=ISO-8859-1');

/*eko.json.post(base + "data/json.php",data, function(json) {
        equals( json[k], 'application/json', 'Content header: text/xml' );
        equals( json.input, eacute, 'Check input param' );
        checkdone();
    },'application/json; charset=utf-8');*/
});

