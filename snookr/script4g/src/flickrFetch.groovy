import groovy.xml.*
import groovy.util.slurpersupport .*
import java.text.SimpleDateFormat;

import net.snookr.flickr.Flickr;
import net.snookr.flickr.Photos;
import net.snookr.db.Database;
import net.snookr.db.FlickrImageDAO;
import net.snookr.util.Spawner;
import net.snookr.util.Progress;
import net.snookr.util.MD5;
import net.snookr.model.FlickrImage;
/*
  What this script does: 
    Fetches photos...
getSizes returns data as:
source: is the url for the image itself
url: is a web page for that photo at that size

<sizes canblog="1" canprint="1" candownload="1">
        <size label="Square" width="75" height="75" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_s.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=sq" />
        <size label="Thumbnail" width="100" height="75" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_t.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=t" />
        <size label="Small" width="240" height="180" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_m.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=s" />
        <size label="Medium" width="500" height="375" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=m" />
        <size label="Large" width="1024" height="768" source="http://farm1.static.flickr.com/145/419443247_34755ec3f3_b.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=l" />
        <size label="Original" width="2592" height="1944" source="http://farm1.static.flickr.com/145/419443247_1195f586b4_o.jpg" url="http://www.flickr.com/photo_zoom.gne?id=419443247&amp;size=o" />
</sizes>

*/

Flickr f = new Flickr();
    public void makeDir(File dir) {
        if(!dir.exists()){
            boolean success = dir.mkdir();
            if (!success) {
                println("Directory creation failed: "+dir);
                throw new Exception("Directory creation failed: "+dir);
                return;
            }
        }
    }
    public void saveToFile(String photoid,String urlStr,File newFile) {
        println "  url: ${urlStr} file:${newFile}";
        URL url = new URL(urlStr);
        BufferedInputStream inStream = new BufferedInputStream(url.openStream());
        FileOutputStream fos = new FileOutputStream(newFile);
        int read;
        while ((read = inStream.read()) != -1) {
            fos.write(read);
        }
        fos.flush();
        fos.close();
        inStream.close();
    }
    public void saveSizesToFiles(String photoid,Map mapOfSizeUrls) {
        //listOfSizes = ["Thumbnail","Square","Small"];
        listOfSizes = ["Square","Small"];
        static final File BASE_DIRECTORY = new File('C:\\Users\\daniel\\SnookrFetchDir').getCanonicalFile();
        makeDir(BASE_DIRECTORY);
        println("Fetching " + photoid);
        listOfSizes.each() { whichSize -> //
            sizeDir = new File(BASE_DIRECTORY,whichSize);
            makeDir(sizeDir);
            String filename = photoid+".jpg";
            File newFile = new File(sizeDir,filename);
            saveToFile(photoid,mapOfSizeUrls[whichSize],newFile);
        }
    }

String Krazrid = "2188744290";
String SD300id = "419443247";
[Krazrid,SD300id].each() { photoid -> //
    Map mapOfSizeUrls =  new Photos().getSizes(photoid);
    saveSizesToFiles(photoid,mapOfSizeUrls);
}

//System.exit(0);


// getting the list from flickr, could get from db instead.
int getPhotoListThreads=10;
def flickrList  = new Photos().getPhotoList(getPhotoListThreads);

    int getPhotoSizesThreads=20;
    Closure getPhotoSizesClosure = { photo ->
        String photoid = photo.photoid;
        Map mapOfSizeNames =  new Photos().getSizes(photoid);
        saveSizesToFiles(photoid,mapOfSizeUrls);
    }
    new Spawner(flickrList,getPhotoSizesClosure,getPhotoSizesThreads).run();
