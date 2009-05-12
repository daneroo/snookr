/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import net.scalr.JSON;
import net.scalr.MD5;
import net.scalr.model.CloudZip;
import net.scalr.model.CloudZipEntry;

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
            cz.getEntries();
            return cz;
        } finally {
            pm.close();
        }
    }

    public void delete(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudZip cz = internalGet(pm, name);
            if (cz != null) {
                pm.deletePersistent(cz);
            }
        } finally {
            pm.close();
        }
    }

    public void createOrReplace(CloudZip cz) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudZip persisted = internalGet(pm, cz.getName());
            if (persisted != null) {
                System.out.println("Replacing CloudZip ==> Deleting first");
                pm.deletePersistent(persisted);
            } else {
                System.out.println("Creating new CloudZip");
            }
            pm.makePersistent(cz);
        } finally {
            pm.close();
        }
    }

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

    // just to cath the runtime exception:
    // avax.jdo.JDOObjectNotFoundException: Could not retrieve entity of kind CloudZip with key CloudZip(....
    private CloudZip internalGet(PersistenceManager pm, String name) {
        CloudZip cz = null;
        try {
            cz = pm.getObjectById(CloudZip.class, name);
        } catch (Exception e) {
        }
        return cz;
    }
}
