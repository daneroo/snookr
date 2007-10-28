/*
    FileSystem<-->database
    FLickr<-->Database
    compare lists.
*/

import net.snookr.db.Database;
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

//println "-=-=-= Database Summary:  =-=-=-"
//db.printSummary(false);
println "-=-=-= Close Database:  =-=-=-"
db.close();
