package com.yy.cs.base.ip.matchers;

import java.util.regex.Pattern;

import com.yy.cs.base.ip.IpUtil;

public class RangeIpMatcher extends AbstractIpMatcher {

    private static Pattern pattern = Pattern
            .compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}~\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    protected long start = 0;
    protected long end = 0;

    @Override
    protected IpMatcher doBuild(String ipMatch) {
        String[] t = ipMatch.split("~");
        this.start = IpUtil.atoi(t[0]);
        this.end = IpUtil.atoi(t[1]);
        return this;
    }

    @Override
    public boolean isMatch(String ip) {
        long tmp = IpUtil.atoi(ip);
        return start <= tmp && tmp <= end;
    }

    @Override
    public Pattern getMatchPatter() {
        return pattern;
    }

    @Override
    protected IpMatcher newInstance() {
        return new RangeIpMatcher();
    }

    @Override
    public String toString() {
        return "RangeIpMatcher [start=" + IpUtil.numberHex(start) + ", end=" + IpUtil.numberHex(end) + ", ipMatch=" + ipMatch + "]";
    }

}
