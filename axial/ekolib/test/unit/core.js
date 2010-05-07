module("core");

test("Basic requirements", function() {
	expect(4);
	ok( jQuery,"Check that jQuery global is defined");
	ok( $,"Check that $ variable is defined");
    equals($.fn.jquery,"1.4.2","Check for expected version of jQuery");
    // how about Json plugin
	ok( $.toJSON,"Check jquery-json presence");
});


