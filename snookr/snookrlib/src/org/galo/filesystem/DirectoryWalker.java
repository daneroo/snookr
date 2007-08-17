/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.filesystem;

import java.util.List;
import java.io.File;

/**
 * This class provides static shortcuts for
 * often used BaseWalker patterns
 * @author Daniel Lauzon
 */

public class DirectoryWalker {
    
    /**
     * Walk a directory tree and invoke a handler
     * for each file found.
     *
     * @param base is a valid directory, or file which can be read.
     */
    public static List list( File base ) {
        ListingFileHandler handler = new ListingFileHandler();
        BaseWalker walker = new BaseWalker(handler,null);
        walker.execute(base);
        return handler.getList();
    }

    public static List list( List fileOrDirList ) {
        ListingFileHandler handler = new ListingFileHandler();
        BaseWalker walker = new BaseWalker(handler,null);
        walker.execute(fileOrDirList);
        return handler.getList();

    }
    public static int count( File base ) {
        CountingFileHandler handler = new CountingFileHandler();
        BaseWalker walker = new BaseWalker(handler,null);
        walker.execute(base);
        return handler.count();
    }

    public static int count( List fileOrDirList ) {
        CountingFileHandler handler = new CountingFileHandler();
        BaseWalker walker = new BaseWalker(handler,null);
        walker.execute(fileOrDirList);
        return handler.count();
    }
    
} 
