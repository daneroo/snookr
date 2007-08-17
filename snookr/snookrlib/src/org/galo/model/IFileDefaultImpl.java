package org.galo.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//public class Directory extends BaseObject {
public class IFileDefaultImpl implements IFile,Serializable {
    private Host host;
    private Directory parent;
    private String fileName;
    private Date lastModified;
    private Long id;
    
    public Host getHost() {
        return host;
    }
    public void setHost(Host host) {
        this.host = host;
    }

    public Directory getParent() {
        return parent;
    }
    public void setParent(Directory parent){
        this.parent = parent;
    }

    public String getFileName() {
	return fileName;
    }
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }
    
    public Date getLastModified() {
	return lastModified;
    }
    public void setLastModified(Date lastModified) {
	this.lastModified = lastModified;
    }

    public Long getId() {
	return id;
    }
    public void setId(Long id) {
	this.id = id;
    }


    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
	IFileDefaultImpl myClass = (IFileDefaultImpl) o;
	return new CompareToBuilder()
            //.appendSuper(super.compareTo(o))
	    .append(this.host, myClass.host)
	    .append(this.parent, myClass.parent)
	    .append(this.fileName, myClass.fileName)
	    .append(this.lastModified, myClass.lastModified)
	    .append(this.id, myClass.id)
	    .toComparison();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
	if (!(o instanceof IFileDefaultImpl)) {
	    return false;
	}
	IFileDefaultImpl rhs = (IFileDefaultImpl) o;
	return new EqualsBuilder()
	    .appendSuper(super.equals(o))
	    .append(this.host, rhs.host)
	    .append(this.parent, rhs.parent)
	    .append(this.fileName, rhs.fileName)
	    .append(this.lastModified, rhs.lastModified)
	    .append(this.id, rhs.id)
	    .isEquals();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
	return new HashCodeBuilder(810721769, 187456565)
	    .appendSuper(super.hashCode())
	    .append(this.host)
	    .append(this.parent)
	    .append(this.fileName)
	    .append(this.lastModified)
	    .append(this.id)
	    .toHashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return new ToStringBuilder(this)
	    .append("id", this.id)
	    .appendAsObjectToString(this.host)
	    .appendAsObjectToString(this.parent)
	    .append("fileName", this.fileName)
	    .append("lastModified", this.lastModified)
	    .toString();
    }
}    
