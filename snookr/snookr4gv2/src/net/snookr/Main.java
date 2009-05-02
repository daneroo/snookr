/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr;

import java.io.IOException;
import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.snookr.db.Database;
import net.snookr.db.FSImageDAO;
import net.snookr.model.FSImage;
import net.snookr.scalr.ScalrImpl;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;
import net.snookr.synch.SymmetricDiffs;
import net.snookr.synch.FixFlickrPostedDates;
import net.snookr.synch.FlickrFetch;
import net.snookr.synch.ImageClassification;
import net.snookr.synch.ReadWriteJSON;
import net.snookr.synch.ClearFlickrDB;
import net.snookr.util.Exif;
import net.snookr.util.MD5;
import net.snookr.util.Timer;
import net.snookr.filesystem.Filesystem;
import net.snookr.synch.FilesystemSynch;

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

        //m.scalr();
        //m.classify();
        //m.readWriteJSON();
        //m.clearFlickrDB();
        //System.exit(0);

        List<Runnable> runParts = m.parse(args);
        for (Runnable r : runParts) {
            System.out.println("Running: " + r.getClass().getSimpleName());
            r.run();
        }
        System.exit(0);
    }

    public void readWriteJSON() {
        //FS2DB();
        new ReadWriteJSON().run();
    }

    /* Exercise the httpclient 3.1 multipart post
     */
    static final String postURL = "http://localhost:8080/zip";
    //static final String postURL = "http://scalr.appspot.com/zip";

    public void scalr() {
        ScalrImpl scalr = new ScalrImpl();
        if (false) {
            // LinkedHashMap preserves insertion order in iteration
            Map params = new LinkedHashMap();
            params.put("testkeyforstring", "Hello Scalr, from String");
            params.put("testkeyforbytes1", "Hello Scalr from Bytes-1".getBytes());
            params.put("testkeyforbytes2", "Hello Scalr from Bytes-2".getBytes());
            params.put("testkeyforbytes3", "Hello Scalr from Bytes-3".getBytes());
            params.put("testkeyforfile", new File("/Users/daniel/small.txt"));
            String result = scalr.postMultipart(postURL, params);
            System.out.println("Result: -=-=-=-=-=-=-=-");
            System.out.println(result);
            System.out.println("-=-=-=-=-=-=-=-=-=-=--=");
        }


        int maxSz = 1024 * 1024 * 8;
        //maxSz = 1024*1024;
        for (int sz = 1024 * 512; sz <= maxSz; sz *= 2) {
            Map params = new LinkedHashMap();
            byte[] content = new byte[sz];
            for (int i = 0; i < content.length; i++) {
                content[i] = (byte) (i % 256);
            }
            String expectedMD5 = MD5.digest(content);
            System.out.println(String.format("Testing size: %.1f kB expecting MD5: %s", sz / 1024.0, expectedMD5));

            params.put("key", "test:sz:" + sz);
            params.put("value", content);
            Timer tt = new Timer();
            String result = scalr.postMultipart(postURL, params);
            float rate = tt.rate(sz) / 1024.0f;
            System.out.println(String.format("Transfer rate: %.1f kB/s (%.1fs)", rate, tt.diff()));
            System.out.println("Result: -=-=-=-=-=-=-=-");
            System.out.println(result);
            System.out.println("-=-=-=-=-=-=-=-=-=-=--=");
        }
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

    class FS2JSON implements Runnable {

        final File sourceDir;
        final String hostname;

        FS2JSON(File sourceDir, String hostname) {
            this.sourceDir = sourceDir;
            this.hostname = hostname;
        }

        public void run() {
            Filesystem fs = new Filesystem();
            fs.setBaseDir(sourceDir);
            List<FSImage> list = fs.getFSImageList();

            Database db = new Database();
            FSImageDAO fsImageDAO = new FSImageDAO();
            fsImageDAO.setDatabase(db);

            Map dbMapByFileName = fsImageDAO.getMapByPrimaryKey();
            db.close();

            // part 1 - extract camera info
            List predictor = new ArrayList(dbMapByFileName.size());
            predictor.addAll(dbMapByFileName.values());

            FilesystemSynch fss = new FilesystemSynch(list, predictor);
            fss.run();
        }
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
                accepts("host", "identify this host for naming purposes (db, json filename)").withRequiredArg().ofType(String.class).describedAs("hostname-alias");
                accepts("fetch", "synch FROM flickr to default: ~/SnookrFetchDir").withOptionalArg().ofType(File.class).describedAs("destination directory");
                accepts("push", "synch filesystem TO flickr").withRequiredArg().ofType(File.class).describedAs("source directory");
                //accepts("push", "synch from filesystem TO flickr").withRequiredArg().describedAs("/path1" + pathSeparatorChar + "/path2:...").ofType(File.class).withValuesSeparatedBy(pathSeparatorChar);
                accepts("fs2db", "synch filesystem TO db").withRequiredArg().ofType(File.class).describedAs("source directory");
                accepts("fs2json", "synch filesystem TO <host>.json.zip").withRequiredArg().ofType(File.class).describedAs("source directory");
                accepts("fli2db", "synch flickr TO db");
            }
        };

        boolean showHelp = false;
        String hostname = null;
        List<Runnable> runParts = new ArrayList<Runnable>();
        try {
            OptionSet options = parser.parse(args);
            if (options.has("verbose")) {
                System.out.println("I will be chatty!");
            }
            if (options.has("dry-run")) {
                System.out.println("This is only a test!");
            }

            if (options.has("host")) {
                hostname = (String) options.valueOf("host");
            } else {
                hostname = getDefaultHost();
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
            if (options.has("fs2json")) {
                File sourceDir = (File) options.valueOf("fs2json");
                if (!sourceDir.exists()) {
                    throw new RuntimeException("Source dir not found: " + sourceDir);
                }
                if (hostname == null) {
                    throw new RuntimeException("hostname undefined: use --host <hostalias> ");
                }
                System.out.println("fs2son: to " + hostname + ".json.zip FROM: " + sourceDir);
                runParts.add(new FS2JSON(sourceDir, hostname));
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

    // this doesn't work on hilbert
    private String getDefaultHost() {
        try {
            System.err.println("canonical host: " + java.net.InetAddress.getLocalHost().getCanonicalHostName());
            String hostnamealias = java.net.InetAddress.getLocalHost().getHostName();
            System.err.println("host: " + hostnamealias);
            int firstDot = hostnamealias.indexOf(".");
            if (firstDot > 0) {
                hostnamealias = hostnamealias.substring(0, firstDot);
            }
            hostnamealias = hostnamealias.toLowerCase();
            System.err.println("alias: " + hostnamealias);
            return hostnamealias;
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
