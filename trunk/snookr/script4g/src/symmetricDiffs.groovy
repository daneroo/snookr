/*
    FileSystem<-->database
    FLickr<-->Database
    compare lists.
*/

import net.snookr.db.Database;
import net.snookr.db.FSImageDAO;
import net.snookr.db.FlickrImageDAO;
import net.snookr.flickr.Photos;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;

println "Hello Symmetric Diffs"
def baseDir = new File('/home/daniel/media').getCanonicalFile();
//def baseDir = new File('C:\\Users\\daniel\\Pictures').getCanonicalFile();
//def baseDir = new File('/home/daniel/media/Europe2002/5-Mirabel');

Database db = new Database();

def fs2db = new Filesystem2Database();
fs2db.setBaseDir(baseDir);
fs2db.setDatabase(db);
fs2db.run();

def flickr2db = new Flickr2Database();
flickr2db.setDatabase(db);
flickr2db.run();

/*
  Diffs algorithm
   find all FSImages in db that are
     -still present on disk
     -not present in FlickrImage table.
*/

FSImageDAO fsImageDAO = new FSImageDAO();
fsImageDAO.setDatabase(db);
FlickrImageDAO flickrImageDAO = new FlickrImageDAO();
flickrImageDAO.setDatabase(db);
Photos photos = new Photos();

Map dbMapByFileName = fsImageDAO.getMapByPrimaryKey();
int total=0;
int totalFound=0;
int totalMissing=0;
dbMapByFileName.each() { fileName,fsima -> //
    //println("fn:${fileName} -> fsima:${fsima}");
    //println "fn:${fileName} looking for md5:${fsima.md5}";
    List found = flickrImageDAO.fetchForMD5(fsima.md5);
    if (found && found.size()>0) {
        totalFound++;
    } else {
        println "fn:${fileName} could not find md5:${fsima.md5}";
        totalMissing++;

        File f = new File(fileName);
        if (f.exists()) {
            int nuPhotoid = photos.uploadPhoto(f);
            println "new photoid: ${nuPhotoid}";
        } else {
            println "Missing file not found in filesystem. cannot upload";
        }

    }
    total++;
}
println "examined ${total} FSImages, found:${totalFound} missing:${totalMissing}";

//println "-=-=-= Database Summary:  =-=-=-"
//db.printSummary(false);
println "-=-=-= Close Database:  =-=-=-"
db.close();
