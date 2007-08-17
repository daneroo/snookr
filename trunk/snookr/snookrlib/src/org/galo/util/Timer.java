/* Copyright Notice
 * This file contains proprietary information of Sologlobe Logistique Inc.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 1997-2005
 */
package org.galo.util;

public class Timer { //measures things in seconds.
    private long startTime;
    public Timer() { restart(); }
    public void restart() { 
	startTime = System.currentTimeMillis(); 
    }
    public float diff() {
	return (System.currentTimeMillis()-startTime)/1000f;  
    }
    public float rate(int n){ 
	return n/diff(); 
    }
}
