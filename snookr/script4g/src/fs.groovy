
import java.util.regex.Pattern
import java.text.SimpleDateFormat

import net.snookr.db.Database;
import net.snookr.util.Environment;
import net.snookr.util.MD5;
import net.snookr.util.Exif;
import net.snookr.model.*;


println "-=-=-= Hello fs =-=-=-"
println " Env: ${Environment.yapFile}"

Database db = new Database();
println "-=-=-= Database Summary:  =-=-=-"
//db.printSummary(false);

//def baseDir = new File('/home/daniel/media').getCanonicalFile();
def baseDir = new File('C:\\Users\\daniel\\Pictures').getCanonicalFile();
//def baseDir = new File('/home/daniel/media/Europe2002/5-Mirabel');

// Classify FileSystem walk the fileSystem and make
// 4 lists: files[image|directory|skipped|other]
Map files = [
    "image":[],
    "skipped":[],
    "directory":[],
    "other":[] 
];   

baseDir.eachFileRecurse { f -> // examine each File
    if (f.isFile()) {
        String fileName = f.getName();
        if ( fileName.endsWith(".JPG") || fileName.endsWith(".jpg") ) {
            files["image"] << f.getCanonicalFile();
        } else {
            files["skipped"] << f.getCanonicalFile();
        }
    } else {
        // if not a directory what ?
        if (f.isDirectory()) {
            //println "Directory: ${f.getCanonicalFile()}"
            files["directory"] << f.getCanonicalFile();
        } else {
            files["other"] << f;
        }
    }
}

println "fs classification"
files.each() { k,v ->
    println "  ${k} : ${v.size()}"
}

areUnique = [:];
files["image"].each() { f -> // examine each image File
    def cp = f.getPath();
    if (areUnique[cp]!=null) {
        println "not unique ${cp} == ${areUnique[cp].getPath()}"
    }
    areUnique[cp]=f;
}
println "areUnique has size: ${areUnique.size()}"

    /* two ways to get a predictor - from map, 
      then from db (although map is created from db)
      after indexing db (which broke the lookup)
       24 seconds to look up by individual querys (1824 images)
       14 seconds when all image are preloaded in map
    */

//Simply use this as  predictor
fsPredictorByFilename = null;
Closure getFSImageForFileFromMap = { f -> // use map
    if (fsPredictorByFilename==null) {
        fsPredictorByFilename = db.getMapForClassByPrimaryKey(FSImage.class,"fileName");
        println "getMapForClassByField has ${fsPredictorByFilename.size()} entries"
    }
    return fsPredictorByFilename[f.getCanonicalPath()];
}

Closure getFSImageForFileEach = { f -> // call db each time
    return db.getForPrimaryKey(FSImage.class,"fileName",f.getCanonicalPath());
}

//Closure getFSImageForFile = getFSImageForFileFromMap;
Closure getFSImageForFile = getFSImageForFileEach;

static int md5Never = 0;
static int md5AsNeeded = 1; // if not already calculated
static int md5Always = 2;
int md5Behaviour = md5AsNeeded;
Map returnCodes = [:];
files["image"].each() { f -> // examine each image File
    boolean isNew = false;
    boolean isModified = false;

    // get the predictor (persistent version) if it exists.
    def persist = getFSImageForFile(f);

    if (persist==null) {
        persist = new FSImage();
        persist.fileName = f.getCanonicalPath();
        isNew = isModified = true;
    }

    // actual attributes.
    Long size = new Long(f.length());
    if (size != persist.size) {
        persist.size=size;
        isModified=true;
    }

    Date lastModified = new Date(f.lastModified());
    if (lastModified != persist.lastModified) {
        persist.lastModified = lastModified
        isModified = true;
    }

    // TODO behaviour thing like md5: always/never/asNeeded
    if (persist.taken==null) { 
        Date taken = Exif.getExifDate(f);
        if (taken != persist.taken) {
            persist.taken = taken
            isModified = true;
        }
    }

    if (  (md5Behaviour!=md5Never) && 
              (persist.md5 == null || md5Behaviour == md5Always) ) {
        md5 = MD5.digest(f);
        println "calculated md5 ${md5} ${f.getName()}"
        if (md5 != persist.md5) {
            persist.md5 = md5;
            isModified=true;
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


returnCodes.each() { k,v ->
        println "fs<-->bd  ${k} : ${v}"
}

println "-=-=-= Database Summary:  =-=-=-"
//db.printSummary(false);


println "-=-=-= Close Database:  =-=-=-"
db.close();






