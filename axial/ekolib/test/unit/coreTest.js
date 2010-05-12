module("core");

test("Basic external requirements", function() {
    expect(4);
    ok( jQuery,"Check that jQuery global is defined");
    ok( $,"Check that $ variable is defined");
    equals($.fn.jquery,"1.4.2","Check for expected version of jQuery");
    // how about JSON plugin
    ok( $.toJSON,"Check jquery-json presence");
});

test("Basic requirements", function() {
    expect(4);
    ok( eko,"Check that eko global is defined");
    ok( eko.version,"Check that eko.version eixists");
    equals(typeof(eko.debug),"function","Check that eko.debug is a function");
    equals(typeof(eko.isEmpty),"function","Check that eko.isEmpty is a function");
});

test("trim and isEmpty", function() {
    expect(15);
    // trim and empty
    equals($.trim("OK"),"OK","trim(OK)");
    equals($.trim('   leading'),'leading',"trim('  leading')");
    equals($.trim('trailing  '),'trailing',"trim('trailing  ')");
    equals($.trim('   both   '),'both',"trim('  both  ')");
    equals($.trim(''),'',"trim('')");
    equals($.trim(null),"","trim(null)");
    equals($.trim(),"","trim()");
    equals(typeof(eko.isEmpty),"function","Check that eko.isEmpty is a function");
    equals(eko.isEmpty("OK"),false,"eko.isEmtpy('OK')");
    equals(eko.isEmpty("   leading"),false,"eko.isEmtpy('   leading')");
    equals(eko.isEmpty("trailing  "),false,"eko.isEmtpy('trailing  ')");
    equals(eko.isEmpty("  both  "),false,"eko.isEmtpy('   both  ')");
    equals(eko.isEmpty(""),true,"eko.isEmtpy('')");
    equals(eko.isEmpty(null),true,"eko.isEmtpy(null)");
    equals(eko.isEmpty(),true,"eko.isEmtpy()");
});


