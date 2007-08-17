/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.digest;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5NativeImpl implements IMD5Impl{
    /* 
       Message Digest Algorithms : from java.security
       "MD2","MD5","SHA", and "SHA-1", same as SHA
       "SHA-256","SHA-384","SHA-512"   Unsupported by IBM
    */
    static final String algorithm = "MD5";
    
    MessageDigest getImplementation() {
        try {
            return MessageDigest.getInstance(algorithm);  
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae.getMessage());
        }
    }
    public String digest(byte b[]) {
        MessageDigest md = getImplementation();
        return toHex(md.digest(b));
    }


    public String digest(InputStream in) throws IOException {
        MessageDigest md = getImplementation();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1){
            md.update(buffer,0, read);
        }
        return toHex(md.digest());
    }
    
    /*
      copied cause private: return com.Ostermiller.util.MD5.toHex(b);
     */
    private static String toHex(byte hash[]){
        StringBuffer buf = new StringBuffer(hash.length * 2);
        for (int i=0; i<hash.length; i++){
            int intVal = hash[i] & 0xff;
            if (intVal < 0x10){
                // append a zero before a one digit hex
                // number to make it two digits.
                buf.append("0");
            }
            buf.append(Integer.toHexString(intVal));
        }
        return buf.toString();
    }
    
}
