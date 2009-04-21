/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main m = new Main();


        //m.classify();
        //m.readWriteJSON();
        //m.clearFlickrDB();

        List<Runnable> runParts = m.parse(args);
        for (Runnable r : runParts) {
            System.out.println("Runing: " + r.getClass().getSimpleName());
            r.run();
        }
        System.exit(0);
    }

    public void readWriteJSON() {
        //FS2DB();
        new ReadWriteJSON().run();
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

    class FS2DB implements Runnable {

        File sourceDir;

        FS2DB(File sourceDir) {
            this.sourceDir = sourceDir;
        }

        public void run() {
            Database db = new Database();
            Filesystem2Database fs2db = new Filesystem2Database();
            fs2db.setBaseDir(sourceDir);
            fs2db.setDatabase(db);
            fs2db.run();
            db.close();
        }
    }

    class Fli2DB implements Runnable {

        public void run() {
            Database db = new Database();
            Flickr2Database fli2db = new Flickr2Database();
            fli2db.setDatabase(db);
            fli2db.run();
            db.close();
        }
    }

    class PushToFlickr implements Runnable {

        File sourceDir;

        PushToFlickr(File sourceDir) {
            this.sourceDir = sourceDir;
        }

        public void run() {
            SymmetricDiffs sd = new SymmetricDiffs();
            sd.setBaseDir(sourceDir);
            sd.run();

            System.out.println("");
            System.out.println("Now Fix Dates");
            FixFlickrPostedDates ffpd = new FixFlickrPostedDates();
            ffpd.run();
        }
    }

    class FetchFromFlickr implements Runnable {

        File fetchDir;

        FetchFromFlickr(File fetchDir) {
            this.fetchDir = fetchDir;
        }

        public void run() {
            FlickrFetch ff = new FlickrFetch();
            ff.setBaseDir(fetchDir);
            ff.run();
        }
    }

    private List<Runnable> parse(String[] args) {
        System.out.println("Hello Snookr!");
        OptionParser parser = new OptionParser() {

            {
                acceptsAll(Arrays.asList("verbose", "v"), "be more verbose");
                acceptsAll(Arrays.asList("dry-run", "n"), "dry-run (no side effects)");
                acceptsAll(Arrays.asList("help", "h", "?"), "show this help message");
                accepts("fetch", "synch FROM flickr to default: ~/SnookrFetchDir").withOptionalArg().ofType(File.class).describedAs("destination directory");
                accepts("push", "synch filesystem TO flickr").withRequiredArg().ofType(File.class).describedAs("source directory");
                //accepts("push", "synch from filesystem TO flickr").withRequiredArg().describedAs("/path1" + pathSeparatorChar + "/path2:...").ofType(File.class).withValuesSeparatedBy(pathSeparatorChar);
                accepts("fs2db", "synch filesystem TO db").withRequiredArg().ofType(File.class).describedAs("source directory");
                accepts("fli2db", "synch flickr TO db");
            }
        };

        boolean showHelp = false;
        List<Runnable> runParts = new ArrayList<Runnable>();
        try {
            OptionSet options = parser.parse(args);
            if (options.has("verbose")) {
                System.out.println("I will be chatty!");
            }
            if (options.has("dry-run")) {
                System.out.println("This is only a test!");
            }

            if (options.has("fetch")) {
                //if (options.hasArgument("fetch")) {...}
                //  null if no argument
                File fetchDir = (File) options.valueOf("fetch");
                if (fetchDir == null) {
                    fetchDir = getDefaultFetchDirectory();
                }
                if (!fetchDir.exists()) {
                    System.err.println("Warning Destination directory not found: " + fetchDir);
                    System.err.println(" Will attempt to create");
                }
                System.out.println("Fetch: from flickr TO: " + fetchDir);
                runParts.add(new FetchFromFlickr(fetchDir));
            }
            if (options.has("push")) {
                File sourceDir = (File) options.valueOf("push");
                if (!sourceDir.exists()) {
                    throw new RuntimeException("Source dir not found: " + sourceDir);
                }
                System.out.println("Push: to flickr FROM: " + sourceDir);
                runParts.add(new PushToFlickr(sourceDir));
            }
            if (options.has("fs2db")) {
                File sourceDir = (File) options.valueOf("fs2db");
                if (!sourceDir.exists()) {
                    throw new RuntimeException("Source dir not found: " + sourceDir);
                }
                System.out.println("fs2db: to db FROM: " + sourceDir);
                runParts.add(new FS2DB(sourceDir));
            }
            if (options.has("fli2db")) {
                System.out.println("fli2db: to db FROM: flickr");
                runParts.add(new Fli2DB());
            }

            if (options.has("help")) {
                try {
                    parser.printHelpOn(System.out);
                } catch (IOException ex) {
                }
            }
        } catch (Exception e) { //  At lest OptionException oe
            try {
                parser.printHelpOn(System.err);
                System.err.println("");
                System.err.println(e.getMessage());
            } catch (IOException ex) {
            }
        }
        for (Runnable r : runParts) {
            System.out.println("will run: " + r.getClass().getSimpleName());
        }
        return runParts;
    }

    private File getDefaultFetchDirectory() {
        String homeDirPath = System.getProperty("user.home");
        File homeDir = new File(homeDirPath);
        if (!homeDir.exists()) {
            throw new RuntimeException("Cannot Find HomeDir: " + homeDir);
        }
        File fetchDir = new File(homeDir, "SnookrFetchDir");
        return fetchDir;
    }
}
