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
import net.snookr.transcode.JSONZip;
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
 *  The objective here is to encode/decode a List of [FSImage|FlickrImage] objects
 *   each encoded as JSON, and then zipped.
 *   The List needs to be split into parts for 2 reasons:
 *     - JSON encode decode performance,
 *     - The List parts will be the first level of incremental transport
 *    SO The List need to be reliably partitioned, and ordered.
 *     - Order by primaryKey split by partSize
 *     - Order by date, split by Year/Month
 *  These cand be specified in groovy by sort closure and Collection.groupBy Closure.
 *    - We will work in memory (byte[])
 *     List -> byte[] { zip (parts.json) }
 */
class ReadWriteJSON {
    public void run() {
        println "Hello JSON Read-Write"
        Timer tt = new Timer();

        List list;

        def sizes=[10,50,100,200,500,1000];
        //def sizes=[200];
        sizes.each() { partSize -> //
            [1,2,3].each() {
                oldTest(partSize);
            }
        }
        println "Hello JSONZip Read-Write"
        [true,false].each() { memoryBased -> //
            println(((memoryBased)?"Memory":"File") + " Based Test");
            sizes.each() { partSize -> //
                [1,2,3].each() {
                    newTest(memoryBased,partSize);
                }
            }
        }
    }

    private void oldTest(int partSize){
        Timer tt = new Timer();
        tt.restart();
        List list = readFromDB();

        tt.restart();
        writeToGSONZipFile(list,partSize);
        def wtime = tt.diff();

        list = null;
        tt.restart();
        list = readFromGSONZipFile();
        def rtime = tt.diff();
        println("part size partsz=${String.format("%4d",partSize)} write: ${wtime}s. read: ${rtime}s. -> ${list.size()} images ${new File(gsonZipFilename).size()/1024.0} kB");
    }
    private void newTest(boolean memoryBased,int partSize){
        Timer tt = new Timer();
        tt.restart();
        tt.restart();
        Map map = groupByPartSize(readFromDB(),partSize);

        tt.restart();
        byte[] b = null;
        if (memoryBased) {
            b = new JSONZip().encode(map);
        } else {
            OutputStream fos = new FileOutputStream(gsonZipFilename);
            new JSONZip().encode(map,fos);
            fos.close();
        }
        //println("Wrote ${list.size()} entries to   gson.zip[sz=${String.format("%4d",partSize)}] in ${tt.diff()}s.");
        def wtime = tt.diff();

        List list = null;
        tt.restart();
        if (memoryBased) {
            list = join(new JSONZip().decode(b,JSONZip.FSImageListType));
        } else {
            InputStream fis = new FileInputStream(gsonZipFilename);
            list = join(new JSONZip().decode(fis,JSONZip.FSImageListType));
            fis.close();
        }

        def rtime = tt.diff();
        long size = (memoryBased)?b.length:new File(gsonZipFilename).size();
        println("part size partsz=${String.format("%4d",partSize)} write: ${wtime}s. read: ${rtime}s. -> ${list.size()} images ${size/1024.0} kB");
    }
    private List join(Map<String,List> map){
        List list = [];
        for (Map.Entry<String, List> e : map.entrySet()) {
            String name = e.getKey();
            List part = e.getValue();
            list.addAll(part);
        }
        return list;
    }
    private Map<String,List> groupByPartSize(List list,int partSize){
        Map<String,List> map = new LinkedHashMap<String,List>();
        for (int sub = 0; sub < list.size(); sub += partSize) {
            String name = String.format("part-%08d.json", sub);
            List nextPart = list.subList(sub, Math.min(sub + partSize, list.size()));
            map.put(name,nextPart);
        }
        return map;
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

