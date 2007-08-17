/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.digest;

import java.io.*;

class MD5OsterMillerImpl implements IMD5Impl {

    public String digest(InputStream in) throws IOException {
        return com.Ostermiller.util.MD5.getHashString(in);
    }
    public String digest(byte b[]) {
        return com.Ostermiller.util.MD5.getHashString(b);
    }

}
