/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.model;

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

// ...
    @PrimaryKey
    @Persistent
    private String name;

    public String getName() {
        return name;
    }

    @Persistent(defaultFetchGroup="true")
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public CloudMap(String name,byte[] content){
        this.name=name;
        this.content=content;
    }
}
