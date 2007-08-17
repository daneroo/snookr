/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.filesystem;

import java.io.File;

/**
 * This class is a callback interface for
 * BaseWalker and subclasses.
 * It should throw an unchecked Exception 
 * such as IlleaglArgumentException, if an error occurs.
 * @author Daniel Lauzon
 */

public class CountingFileHandler implements IFileHandler  {
    int n=0;
    public void handle(File f) {
        n++;
    }
    public int count() {
        return n;
    }
} 
