package com.yy.cs.base.censor.impl;

import com.yy.cs.base.chinese.ZHConverter;
import com.yy.cs.base.censor.WordsFilter;

/**
 * Created by zhuhui@yy.com on 2014/10/28.
 */
public class StandardWordsFilterImpl implements WordsFilter {
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
    @Override
    public String filter(String word) {
        word = converter.convert(word).toLowerCase();
        StringBuilder sb = new StringBuilder();
        int len = word.length();
        char c;
        for (int i = 0; i < len; ++i) {
            c = word.charAt(i);
            if (c == ' ' || c == '　') {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
