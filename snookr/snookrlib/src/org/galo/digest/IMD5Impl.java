/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */

package org.galo.digest;

import java.io.*;

public interface IMD5Impl {
    public String digest(InputStream in) throws IOException;
    public String digest(byte b[]);
}
