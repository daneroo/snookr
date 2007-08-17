package org.galo.model;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class FlickrPhotoEntry implements Serializable {
    public String photoId;
    public String title;
    public String md5; // from tag
    public Date dateTaken;
    public Date datePosted;
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	return "[id,title,md5,taken,posted]="+
	    "["+photoId+","+title+","+md5+","+dateTaken+","+datePosted+"]";
    }
}
