/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.filesystem;

import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.io.*;
import org.galo.util.Timer;

public class ReadableDirectoryFilter implements FileFilter {
    public boolean accept(File pathname) {
        if ( pathname == null
             || !pathname.exists()
             || !pathname.isDirectory()
             || !pathname.canRead()  ) {
            return false;
        }
        return true;
    }
} 
