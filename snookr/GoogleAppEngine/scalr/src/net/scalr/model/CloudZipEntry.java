/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import net.scalr.GZIP;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CloudZipEntry {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    @Persistent
    private String name;

    public String getName() {
        return name;
    }

    /* Careful with fetching...
     */
    //@Persistent(defaultFetchGroup = "true")
    @Persistent
    private Blob content;

    public byte[] getContent() {
        if (content == null) {
            return null;
        }
        // return content.getBytes();
        byte[] uncompressed = new GZIP().gunzip(content.getBytes());
        System.out.println("  Uncompressed "+content.getBytes().length+"->"+uncompressed.length);
        return uncompressed;
    }

    public void setContent(byte[] content) {
        byte[] compressed = new GZIP().gzip(content);
        //System.out.println("  Compressed "+content.length+"->"+compressed.length);
        this.content = new Blob(compressed);
    }

    public CloudZipEntry(String name, byte[] content) {
        this.name = name;
        setContent(content);
    }
}
