/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snookrtui;

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
        //m.readWriteJSON();
        //m.classify();
        
        //m.clearFlickrDB();
        m.pushToFlickr();

    }

    private Main(String baseDirName) {
        baseDir = new File(baseDirName);
    }

    public void readWriteJSON() {
        //fs2db();
        new ReadWriteJSON().run();
    }

    public void classify() {
        fs2db();
        new ImageClassification().run();
    }

    public void fs2db() {
        Database db = new Database();
        Filesystem2Database fs2db = new Filesystem2Database();
        fs2db.setBaseDir(baseDir);
        fs2db.setDatabase(db);
        fs2db.run();
        db.close();
    }

    public void clearFlickrDB() {
        new ClearFlickrDB().run();
    }

    public void pushToFlickr() {
        SymmetricDiffs sd = new SymmetricDiffs();
        sd.setBaseDir(baseDir);
        sd.run();

        System.out.println("");
        System.out.println("Now Fix Dates");
        FixFlickrPostedDates ffpd = new FixFlickrPostedDates();
        ffpd.run();
    }

    private static void testProgress() {
        if (false) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                } // wait a little
                progress("Message: " + String.format("%5d", i));
            }
        }
    }
    static final byte besc[] = {27};
    static final String esc = new String(besc);
    static final String clearline = esc + "[K";

    public static void progress(String msg) {
        msg = " " + msg; // room for cursor.
        int size = msg.length();
        String rewind = esc + "[" + size + "D";
        System.err.print(clearline + msg + rewind);
        System.err.flush();
    }
}
