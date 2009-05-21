/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import net.scalr.JSON;
import net.scalr.model.CloudMap;

/**
 *
 * @author daniel
 */
public class CloudMapDAO {

    private static final Logger log =
            Logger.getLogger(CloudMapDAO.class.getName());

    private byte[] ramp(int sz) {
        byte[] content = new byte[sz];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }

    private byte[] rand(int sz) {
        byte[] content = new byte[sz];
        rnd.nextBytes(content);
        return content;
    }
    Random rnd = new Random(123456);

//        int constSz = 512 * 1024;
    public String testBigRandRW() {
        return testRandRW("group-rw-big",512*1024, 30, 10);
    }
    public String testSmallRandRW() {
        return testRandRW("group-rw-small",1024, 300, 100);
    }
    public String testRandRW(String group,int writeSize,int totalEntries,int entriesToWrite) {
        // random read-write
        for (int i = 0; i < entriesToWrite; i++) {
            int index = new Random().nextInt(totalEntries);
            CloudMap clm = new CloudMap(group, "const:sz:" + String.format("%04d", index), ramp(writeSize));
            createOrUpdate(clm);
        }
        return makeManifest(getAll());
    }

    public String testCRD() {
        System.out.println("Before Create:" + makeManifest(getAll()));
        // testing duplicate group/name
        for (int i = 0; i < 3; i++) {
            CloudMap clm = new CloudMap("group-dup", "name1", ("content-" + i).getBytes());
            createOrUpdate(clm);
        }

        // testing size
        int maxRandSz = 1024 * 1024 * 1;
        for (int sz = 1024; sz < maxRandSz; sz *= 2) {
            CloudMap clm = new CloudMap("group-size", "rand:sz:" + String.format("%07d", sz), rand(sz));
            createOrUpdate(clm);
        }
        int maxRampSz = 1024 * 1024 * 10;
        for (int sz = 1024; sz < maxRampSz; sz *= 2) {
            CloudMap clm = new CloudMap("group-size", "ramp:sz:" + String.format("%07d", sz), ramp(sz));
            createOrUpdate(clm);
        }

        System.out.println("After Create-Before Delete:" + makeManifest(getAll()));
        return makeManifest(getAll());
    }

    public CloudMap get(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void delete(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void createOrUpdate(CloudMap clm) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            // first delete duplicates - there should only ever be one !
            List<CloudMap> toDelete = internalGetEntry(pm, clm.getGroup(), clm.getName());
            internalDelete(pm, toDelete);

            // Now make the new one persistent
            pm.makePersistent(clm);
        } finally {
            pm.close();
        }
    }

    public List<CloudMap> getAll() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            List<CloudMap> all = internalGetAll(pm);
            // Sort this thing
            return sortByGroupAndName(all);
        } finally {
            pm.close();
        }
    }

    public void deleteAll() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            List<CloudMap> all = internalGetAll(pm);
            internalDelete(pm, all);
        } finally {
            pm.close();
        }
    }

    private String makeManifest(List<CloudMap> entries) {
        List<Map<String, String>> manifestList = new ArrayList<Map<String, String>>();
        //String now = new Date().toString();
        //log.warning("Making Manifest @ " + now);
        if (entries != null) {
            boolean myOwnPrettyJson = true;
            if (myOwnPrettyJson) {
                if (entries.size() == 0) {
                    return "[]";
                }
                StringBuilder sb = new StringBuilder("[\n");
                for (CloudMap e : entries) {
                    sb.append(String.format("g:%s n:%s l:%7d md5:%s\n", e.getGroup(), e.getName(), e.getLength(), e.getMd5()));
                }
                // remove the last newline
                sb.append("] sz="+entries.size());
                return sb.toString();
            }
            for (CloudMap e : entries) {
                if (e == null || e.getName() == null) {
                    log.warning("Manifest has null entry: " + e.getKeyDescription());
                } else {
                    log.info("Manifest has good entry: " + e.getKeyDescription());
                    Map<String, String> manifestEntry = new LinkedHashMap<String, String>();
                    manifestEntry.put("group", e.getGroup());
                    manifestEntry.put("name", e.getName());
                    manifestEntry.put("length", "" + e.getLength());
                    manifestEntry.put("md5", e.getMd5());
                    manifestList.add(manifestEntry);
                }
            }
            return new JSON().encode(manifestList);
        } else { // null entries
            return "[]";
        }
    }

    // this is only meant for persistent entries!
    private void internalDelete(PersistenceManager pm, List<CloudMap> entries) {
        for (CloudMap entry : entries) {
            log.info("Deleting CloudMap Entry:" + entry.getKeyDescription());
        //pm.deletePersistent(entry);
        }
        pm.deletePersistentAll(entries);
    }

    private List<CloudMap> internalGetEntry(PersistenceManager pm, String group, String name) {
        Query query = pm.newQuery(CloudMap.class,
                "group == groupParam && name == nameParam");
        query.declareParameters("java.lang.String groupParam,java.lang.String nameParam");
        List<CloudMap> entries = (List<CloudMap>) query.execute(group,name);
        // hit this thing....
        entries.size();
        return entries;
    }
    // unused replaced by different Query
    private List<CloudMap> internalGetEntryWithFilter(PersistenceManager pm, String group, String name) {
        List<CloudMap> entries = internalGetGroupUnsorted(pm, group);
        List<CloudMap> matched = new ArrayList<CloudMap>(1);
        for (CloudMap entry : entries) {
            if (name.equals(entry.getName())) {
                matched.add(entry);
            }
        }
        return matched;
    }

    /* This one is sortByGroupAndName
     */
    private List<CloudMap> internalGetGroup(PersistenceManager pm, String group) {
        return sortByGroupAndName(internalGetGroupUnsorted(pm, group));
    }

    /* Sort a retuned copy by group and name
     */
    public List<CloudMap> sortByGroupAndName(List<CloudMap> list) {
        class GroupAndNameComparator implements Comparator<CloudMap> {

            public int compare(CloudMap o1, CloudMap o2) {
                int cmp = o1.getGroup().compareTo(o2.getGroup());
                if (cmp == 0) {
                    cmp = o1.getName().compareTo(o2.getName());
                }
                return cmp;
            }
        }

        List<CloudMap> sortedListCopy = new ArrayList<CloudMap>(list);
        Collections.sort(sortedListCopy, new GroupAndNameComparator());
        return sortedListCopy;
    }

    private List<CloudMap> internalGetGroupUnsorted(PersistenceManager pm, String group) {
        Query query = pm.newQuery(CloudMap.class,
                "group == groupParam");
        query.declareParameters("java.lang.String groupParam");
        List<CloudMap> entries = (List<CloudMap>) query.execute(group);
        // hit this thing....
        entries.size();
        return entries;
    }

    private List<CloudMap> internalGetAll(PersistenceManager pm) {
        Query query = pm.newQuery(CloudMap.class);
        List<CloudMap> entries = (List<CloudMap>) query.execute();
        // hit this thing....
        entries.size();
        return entries;
    }
}
