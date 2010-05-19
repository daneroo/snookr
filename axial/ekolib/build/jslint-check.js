load("build/jslint.js");


var eachSource=true;
if (!eachSource) {    
    doOne("dist/ekolib.js");
} else {
    srcs = [
        "src/ekolib-core-1.0.0.js",
        "src/ekolib-constants-1.0.0.js",
        "src/ekolib-data-1.0.0.js",
        "src/ekolib-editor-1.0.0.js",
        "src/ekolib-chained-1.0.0.js",
        "src/ekolib-render-1.0.0.js",
        "src/ekolib-lifecycle-1.0.0.js"
        ];
    for (var i=0;i<srcs.length;i++){
        doOne(srcs[i]);
    }
}

function doOne(srcFilename){
    print( "JSLint checking file: "+srcFilename+".");

    var src = readFile(srcFilename);
    JSLINT(src, {
        evil: true,
        forin: true
    });

    // All of the following are known issues that we think are 'ok'
    // (in contradiction with JSLint) more information here:
    // http://docs.jquery.com/JQuery_Core_Style_Guidelines
    //
    // Let's start from scratch'
    var ok = {
    /*
	"Expected an identifier and instead saw 'undefined' (a reserved word).": true,
	"Use '===' to compare with 'null'.": true,
	"Use '!==' to compare with 'null'.": true,
	"Expected an assignment or function call and instead saw an expression.": true,
	"Expected a 'break' statement before 'case'.": true
    */
    };

    var e = JSLINT.errors, found = 0, w;

    try {
        for ( var i = 0; i < e.length; i++ ) {
            w = e[i];

            if ( !ok[ w.reason ] ) {
                found++;
                print( srcFilename+":" + w.line + " character " + w.character + ": " + w.reason );
                print( w.evidence + "\n" );
            }
        }
    } catch (exception) {
        print( "\n *** caught exception: " +  exception );

    }

    if ( found > 0 ) {
        print( "\n" + found + " Error(s) found." );

    } else {
        print( "JSLint check passed for "+srcFilename+".");
    }
}
