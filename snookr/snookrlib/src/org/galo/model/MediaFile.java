package org.galo.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//public class MediaFile extends BaseObject {
//public class MediaFile extends RegularFile {
public class MediaFile extends IFileDefaultImpl {

    private IMediaContent content;

    public IMediaContent getContent() {
        return content;
    }
    public void setContent(IMediaContent content) {
        this.content = content;
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
	MediaFile myClass = (MediaFile) o;
	return new CompareToBuilder()
            .appendSuper(super.compareTo(o))
            .append(this.content, myClass.content)
	    .toComparison();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
	if (!(o instanceof MediaFile)) {
	    return false;
	}
	MediaFile rhs = (MediaFile) o;
	return new EqualsBuilder()
	    .appendSuper(super.equals(o))
	    .append(this.content, rhs.content)
	    .isEquals();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
	return new HashCodeBuilder(854156087, 872401461)
	    .appendSuper(super.hashCode())
	    .append(this.content)
	    .toHashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .appendAsObjectToString(this.content)
	    //.append("field1",this.filed1)
	    .toString();
    }
}    
