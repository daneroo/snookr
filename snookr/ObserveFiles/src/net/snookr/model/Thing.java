/*
 * Thing.java
 *
 * Created on February 12, 2008, 1:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.snookr.model;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import net.snookr.util.DateFormat;
/**
 * Entity class Thing
 *
 * @author daniel
 */
@Entity
@NamedQuery(
    name="findThingByFileName",
    query="SELECT thing FROM Thing thing WHERE  thing.fileName = :fileName"
)
public class Thing implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fileName; // canonical path - Natural (unique) key
    private Long fileSize;
    private String md5;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    @Temporal(TemporalType.TIMESTAMP)
    private Date taken;  // this is extracted from exif data when available - null if none available
    
    /** Creates a new instance of Thing */
    public Thing() {
    }
    public Thing(FSImage fsima) {
        setFileName(fsima.fileName);
        setFileSize(fsima.size);
        setMd5(fsima.md5);
        setLastModified(fsima.lastModified);
        setTaken(fsima.taken);
    }
    
    /**
     * Gets the id of this Thing.
     * @return the id
     */
    public Long getId() {
        return this.id;
    }
    
    /**
     * Sets the id of this Thing to the specified value.
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
    
    /**
     * Determines whether another object is equal to this Thing.  The result is
     * <code>true</code> if and only if the argument is not null and is a Thing object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Thing)) {
            return false;
        }
        Thing other = (Thing)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }
    
    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        File f = new File(fileName);
        return "id:"+id+" file:"+f.getPath()+" sz:"+fileSize+" md5:"+md5+" mod:"+safeDate(lastModified)+" exif:"+safeDate(taken);
    }
    String safeDate(Date d) {
        return DateFormat.format(d,"????-??-?? ??:??:??");
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long filesize) {
        this.fileSize = fileSize;
    }
    
    public String getMd5() {
        return md5;
    }
    
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    public Date getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public Date getTaken() {
        return taken;
    }
    
    public void setTaken(Date taken) {
        this.taken = taken;
    }
    
}
