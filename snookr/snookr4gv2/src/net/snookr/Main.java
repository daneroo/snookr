/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr;

import java.io.File;
import net.snookr.db.Database;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;
import net.snookr.synch.SymmetricDiffs;
import net.snookr.synch.FixFlickrPostedDates;
import net.snookr.synch.FlickrFetch;
import net.snookr.synch.ImageClassification;
import net.snookr.synch.ReadWriteJSON;
import net.snookr.synch.ClearFlickrDB;
import net.snookr.util.Exif;

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
        //testProgress();
        if (args.length < 1) {
            System.err.println("Please Specify baseDir, as in:");
            System.err.println("  java xx.jar /Volumes/DarwinScratch/photo");
            System.err.println("  java xx.jar /home/daniel/media");
            return;
        }

        Main m = new Main(args[0]);
        //m.fs2db();
        m.fli2db();
        //m.classify();
        //m.readWriteJSON();
        //m.pushToFlickr();
        //m.clearFlickrDB();
        //m.fetch();
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
    public void fli2db() {
        Database db = new Database();
        Flickr2Database fli2db = new Flickr2Database();
        fli2db.setDatabase(db);
        fli2db.run();
        db.close();
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

    public void readWriteJSON() {
        //fs2db();
        new ReadWriteJSON().run();
    }

    public void fetch() {
        new FlickrFetch().run();
    }

    public void classify() {
        //Exif.showAllTags(new File("/Volumes/DarwinScratch/photo/catou/2002_06_30/100-0065_IMG.JPG"));
        Exif.getCameraOwner(new File("/Volumes/DarwinScratch/photo/catou/2002_06_30/100-0065_IMG.JPG"));
        //Exif.showAllTags(new File("/Volumes/DarwinScratch/photo/dadSulbalcon/200207/100-0063_IMG.JPG"));
        Exif.getCameraOwner(new File("/Volumes/DarwinScratch/photo/dadSulbalcon/200207/100-0063_IMG.JPG"));

        new ImageClassification().run();
    }

    public void clearFlickrDB() {
        new ClearFlickrDB().run();
    }

    private static void testProgress() {
        if (true) {
            for (int i = 0; i < 100; i++) {
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
