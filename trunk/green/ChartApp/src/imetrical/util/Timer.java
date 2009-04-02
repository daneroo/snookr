/*
 * Created on May 24, 2006
 *
 * TODO Add a real class description
 * example use
 * Timer tt = new Timer();
 * String msg = ""+count+" processed at rate "+tt.rate(count)+" xx/s or "+tt.diff()/count+"s/xx)";
 */
package imetrical.util;

public class Timer { //measures things in seconds.
    private long startTime;

    public Timer() {
        restart();
    }

    public void restart() {
        startTime = System.currentTimeMillis();
    }

    public float diff() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public float rate(int n) {
        return n / diff();
    }
}