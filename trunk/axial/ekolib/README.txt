on OSX :
  export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home

The project is built by ant, requires a 1.6 jvm
    uses google's compiler
    uses jslint (with rhino js engine)

Project hierarchy for ekolib:

    build      : build script support
    data       : data files to support demo files
    demo       : examples of use
    dist       : where the built (min) library goes
    nbproject  : Netbeans control files
    src        : the actual library sources themselves
    test       : unit testing directory
    test/data  : data support sirectory for test cases
    test/qunit : qunit sources
    test/unit  : the unti tests themselves
    upstream   : upstream distros for build and included js libs.

rsync -n  -av --progress --exclude .DS_Store --exclude .svn --exclude nbproject /Users/daniel/Documents/NetBeansProjects/axial/ekolib/  mirawatt@axial.mirawatt.com:httpdocs/com/imetrical/axial/ekolib/
