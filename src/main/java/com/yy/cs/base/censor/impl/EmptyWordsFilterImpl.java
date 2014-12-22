package com.yy.cs.base.censor.impl;


import com.yy.cs.base.censor.WordsFilter;

/**
 * Created by zhuhui@yy.com on 2014/10/28.
 */
public class EmptyWordsFilterImpl implements WordsFilter {
    @Override
    public String filter(String word) {
        return word;
    }
}
