import groovy.xml.*
import groovy.util.slurpersupport .*
import java.text.SimpleDateFormat;
// MD5.doATest();
import net.snookr.flickr.Flickr;
import net.snookr.flickr.Photos;
import net.snookr.db.Database;
import net.snookr.util.Spawner;
import net.snookr.util.Progress;
import net.snookr.util.MD5;
import net.snookr.model.FlickrImage;

println "Deprecated, see filckrSynch.";
System.exit(0);


Flickr f = new Flickr();

// wow this worked !
// println f.uploadPhoto(new File("snookr.jpg"));

/* Other examples and encoding test....
String china = "\u4e2d\u56fd";
String ete = "\u00e9t\u00e9";
printAndParse(f.getEcho(["ampersands":"A &&??;A","china":china,"ete":ete]));
printAndParse(f.getEcho(["a":"AA","b":"BB","c":"CC"]));
printAndParse(f.getPhotoInfo(["photo_id":"405488245"]));
printAndParse(f.getPhotoSearch(["user_id":Environment.user_id,"per_page":"3","page":"2"]));
*/

if (false) {
    // test data - some exif - some not
    ["421554331","421387826","398440118","420940300","420939034"].each() {
        testPhotoDate(f,it);
        testExif(f,it);
    }
    
    // these five photoid hav >0 exif tagsbut <=8 and no dates
    ["420939034","420938870","420933228","420929005","419613600"].each() {
        //testPhotoDate(f,it);
        testExif(f,it);
    }

    System.exit(0)    
}


void testExif(Flickr ff,String photoid) {
    Map attr = ["photoid":photoid];
    testExif(ff,attr);
}

void testExif(Flickr ff,Map attr) { 
    String exif = ff.getExif(["photo_id":attr.photoid]);
    //printAndParse(exif);
    def rsp = new XmlSlurper(false,true).parseText(exif);

    // this test should really be : has exif date!
    // for now found this photo 419613600, with 8 tags, and no date!
    boolean hasExif = (rsp.photo.exif.list().size()>8)

    //println "----- ${attr.photoid} hasExif:${hasExif} exif segs: " + rsp.photo.exif.list().size()

    def tiffDate = rsp.photo.exif.findAll{ it.@tagspace == "TIFF" && it.@tag=="306" }.text()
    def exifDate = rsp.photo.exif.findAll{ it.@tagspace == "EXIF" && it.@tag=="36867" }.text()
    println "myTest:${hasExif} Date and Time tiff:${tiffDate} exif:${exifDate}";

    if (hasExif) assert tiffDate != null
    if (!hasExif) assert tiffDate == ""
    if (hasExif) assert exifDate != null
    if (!hasExif) assert exifDate == ""
    // got Expression: (exifDate==tiffDate). Values: exifDate = , tiffDate = 2002:07:30 17:32:12
    // got Expression: (exifDate== tiffDate). Values: exifDate = 2003:11:01 18:53:44, tiffDate = 2003:12:09 13:35:51
    
    assert exifDate==tiffDate
    return;


    println "----- ${attr.photoid} hasExif:${hasExif} exif segs: " + rsp.photo.exif.list().size()
    if (!hasExif) {
        println "+++++ ${attr.photoid} hasExif:${hasExif} exif segs: " + rsp.photo.exif.list().size()
        println "setNoExifDate disabled"
        //String nxfrsp  = ff.setNoExifDates(attr.photoid);
        //println nxfrsp;
    }
}
void testPhotoDate(Flickr ff,String photoid) {
    Map attr = fillInfo(photoid,ff);
    testPhotoDate(ff,attr);
}

// prob with 419604505 ??
// 396326271

void testPhotoDate(Flickr ff,Map attr) {
    //println "PhotoDate - ${attr.photoid}"
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date posted = null;;
    try {
        posted = new Date(Long.parseLong(attr.posted)*1000l);
    } catch (Exception e) {
        e.printStackTrace();
        println "***********************  BAD posted Date ***********************************"
        println "${attr}"
        println "****************************************************************************"
        return;
    }
    Date taken = sdf.parse(attr.taken)
    // apply the test
    // if taken == posted -> ok because already set, or no exif
    if (taken.equals(posted)) {
        //println " ${attr.photoid} -already equal: ${sdf.format(taken);} ${sdf.format(posted);} th:${Thread.currentThread().getName()}"
    } else {
        println "${attr}"
        //println "  photo has posted=${posted.getTime()} "+sdf.format(posted);
        //println "  photo has  taken=${taken.getTime()} "+sdf.format(taken);

        println " attr.photoid} -set posted to  ${sdf.format(taken)} (overwrite ${sdf.format(posted)} )  th:${Thread.currentThread().getName()}"

        println "*** actually updating dates is disabled"
        //String rsp  = ff.setPostedDate(attr.photoid,taken);
        //println rsp;
    }
    
}



//def flickrIdListSingle   = new Photos().getPhotoList(1);
//def flickrIdListMulti2   = new Photos().getPhotoList(2);
def flickrIdListMulti10  = new Photos().getPhotoList(10);

def flickrIdList  = flickrIdListMulti10


// -=-=-=-= get predictor (from db ?)

Database db = new Database();
println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);

dbPredictorByPhotoid = db.getMapForClassByPrimaryKey(FlickrImage.class,"photoid");
println "getMapForClassByPrimaryKey has ${dbPredictorByPhotoid.size()} entries"

flickrPredictorByPhotoid = [:];


def doit = {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /*
    // Test for exif presence (>8 exif tags) instead...
    testExif(f,["photoid":it]);
    return;
    */
    
    // this thing should synch on flickrIdMap
    Map attr = fillInfo(it,f)
    synchronized(flickrPredictorByPhotoid) {
        flickrPredictorByPhotoid[attr.photoid]=attr;
        /*
        def persist = new FlickrImage();
        persist.photoid = attr.photoid;
        persist.md5 = attr.md5;
        persist.taken = sdf.parse(attr.taken)
        db.oc.set(persist);
        */
    }
    //println "  ${attr}"; 

    testPhotoDate(f,attr);

}

int nThreads=10;
Spawner spawner = new Spawner(flickrIdList,doit,nThreads);
spawner.run();
println "Done fetching images"



Map fillInfo(photo,Flickr ff) {
    photoid = photo.photoid;
    // each entry maps photoid to attribute map
    def attr = ["photoid":photoid];
    
    def photoInfo = ff.getPhotoInfo(["photo_id":photoid]);
    //printAndParse(photoInfo)
    
    def rsp = new XmlSlurper(false,true).parseText(photoInfo);
    try {
        assert photoid == rsp.photo.'@id'.text();
    } catch (Throwable t) {
        t.printStackTrace();
        printAndParse(photoInfo);
        return null; // map or null ?
    }
    
    // taken
    attr.taken = rsp.photo.dates.'@taken'.text();
    // posted
    attr.posted = rsp.photo.dates.'@posted'.text();
    // lastupdate
    attr.lastupdate = rsp.photo.dates.'@lastupdate'.text();
    /* try to parse these longs
        try {
            attr.lastupdate = new Date(Long.parseLong(attr.lastupdate.toString()))
        } catch (Exception e) {}
        */
    
    // md5
    def md5List = rsp.photo.tags.tag.findAll(){ it.text() =~ /snookr:md5=/};
    assert md5List.size()<=1;
    attr.md5 = (md5List[0].text() =~ /snookr:md5=/).replaceFirst("");
    
    return attr;

}



void printAndParse(String contentAsString) {
    println "------------------------------"
    println contentAsString;
    
    //parse the above string
    boolean validating=false;
    boolean namespaceAware = true;
    def rsp = new XmlSlurper(validating,namespaceAware).parseText(contentAsString);
    println "Response has ${rsp.children().size()} nodes.";
    println "------------------------------"
    println ""
}




println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);


println "-=-=-= Close Database:  =-=-=-"
db.close();
