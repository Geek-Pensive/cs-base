package com.yy.cs.base.ip;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yy.cs.base.ip.matchers.IpMatcher;

public class IpMatcherManager {

    private List<IpMatcher> matchers;
    private Map<String, SoftReference<Boolean>> matchCache = new ConcurrentHashMap<>();

    public IpMatcherManager(List<IpMatcher> matchers) {
        this.matchers = matchers;
    }

    public boolean isMatch(String ip) {
        if (null == matchers || matchers.size() < 1) {
            return false;
        }
        if (matchCache.get(ip) !=null) {
            return matchCache.get(ip).get();
        }
        boolean m = false;
        try {
            for (IpMatcher matcher : matchers) {
                if (matcher.isMatch(ip)) {
                    m = true;
                    return true;
                }
            }
        } finally {
            matchCache.put(ip, new SoftReference<Boolean>(m));
        }
        return m;
    }

    public List<IpMatcher> getMatchers() {
        return matchers;
    }
}
