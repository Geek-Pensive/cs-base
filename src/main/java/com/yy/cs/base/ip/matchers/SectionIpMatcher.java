package com.yy.cs.base.ip.matchers;

import java.util.regex.Pattern;

import com.yy.cs.base.ip.IpUtil;

public class SectionIpMatcher extends RangeIpMatcher {
    private static Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}/\\d{1,2}");

    @Override
    protected IpMatcher doBuild(String ipMatch) {
        String[] s = ipMatch.split("/");
        String ip = s[0];
        int section = Integer.parseInt(s[1]);
        if (section > 32 || section < 0) {
            return null;
        }
        long num = IpUtil.atoi(ip);
        int osection = 32 - section;
        long start = num;

        for (int j = 0; j < osection; j++) {
            start = start & (0xffffffff ^ (1 << j));
        }
        long end = start;
        for (int j = 0; j < osection; j++) {
            end = end | ((long) 1 << j);
        }
        this.start = start;
        this.end = end;
        return this;
    }

    @Override
    public Pattern getMatchPatter() {
        return pattern;
    }

    @Override
    protected IpMatcher newInstance() {
        return new SectionIpMatcher();
    }

    @Override
    public String toString() {
        return "SectionIpMatcher [start=" + IpUtil.numberHex(start) + ", end=" + IpUtil.numberHex(end) + ", ipMatch="
                + ipMatch + "]";
    }

}
