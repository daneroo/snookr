/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.filesystem;

import java.io.File;
import java.util.List;
import java.util.Vector;

/**
 * This class is an IFileHandler implemntation
 * which accumulates handled Files in a List
 * @author Daniel Lauzon
 */

public class ListingFileHandler implements IFileHandler  {
    List list;
    public ListingFileHandler() {
        list = new Vector();
    }
    public ListingFileHandler(List list) {
        this.list = list;
    }
    public void handle(File f) {
        list.add(f);
    }
    public List getList() {
        return list;
    }
} 
