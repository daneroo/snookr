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

/**
 * This class simply traverses a FileSystem
 * invoking either the dirhandler or filehandler
 *
 * See FileWalker and DirectoryWalker for static shortcuts
 *
 * A typical use would be:
 *   List dirs = new java.util.Vector();
 *   List files = new java.util.Vector();
 *   new BaseWalker(new ListingFileHandler(dirs),
 *                  new ListingFileHandler(files)).execute(baseDir);
 *  use dirs, and files...
 *
 * No guarantees that this will terminate (yet), 
 * To implement safeguards against infinite recursion :
 *   we keep the stack of canonical paths for
 *  and eiher ignore duplicates, or throh a runtime exception
 * in the presensence of circular symbolic links, for example
 * To implement safeguards against infinite recursion :
 *   -implement hardLimits on maximum Fils/Dirs/Levels
 *   -implement hash of "seen" canonical paths.
 *
 * each File in the tree is classified as either
 *  - a readble directory
 *  - a readble file - (as in File.isFile()) 
 *  - or ignored
 * @author Daniel Lauzon
 */
public class BaseWalker {
    
    FileFilter readableDirectoryFilter;   
    FileFilter readableFileFilter;
    IFileHandler directoryHandler;
    IFileHandler fileHandler;
    private IFileHandler recursiveInternalDirectoryHandler;

    public BaseWalker(IFileHandler directoryHandler,
                      IFileHandler fileHandler) {
        readableDirectoryFilter = new ReadableDirectoryFilter();
        readableFileFilter = new ReadableFileFilter();
        this.directoryHandler = directoryHandler;
        this.fileHandler = fileHandler;
        this.recursiveInternalDirectoryHandler =  new IFileHandler() {
                public void handle(File subdir) {
                    internalExecuteDirectory(subdir);
                }
            };
    }

    boolean isValidDirectory(File f) {
        return readableDirectoryFilter.accept(f); 
    }
    boolean isValidFile(File f) {
        return readableFileFilter.accept(f); 
    }

    /*
     * This methid handles recursion, but also invokes different callbacks
     * for direcoties, and files
     *
     * @param fileOrDir is a valid file or directory, which can be read, and exists

     */
    public void execute( File fileOrDir ) {
        if (isValidDirectory(fileOrDir)) {
            internalExecuteDirectory(fileOrDir);
        } else if (isValidFile(fileOrDir)) {
            if (fileHandler!=null) { fileHandler.handle(fileOrDir);}
        } else {
            // could have an ignored Handler instead
            // but this idea would need to be propagated to the
            // recursion ??
            throw new IllegalArgumentException("Invalid File (should exist and be readble): "+fileOrDir);
        }
            
    }

    /*
     * Invokes the public execute(File f) method for every element in the list
     * for direcoties, and files
     *
     * @param fileOrDirList is a valid directory, which can be read.

     */
    public void execute( List fileOrDirList ) {
        Iterator iter = fileOrDirList.iterator();
        while ( iter.hasNext() ) {
            execute((File)iter.next());
        }        
    }
        
    ////////////////////////////////////////
    // private methods below.
    ////////////////////////////////////////

    /* this handle recursion for directories
     * the reason we don't use the public execute method for this is to avoid
     * the classification overhead on each child node
     *
     * Recursion : gather history of traversed paths
     *  prevent traversing a conical path twice
     *  might also prevent follwing links outside top basedir
     *  might also prevent following any path which is not cacnonical (links)
     * might throw an exception instead of ignoring
     */

    // Map of traversed canonicalpaths: ( the stack is not enough ?)
    java.util.SortedMap history  = new java.util.TreeMap();
    private void internalExecuteDirectory( File baseDir ) {
        try {
            String currentCanonicalPath = baseDir.getCanonicalPath();
            File previous = (File)history.get(currentCanonicalPath);
            if (previous!=null) {
                /*
                Iterator iter = history.values().iterator();
                while ( iter.hasNext() ) {
                    System.out.println("history: "+(File)iter.next());
                }
                System.out.println("current  "+baseDir+" : "+baseDir.getCanonicalPath());
                System.out.println("previous "+previous+" : "+previous.getCanonicalPath());
                */
                
                return;
                //throw new RuntimeException("Recursion loop");
            }
            history.put(currentCanonicalPath,baseDir);
        } catch (IOException ioe){
            throw new RuntimeException(ioe.getMessage());
        }

        // handle self
        if (directoryHandler!=null) {
            directoryHandler.handle(baseDir);
        }

        // handle File Children - first
        if (fileHandler!=null) {
            execute(baseDir,readableFileFilter,fileHandler);
        }

        // handle Directory Children - by recursion
        // use the recursiveInternalDirectoryHandler to reuse
        // the factored method below.
        execute(baseDir,readableDirectoryFilter,recursiveInternalDirectoryHandler);
    }

    /* used to find and iterate through children file, and child dirs */
    private void execute(File baseDir,FileFilter fileFilter,IFileHandler handler) {
        File[] files =  baseDir.listFiles(fileFilter);
        List fileList = Arrays.asList(files);
        Collections.sort(fileList);
        Iterator fileIter = fileList.iterator();
        while ( fileIter.hasNext() ) {
            handler.handle((File)fileIter.next());
        }
    }
    
} 
