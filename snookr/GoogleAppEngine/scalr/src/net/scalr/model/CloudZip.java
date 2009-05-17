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
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
// Unique might include owner, later
//@Unique(name="ZIP_NAME_IDX", members={"name"})
public class CloudZip {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;

    @Persistent
    @Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
    private String name;


    public String getKeyDescription(){
        return String.format("Key as String: %s | name: %s",encodedKey,name);
    }
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

    public List<CloudZipEntry> getEntries() {
        return entries;
    }
    public void setEntries(List<CloudZipEntry> entries) {
        this.entries = entries;
    }

    public CloudZip(String name) {
        this(name,new ArrayList<CloudZipEntry>());
    }
    public CloudZip(String name, List<CloudZipEntry> entries) {
        this.name = name;
        setEntries(entries);
    }
}
