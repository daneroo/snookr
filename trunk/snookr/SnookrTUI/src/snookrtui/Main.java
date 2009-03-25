/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snookrtui;

import java.io.File;

/**
 *
 * @author daniel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }// wait a little
            progress("Message: " + String.format("%5d", i));
        }
        System.exit(0);
        //Report:
        new ImageClassification().run();
        System.exit(0);

        if (args.length < 1) {
            System.err.println("Please Specify baseDir, as in:");
            System.err.println("  java xx.jar /Volumes/DarwinScratch/photo");
            System.err.println("  java xx.jar /home/daniel/media");
            return;
        }
        File baseDir = new File(args[0]);
        SymmetricDiffs sd = new SymmetricDiffs();
        sd.setBaseDir(baseDir);
        sd.run();

        System.out.println("");
        System.out.println("Now Fix Dates");
        FixFlickrPostedDates ffpd = new FixFlickrPostedDates();
        ffpd.run();

        //Report:
        new ImageClassification().run();
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
