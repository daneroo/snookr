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
import net.scalr.MD5;

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
    private final String name;
    @Persistent
    private final int length;
    @Persistent
    private final String md5;

    public String getName() {
        return name;
    }
    public int getLength() {
        return length;
    }
    public String getMd5() {
        return md5;
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
        //return content.getBytes();
        byte[] uncompressed = new GZIP().gunzip(content.getBytes());
        //System.out.println("  Uncompressed "+content.getBytes().length+"->"+uncompressed.length);
        return uncompressed;
    }

    public void setContent(byte[] content) {
        //this.content = new Blob(content);
        byte[] compressed = new GZIP().gzip(content);
        //System.out.println("  Compressed "+content.length+"->"+compressed.length);
        this.content = new Blob(compressed);
    }

    public CloudZipEntry(String name, byte[] content) {
        this.name = name;
        this.length = content.length;
        this.md5 = MD5.digest(content);
        setContent(content);
    }
}
