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
import net.snookr.util.DateFormat;
import net.snookr.util.Exif;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifDirectory;
import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 *
 * @author daniel
 */
class ImageClassification {
    String cameraOfInterest = "Canon|Canon PowerShot S30";
    //String cameraOfInterest = "Canon|Canon PowerShot S30";
    def verbose=false;
    public void run() {
        println "Hello Image Classification";
        Map dbMapByFileName = getMapByFileName();
        println "Hello Image Classification";
        showHisto(dbMapByFileName);
        showListForCamera(dbMapByFileName,cameraOfInterest);
    }

    private void showListForCamera(Map dbMapByFileName,String camera){
        List list = [];
        dbMapByFileName.each() { fileName,fsima -> //
            if (camera==fsima.camera) {
                list << fsima;
            }
        }
        def patterns = [ // list of Pattern's'
            ~/\d+-(\d+)_IMG.JPG/,
            ~/IMG_(\d+).JPG/,
            ~/IMG_(\d+).orig.JPG/,
            ~/ST[ABCD]_(\d)+.JPG/,
        ];

        list.sort(){ fsima -> //
            fsima.taken.getTime();
        }
        list.each(){ fsima -> //
            //println(DateFormat.format(fsima.taken));
        };

        int prevIndex=-1;
        list.each(){ fsima -> //
            //println(fsima);
            File f = new File(fsima.fileName);
            String baseName = f.getName();
            Pattern matchedPattern = null;
            int parsedInt = -1;
            patterns.each(){ pattern -> //
                Matcher matcher = pattern.matcher(baseName);
                if (matcher.matches()){
                    matchedPattern = pattern;
                    parsedInt = Integer.parseInt(matcher[0][1]);
                }
            }
            int delta = parsedInt-prevIndex;
            if (prevIndex==-1 || delta<0 || delta>5) {
                println("  HEAD : ${baseName} : ${String.format("%6d",parsedInt)} : ${DateFormat.format(fsima.taken)} : ${matchedPattern}");
                //Exif.showAllTags(f);
                Exif.identifyCamera(f);
            }

            if (null==matchedPattern){
                println("OTHER: ${baseName}");
            } else {
                //println("${baseName} : ${String.format("%6d",parsedInt)} : ${DateFormat.format(fsima.taken)} : ${matchedPattern}");
            }
            prevIndex=parsedInt;
        }
        println("Photos for camera: ${camera} : ${list.size()}");

    }
    private Map getMapByFileName(){
        Database db = new Database();
        FSImageDAO fsImageDAO = new FSImageDAO();
        fsImageDAO.setDatabase(db);
        Map dbMapByFileName = fsImageDAO.getMapByPrimaryKey();
        db.close();
        return dbMapByFileName;
    }
    private void showHisto(Map dbMapByFileName){
        // part 1 - extract camera info
        Map cameraMap = [:];
        dbMapByFileName.each() { fileName,fsima -> //
            File f = new File(fileName);
            def camera = fsima.camera;
            if (cameraMap[camera]!=null) {
                if (verbose) {
                    println("${camera}: ${fsima.md5}");
                }
                cameraMap[camera] = cameraMap[camera]+1;
            } else {
                if (verbose) {
                    println("first ${camera}: ${fsima.md5}");
                }
                cameraMap[camera] = 1;
            }
        }
        cameraMap.each() { camera,count -> //
            println("${String.format("%5d",count)} : ${camera} ");
        }

    }
}
	