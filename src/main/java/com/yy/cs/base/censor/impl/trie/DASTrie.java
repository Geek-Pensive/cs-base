package com.yy.cs.base.censor.impl.trie;

import java.util.*;

/**
 * Created by zhuhui@yy.com on 2014/12/21.
 */
public class DASTrie {

    int[] check, base, tail;
    int pos;
    ArrayList<Set<Integer>> childs;

    int hashChar(byte c) {
        return (c & 0xff) + 1;
    }
    byte deHashChar(int i) {
        return (byte)((i - 1) & 0xff);
    }

    int commonPrefix(byte[] str, int begin, int len, int[] b, int bbegin) {
        int i;
        for (i = 0; i < len && b[i + bbegin] != 0; ++i) {
            if (hashChar(str[i + begin]) != b[i + bbegin]) {
                break;
            }
        }
        if (b[i + bbegin] == 0) {
            return -1;
        }
        return i;
    }

    int prefixCmp(byte[] a, int begin, int len, int[] b, int bbegin) {
        int i;
        for (i = 0; b[i + bbegin] != 0; ++i) {
            if (i >= len) {
                return 1;
            }
            if (hashChar(a[i + begin]) != b[i + bbegin]) {
                return 1;
            }
        }
        return 0;
    }

    int x_check(byte[] ca, int bp) {
        int q = 1, i = 0, t = 0, ct;
        do {
            ct = hashChar(ca[i]) + q;
            if (ct < base.length && (base[ct] != 0 || ct == bp)) {
                do {
                    ++q;
                    if (q == bp) continue;
                    ct = hashChar(ca[i]) + q;
                } while (ct < base.length && (base[ct] != 0 || ct == bp));
                i = 0;
                t = 0;
                continue;
            }
            if (ct >= t) {
                t = ct;
            }
            ++i;
        } while (i < ca.length);
        if (t >= base.length) {
            extendBaseArrays(t);
        }
        return q;
    }
    void insert(byte[] str){
        int i, p = 1, t;
        for (i = 0; i < str.length; ++i) {
            t = base[p] + hashChar(str[i]);
            if (t >= base.length) {
                extendBaseArrays(t);
            }
            if (p == t) {
                byte[] pt = new byte[1];
                pt[0] = str[i];
                relocate(p, pt);
                --i;
                continue;
            }
            if (check[t] == p) {
                if (base[t] > 0) {
                    p = t;
                }
                else if (base[t] < 0) {
                    int cn;
                    cn = commonPrefix(str, i + 1, str.length - i - 1, tail, - base[t]);
                    if (cn == -1) {
                        return;
                    }
                    int ptn = base[t];
                    int ntn = straightInsert(str, i + 1, cn, t);
                    i += cn;
                    ptn -= cn;
                    byte[] ta = new byte[2];
                    ta[0] = str[i + 1];
                    ta[1] = deHashChar(tail[-ptn]);
                    relocate(ntn, ta);
                    addChild(ntn, base[ntn] + tail[-ptn]);
                    base[base[ntn] + tail[-ptn]] = ptn - 1;
                    p = ntn;
                    continue;
                }
            }
            else if (base[t] == 0 && check[t] == 0) {
                addChild(p, t);
                base[t] = -writeTail(str, i + 1, str.length - i - 1);
                return;
            }
            else {
                Set<Integer> org = childs.get(p);
                Set<Integer> obs = childs.get(check[t]);
                int si = 0;
                if (null != org) {
                    si = org.size();
                }
                if (si + 1 > obs.size() && check[p] != check[t]) {
                    relocate(check[t], null);
                }
                else {
                    byte[] tr = new byte[1];
                    tr[0] = str[i];
                    relocate(p, tr);
                }
                --i;
                continue;
            }
        }
    }
    int straightInsert(byte[] str, int begin, int len, int start) {
        int i, p = start, t = start;
        if (len > 0) {
            byte[] ca = new byte[1];
            ca[0] = str[begin];
            base[p] = x_check(ca, p);
        }
        for (i = 0; i < len; ++i) {
            t = base[p] + hashChar(str[i]);
            if (t >= base.length) {
                extendBaseArrays(t);
            }
            addChild(p, t);
            if (i < len - 1) {
                byte[] xb = new byte[1];
                xb[0] = str[i + 1];
                base[t] = x_check(xb, t);
                p = t;
            }
        }
        return t;
    }
    int[] extendIntArray(int[] ar, int toLen) {
        int tn = ar.length;
        while (toLen >= tn) {
            tn *= 2;
        }
        if (tn > ar.length) {
            int[] tmp = new int[tn];
            System.arraycopy(ar, 0, tmp, 0, ar.length);
            Arrays.fill(tmp, ar.length, tn, 0);
            ar = tmp;
        }
        return ar;
    }
    void extendSetArray(ArrayList<Set<Integer>> ar, int toLen) {
        int tn = ar.size();
        while (toLen >= tn) {
            tn *= 2;
        }
        if (tn > ar.size()) {
            ar.ensureCapacity(tn);
            int i;
            for (i = ar.size(); i < tn; ++i) {
                ar.add(i, null);
            }
        }
    }
    void extendBaseArrays(int toLen) {
        check = extendIntArray(check, toLen);
        base = extendIntArray(base, toLen);
        extendSetArray(childs, toLen);
    }
    void initArrays() {
        clear();
        check = new int[4];
        base = new int[4];
        tail = new int[4];
        childs = new ArrayList<>();
        int i;
        for (i = 0; i < 4; ++i) {
            childs.add(null);
        }
        pos = 1;
    }
    int writeTail(byte[] str, int begin, int sn) {
        int len = pos + sn + 1;
        int i;
        if (len >= tail.length) {
            tail = extendIntArray(tail, len);
        }
        int r = pos;
        for (i = 0; i < sn; ++i, ++pos) {
            tail[pos] = hashChar(str[i + begin]);
        }
        tail[pos] = 0;
        pos = len;
        return r;
    }
    void addChild(int parent, int child) {
        check[child] = parent;
        if (null != childs.get(parent)) {
            childs.set(parent, new HashSet<Integer>());
        }
        childs.get(parent).add(child);
    }
    int relocate(int p, byte[] t) {
        int cnt = t.length;
        if (null != childs.get(p)) {
            childs.set(p, new HashSet<Integer>());
        }
        Set<Integer> ss = childs.get(p);
        Set<Integer> rs = new HashSet<>();
        cnt += ss.size();
        byte[] tc = new byte[cnt];
        System.arraycopy(t, 0, tc, 0, t.length);
        int i = t.length;
        Iterator<Integer> it = ss.iterator();
        while (it.hasNext()) {
            Integer n = it.next();
            tc[i++] = deHashChar(n - base[p]);
        }
        int np = x_check(tc, p);
        int mi = np - base[p], im;
        it = ss.iterator();
        while (it.hasNext()) {
            im = it.next();
            rs.add(im + mi);
            check[im + mi] = check[im];
            base[im + mi] = base[im];
            childs.set(im + mi, childs.get(im));
            changeRoot(childs.get(im + mi), im + mi);
            childs.set(im, null);
            base[im] = 0;
            check[im] = 0;
        }
        childs.set(p, rs);
        base[p] = np;
        return p;
    }
    void constructionDone() {
        childs = null;
        tail = shrinkIntArray(tail, pos);
        int i;
        for (i = base.length - 1; i >= 0; --i) {
            if (base[i] != 0) {
                break;
            }
        }
        base = shrinkIntArray(base, i + 1);
        check = shrinkIntArray(check, i + 1);
    }
    int[] shrinkIntArray(int[] p, int tl) {
        int[] res = new int[tl];
        System.arraycopy(p, 0, res, 0, tl);
        return res;
    }
    void changeRoot(Set<Integer> s, int np) {
        if (null != s) {
            Iterator<Integer> it = s.iterator();
            while (it.hasNext()) {
                Integer t = it.next();
                check[t] = np;
            }
        }
    }
    public DASTrie() {
        clear();
    }
    public int initStrings(List<byte[]> words) {
        int has_error = 0;
        initArrays();
        Comparator<byte[]> cmp = new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                return o1.length - o2.length;
            }
        };
        Collections.sort(words, cmp);
        Iterator<byte[]> it = words.iterator();
        while (it.hasNext()) {
            byte[] str = it.next();
            insert(str);
        }
        constructionDone();
        return has_error;
    }
    public boolean hasWord(byte[] text) {
        int i, j, p = 1, t;
        int len = text.length;
        for (j = 0; j < len; ++j) {
            p = 1;
            for (i = j; i < len; ++i) {
                t = base[p] + hashChar(text[i]);
                if (t < base.length) {
                    if (check[t] == p) {
                        if (base[t] < 0) {
                            if (prefixCmp(text, i + 1, len - i - 1, tail, - base[t]) == 0) {
                                return true;
                            }
                            break;
                        }
                        p = t;
                        continue;
                    }
                }
                break;
            }
        }
        return false;
    }
    public void clear() {
        check = null;
        base = null;
        tail = null;
        childs = null;
        pos = -1;
    }
}
