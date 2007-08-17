/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */
package org.galo.util;

import java.io.*;

public class Stream { 

    public static void copy(Reader reader,Writer writer) throws IOException {
        int c;
        while ((c = reader.read()) != -1) writer.write(c);
    }        
    public static String readerToString(Reader reader) throws IOException {
        StringWriter sw = new StringWriter();
        copy(reader,sw);
        return sw.toString();
    }

    public static void copy(InputStream is,Writer writer) throws IOException {
        int c;
        while ((c = is.read()) != -1) writer.write(c);
    }        
    public static String inputStreamToString(InputStream is) throws IOException {
        StringWriter sw = new StringWriter();
        copy(is,sw);
        return sw.toString();
    }

    // used to override System.out in  con.drew,..
    public static OutputStream nullOutputStream() {
        return new OutputStream() {
                public void close() throws IOException {}
                public void flush() throws IOException {}
                public void write(byte b[]) throws IOException {}
                public void write(byte b[], int off, int len) throws IOException {}
                public void write(int b) throws IOException {}
            };
    }
}
