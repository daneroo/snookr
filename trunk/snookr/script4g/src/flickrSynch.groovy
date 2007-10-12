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

Flickr f = new Flickr();

int getPhotoListThreads=10;
def flickrList  = new Photos().getPhotoList(getPhotoListThreads);
flickrList.each() { photo -> // straight list of photos
    //println "photo: ${photo}"
}

// -=-=-=-= get predictor (from db ?)
Database db = new Database();
// inject into DAO
FlickrImageDAO.setDatabase(db);
FlickrImageDAO flickrImageDAO = new FlickrImageDAO();

println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);

Map returnCodes = [:];

Progress pr = new Progress(flickrList.size(),"ph",5000);
returnCodes = [:]; //reset counts
Map dbPredictorByPhotoid = flickrImageDAO.getMapByPrimaryKey();
println "flickrImageDAO.getMapByPrimaryKey has ${dbPredictorByPhotoid.size()} entries"
flickrList.each() { flima -> // all flickr images
    def returnCode = flickrImageDAO.createOrUpdateInternal(flima,dbPredictorByPhotoid[flima.photoid]);
    def count = returnCodes[returnCode];
    returnCodes[returnCode] = (count==null)?1:(count+1);
    pr.increment();
}
returnCodes.each() { k,v -> // print histogram of return codes
    println "after1 flickr<-->bd  ${k} : ${v}"
}

pr = new Progress(flickrList.size(),"ph",4000);
returnCodes = [:]; //reset counts
// change persiten lookuup method
flickrList.each() { flima -> // all flickr images
    def returnCode = flickrImageDAO.createOrUpdate(flima);
    def count = returnCodes[returnCode];
    returnCodes[returnCode] = (count==null)?1:(count+1);
    pr.increment();
}
returnCodes.each() { k,v -> // print histogram of return codes
    println "after2 flickr<-->bd  ${k} : ${v}"
}

println "-=-=-= Database Summary:  =-=-=-"
db.printSummary(false);


println "-=-=-= Close Database:  =-=-=-"
db.close();

/* This section was meant as test to show that
  the new photoSearch with setExtras("tags,..") return the same information as the previous
  process, which consited in getting the list, and the getting each image...
*/
if (false) {
  def compareSearchToGetInfo = { flimaFromSearch -> // fetch info from flickr as attributes
      def flimaFromGetInfo = new Photos().getFlickrImage(flimaFromSearch.photoid);
      assert flimaFromSearch.photoid==flimaFromGetInfo.photoid;
      assert flimaFromSearch.md5==flimaFromGetInfo.md5;
      assert flimaFromSearch.taken==flimaFromGetInfo.taken;
      assert flimaFromSearch.posted==flimaFromGetInfo.posted;
      assert flimaFromSearch.lastUpdate==flimaFromGetInfo.lastUpdate;
      println "${Thread.currentThread().getName()} - Photo: ${flimaFromSearch.photoid} verified";
  }

  int compareThreads=10;
  Spawner spawner = new Spawner(flickrList,compareSearchToGetInfo,compareThreads);
  spawner.run();
  println "Done comparing search and getInfo images"
}

