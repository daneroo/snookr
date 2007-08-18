package org.galo;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import org.galo.digest.MD5;

import org.galo.util.Timer;
import org.galo.util.HostInfo;

import org.galo.filesystem.DirectoryWalker;
import org.galo.filesystem.FileWalker;
import org.galo.filesystem.BaseWalker;
import org.galo.filesystem.IFileHandler;
import org.galo.filesystem.ListingFileHandler;

import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
//import com.drew.metadata.Directory;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifDirectory;

import org.galo.model.Host;
import org.galo.model.Directory;
import org.galo.model.MediaFile;
import org.galo.model.IMediaContent;
import org.galo.model.Image;


/** Uses a SwingWorker to perform a time-consuming (and utterly fake) task. */

/*
 * LongTask.java is used by:
 *   ProgressBarDemo.java
 *   ProgressBarDemo2.java
 *   ProgressMonitorDemo
 */
public class DiscoverFileSystem {
    
    static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void main(String[] args) {
        File baseDir = new File(JnlpPersist.baseDir());
        //new DiscoverFileSystem().myComparer(baseDir);
        new DiscoverFileSystem().test(baseDir);
    }
    
    void log(String msg) {
        System.err.println(msg);
    }
    void setMessage(String msg) {
        log(msg);
    }
    
    
    class UniqueingContext { // pattern for findOrCreate...
        // these express the natural key unqieness constraint idea.
        Map ctxmap = new HashMap();
        Object unique(Object key,Object o) {
            Object exists = ctxmap.get(key);
            if (exists!=null) return exists;
            ctxmap.put(key,o);
            return o;
        }
        Host getHost(Host host) {
            Object key = "host:"+host.getMACAddress(); // notsure for this one..
            return (Host)unique(key,host);
        }
        Directory getDirectory(Directory dir) {
            Object key = "directory:"+dir.getFileName();
            return (Directory)unique(key,dir);
        }
        MediaFile getMediaFile(MediaFile mf) {
            Object key = "mediafile:"+mf.getFileName();
            return (MediaFile)unique(key,mf);
        }
        /*
        Image getImage(Image ima) { // would love to put md5, and or exifDate,...
            // but not available...
            Object key = "image:"+ima.getFileName();
            return (Directory)unique(key,ima);
        }
         */
    }
    
    public void myComparer(File baseDir) {
        ListingFileHandler ref = new ListingFileHandler();
        IFileHandler fh[]  = new IFileHandler[] {
            new IFileHandler() {
                public void handle(File f){}
            },
            new IFileHandler() {
                public void handle(File f){
                    Date lm = new Date(f.lastModified());
                }
            },
            new IFileHandler() {
                public void handle(File f){
                    Date lm = new Date(f.lastModified());
                    long sz = f.length();
                }
            },
            new IFileHandler() {
                public void handle(File f){
                    MediaFile mf = new MediaFile();
                    mf.setFileName(f.toURI().getPath());
                    new ExiferCmd().exec(mf);
                }
            },
            new IFileHandler() {
                public void handle(File f){
                    Date lm = new Date(f.lastModified());
                    long sz = f.length();
                    MediaFile mf = new MediaFile();
                    mf.setFileName(f.toURI().getPath());
                    new ExiferCmd().exec(mf);
                }
            },
        };
        for (int i=0;i<10;i++) {
            Timer tt = new Timer();
            new BaseWalker(null,ref).execute(baseDir);
            int sz = ref.getList().size(); ref.getList().clear();
            log("ref : "+tt.diff()+"s ("+tt.rate(sz)+" file/s) for basedir: "+baseDir );
            for (int h=0;h< fh.length;h++) {
                tt.restart();
                new BaseWalker(null,fh[h]).execute(baseDir);
                log("fh["+h+"] : "+tt.diff()+"s ("+tt.rate(sz)+" file/s) for basedir: "+baseDir );
            }
            log("");
        }
        
    }
    public void test(File baseDir) {
        
        Timer outer = new Timer();
        try {
            
            Timer tt = new Timer();
            getFilesAndDirs(getHost(),baseDir);
            //setMessage("Found  "+files.size()+" files, "+dirs.size()+" dirs  in "+tt.diff()+"s ("+tt.rate(files.size()+dirs.size())+" [file|dir]/s) for basedir: "+baseDir );
            setMessage("Found  "+mediaFiles.size()+" files in "+tt.diff()+"s ("+tt.rate(mediaFiles.size())+" file/s) for basedir: "+baseDir );
            
            applyCommand(new PredictorMapperCmd());
            applyCommand(new ExiferCmd());
            applyCommand(new DigesterCmd());
            
            db4oTest();
            
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        } finally {
            setMessage( "Total time:"+outer.diff()+"s for basedir: "+baseDir );
            setMessage( "Done" );
            setMessage( "" );
        }
    }
    
    public Host getHost() {
        Host host = new Host();
        host.setOSName(HostInfo.getOSName());
        host.setIPAddress(HostInfo.getIPAddress());
        host.setHostName(HostInfo.getHostName());
        host.setMACAddress(HostInfo.getMACAddress());
        setMessage("Host  "+host);
        return host;
    }
    
    List mediaFiles = null;
    public void getFilesAndDirs(Host host,File baseDir) {
        List realFiles = new java.util.Vector();
        mediaFiles = new java.util.Vector();
        
        // maybe this whole thing should be a handler !
        
        new BaseWalker(null,new ListingFileHandler(realFiles)).execute(baseDir);
        
        //while (files.size()>1000) files.remove(files.size()-1);
        
        UniqueingContext ctx = new UniqueingContext();
        host = ctx.getHost(host);
        Iterator iter = realFiles.iterator();
        while ( iter.hasNext()) {
            // count++; progress bar
            File f = (File)iter.next();
            File d = f.getParentFile();
            
            Directory directory = new Directory();
            directory.setFileName(d.toURI().getPath());
            directory = ctx.getDirectory(directory);
            directory.setLastModified(new Date(d.lastModified()));
            
            Image image = new Image();
            image.setFileSize(f.length());
            
            MediaFile mf = new MediaFile();
            mf.setFileName(f.toURI().getPath());
            //mf.setFileName(""+f);
            mf = ctx.getMediaFile(mf);
            mf.setLastModified(new Date(f.lastModified()));
            mf.setParent(directory);
            mf.setContent(image);
            mediaFiles.add(mf);
        }
    }
    
    // Command Patterns :
    public void applyCommand(MediaFileCommand mfc) {
        //setMessage( ic.getName()+" Starting");
        Timer tt = new Timer();
        
        int size=mediaFiles.size();
        int count=0;
        
        Iterator iter = mediaFiles.iterator();
        while ( iter.hasNext()) {
            count++;
            MediaFile mf = (MediaFile)iter.next();
            
            mfc.exec(mf);
            //log(mfc.getName()+" - "+mf.getFileName());
            
            // Progress bar update
            // current = count*1000/size;
        }
        setMessage( mfc.getName()+" "+mediaFiles.size()+" files in "+tt.diff()+"s ("+tt.rate(mediaFiles.size())+" files/s)");
    }
    
    // might not be what we want as default behaviour....
    public Image castContentToImageOrCreate(MediaFile mf) {
        IMediaContent mc = mf.getContent();
        try {
            if (mf.getContent()==null) mf.setContent(new Image());
            return (Image)mf.getContent();
        } catch (ClassCastException cce) {
            log(cce.getMessage());
            log("MedaFile contains non Image content: "+mf.getContent().getClass().getName());
            log("overriding with new Image ");
            mf.setContent(new Image());
            return (Image)mf.getContent();
            
        }
    }
    
    Map predictorForFile = null; // read by jsonReadTest
    class PredictorMapperCmd implements MediaFileCommand {
        public String getName() { return "Predictor"; }
        public void exec(MediaFile mf) {
            if (predictorForFile==null) {
                log(" Getting JSON Predictor "+new Date());
                //predictorForFile = new JSONTest().readTest();
                Map oldPredictorForFile = new JSONTest().readTest();
                // jut duplicate keys....
                // translate mapping from File -> Image , to URI.getPath -> Image
                predictorForFile = new TreeMap();
                Iterator it = oldPredictorForFile.keySet().iterator();
                while (it.hasNext()){
                    String oldkey = it.next().toString();
                    String newkey = new File(oldkey).toURI().getPath();
                    predictorForFile.put(newkey,oldPredictorForFile.get(oldkey));
                }
                log(" Got     JSON Predictor "+new Date());
            }
            if (predictorForFile==null) predictorForFile = new TreeMap();
            Image predictor = (Image)predictorForFile.get(mf.getFileName());
            
            Image image = castContentToImageOrCreate(mf);
            
            if (predictor!=null) {
                // digester
                image.setMd5(predictor.getMd5());
                // exifer
                Date ed = predictor.getExifDate();
                if (ed!=null) {
                    image.setExifDate( ed  );
                    image.setStamp( ed );
                    image.setWidth( image.getWidth() );
                    image.setHeight( image.getHeight() );
                }
            } else {
                log("no predictor for: "+mf.getFileName());
            }
        }
    }
    
    class ExiferCmd implements MediaFileCommand {
        public String getName() { return "Exifer"; }
        public void exec(MediaFile mf) {
            PrintStream realout = System.out;
            System.setOut(new PrintStream(org.galo.util.Stream.nullOutputStream()));
            
            Image image = castContentToImageOrCreate(mf);
            try {
                // since the exif data may have been set by predictor, test for null, ans skip if set
                if (image.getExifDate()!=null) {
                    return;
                }
                
                // 1-
                File f = new File(mf.getFileName());
                Metadata metadata = JpegMetadataReader.readMetadata(f);
                
                // for debugging, and finding other tags.
                //System.out.println(" exif for :"+f);
                //printAllExifTags(metadata);
                
                com.drew.metadata.Directory directory = metadata.getDirectory(ExifDirectory.class);
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
    class DigesterCmd implements MediaFileCommand {
        public String getName() { return "Digester"; }
        public void exec(MediaFile mf) {
            Image image = castContentToImageOrCreate(mf);
            try {
                // since the digest may have been set by predictor, test for null, ans skip if set
                if (image.getMd5()!=null) return;
                
                log("getting new MD5 for "+mf);
                File f = new File(mf.getFileName());
                image.setMd5(MD5.digest(f));
                
                // This is how you force an implentation choice for MD5
                //image.setMd5(MD5.getImplementation(MD5.NATIVE).digest(new FileInputStream(f)));
                //image.setMd5(MD5.getImplementation(MD5.OSTERMILLER).digest(new FileInputStream(f)));
            } catch (IOException ioe) {}
        }
    }
    
    
    
    public void db4oTest() {
        Timer tt = new Timer();
        new Db4oTest().test(mediaFiles);
        setMessage("db4o: time:"+tt.diff()+"s ("+tt.rate(mediaFiles.size())+" images/s)" );
    }
    
    interface MediaFileCommand {
        public String getName();
        public void exec(MediaFile mf);
    }
    
    
}




