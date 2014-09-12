package com.yy.cs.base.censor;

/**
 * Author zhuhui@yy.com
 */
public class CensorNode {
    private boolean over = false;
    private CensorNode[] ar = new CensorNode[256];
    public CensorNode next(byte b) {
        return ar[0xff & b];
    }
    public boolean isOver() {
        return over;
    }
    protected CensorNode addNode(byte b) {
        CensorNode n = new CensorNode();
        ar[0xff & b] = n;
        return n;
    }
    protected void setOver() {
        over = true;
    }
}
