package edu.buffalo.cse.ir.wikiindexer.indexer;

public class GenericIndexEntry {

    private int key;
    private int value;
    private int frequency = -1;
    boolean inverted = false;

    private int skipGap = -1;

    public int getSkipGap() {
        return skipGap;
    }

    public void setSkipGap(int skipGap) {
        this.skipGap = skipGap;
    }

    private int skipValue = -1;

    public int getSkipValue() {
        return skipValue;
    }

    public void setSkipValue(int skipValue) {
        this.skipValue = skipValue;
    }

    public GenericIndexEntry(int k, int v, int f, boolean inv) {
        key = k;
        value = v;
        inverted = inv;
        if(inv) { frequency = f; }
    }

    public GenericIndexEntry(int k, String vs, int f, boolean inv) {
        key = k;
        value = vs.hashCode();
        inverted = inv;
        if(inv) { frequency = f; }
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return  value;
    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isInverted() {
        return inverted;
    }

}