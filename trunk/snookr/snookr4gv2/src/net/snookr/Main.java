/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.snookr;

import java.io.File;
import net.snookr.db.Database;
import net.snookr.synch.Filesystem2Database;

/**
 *
 * @author daniel
 */
public class Main {
    private File baseDir;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Please Specify baseDir, as in:");
            System.err.println("  java xx.jar /Volumes/DarwinScratch/photo");
            System.err.println("  java xx.jar /home/daniel/media");
            return;
        }

        Main m = new Main(args[0]);
        m.fs2db();
    }

    private Main(String baseDirName) {
        baseDir = new File(baseDirName);
    }

    public void fs2db() {
        Database db = new Database();
        Filesystem2Database fs2db = new Filesystem2Database();
        fs2db.setBaseDir(baseDir);
        fs2db.setDatabase(db);
        fs2db.run();
        db.close();
    }

}
