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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import net.snookr.filesystem.Filesystem;
import net.snookr.model.FSImage;
import net.snookr.model.Thing;

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
        // TODO code application logic here
        //String baseDirPath = "/home/daniel/media";
        String baseDirPath = "C:\\Users\\daniel\\Pictures";
        //String baseDirPath = "/home/daniel/media/Europe2002/5-Mirabel";
        
        emf = Persistence.createEntityManagerFactory("ObserveFilesPU");
        traverseFromBaseDir(baseDirPath);
        //testPU();
    }
    
    private static void traverseFromBaseDir(final String baseDirPath) {
        try {
            File baseDir = new File(baseDirPath).getCanonicalFile();
            
            System.out.println("Hello FileSystem");
            Filesystem fs = new Filesystem();
            fs.setBaseDir(baseDir);
            
            List fsImageList = fs.getFSImageList();
            for (Object o : fsImageList) {
                FSImage fsima = (FSImage)o;
                createOrUpdate(fsima);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    static Thing findThing(String fileName) {
        Query queryFindThingByFileName = em.createNamedQuery("findThingByFileName");
        queryFindThingByFileName.setParameter("fileName", fileName);
        try {
            return (Thing)queryFindThingByFileName.getSingleResult();
        } catch (EntityNotFoundException enfe) {
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    public static void createOrUpdate(FSImage fsima) {
        System.out.println("new fsima: "+fsima);
        createTransactionalEntityManager();
        Thing thing = new Thing(fsima);
        Thing priorThing = findThing(thing.getFileName());
        if (priorThing!=null) {
            System.out.println("prior: "+priorThing);
        } else {
            em.persist(thing);
        }
        closeTransactionalEntityManager();
    }
    
    private static void testPU() {
        
        // Persist all entities
        createTransactionalEntityManager();
        // Create new customer
        Thing thing = new Thing();
        //customer0.setId(1);
        thing.setFileName("file001");
        // Persist the customer
        em.persist(thing);
        
        
        closeTransactionalEntityManager();
        
        // Test query and navigation
        createEntityManager();
        //Query q = em.createQuery("select c from Customer c where c.name = :name");
        //q.setParameter("name", name);
        //return (Customer)q.getSingleResult();
        
        Query q = em.createQuery("select t from Thing t");
        List<Thing> listOfThings  = q.getResultList();
        for (Thing t : listOfThings) {
            System.out.println(""+t);
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
