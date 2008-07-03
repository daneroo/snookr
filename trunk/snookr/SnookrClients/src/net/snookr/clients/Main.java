/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.snookr.clients;

import java.io.File;
import net.snookr.db.Database;
import net.snookr.db.FSImageDAO;
import net.snookr.db.FlickrImageDAO;
import net.snookr.flickr.Photos;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;

/**
 *
 * @author daniel
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello Snookr I am a client");

        try {
            System.out.println("Hello Symmetric Diffs");
            File baseDir = new File("/home/daniel/media").getCanonicalFile();
            //def baseDir = new File('C:\\Users\\daniel\\Pictures').getCanonicalFile();
            //def baseDir = new File('/home/daniel/media/Europe2002/5-Mirabel');

            Database db = new Database();

            Filesystem2Database fs2db = new Filesystem2Database();
            fs2db.setBaseDir(baseDir);
            fs2db.setDatabase(db);
            fs2db.run();

            Flickr2Database flickr2db = new Flickr2Database();
            flickr2db.setDatabase(db);
            flickr2db.run();

            //println "-=-=-= Database Summary:  =-=-=-"
            //db.printSummary(false);
            System.out.println("-=-=-= Close Database:  =-=-=-");
            db.close();

        } catch (Exception e) {
            //khaskdhkashd
        }
    }
}
