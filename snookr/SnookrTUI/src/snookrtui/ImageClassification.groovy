/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package snookrtui

import net.snookr.db.Database;
import net.snookr.db.FSImageDAO;
import net.snookr.db.FlickrImageDAO;
import net.snookr.flickr.Photos;
import net.snookr.synch.Filesystem2Database;
import net.snookr.synch.Flickr2Database;

import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
//import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifDirectory;

/**
 *
 * @author daniel
 */
class ImageClassification {
    public void run() {
        def verbose=false;
        println "Hello Image Classification"

        Database db = new Database();


        FSImageDAO fsImageDAO = new FSImageDAO();
        fsImageDAO.setDatabase(db);

        Map dbMapByFileName = fsImageDAO.getMapByPrimaryKey();

        // part 1 - extract camera info
        def countem=0;
        Map makeMap = [:];
        dbMapByFileName.each() { fileName,fsima -> //
            //println(fsima)
            File f = new File(fileName);
            def makeNModel = getCamera(f);
            if (makeMap[makeNModel]!=null) {
                if (verbose) {
                    println("${fsima.md5}: ${makeNModel}");
                }
                makeMap[makeNModel] = makeMap[makeNModel]+1;
            } else {
                println("${fsima.md5}: ${makeNModel}");
                makeMap[makeNModel] = 1;
            }
            if (countem++>100000) {
                throw new RuntimeException("Done")
            }
        }
        makeMap.each() { makeNModel,count -> //
            println("${String.format("%5d",count)} : ${makeNModel} ");
        }

        println "-=-=-= Close Database:  =-=-=-"
        db.close();
    }

    public String getCamera(f) {
        try {
            // equivalent function for InputStream
            Metadata metadata = JpegMetadataReader.readMetadata(f);
            Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
            String cameraMake = exifDirectory.getString(ExifDirectory.TAG_MAKE);
            String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MODEL);
            if (cameraMake==null && cameraModel==null) {
                println("${f} has null make and model");
            }
            return "${cameraMake} | ${cameraModel}";
        } catch (com.drew.metadata.MetadataException me) {
            System.err.println(me.getClass().getName()+" "+me.getMessage()+" f: "+f);
        } catch (JpegProcessingException jpe) {
            System.err.println(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
        }
        return "UNKNOWN";
    }

}
	

