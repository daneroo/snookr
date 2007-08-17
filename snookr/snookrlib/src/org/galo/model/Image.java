package org.galo.model;

import java.util.Date;
import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//public class Media extends BaseObject {
public class Image implements IMediaContent,Serializable {
    private Long id;
    private String fileName;
    
    private Date   stamp; // exif , else fileSystem
    private long    fileSize;
    
    private Date   lastModified; // from filesystem
    
    // Digest
    private String md5;
    
    // Exif
    private Date  exifDate; // from filesystem
    private int    width;
    private int    height;
    
    /**
     * @return Returns the exifDate.
     */
    public Date getExifDate() {
        return exifDate;
    }
    
    /**
     * @param exifDate The exifDate to set.
     */
    public void setExifDate(Date exifDate) {
        this.exifDate = exifDate;
    }
    
    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * @return Returns the fileSize.
     */
    public long getFileSize() {
        return fileSize;
    }
    
    /**
     * @param fileSize The fileSize to set.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * @return Returns the height.
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * @param height The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return Returns the lastModified.
     */
    public Date getLastModified() {
        return lastModified;
    }
    
    /**
     * @param lastModified The lastModified to set.
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    /**
     * @return Returns the md5.
     */
    public String getMd5() {
        return md5;
    }
    
    /**
     * @param md5 The md5 to set.
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    /**
     * @return Returns the stamp.
     */
    public Date getStamp() {
        return stamp;
    }
    
    /**
     * @param stamp The stamp to set.
     */
    public void setStamp(Date stamp) {
        this.stamp = stamp;
    }
    
    /**
     * @return Returns the width.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @param width The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }
    
    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object object) {
        Image myClass = (Image) object;
        return new CompareToBuilder()
            .append(this.width, myClass.width)
            .append(this.fileName, myClass.fileName)
            .append(this.height, myClass.height)
            .append(this.exifDate, myClass.exifDate)
            .append(this.stamp, myClass.stamp)
            .append(this.lastModified, myClass.lastModified)
            .append(this.fileSize, myClass.fileSize)
            .append(this.md5, myClass.md5)
            .append(this.id, myClass.id)
            .toComparison();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof Image)) {
            return false;
        }
        Image rhs = (Image) object;
        return new EqualsBuilder()
            .appendSuper(super.equals(object))
            .append(this.width, rhs.width)
            .append(this.fileName, rhs.fileName)
            .append(this.height, rhs.height)
            .append(this.exifDate, rhs.exifDate)
            .append(this.stamp, rhs.stamp)
            .append(this.lastModified, rhs.lastModified)
            .append(this.fileSize, rhs.fileSize)
            .append(this.md5, rhs.md5)
            .append(this.id, rhs.id)
            .isEquals();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(763364937, 778313533)
            .appendSuper(super.hashCode())
            .append(this.width)
            .append(this.fileName)
            .append(this.height)
            .append(this.exifDate)
            .append(this.stamp)
            .append(this.lastModified)
            .append(this.fileSize)
            .append(this.md5)
            .append(this.id)
            .toHashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", this.id)
            .append("width", this.width)
            .append("height", this.height)
            .append("stamp", this.stamp)
            .append("fileSize", this.fileSize)
            .append("fileName", this.fileName)
            .append("lastModified", this.lastModified)
            .append("exifDate", this.exifDate)
            .append("md5", this.md5)
            .toString();
    }
}
