/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imetrical.model;

/**
 *
 * @author daniel
 */
public class ExpandedSignal {

    public int intervalLengthSecs = 1;    // 10
    public long offsetMS;
    public double values[];

    public ExpandedSignal(int size) {
        values = new double[size];
    }

    // same size: do NOT copy data.
    public ExpandedSignal(ExpandedSignal other) {
        intervalLengthSecs = other.intervalLengthSecs;
        offsetMS = other.offsetMS;
        values = new double[other.values.length];
    }

    // copy data
    public ExpandedSignal copy() {
        ExpandedSignal newES = new ExpandedSignal(this);
        for (int i = 0; i < values.length; i++) {
            newES.values[i] = values[i];
        }
        return newES;
    }

    public double min() {
        return values[minIndex()];
    }

    public int minIndex() {
        int minIndex = 0;
        double minV = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] < minV) {
                minV = values[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    public double max() {
        return values[maxIndex()];
    }

    public int maxIndex() {
        int maxIndex = 0;
        double maxV = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > maxV) {
                maxV = values[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // min..max -> 0..1
    public void normalize() {
        normalize(1.0);
    }
    public void normalize(double newMax) {
        double mn=min();
        double mx=max();
        double factor=0;
        if ((mx-mn)!=0) factor=newMax/(mx-mn);
        System.out.println(String.format("norm %8.2f %8.2f %8.2f",mn,mx,factor));
        multiply(factor);
    }
    public void multiply(double c) {
        for (int i = 0; i < values.length; i++) {
            values[i] *= c;
        }
    }
    
    public void add(double c) {
        for (int i = 0; i < values.length; i++) {
            values[i] += c;
        }
    }

    public double avg() { // onZeroes ?
        double sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }

    // return average power*time
    public double kWh() {
        double avgPower = avg();
        int nSecs = values.length * intervalLengthSecs;
        return avgPower * nSecs / 3600000;
    }
}
