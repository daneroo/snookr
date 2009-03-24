/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.snookr.task

/**
 *
 * @author daniel
 */
class DirListing  extends org.jdesktop.application.Task<Object, Void> {
    DirListing(org.jdesktop.application.Application app) {
        // Runs on the EDT.  Copy GUI state that
        // doInBackground() depends on from parameters
        // to GrooveSomethingTask fields, here.
        super(app);
        setBaseDir(new File(System.getProperty("user.home")+"/media"));
    }

    File baseDir=null;
    public void setBaseDir(File aBaseDir){
        baseDir = aBaseDir;
    }

    long start;
    Map files = [
        "image":[],
        "skipped":[],
        "directory":[],
        "other":[]
    ];
    def dirs=[];
    @Override protected Object doInBackground() {
        // Your Task's code here.  This method runs
        // on a background thread, so don't reference
        // the Swing GUI from here.

        start = new Date().getTime();
        //baseDir.eachFileRecurse { f -> // examine each File
        baseDir.eachDirRecurse { f -> // examine each Dir
            //setMessage("Directory: ${f.getCanonicalFile()}");
            setMessage("Found ${dirs.size()} directories}");
            dirs << f.getCanonicalFile();
        }
        long elapsed = new Date().getTime()-start;
        println "Dirs in ${elapsed/1000.0}";
        start = new Date().getTime();
        baseDir.eachFileRecurse { f -> // examine each Dir
            if (f.isFile()) {
                String fileName = f.getName();
                if ( fileName.endsWith(".JPG") || fileName.endsWith(".jpg") ) {
                    files["image"] << f.getCanonicalFile();
                } else {
                    files["skipped"] << f.getCanonicalFile();
                }
            } else {
                // if not a directory what ?
                if (f.isDirectory()) {
                    files["directory"] << f.getCanonicalFile();
                } else {
                    files["other"] << f;
                }
            }
        }
        elapsed = new Date().getTime()-start;
        println "Files+Dirs in ${elapsed/1000.0}";
        start = new Date().getTime();

        return null;  // return your result
    }
    @Override protected void succeeded(Object result) {
        // Runs on the EDT.  Update the GUI based on
        // the result computed by doInBackground().
        println "fs classification"
        files.each() { k,v ->
            println "  ${k} : ${v.size()}"
        }
        println "Directories: ${dirs.size()}";
        long elapsed = new Date().getTime()-start;
        setMessage("Files in ${elapsed/1000.0}");

    }
}
