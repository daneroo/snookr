/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.digest;

import java.io.*;
import com.Ostermiller.util.*;

/*
  MD5 client example from solodump/java/src/Speed.java 
  two implementations : native and ostermiller
    http://ostermiller.org/utils

    Acts as a static point of invocation 
    and a factory for the two implementations
 */

public class MD5 {
    public static final int NATIVE = 1;
    public static final int OSTERMILLER = 2;
    public static final int DEFAULT = NATIVE;//OSTERMILLER; // faster under sun 1.5
    //public static final int DEFAULT = OSTERMILLER; // was faster under ibm 1.4.0

    //public static shortcuts
    public static String digest(File f) throws IOException {
        return digest(new FileInputStream(f));
    }
    public static String digest(String s) {
        return digest(s.getBytes());
    }
    public static String digest(String s,String encoding) throws UnsupportedEncodingException {
        return digest(s.getBytes(encoding));
    }

    public static String digest(byte b[]) {
        return getImplementation().digest(b);
    }

    public static String digest(InputStream in) throws IOException {
        return getImplementation().digest(in);
    }


    public static IMD5Impl getImplementation() {
        return getImplementation(DEFAULT); 
    }
    public static IMD5Impl getImplementation(int implementation) {
        if (implementation==OSTERMILLER) {
            return new MD5OsterMillerImpl();
        } else if (implementation==NATIVE) {
            return new MD5NativeImpl();
        }
        throw new RuntimeException("No Such Implementaion: "+implementation);
    }



}



