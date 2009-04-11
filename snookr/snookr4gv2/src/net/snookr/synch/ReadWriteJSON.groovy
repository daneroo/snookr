/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.snookr.synch
import net.snookr.db.Database;
import net.snookr.db.FSImageDAO;
import net.snookr.db.FlickrImageDAO;
import net.snookr.flickr.Photos;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;
import net.snookr.transcode.JSON;
import net.snookr.util.Timer;

/**
 *
 * @author daniel
 */
class ReadWriteJSON {
    public void run() {
        def verbose=false;
        println "Hello JSON Read-Write"
        Timer tt = new Timer();

        tt.restart();
        List first = [];//readFromJSONFile();
        println("+List has ${first.size()} entries");
        println("Read ${first.size()} entries from json in ${tt.diff()}s.");

        tt.restart();
        List list = readFromDB();
        println("Read ${list.size()} entries from db in ${tt.diff()}s.");
        println("-List has ${list.size()} entries");

        tt.restart();
        writeToJSONFile(list);
        println("Wrote ${list.size()} entries to json in ${tt.diff()}s.");

        tt.restart();
        List back = readFromJSONFile();
        println("+List has ${back.size()} entries");
        println("Read ${back.size()} entries from json in ${tt.diff()}s.");
    }

    private static String prefix = "/Volumes/DarwinScratch/photo";
    public void trim(List list){
        while (list.size()>1000) {
            list.remove(0);
        }
        return;
        list.each() { fsima -> //
            fsima.fileName = fsima.fileName.replaceFirst(prefix,"");
        }
    }
    public void expand(List list){
        return;
        list.each() { fsima -> //
            fsima.fileName = prefix+fsima.fileName;
        }
    }
    private static String jsonFilename = "filesystem.json";
    public void writeToJSONFile(List list){
        trim(list);
        FileWriter fw = new FileWriter(jsonFilename);
        new JSON().encode(list,fw);
        fw.close();
    }
    public List readFromJSONFile() {
        FileReader fr = new FileReader(jsonFilename);
        List list = new JSON().decodeFSImageList(fr);
        fr.close();
        expand(list);
        return list;
    }
    public List readFromDB() {
        Database db = new Database();
        FSImageDAO fsImageDAO = new FSImageDAO();
        fsImageDAO.setDatabase(db);

        Map dbMapByFileName = fsImageDAO.getMapByPrimaryKey();
        db.close();

        // part 1 - extract camera info
        List list = [];
        dbMapByFileName.each() { fileName,fsima -> //
            list.add(fsima);
        }
        return list;
    }
}

