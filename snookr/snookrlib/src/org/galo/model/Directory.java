package org.galo.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//public class Directory extends BaseObject {
public class Directory extends IFileDefaultImpl {
    
    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o) {
	Directory myClass = (Directory) o;
	return new CompareToBuilder()
            .appendSuper(super.compareTo(o))
            //.append(this.field1, myClass.field1)
	    .toComparison();
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
	if (!(o instanceof Directory)) {
	    return false;
	}
	Directory rhs = (Directory) o;
	return new EqualsBuilder()
	    .appendSuper(super.equals(o))
	    //.append(this.field1, rhs.field1)
	    .isEquals();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
	return new HashCodeBuilder(580457527, 541272143)
	    .appendSuper(super.hashCode())
	    //.append(this.filed1)
	    .toHashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return new ToStringBuilder(this)
            .appendSuper(super.toString())
	    //.append("field1",this.filed1)
	    .toString();
    }
}    
