package org.galo.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//public class Host extends BaseObject {
public class Host implements Serializable {
    private String osName;
    private String ipAddress;
    private String hostName;
    private String macAddress;
    private Long id;
    
    /**
     * @return Returns the OS Name
     */
    public String getOSName() {
	return osName;
    }
    
    /**
     * @param osName The OS Name to set.
     */
    public void setOSName(String osName) {
	this.osName = osName;
    }
    

    /**
     * @return Returns the IP Address
     */
    public String getIPAddress() {
	return ipAddress;
    }
    
    /**
     * @param osName The IP Address to set.
     */
    public void setIPAddress(String ipAddress) {
	this.ipAddress = ipAddress;
    }

    /**
     * @return Returns the Host Name
     */
    public String getHostName() {
	return hostName;
    }
    
    /**
     * @param hostName The Host Name to set.
     */
    public void setHostName(String hostName) {
	this.hostName = hostName;
    }

    /**
     * @return Returns the OS Name
     */
    public String getMACAddress() {
	return macAddress;
    }
    
    /**
     * @param osName The MAC Address to set.
     */
    public void setMACAddress(String macAddress) {
	this.macAddress = macAddress;
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
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object object) {
	Host myClass = (Host) object;
	return new CompareToBuilder()
	    .append(this.osName, myClass.osName)
	    .append(this.ipAddress, myClass.ipAddress)
	    .append(this.hostName, myClass.hostName)
	    .append(this.macAddress, myClass.macAddress)
	    .append(this.id, myClass.id)
	    .toComparison();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
	if (!(object instanceof Host)) {
	    return false;
	}
	Host rhs = (Host) object;
	return new EqualsBuilder()
	    .appendSuper(super.equals(object))
	    .append(this.osName, rhs.osName)
	    .append(this.ipAddress, rhs.ipAddress)
	    .append(this.hostName, rhs.hostName)
	    .append(this.macAddress, rhs.macAddress)
	    .append(this.id, rhs.id)
	    .isEquals();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
	return new HashCodeBuilder(780243129, 106927237)
	    .appendSuper(super.hashCode())
	    .append(this.osName)
	    .append(this.ipAddress)
	    .append(this.hostName)
	    .append(this.macAddress)
	    .append(this.id)
	    .toHashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return new ToStringBuilder(this)
	    .append("id", this.id)
	    .append("osName", this.osName)
	    .append("ipAddress", this.ipAddress)
	    .append("hostName", this.hostName)
	    .append("macAddress", this.macAddress)
	    .toString();
    }
}    
