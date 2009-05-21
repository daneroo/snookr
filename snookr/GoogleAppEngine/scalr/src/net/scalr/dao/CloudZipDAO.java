/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.scalr.MD5;
import net.scalr.model.CloudMap;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author daniel
 */
public class CloudZipDAO {

    private static final Logger log =
            Logger.getLogger(CloudZipDAO.class.getName());
    final CloudMapDAO cmdao = new CloudMapDAO();

    public void delete(String name) {
        cmdao.deleteGroup(name);
    }

    /* Take a look at:
     * http://www.datanucleus.org/products/accessplatform_1_1/jdo/query.html
     * :: query.setResult("count(param1), max(param2), param3"); --> Obect[]
     *  Hmm Could not get that to work
     */
    public Set<String> getAllGroups() {
        List<CloudMap> all = cmdao.getAll();
        log.warning("getAllGroups found: "+all.size()+" map entries");
        Set<String> groups = new HashSet<String>();
        for (CloudMap cm : all) {
            groups.add(cm.getGroup());
        }
        return groups;
    }

    // As we traverse the incoming zip entries,
    // track which of the preivious entries have been replaced, by removing them from
    //  a tracking set: entryNamesToDeleteAfter.
    // at the end we can remove remaining entries from the original list.
    public String updateWithStream(String name, InputStream is) {
        Set<String> entryNamesDeleteAfter = getEntryNames(name);
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
                    entryNamesDeleteAfter.remove(ename);
                    cmdao.delete(new CloudMap(name, ename, null));
                } else if (preserve) {
                    entryNamesDeleteAfter.remove(ename);
                } else { // normal add/replace mode
                    entryNamesDeleteAfter.remove(ename);
                    cmdao.createOrUpdate(new CloudMap(name, ename, content));
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
                cmdao.delete(new CloudMap(name, ename, null));
            }
        }
        return cmdao.makeManifest(name);
    }

    // used to get names of entries to delete if not preserved....
    private Set<String> getEntryNames(String name) {
        Set<String> entryNamesDeleteAfter = new HashSet<String>();
        List<CloudMap> entries = cmdao.getGroup(name);
        if (entries != null) {
            log.info("Examining entries before; size:" + entries.size());
            for (CloudMap cme : entries) {
                log.info("  entry: " + cme.getKeyDescription());
                entryNamesDeleteAfter.add(cme.getName());
            }
        } else {
            log.info("Entries before null List:");
        }
        return entryNamesDeleteAfter;
    }
    private final long timeout = 22000;

    public String updateWithStreamAndTimeout(String name, InputStream is) {
        long start = new Date().getTime();
        boolean expiredTimeout = false;
        Set<String> entryNamesDeleteAfter = getEntryNames(name);

        ZipInputStream zipis = new ZipInputStream(is);

        while (true) {
            try {
                long elapsed = new Date().getTime() - start;
                if (elapsed > timeout) {
                    log.severe("Timeout about to expire: Bolting");
                    expiredTimeout = true;
                    break;
                } else {
                    log.severe("Continue: elapsed= " + elapsed);
                }
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
                    entryNamesDeleteAfter.remove(ename);
                    cmdao.delete(new CloudMap(name, ename, null));
                } else if (preserve) {
                    entryNamesDeleteAfter.remove(ename);
                } else { // normal add/replace mode
                    entryNamesDeleteAfter.remove(ename);
                    cmdao.createOrUpdate(new CloudMap(name, ename, content));
                }

            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                break;
            }

        }
        // now delete any unnaccounted for entries
        if (expiredTimeout) { // contains too many entries: not all handled
            log.info("No time for cleanup! " + entryNamesDeleteAfter.size() + " entries");
        } else {
            // now delete any unnaccounted for entries
            log.info("Cleaning up remaining " + entryNamesDeleteAfter.size() + " entries");
            for (String ename : entryNamesDeleteAfter) {
                if (ename == null) {
                    log.severe("CloudZip entry name is null");
                } else {
                    log.warning("Cleaning up remaining entry with name:" + ename);
                    cmdao.delete(new CloudMap(name, ename, null));
                }
            }
        }
        return cmdao.makeManifest(name);
    }


    // not used: moved from Servlet for callback based template
    private List<CloudMap> expandZipStream(String group,InputStream is) {
        // LinkedHashMap preserves insertion order in iteration
        List<CloudMap> entries = new ArrayList<CloudMap>();
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
                String ename = ze.getName();
                if (ze.getExtra() != null) {
                    log.info("Extra: " + new String(ze.getExtra()) + " " + ename);
                }

                byte[] content = IOUtils.toByteArray(zipis);
                entries.add(new CloudMap(group,ename, content));
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
                break;
            }
        }
        return entries;
    }

}
