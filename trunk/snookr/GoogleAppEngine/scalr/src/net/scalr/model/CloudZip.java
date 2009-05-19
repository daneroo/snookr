/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

import com.google.appengine.api.datastore.Text;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.scalr.JSON;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
// Unique might include owner, later
//@Unique(name="ZIP_NAME_IDX", members={"name"})
public class CloudZip {

    private static final Logger log =
            Logger.getLogger(CloudZip.class.getName());
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String encodedKey;
    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.pk-name", value = "true")
    private String name;

    public String getKeyDescription() {
        return String.format("Key as String: %s | name: %s", encodedKey, name);
    }

    public String getName() {
        return name;
    }
    @Persistent(defaultFetchGroup = "true")
    private Text manifest;

    public String getOrCreateManifest() {
        if (isManifestValid()) {
            log.info("Reusing manifest");
            return manifest.getValue();
        }
        // make the manifest
        log.warning("Making new manifest");
        this.manifest = new Text(makeManifest());

        return manifest.getValue();
    }

    public boolean isManifestValid() {
        if (manifest == null) {
            return false;
        }
        return true;
    }

    public void invalidateManifest() {
        if (!isManifestValid()) {
            return;
        }
        log.warning("Invalidating manifest");
        this.manifest = null;
    }

    /*  Handle fetching carefully: not default, resolve in dao.get(name)
     */
    //@Persistent(defaultFetchGroup = "true")
    @Persistent
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "name asc"))
    private List<CloudZipEntry> entries;

    public List<CloudZipEntry> getEntries() {
        return entries;
    }

    public void deleteAllEntriesWithName(String entryName) {
        if (entries == null) {
            log.info("No entries to delete from = with name: " + entryName);
            return;
        }
        log.info("Delete examining " + entries.size() + " entries for name: " + entryName);
        for (ListIterator<CloudZipEntry> it = entries.listIterator(); it.hasNext();) {
            int index = it.nextIndex();
            CloudZipEntry ze = it.next();
            if (entryName.equals(ze.getName())) {
                log.warning("Deleting ZipEntry with index:" + index + " name:" + ze.getName());
                it.remove();
                invalidateManifest();
            }
            // this should not happen any more
            // remove code when tested
            if (ze.getName() == null) {
                log.severe("Deleting ZipEntry with index:" + index + " name:" + ze.getName());
                it.remove();
                invalidateManifest();
            }
        }
    }

    public void addEntry(CloudZipEntry entry) {
        log.warning("Adding entry with name: " + entry.getName());
        if (entries == null) {
            this.entries = new ArrayList<CloudZipEntry>();
        }
        deleteAllEntriesWithName(entry.getName());
        invalidateManifest();
        this.entries.add(entry);
    }

    public CloudZip(String name) {
        this.name = name;
    }

    private CloudZip() {
// Default construcot required by JDO, even if private
    }

    private String makeManifest() {
        List<Map<String, String>> manifestList = new ArrayList<Map<String, String>>();
        //String now = new Date().toString();
        //log.warning("Making Manifest @ " + now);
        if (entries != null) {
            for (CloudZipEntry e : entries) {
                if (e == null || e.getName() == null) {
                    log.warning("Manifest has null entry: " + e.getKeyDescription());
                } else {
                    log.info("Manifest has good entry: " + e.getKeyDescription());
                    Map<String, String> manifestEntry = new LinkedHashMap<String, String>();
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
}
