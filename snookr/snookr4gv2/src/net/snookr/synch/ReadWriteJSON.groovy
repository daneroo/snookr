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
import java.text.SimpleDateFormat;
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

        println "Hello JSONZip Read-Write (${getHost()})"

        if (true){
            println("  -Memory vs File Tests");
            int defaultPartSize=500;
            [true,false].each() { memoryBased -> //
                println(((memoryBased)?"Memory":"File") + " Based Test");
                roundTripTimerTest(memoryBased,defaultPartSize);
            }

            println("  -Part Size Tests");
            def sizes=[50,100,200,500,1000];
            sizes.each() { partSize -> //
                roundTripTimerTest(false,partSize);
            }
        }

        // partitioning: byPartSize, byDirectory, byDate, byHash
        println("Partitioning Tests");
        // Here we need a comparator, and a groupByClosure.
        SimpleDateFormat yyyyFmt=new SimpleDateFormat("yyyy");
        SimpleDateFormat yyyyMMFmt=new SimpleDateFormat("yyyy-MM");
        def partitioners = [
            [   name: "byYear",
                comparator: { fsima -> fsima.taken.getTime();  },
                grouper: { fsima -> yyyyFmt.format(fsima.taken); },
            ],
            [   name: "byYearMonth",
                comparator: { fsima -> fsima.taken.getTime();  },
                grouper: { fsima -> yyyyMMFmt.format(fsima.taken); },
            ],
            [   name: "byDirectory",
                comparator: { fsima -> fsima.fileName;  },
                grouper: { fsima -> new File(fsima.fileName).getParent().replaceAll("/",":"); },
            ],
        ];
        partitioners.each() { partitioner -> //
            List list = readFromDB();
            showPartition(list,partitioner);

        }
    }

    private String getHost() {
        try {
            //return java.net.InetAddress.getLocalHost().getCanonicalHostName();
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(JSONZip.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private void showPartition(List list, partitioner){
        println("Partitioner: ${partitioner.name}");
        List ordered = list.sort(partitioner.comparator);
        Map cloMap = list.groupBy(partitioner.grouper);
        TreeMap sortedMap = new TreeMap(cloMap);
        int i=0;
        sortedMap.each() { mappedval,coll -> //
            if (i<2 || i>sortedMap.size()-3){
                println("${String.format("%6d",coll.size())} : ${mappedval} ");
            } else {
                if (i==2) println("      ...");
            }
            i++;
        }
        println("${String.format("%6d",list.size())} : Total ");
        def sizes = sortedMap.collect { mappedval,coll -> coll.size() }
        println("sizes: ${sizes}");
        println("sizes min:${sizes.min()} max:${sizes.max()}");
        roundTripTimerTest(false,sortedMap,"${getHost()}-${partitioner.name}.json.zip");

    }

    private void roundTripTimerTest(boolean memoryBased,int partSize){
        Map map = groupByPartSize(readFromDB(),partSize);
        roundTripTimerTest(memoryBased,map,"${getHost()}-byPart-${String.format("%04d",partSize)}.json.zip");
    }

    private void roundTripTimerTest(boolean memoryBased,Map map,String zipName){
        Timer tt = new Timer();
        tt.restart();
        byte[] b = null;
        if (memoryBased) {
            b = new JSONZip().encode(map);
        } else {
            OutputStream fos = new FileOutputStream(zipName);
            new JSONZip().encode(map,fos);
            fos.close();
        }
        def wtime = tt.diff();

        List list = null;
        tt.restart();
        if (memoryBased) {
            list = join(new JSONZip().decode(b,JSON.FSImageListType));
        } else {
            InputStream fis = new FileInputStream(zipName);
            list = join(new JSONZip().decode(fis,JSON.FSImageListType));
            fis.close();
        }

        def rtime = tt.diff();
        long size = (memoryBased)?b.length:new File(zipName).size();
        println("${String.format("%20s",zipName)} write: ${wtime}s. read: ${rtime}s. -> ${list.size()} images ${size/1024.0} kB");
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

