/*
 * SnookrWS.java
 *
 * Created on February 15, 2008, 10:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.snookr.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author daniel
 */
@WebService()
public class SnookrWS {
    /**
     * Simple echo service
     */
    @WebMethod(operationName = "echo")
    public String echo(
            @WebParam(name = "str") String str) {
        return str;
    }
    
    
    private static EntityManagerFactory emf;
    private static EntityManager em;
    /**
     * Simple echo service
     */
    @WebMethod(operationName = "countForHost")
    public int countForHost(
            @WebParam(name = "host") String host) {
        if (emf==null) {
            //emf = Persistence.createEntityManagerFactory("ObserveFilesPU");
            emf = Persistence.createEntityManagerFactory("SnookrWebAppPU");
        }
        createEntityManager();
        Query q = em.createNamedQuery("NetImage.findByHost");
        q.setParameter("host",host);
        //Integer count = (Integer)q.getSingleResult();
        Object o = q.getSingleResult();
        System.out.println("counting for host val:"+o+" claz:"+o.getClass().getName());
        Integer count = (Integer)o;
        closeEntityManager();
        return count.intValue();
    }
    
    /**
     * Create Or update for NetImage
     */
    @WebMethod(operationName = "createOrUpdate")
    public String createOrUpdate(
            @WebParam(name = "host") String host,
    @WebParam(name = "fileName") String fileName,
    @WebParam(name = "fileSize") Long fileSize,
    @WebParam(name = "lastModified") Date lastModified,
    @WebParam(name = "md5") String md5,
    @WebParam(name = "taken") Date taken ) {
        
        return "host:"+host+" file:"+fileName+" sz:"+fileSize+" md5:"+md5+" mod:"+safeDate(lastModified)+" exif:"+safeDate(taken);
        
    }
    
    //Date utility
    String safeDate(Date d) {
        String defaultValue="????-??-?? ??:??:??";
        final String YYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat SDF = new SimpleDateFormat(YYYMMDDHHMMSS);
        try {
            return SDF.format(d);
        } catch (Exception e) {
            return defaultValue;
        }
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
