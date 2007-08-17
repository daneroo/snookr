package org.galo.model;

import java.util.Date;

public interface IFile {
    
    public Host getHost();
    public void setHost(Host host);
    public Directory getParent();
    public void setParent(Directory parent);

    public String getFileName();
    public void setFileName(String fileName);
    public Date getLastModified();
    public void setLastModified(Date lastModified);

    public Long getId();
    public void setId(Long id);

    /*
      Implement with builders from org.apache.commons.lang.builder.
    */
    public int compareTo(Object object);
    public boolean equals(Object object);
    public int hashCode();
    public String toString();

}    
