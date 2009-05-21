/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import net.scalr.GZIP;
import net.scalr.MD5;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CloudMap {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    @Persistent
    private String group;
    @Persistent
    private String name;
    @Persistent
    private int length;
    @Persistent
    private String md5;
    //@Persistent(defaultFetchGroup="true")
    @Persistent()
    private Blob content;

    public String getKeyDescription() {
        return String.format("Key: %s | group: %s name: %s", KeyFactory.keyToString(key), group, name);
    }

    public Key getKey() {
        return key;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public String getMd5() {
        return md5;
    }

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
        this.length = content.length;
        this.md5 = MD5.digest(content);
        byte[] compressed = new GZIP().gzip(content);
        //System.out.println("  Compressed "+content.length+"->"+compressed.length);
        this.content = new Blob(compressed);
    }

    public CloudMap(String group, String name, byte[] content) {
        this.group = group;
        this.name = name;
        setContent(content);
    }

    private CloudMap() {
    }
}
