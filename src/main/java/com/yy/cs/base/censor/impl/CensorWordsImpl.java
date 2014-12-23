package com.yy.cs.base.censor.impl;


import com.yy.cs.base.censor.CensorWords;
import com.yy.cs.base.censor.WordsFilter;
import com.yy.cs.base.censor.impl.trie.DASTrie;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhuhui@yy.com on 2014/8/21.
 */
public class CensorWordsImpl implements CensorWords {
    private DASTrie trie = null;
    private WordsFilter wf;
    private CensorWordsImpl() {}
    public static CensorWords build(List<String> words) {
        return build(words, new StandardWordsFilterImpl());
    }
    public static CensorWords build(List<String> words, WordsFilter wf) {
        Comparator<String> cmp = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        };
        Collections.sort(words, cmp);
        CensorWordsImpl cw = new CensorWordsImpl();
        cw.trie = new DASTrie();
        cw.wf = wf;
        List<byte[]> wli = new LinkedList<byte[]>();
        String strf;
        for (String w: words) {
            strf = wf.filter(w);
            wli.add(strf.getBytes());
        }
        cw.trie.initStrings(wli);
        return cw;
    }
    @Override
    public Boolean isCensor(String str) {
        String strf = wf.filter(str);
        return trie.hasWord(strf.getBytes());
    }
}
