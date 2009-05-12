/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import net.scalr.model.CloudMap;

/**
 *
 * @author daniel
 */
public class CloudMapDAO {

    public CloudMap get(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudMap clm = pm.getObjectById(CloudMap.class, name);
            //System.out.println(" DAO Fetched: "+clm.getName()+" --> "+clm.getContent());
            return clm;
        } finally {
            pm.close();
        }
    }

    public void delete(String name) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudMap clm = pm.getObjectById(CloudMap.class, name);
            if (clm != null) {
                pm.deletePersistent(clm);
            }
        } finally {
            pm.close();
        }
    }

    public void createOrUpdate(CloudMap clm) {
        //CloudMap persisted = get(clm.getName());
        // this is the default behaviour : overwite!
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(clm);
        } finally {
            pm.close();
        }
    }

    public List<CloudMap> getAll() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(CloudMap.class);
            //Query query = pm.newQuery("select from CloudMap");
            List<CloudMap> results = (List<CloudMap>) query.execute();
            return results;
        } finally {
            pm.close();
        }

    }

    public void print(List<CloudMap> list) {
        System.out.println("List has " + list.size() + " CloudMap entries");
        for (CloudMap clm : list) {
            System.out.println(" " + clm.getName() + " --> " + new String(clm.getContent()));
        }

    }
}
