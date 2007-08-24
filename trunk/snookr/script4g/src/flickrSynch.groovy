import groovy.xml.*
import groovy.util.slurpersupport .*
import java.text.SimpleDateFormat;

import net.snookr.flickr.Flickr;
import net.snookr.flickr.Photos;
import net.snookr.db.Database;
import net.snookr.util.Spawner;
import net.snookr.util.Progress;
import net.snookr.util.MD5;
import net.snookr.model.FlickrImage;

Flickr f = new Flickr();

int getPhotoListThreads=10;
def flickrList  = new Photos().getPhotoList(getPhotoListThreads);
flickrList.each() { photo -> // straight list of photos
    //println "photo: ${photo}"
}

// -=-=-=-= get predictor (from db ?)
Database db = new Database();
println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);

//Simply use this as  predictor
dbPredictorByPhotoid = db.getMapForClassByPrimaryKey(FlickrImage.class,"photoid");
println "getMapForClassByField has ${dbPredictorByPhotoid.size()} entries"
Closure getFlickrImageForPhotoidFromMap = { photoid -> // use map
    return dbPredictorByPhotoid[photoid];
}
Closure getFlickrImageForPhotoidEach = { photoid -> // call db each time
    return db.getForPrimaryKey(FlickrImage.class,"photoid",photoid);
}

Closure getFlickrImageForPhotoid = getFlickrImageForPhotoidFromMap;
//Closure getFlickrImageForPhotoid = getFlickrImageForPhotoidEach;
Map returnCodes = [:];

def createOrUpdate =  { flima ->
        // implement parse (attr) and persist photo info from flickr
    boolean isNew = false;
    boolean isModified = false;

    def photoid = flima.photoid;

    def persist = getFlickrImageForPhotoid(photoid);

    if (persist==null) {
        persist = flima;
        isNew = isModified = true;
    } else {
        if (persist.md5 != flima.md5) {
            persist.md5 = flima.md5;
            isModified = true;
        }
        if (persist.taken != flima.taken) {
            persist.taken = flima.taken;
            isModified = true;
        }
    }

    // ! syntax highlitee hates nested conditional expressions
    def returnCode = (isModified)? "Update":"Unmodified";
    if (isNew) returnCode="New";

    if (isModified) {
        db.save(persist);
        println "saved (${returnCode}) ${persist}";
    }

    def count = returnCodes[returnCode];
    returnCodes[returnCode] = (count==null)?1:(count+1);

}

//Make the list a map !turn
flickrPredictorByPhotoid = [:];
flickrList.each() { flima -> //map all FlickrImages by photoid
    flickrPredictorByPhotoid[flima.photoid]=flima;
}


//System.exit(0);

returnCodes.each() { k,v -> // print histogram of return codes
    println "during flickr<-->bd  ${k} : ${v}"
}

Progress pr = new Progress(flickrPredictorByPhotoid.size(),"ph")
returnCodes = [:]; //reset counts
flickrPredictorByPhotoid.each() { photoid,flima -> // accumulated FlickrImages
    createOrUpdate(flima);
    pr.increment();
}
returnCodes.each() { k,v -> // print histogram of return codes
    println "after1 flickr<-->bd  ${k} : ${v}"
}

pr = new Progress(flickrPredictorByPhotoid.size(),"ph")
returnCodes = [:]; //reset counts
// change persiten lookuup method
getFlickrImageForPhotoid = getFlickrImageForPhotoidEach
flickrPredictorByPhotoid.each() { photoid,flima -> // accumulated FlickrImages
    // is this being called twice ?
    createOrUpdate(flima);
    pr.increment();
}
returnCodes.each() { k,v -> // print histogram of return codes
    println "after2 flickr<-->bd  ${k} : ${v}"
}

println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);


println "-=-=-= Close Database:  =-=-=-"
db.close();

def compareSearchToGetInfo = { flimaFromSearch -> // fetch info from flickr as attributes
    def flimaFromGetInfo = new Photos().getFlickrImage(flimaFromSearch.photoid);
    assert flimaFromSearch.photoid==flimaFromGetInfo.photoid;
    assert flimaFromSearch.md5==flimaFromGetInfo.md5;
    assert flimaFromSearch.taken==flimaFromGetInfo.taken;
    //println "${Thread.currentThread().getName()} - Photo: ${flimaFromSearch.photoid} verified";
}

int compareThreads=10;
Spawner spawner = new Spawner(flickrList,compareSearchToGetInfo,compareThreads);
spawner.run();
println "Done comparing search and getInfo images"

