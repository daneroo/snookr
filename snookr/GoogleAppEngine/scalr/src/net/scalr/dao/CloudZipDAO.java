/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
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

    private static final Logger log =
            Logger.getLogger(CloudZipDAO.class.getName());

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
                log.warning("Deleting CloudZip name:" + name);
                pm.deletePersistent(cz);
            } else {
                log.warning("Unable to Delete CloudZip with name: " + name);
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
            log.warning("Creating new CloudZip name:" + cz.getName());
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

    // As we traverse the incoming zip entries,
    // track which of the preivious entries have been replaced, by removing them from
    //  a tracking set: entryNamesToDeleteAfter.
    // at the end we can remove remaining entries from the original list.
    public String updateWithStream(String name, InputStream is) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            CloudZip cz = internalGet(pm, name);
            if (cz == null) {
                log.info("Creating new CloudZip name:" + name);
                cz = new CloudZip(name);
                pm.makePersistent(cz);
            }
            // the manifest is invalidated as needed by insert/deletes
            //cz.invalidateManifest();

            List<CloudZipEntry> entries = cz.getEntries();
            Set<String> entryNamesDeleteAfter = new HashSet<String>();
            if (entries != null) {
                log.info("Examining entries before; size:" + entries.size());
                for (CloudZipEntry cze : entries) {
                    log.info("  entry: " + cze.getKeyDescription());
                    entryNamesDeleteAfter.add(cze.getName());
                }
            } else {
                log.info("Entries before null List:");
            }
            ZipInputStream zipis = new ZipInputStream(is);
            while (true) {
                try {
                    ZipEntry ze = zipis.getNextEntry();
                    if (ze == null) {
                        break;
                    }
                    String ename = ze.getName();
                    if (ze.isDirectory()) {
                        log.info("Ignoring directory: " + ename);
                        continue;
                    }

                    boolean preserve = false;
                    boolean delete = false;
                    byte[] extra = ze.getExtra();
                    if (extra != null) {
                        log.info("Extra: " + new String(extra) + " " + ename);
                        if (new String(extra).startsWith("PRESERVE")) {
                            preserve = true;
                        } else if (new String(extra).startsWith("DELETE")) {
                            delete = true;
                        }
                    }

                    byte[] content = IOUtils.toByteArray(zipis);
                    String md5sum = MD5.digest(content);
                    log.info("zipentry: " + ze.getName() + " length: " + content.length + " md5: " + md5sum);

                    if (delete) {
                        cz.deleteAllEntriesWithName(ename);
                        entryNamesDeleteAfter.remove(ename);
                    } else if (preserve) {
                        entryNamesDeleteAfter.remove(ename);
                    } else { // normal add/replace mode
                        entryNamesDeleteAfter.remove(ename);
                        cz.addEntry(new CloudZipEntry(ename, content));
                    }
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                    break;
                }

            }
            // now delete any unnaccounted for entries
            log.info("Cleaning up remaining " + entryNamesDeleteAfter.size() + " entries");
            for (String ename : entryNamesDeleteAfter) {
                if (ename == null) {
                    log.severe("CloudZip entry name is null");
                } else {
                log.warning("Cleaning up remaining entry with name:" + ename);
                    cz.deleteAllEntriesWithName(ename);
                }
            }
            // to avoid second fetch below - recalculating manifest with fetch implies ordering entries
            if (cz.isManifestValid()){
                return cz.getOrCreateManifest();
            }
        } finally {
            pm.close();
        }
        // new fetch before making manifest
        // this is just to ensure the proper ordering of entities
        // This is wasteful if the manifest has not been invalidated
        pm = PMF.get().getPersistenceManager();
        try {
            CloudZip cz = internalGet(pm, name);
            if (cz != null) {
                return cz.getOrCreateManifest();
            } else {
                log.severe("null CloudZip after update");
                return "[]";
            }
        } finally {
            pm.close();
        }
    }

    // not used: moved from Servlet for callback based template
    private List<CloudZipEntry> expandZipStream(InputStream is) {
        // LinkedHashMap preserves insertion order in iteration
        List<CloudZipEntry> entries = new ArrayList<CloudZipEntry>();
        ZipInputStream zipis = new ZipInputStream(is);
        while (true) {
            try {
                ZipEntry ze = zipis.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (ze.isDirectory()) {
                    log.info("Ignoring directory: " + ze.getName());
                    continue;
                }
                String name = ze.getName();
                if (ze.getExtra() != null) {
                    log.info("Extra: " + new String(ze.getExtra()) + " " + name);
                }

                byte[] content = IOUtils.toByteArray(zipis);
                entries.add(new CloudZipEntry(name, content));
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                break;
            }
        }
        return entries;
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
            log.log(Level.SEVERE, null, e);
        }
        return cz;
    }
}
