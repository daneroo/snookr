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
import net.snookr.model.FSImage
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.OutputStream;
import java.io.FileOutputStream;
/**
 *
 * @author daniel
 */
class ReadWriteJSON {
    public void run() {
        println "Hello JSON Read-Write"
        Timer tt = new Timer();

        List list;

        //def sizes=[10,50,100,200,500,1000];
        def sizes=[200];
        sizes.each() { partSize -> //
           // println("--------------------");
            tt.restart();
            list = readFromDB();
            //println("Read  ${list.size()} entries from db in ${tt.diff()}s.");
            
            //tt.restart();
            //writeToGSONFile(list);
            //println("Wrote ${list.size()} entries to gson in ${tt.diff()}s.");

            tt.restart();
            writeToGSONZipFile(list,partSize);
            //byte[] b = new JSON().encodeZip(list,partSize);
            //println("Wrote ${list.size()} entries to   gson.zip[sz=${String.format("%4d",partSize)}] in ${tt.diff()}s.");
            def wtime = tt.diff();

            //tt.restart();
            //list = readFromGSONFile();
            //println("Read  ${list.size()} entries from gson in ${tt.diff()}s.");

            list = null;
            tt.restart();
            list = readFromGSONZipFile();
            //list = new JSON().decodeFSImageListZip(b);
            //println("Read  ${list.size()} entries from gson.zip[sz=${String.format("%4d",partSize)}] in ${tt.diff()}s.");
            def rtime = tt.diff();

            println("part size partsz=${String.format("%4d",partSize)} write: ${wtime}s. read: ${rtime}s. -> ${new File(gsonZipFilename).size()/1024.0} kB");
            //println("part size partsz=${String.format("%4d",partSize)} write: ${wtime}s. read: ${rtime}s. -> ${b.length/1024.0} kB");

        }
    }

    private static String gsonFilename = "filesystem.json";
    public void writeToGSONFile(List list){
        FileWriter fw = new FileWriter(gsonFilename);
        new JSON().encode(list,fw);
        fw.close();
    }
    public List readFromGSONFile() {
        FileReader fr = new FileReader(gsonFilename);
        List list = new JSON().decodeFSImageList(fr);
        fr.close();
        return list;
    }

    private static String gsonZipFilename = "filesystem.json.zip";
    public void writeToGSONZipFile(List list,int partSize){
        OutputStream out = new FileOutputStream(gsonZipFilename);
        new JSON().encodeZip(list,out,partSize);
        out.close();
    }
    public List readFromGSONZipFile() {
        boolean useStream=true;
        if (useStream){
            InputStream is = new FileInputStream(gsonZipFilename);
            List list = new JSON().decodeFSImageListZip(is);
            is.close();
            return list;
        } else {
            List list = new JSON().decodeFSImageListZip(new File(gsonZipFilename));
            return list;
        }

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

