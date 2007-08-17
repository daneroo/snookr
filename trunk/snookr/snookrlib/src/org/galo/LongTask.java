package org.galo;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import org.galo.digest.MD5;
import org.galo.util.Timer;
import org.galo.filesystem.DirectoryWalker;
import org.galo.filesystem.FileWalker;
import org.galo.filesystem.BaseWalker;
import org.galo.filesystem.IFileHandler;
import org.galo.filesystem.ListingFileHandler;
import org.galo.model.Image;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifDirectory;


/** Uses a SwingWorker to perform a time-consuming (and utterly fake) task. */

/* 
 * LongTask.java is used by:
 *   ProgressBarDemo.java
 *   ProgressBarDemo2.java
 *   ProgressMonitorDemo
 */
public class LongTask {

    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;

    public LongTask() {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
        lengthOfTask = 1000;
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new ActualInnerTask();
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        //return (int)(Math.random() * 1000);
        return current;
    }

    public void stop() {
        canceled = true;
        statMessage = null;
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return statMessage;
    }
    public void setMessage(String msg) {
        statMessage=msg;
        log(msg);
    }


    void log(String msg) {
        System.err.println(msg);
    }
    
    class ActualInnerTask {
        File baseDir=null;
        List dirs = null;
        List files = null;
        ActualInnerTask() {
            baseDir = new File(JnlpPersist.baseDir());
            run();
        }

        public void run() {
            Timer outer = new Timer();
            try {

                Timer tt = new Timer();
                getFilesAndDirs();
                setMessage("Found  "+files.size()+" files, "+dirs.size()+" dirs  in "+tt.diff()+"s ("+tt.rate(files.size()+dirs.size())+" [file|dir]/s) for basedir: "+baseDir );  
                
                applyCommand(new MapperCmd());
                applyCommand(new ExiferCmd());
                applyCommand(new DigesterCmd());
                
            } catch (Exception e) {
                e.printStackTrace();
                log(e.getMessage());
            } finally {
                setMessage( "Total time:"+outer.diff()+"s for basedir: "+baseDir );
                done=true;
                statMessage=null;
            }
        }
        
        public void getFilesAndDirs() {
            dirs = new java.util.Vector();
            files = new java.util.Vector();
            new BaseWalker(new ListingFileHandler(dirs),new ListingFileHandler(files)).execute(baseDir);
            //while (files.size()>1000) files.remove(files.size()-1);
        }
        
        // Command Patterns :
        public void applyCommand(ImageCommand ic) {
            //setMessage( ic.getName()+" Starting");
            Timer tt = new Timer();
            
            int size=files.size();
            int count=0;
            
            Iterator iter = files.iterator();
            while ( iter.hasNext()) {
                count++;
                File f = (File)iter.next();

                //Image map may not yet exist.
                Image image = null;
                try { image = (Image)imageForFile.get(f); } catch (Exception e) {}

                ic.exec(image,f);
                //log(ic.getName()+" - "+f);

                current = count*1000/size;
            }        
            setMessage( ic.getName()+" "+files.size()+" files in "+tt.diff()+"s ("+tt.rate(files.size())+" files/s)");
        }

        Map imageForFile = null;
        class MapperCmd implements ImageCommand {
            public String getName() { return "Mapper"; }
            public void exec(Image unused,File f) {
                if (imageForFile==null) {
                    imageForFile = new TreeMap();
                }
                
                Image image = new Image();
                // file name idea needs to be refined, (encoding, directory, host,...)
                image.setFileName(f.toURI().toString());
                // setStamp after we have exif data (or not)
                image.setFileSize(f.length());
                
                image.setLastModified(new Date(f.lastModified()));
                // set stamp from lastModified : will be overwritten by exif if available
                image.setStamp(image.getLastModified());
                
                //String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(f.lastModified()));

                imageForFile.put(f, image);
            }
        }

    
        class ExiferCmd implements ImageCommand {
            public String getName() { return "Exifer"; }
            public void exec(Image image,File f) {
                PrintStream realout = System.out;
                System.setOut(new PrintStream(org.galo.util.Stream.nullOutputStream()));

                try {
                    // since the exif data may have been set by predictor, test for null, ans skip if set
                    if (image.getExifDate()!=null) {
                        return;
                    }

                    // 1-
                    Metadata metadata = JpegMetadataReader.readMetadata(f);
                    
                    // for debugging, and finding other tags.
                    //System.out.println(" exif for :"+f);
                    //printAllExifTags(metadata);
                    
                    Directory directory = metadata.getDirectory(ExifDirectory.class);
                    //public static final int 	TAG_DATETIME 	306
                    //public static final int 	TAG_EXIF_IMAGE_HEIGHT 	40963
                    //public static final int 	TAG_EXIF_IMAGE_WIDTH 	40962
                    
                    image.setExifDate( directory.getDate(ExifDirectory.TAG_DATETIME) );
                    image.setStamp(image.getExifDate());
                    
                    image.setWidth( directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH) );
                    image.setHeight( directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT) );

                    log("exif for:"+f+" d:"+image.getExifDate()+" w:"+image.getWidth()+" h:"+image.getHeight());

                } catch (com.drew.metadata.MetadataException me) {
                    //log(me.getClass().getName()+" "+me.getMessage()+" f: "+f);
                } catch (JpegProcessingException jpe) {
                    //log(jpe.getClass().getName()+" "+jpe.getMessage()+" f: "+f);
                } catch (Exception e) {
                    //log(e.getClass().getName()+" "+e.getMessage()+" f: "+f);
                } finally {
                    System.setOut(realout);
                }

            }
        }
        class DigesterCmd implements ImageCommand {
            public String getName() { return "Digester"; }
            public void exec(Image image,File f) {
                try {
                    // since the digest may have been set by predictor, test for null, ans skip if set
                    if (image.getMd5()!=null) return;

                    log("getting new MD5 for "+f);
                    image.setMd5(MD5.digest(f));

                    // This is how you force an implentation choice for MD5
                    //image.setMd5(MD5.getImplementation(MD5.NATIVE).digest(new FileInputStream(f)));
                    //image.setMd5(MD5.getImplementation(MD5.OSTERMILLER).digest(new FileInputStream(f)));
                } catch (IOException ioe) {}
            }
        }
    }
    interface ImageCommand {
        public String getName();
        public void exec(Image image,File f);
    }
    
}
