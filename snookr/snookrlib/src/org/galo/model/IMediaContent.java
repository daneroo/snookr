package org.galo.model;

import java.util.Date;

public interface IMediaContent {
    
    public long getFileSize();
    public void setFileSize(long fileSize);
    public String getMd5();
    public void setMd5(String md5);

    /*
      Implement with builders from org.apache.commons.lang.builder.
    */
    public int compareTo(Object object);
    public boolean equals(Object object);
    public int hashCode();
    public String toString();

}    
