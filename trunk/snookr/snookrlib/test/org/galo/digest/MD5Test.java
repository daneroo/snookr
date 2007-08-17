/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.digest;

import junit.framework.*;

public class MD5Test extends TestCase  {

    int implementation;
    String md5;
    byte b[];

    // actual test :
    public void testMD5() {
        IMD5Impl md = MD5.getImplementation(implementation);
        assertEquals(md5,md.digest(b));
        try {
            assertEquals(md5,md.digest(new java.io.ByteArrayInputStream(b)));
        } catch (java.io.IOException ioe) {
            fail("Threw Unexpected IOException");
        }
    }

    public MD5Test(int implementation,String md5,byte b[]) {
        super("testMD5"); // must match the actual test method
        this.implementation = implementation;
        this.md5 = md5;
        this.b = b;
    }

    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        addToSuiteForImplementation(suite,MD5.NATIVE);
        addToSuiteForImplementation(suite,MD5.OSTERMILLER);
        return suite;
    }

    private static void addToSuiteForImplementation(TestSuite suite,int impl) {
        suite.addTest(new MD5Test(impl,"d41d8cd98f00b204e9800998ecf8427e", new byte[]{}));
        suite.addTest(new MD5Test(impl,"0cc175b9c0f1b6a831c399e269772661", new byte[]{'a'}));
        suite.addTest(new MD5Test(impl,"900150983cd24fb0d6963f7d28e17f72", new byte[]{'a','b','c'}));
        suite.addTest(new MD5Test(impl,"f96b697d7cb7938d525a2f31aaf161d0", new byte[]{'m','e','s','s','a','g','e',' ','d','i','g','e','s','t'}));
        suite.addTest(new MD5Test(impl,"c3fcd3d76192e4007dfb496cca67e13b", new byte[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'}));
        suite.addTest(new MD5Test(impl,"d174ab98d277d9f5a5611c2c9f419d9f", new byte[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'}));
        suite.addTest(new MD5Test(impl,"57edf4a22be3c955ac49da2e2107b67a", new byte[]{'1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0','1','2','3','4','5','6','7','8','9','0'}));
    }




}

