/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

import com.google.appengine.api.datastore.Text;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.scalr.MD5;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CloudZip {

    @PrimaryKey
    @Persistent
    private String name;

    public String getName() {
        return name;
    }
    @Persistent(defaultFetchGroup = "true")
    private Text manifest;

    public String getManifest() {
        if (manifest == null) {
            return null;
        }
        return manifest.getValue();
    }

    public void setManifest(String manifest) {
        this.manifest = new Text(manifest);
    }

    /*  Handle fetching carefully: not default, resolve in dao.get(name)
     */
    //@Persistent(defaultFetchGroup = "true")
    @Persistent
    private List<CloudZipEntry> entries;

    public List<CloudZipEntry> getEntriesAsList() {
        return entries;
    }
    public Map<String, byte[]> getEntries() {
        Map<String, byte[]> map = new LinkedHashMap<String, byte[]>(entries.size());
        for (CloudZipEntry ce : entries) {
            byte[] content = ce.getContent();
            map.put(ce.getName(), content);
            //String md5sum = MD5.digest(content);
            //System.out.println("Get: " + ce.getName() + " length: " + content.length + " md5: " + md5sum);
        }
        return map;
    }

    public void setEntries(Map<String, byte[]> entries) {
        List<CloudZipEntry> list = new ArrayList<CloudZipEntry>(entries.size());
        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            list.add(new CloudZipEntry(e.getKey(), e.getValue()));
            //String md5sum = MD5.digest(e.getValue());
            //System.out.println("Set: " + e.getKey() + " length: " + e.getValue().length + " md5: " + md5sum);
        }
        this.entries = list;
    }

    public CloudZip(String name, Map<String, byte[]> entries) {
        this.name = name;
        setEntries(entries);
    }
}
