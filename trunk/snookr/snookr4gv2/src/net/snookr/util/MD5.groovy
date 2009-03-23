package net.snookr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5 {

    // TODO move this test code into a proper unit test.
    public static doATest() {
        // list of maps
        def md5examples =[
            ["": "d41d8cd98f00b204e9800998ecf8427e"],
            ["a":"0cc175b9c0f1b6a831c399e269772661"],
            ["abc": "900150983cd24fb0d6963f7d28e17f72"], 
            ["message digest": "f96b697d7cb7938d525a2f31aaf161d0"], 
            ["abcdefghijklmnopqrstuvwxyz":"c3fcd3d76192e4007dfb496cca67e13b"],
            ["ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789":"d174ab98d277d9f5a5611c2c9f419d9f"], 
            ["12345678901234567890123456789012345678901234567890123456789012345678901234567890":"57edf4a22be3c955ac49da2e2107b67a"] 
        ];
        
        md5examples.each() { it ->
                it.each() { key,value ->
                    println "digesting : "+key;
                def actualMD5 = MD5.digest(key);

                if (value==actualMD5) {
                    println " ${actualMD5} OK";
                } else {
                    println " NOT OK";
                    println " expected ${actualMD5} OK";
                    println " got      ${value} OK";
                }
            }
        }
        
    }


    static final String algorithm = "MD5";
    public static String digest(File f) throws IOException {
        InputStream is = new FileInputStream(f);
        String digest =  digest(is);
        is.close();
        return digest;
    }
    static MessageDigest getImplementation() {
        try {
            return MessageDigest.getInstance(algorithm);  
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae.getMessage());
        }
    }
    public static String digest(String s) {
        return digest(s.getBytes());
    }
    public static String digest(byte[] b) {
        MessageDigest md = getImplementation();
        return toHex(md.digest(b));
    }


    public static String digest(InputStream is) throws IOException {
        MessageDigest md = getImplementation();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1){
            md.update(buffer,0, read);
        }
        return toHex(md.digest());
    }
    private static String toHex(byte[] hash){
        StringBuffer buf = new StringBuffer(hash.length * 2);
        for (b in hash){
            int intVal = b & 0xff;
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
