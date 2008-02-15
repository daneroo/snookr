/*
 * NetImage.java
 *
 * Created on February 14, 2008, 10:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.snookr.model;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Entity class NetImage
 *
 *
 * @author daniel
 */
@Entity
@Table(
name = "netimage",
        uniqueConstraints = {@UniqueConstraint(columnNames={"fileName"})}
)

@NamedQueries( {
    @NamedQuery(name = "NetImage.findByHostAndFileName", query = "SELECT n FROM NetImage n WHERE n.host = :host AND n.fileName = :fileName"),
    @NamedQuery(name = "NetImage.findById", query = "SELECT n FROM NetImage n WHERE n.id = :id"),
    @NamedQuery(name = "NetImage.countByHost", query = "SELECT count(n) FROM NetImage n WHERE n.host = :host"),
    @NamedQuery(name = "NetImage.findByHost", query = "SELECT n FROM NetImage n WHERE n.host = :host"),
    @NamedQuery(name = "NetImage.findByFileName", query = "SELECT n FROM NetImage n WHERE n.fileName = :fileName"),
    @NamedQuery(name = "NetImage.findByFileSize", query = "SELECT n FROM NetImage n WHERE n.fileSize = :fileSize"),
    @NamedQuery(name = "NetImage.findByLastModified", query = "SELECT n FROM NetImage n WHERE n.lastModified = :lastModified"),
    @NamedQuery(name = "NetImage.findByMd5", query = "SELECT n FROM NetImage n WHERE n.md5 = :md5"),
    @NamedQuery(name = "NetImage.findByTaken", query = "SELECT n FROM NetImage n WHERE n.taken = :taken")
})
public class NetImage implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "host")
    private String host;
    
    @Column(name = "fileName")
    private String fileName;
    
    @Column(name = "fileSize")
    private Long fileSize;
    
    @Column(name = "lastModified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    
    @Column(name = "md5")
    private String md5;
    
    @Column(name = "taken")
    @Temporal(TemporalType.TIMESTAMP)
    private Date taken;
    
    /**
     * Creates a new instance of NetImage
     */
    public NetImage() {
    }
    
    public NetImage(String host,FSImage fsima) {
        setHost(host);
        setFileName(fsima.fileName);
        setFileSize(fsima.size);
        setMd5(fsima.md5);
        setLastModified(fsima.lastModified);
        setTaken(fsima.taken);
    }
    
    
    /**
     * Gets the id of this NetImage.
     *
     * @return the id
     */
    public Long getId() {
        return this.id;
    }
    
    /**
     * Sets the id of this NetImage to the specified value.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the host of this NetImage.
     *
     * @return the host
     */
    public String getHost() {
        return this.host;
    }
    
    /**
     * Sets the host of this NetImage to the specified value.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    /**
     * Gets the fileName of this NetImage.
     *
     * @return the fileName
     */
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * Sets the fileName of this NetImage to the specified value.
     *
     * @param fileName the new fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Gets the fileSize of this NetImage.
     *
     * @return the fileSize
     */
    public Long getFileSize() {
        return this.fileSize;
    }
    
    /**
     * Sets the fileSize of this NetImage to the specified value.
     *
     * @param fileSize the new fileSize
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * Gets the lastModified of this NetImage.
     *
     * @return the lastModified
     */
    public Date getLastModified() {
        return this.lastModified;
    }
    
    /**
     * Sets the lastModified of this NetImage to the specified value.
     *
     * @param lastModified the new lastModified
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    /**
     * Gets the md5 of this NetImage.
     *
     * @return the md5
     */
    public String getMd5() {
        return this.md5;
    }
    
    /**
     * Sets the md5 of this NetImage to the specified value.
     *
     * @param md5 the new md5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    /**
     * Gets the taken of this NetImage.
     *
     * @return the taken
     */
    public Date getTaken() {
        return this.taken;
    }
    
    /**
     * Sets the taken of this NetImage to the specified value.
     *
     * @param taken the new taken
     */
    public void setTaken(Date taken) {
        this.taken = taken;
    }
    
    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    /**
     * Determines whether another object is equal to this NetImage.  The result is
     * <code>true</code> if and only if the argument is not null and is a NetImage object that
     * has the same id field values as this object.
     *
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NetImage)) {
            return false;
        }
        NetImage other = (NetImage)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
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
        return "id:"+id+" host:"+host+" file:"+f.getPath()+" sz:"+fileSize+" md5:"+md5+" mod:"+safeDate(lastModified)+" exif:"+safeDate(taken);
    }
    String safeDate(Date d) {
        //return DateFormat.format(d,"????-??-?? ??:??:??");
        String defaultValue="????-??-?? ??:??:??";
        final String YYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
        final SimpleDateFormat SDF = new SimpleDateFormat(YYYMMDDHHMMSS);
        try {
            return SDF.format(d);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
}
