/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

import com.google.appengine.api.datastore.Blob;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 *
 * @author daniel
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CloudMap {

    @PrimaryKey
    @Persistent
    private String name;

    public String getName() {
        return name;
    }

    @Persistent(defaultFetchGroup="true")
    private Blob content;

    public byte[] getContent() {
        if (content==null) return null;
        return content.getBytes();
    }

    public void setContent(byte[] content) {
        this.content = new Blob(content);
    }

    public CloudMap(String name,byte[] content){
        this.name=name;
        this.content=new Blob(content);
    }
}
