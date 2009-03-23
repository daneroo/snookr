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
        if (args.length<1) {
            System.err.println("Please Specify baseDir, as in:");
            System.err.println("  java xx.jar /Volumes/DarwinScratch/photo");
            System.err.println("  java xx.jar /home/daniel/media");
            return;
        }
        File baseDir = new File(args[0]);
        SymmetricDiffs sd = new SymmetricDiffs();
        sd.setBaseDir(baseDir);
        sd.run();
    }

}
