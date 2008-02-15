/*
 * Main.java
 *
 * Created on February 12, 2008, 1:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package observefiles;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.snookr.filesystem.Filesystem;
import net.snookr.model.FSImage;
import net.snookr.model.Thing;
import net.snookr.util.Exif;
import net.snookr.util.MD5;
import net.snookr.util.DateFormat;


/**
 *
 * @author daniel
 */
public class Main2 {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    /** Creates a new instance of Main */
    public Main2() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("  Host: "+ HostInfo.getHostName());
        
        //String baseDirPath = "/home/daniel/media";
        String baseDirPath = "C:\\Users\\daniel\\Pictures";
        //String baseDirPath = "/home/daniel/media/Europe2002/5-Mirabel";
        if (args.length>0 && args[0]!=null) {
            baseDirPath = args[0];
        }
        
        emf = Persistence.createEntityManagerFactory("ObserveFilesPU");
        traverseFromBaseDir(baseDirPath);
        validateStillExists();
    }
    
    private static void traverseFromBaseDir(final String baseDirPath) {
        try {
            File baseDir = new File(baseDirPath).getCanonicalFile();
            
            System.out.println("Hello FileSystem");
            Filesystem fs = new Filesystem();
            fs.setBaseDir(baseDir);
            
            List fsImageList = fs.getFSImageList();
            
            //createTransactionalEntityManager();
            for (Object o : fsImageList) {
                FSImage fsima = (FSImage)o;
                createOrUpdate(fsima);
            }
            //closeTransactionalEntityManager();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    static Thing findThing(String fileName) {
        return findThingByEach(fileName);
        //return findThingByMap(fileName);
    }
    static Map<String,Thing> thingPredictorByFilename=null;
    static Thing findThingByMap(String fileName) {
        if (thingPredictorByFilename==null){
            thingPredictorByFilename = new HashMap<String,Thing>();
            Query queryFindAllThings = em.createNamedQuery("findAllThings");
            List<Thing> listOfThings = queryFindAllThings.getResultList();
            for (Thing t : listOfThings) {
                thingPredictorByFilename.put(t.getFileName(),t);
            }
            System.out.println("Found "+listOfThings.size()+" things");
        }
        return thingPredictorByFilename.get(fileName);
    }
    static Thing findThingByEach(String fileName) {
        Query queryFindThingByFileName = em.createNamedQuery("findThingByFileName");
        queryFindThingByFileName.setParameter("fileName", fileName);
        try {
            return (Thing)queryFindThingByFileName.getSingleResult();
        } catch (EntityNotFoundException enfe) {
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    static final int md5Never = 0;
    static final int md5AsNeeded = 1; // if not already calculated
    static final int md5Always = 2;
    static final int md5Behaviour = md5AsNeeded; // include setter for behaviour
    
    public static void createOrUpdate(FSImage fsima) {
        createTransactionalEntityManager();
        
        Thing thing = new Thing(fsima);
        Thing priorThing = findThing(thing.getFileName());
        createOrUpdateInternal(thing,priorThing);
        
        if (priorThing!=null) {
            //System.out.println("prior: "+priorThing);
        } else {
            System.out.println("new fsima: "+fsima);
        }
        
        closeTransactionalEntityManager();
    }
    
    private static String createOrUpdateInternal(Thing newThing,Thing predictorFromDB) {
        // implement parse (attr) and persist photo info from flickr
        boolean isNew = false;
        boolean isModified = false;
        
        String fileName = newThing.getFileName();
        File f = new File(fileName);
        
        Thing persist = predictorFromDB;
        
        if (persist==null) {
            persist = newThing;
            isNew = isModified = true;
        } else {
            // attributes assumed to be set in newThing!
            if (!newThing.getFileSize().equals( persist.getFileSize() ) ) {
                persist.setFileSize(newThing.getFileSize());
                log("mod for filesize ");
                isModified=true;
            }
            if (! newThing.getLastModified().equals(  persist.getLastModified() ) ) {
                // rouded milliseconds is ok: (Vista has mod times to the milli)
                if (newThing.getLastModified().getTime()/1000l != persist.getLastModified().getTime()/1000l) {
                    log("mod for lastmod: "+DateFormat.format(persist.getLastModified(),"????-??-?? ??:??:??")+" -> "+DateFormat.format(newThing.getLastModified(),"????-??-?? ??:??:??"));
                    log("mod for lastmod ms: "+persist.getLastModified().getTime()+" -> "+newThing.getLastModified().getTime());
                    persist.setLastModified( newThing.getLastModified() );
                    isModified = true;
                } else {
                    //log("close enough: "+persist.getLastModified().getTime()/1000l+" -> "+newThing.getLastModified().getTime()/1000l);
                }
            }
        }
        
        // attributes not assumed to be set in newThing (because of cost...)
        // TODO behaviour thing like md5: always/never/asNeeded
        if (persist.getTaken()==null) {
            Date taken = newThing.getTaken();
            if (taken==null) {
                taken = Exif.getExifDate(f);
                log( "extracted exif date "+DateFormat.format(taken,"????-??-?? ??:??:??")+" "+f.getName() );
            }
            if (taken != persist.getTaken()) { // only tests both null !
                persist.setTaken( taken );
                log("mod for taken");
                isModified = true;
            }
        }
        
        // TODO; OR IF isModified ????? redo md5sum,
        if (  (md5Behaviour!=md5Never) &&
                (persist.getMd5() == null || md5Behaviour == md5Always) ) {
            String md5 = newThing.getMd5();
            if (md5==null || "".equals(md5) ) {
                try {
                    md5 = MD5.digest(f);
                } catch (IOException ioe) {
                    log( "could not calculate md5 for "+f.getName() );
                }
                log( "calculated md5 "+md5+" "+f.getName() );
            }
            if (md5 != persist.getMd5()) {
                persist.setMd5(md5);
                isModified=true;
            }
        }
        
        // ! syntax highlitee hates nested conditional expressions
        String returnCode = (isModified)? "Update":"Unmodified";
        if (isNew) returnCode="New";
        
        if (isModified) {
            em.persist(persist);
            log("saved ("+returnCode+") "+persist );
        }
        return returnCode;
    }
    
    private static void log(String s) {
        System.out.println(s);
    }
    
    private static void validateStillExists() {
        createEntityManager();
        Query queryFindAllThings = em.createNamedQuery("findAllThings");
        List<Thing> listOfThings = queryFindAllThings.getResultList();
        for (Thing t : listOfThings) {
            File f = new File(t.getFileName());
            if (!f.exists()) {
                System.out.println("Could not find "+f);
            }
            
        }
        closeEntityManager();
    }
    
    private static void createTransactionalEntityManager() {
        
        // Create a new EntityManager
        em = emf.createEntityManager();
        
        // Begin transaction
        em.getTransaction().begin();
    }
    
    private static void closeTransactionalEntityManager() {
        
        // Commit the transaction
        em.getTransaction().commit();
        
        // Close this EntityManager
        em.close();
    }
    
    private static void createEntityManager() {
        
        // Create a new EntityManager
        em = emf.createEntityManager();
    }
    
    private static void closeEntityManager() {
        
        // Close this EntityManager
        em.close();
    }
    
    
}
