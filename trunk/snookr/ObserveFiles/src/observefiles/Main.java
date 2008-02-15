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
import net.snookr.model.NetImage;
import net.snookr.util.Exif;
import net.snookr.util.MD5;
import net.snookr.util.DateFormat;


/**
 *
 * @author daniel
 */
public class Main {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String host = HostInfo.getHostName();
        System.out.println("  Host: "+ host);
        
        //String baseDirPath = "/home/daniel/media";
        String baseDirPath = "C:\\Users\\daniel\\Pictures";
        //String baseDirPath = "/home/daniel/media/Europe2002/5-Mirabel";
        if (args.length>0 && args[0]!=null) {
            baseDirPath = args[0];
        }
        
        emf = Persistence.createEntityManagerFactory("snookrLocalMySQLPU");
        traverseFromBaseDir(host,baseDirPath);
        validateStillExists(host);
    }
    
    private static void traverseFromBaseDir(final String host,final String baseDirPath) {
        try {
            File baseDir = new File(baseDirPath).getCanonicalFile();
            
            System.out.println("Hello FileSystem");
            Filesystem fs = new Filesystem();
            fs.setBaseDir(baseDir);
            
            List fsImageList = fs.getFSImageList();
            
            //createTransactionalEntityManager();
            for (Object o : fsImageList) {
                FSImage fsima = (FSImage)o;
                createOrUpdate(host,fsima);
            }
            //closeTransactionalEntityManager();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    
    static NetImage findNetImage(String host,String fileName) {
        Query q = em.createNamedQuery("NetImage.findByHostAndFileName");
        q.setParameter("host", host);
        q.setParameter("fileName", fileName);
        try {
            return (NetImage)q.getSingleResult();
        } catch (EntityNotFoundException enfe) {
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    static final int md5Never = 0;
    static final int md5AsNeeded = 1; // if not already calculated
    static final int md5Always = 2;
    static final int md5Behaviour = md5AsNeeded; // include setter for behaviour
    
    public static void createOrUpdate(String host,FSImage fsima) {
        createTransactionalEntityManager();
        
        NetImage nima = new NetImage(host,fsima);
        NetImage priorNima = findNetImage(host,nima.getFileName());
        if (priorNima!=null) {
            //System.out.println("update: "+priorNima);
        } else {
            System.out.println("create: "+nima);
        }
        createOrUpdateInternal(nima,priorNima);
        
        
        closeTransactionalEntityManager();
    }
    
    private static String createOrUpdateInternal(NetImage newNima,NetImage predictorFromDB) {
        // implement parse (attr) and persist photo info from flickr
        boolean isNew = false;
        boolean isModified = false;
        
        String fileName = newNima.getFileName();
        File f = new File(fileName);
        
        NetImage persist = predictorFromDB;
        
        if (persist==null) {
            persist = newNima;
            isNew = isModified = true;
        } else {
            // attributes assumed to be set in newNima!
            if (!newNima.getFileSize().equals( persist.getFileSize() ) ) {
                persist.setFileSize(newNima.getFileSize());
                log("mod for filesize ");
                isModified=true;
            }
            if (! newNima.getLastModified().equals(  persist.getLastModified() ) ) {
                // rouded milliseconds is ok: (Vista has mod times to the milli)
                if (newNima.getLastModified().getTime()/1000l != persist.getLastModified().getTime()/1000l) {
                    log("mod for lastmod: "+DateFormat.format(persist.getLastModified(),"????-??-?? ??:??:??")+" -> "+DateFormat.format(newNima.getLastModified(),"????-??-?? ??:??:??"));
                    log("mod for lastmod ms: "+persist.getLastModified().getTime()+" -> "+newNima.getLastModified().getTime());
                    persist.setLastModified( newNima.getLastModified() );
                    isModified = true;
                } else {
                    //log("close enough: "+persist.getLastModified().getTime()/1000l+" -> "+newNima.getLastModified().getTime()/1000l);
                }
            }
        }
        
        // attributes not assumed to be set in newNima (because of cost...)
        // TODO behaviour thing like md5: always/never/asNeeded
        if (persist.getTaken()==null) {
            Date taken = newNima.getTaken();
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
            String md5 = newNima.getMd5();
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
    
    private static void validateStillExists(String host) {
        createEntityManager();
        Query q = em.createNamedQuery("NetImage.findByHost");
        q.setParameter("host",host);
        List<NetImage> listOfNimas = q.getResultList();
        for (NetImage t : listOfNimas) {
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
