/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import net.scalr.MD5;
import net.scalr.model.CloudZip;
import net.scalr.model.CloudZipEntry;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author daniel
 */
public class CloudZipDAO {

    public CloudZip get(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudZip cz = internalGet(pm, name);
            // Touch entries, so we have access.
            if (cz != null) {
                cz.getEntries();
            }
            return cz;
        } finally {
            pm.close();
        }
    }

    public void delete(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            CloudZip cz = internalGet(pm, name);
            if (cz != null) {
                System.out.println("Deleting CloudZip");
                pm.deletePersistent(cz);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void create(CloudZip cz) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            System.out.println("Creating new CloudZip");
            pm.makePersistent(cz);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void createOrReplace(CloudZip cz) {
        delete(cz.getName());
        create(cz);
    }

    /* Take a look at:
     * http://www.datanucleus.org/products/accessplatform_1_1/jdo/query.html
     * :: query.setResult("count(param1), max(param2), param3"); --> Obect[]
     *  Hmm Could not get that to work
     */
    public List<String> getAllKeys() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(CloudZip.class);
            //Query query = pm.newQuery("select from CloudMap");
            List<CloudZip> results = (List<CloudZip>) query.execute();
            List<String> keys = new ArrayList<String>(results.size());
            for (CloudZip cz : results) {
                keys.add(cz.getName());
            }
            return keys;
        } finally {
            pm.close();
        }
    }

    public void subsample(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        //Transaction tx = pm.currentTransaction();
        try {
            //tx.begin();
            CloudZip cz = internalGet(pm, name);
            if (cz != null) {
                System.out.println("Subsampling CloudZip: " + cz.getName());
                //List<CloudZipEntry> nuList = new ArrayList<CloudZipEntry>();
                for (ListIterator<CloudZipEntry> it = cz.getEntries().listIterator(); it.hasNext();) {
                    int index = it.nextIndex();
                    boolean delete = (index % 2) == 0;
                    //boolean delete = index <2;
                    CloudZipEntry ze = it.next();
                    if (delete) {
                        System.out.println("Deleting ZipEntry with index:" + index + " name:" + ze.getName());
                        it.remove();
                    } else {
                        //nuList.add(ze);
                    }
                }
            //cz.setEntries(nuList);
            }
        //tx.commit();
        } finally {
            //if (tx.isActive()) {
            //    tx.rollback();
            //}
            pm.close();
        }

    }

    public void updateWithStream(String name, InputStream is) {
        ZipInputStream zipis = new ZipInputStream(is);
        while (true) {
            try {
                ZipEntry ze = zipis.getNextEntry();
                if (ze == null) {
                    break;
                }
                String ename = ze.getName();
                if (ze.isDirectory()) {
                    System.err.println("Ignoring directory: " + ename);
                    continue;
                }
                byte[] extra = ze.getExtra();
                if (extra != null) {
                    System.out.println("Extra: " + new String(extra) + " " + ename);
                }

                byte[] content = IOUtils.toByteArray(zipis);
                String md5sum = MD5.digest(content);
                System.out.println("upd: " + ze.getName() + " length: " + content.length + " md5: " + md5sum);

            //map.put(ename, content);
            } catch (IOException ex) {
                Logger.getLogger(CloudZipDAO.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }

    // just to catch the runtime exception:
    // avax.jdo.JDOObjectNotFoundException: Could not retrieve entity of kind CloudZip with key CloudZip(....
    private CloudZip internalGet(PersistenceManager pm, String name) {
        CloudZip cz = null;
        try {
            cz = pm.getObjectById(CloudZip.class, name);
        } catch (javax.jdo.JDOObjectNotFoundException jdonfe) {
            // we are meant to discard this exception
        } catch (Exception e) {
            Logger.getLogger(CloudZipDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return cz;
    }
}
