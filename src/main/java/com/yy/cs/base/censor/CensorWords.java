package com.yy.cs.base.censor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author zhuhui@yy.com
 */
public class CensorWords {
    private CensorNode root = null;
    private CensorWords() {}
    public static CensorWords build(List<String> words) {
        Comparator<String> cmp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };
        Collections.sort(words, cmp);
        CensorWords cw = new CensorWords();
        CensorNode root = new CensorNode();
        cw.root = root;
        for (String str: words) {
            cw.addCensor(str);
        }
        return cw;
    }
    public Boolean isCensor(String str) {
        byte[] b = str.getBytes();
        int i = 0, back = 0, k, l = b.length;
        CensorNode n = null;
        for (i = 0; i < l; ++i) {
            n = root;
            for (k = i; k < l; ++k) {
                n = n.next(b[k]);
                if (n == null) {
                    break;
                }
                else if (n.isOver()) {
                    return true;
                }
            }
        }
        return false;
    }
    private int addCensor(String c) {
        byte[] b = c.getBytes();
        int i, len = b.length;
        CensorNode n = root, tmp;
        for (i = 0; i < len; ++i) {
            tmp = n.next(b[i]);
            if (null == tmp) {
                tmp  = n.addNode(b[i]);
            }
            if (tmp.isOver()) return 0;
            n = tmp;
        }
        n.setOver();
        return 1;
    }
}
