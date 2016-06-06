package com.yy.cs.base.ip.matchers;

import java.util.regex.Pattern;

import com.yy.cs.base.ip.IpUtil;

public class CommonIpMatcher extends AbstractIpMatcher {

    private long ip;
    private static Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    @Override
    public boolean isMatch(String ip) {
        return this.ip == IpUtil.atoi(ip);
    }

    @Override
    protected IpMatcher doBuild(String ipMatch) {
        this.ip = IpUtil.atoi(ipMatch);
        return this;
    }

    @Override
    public Pattern getMatchPatter() {
        return pattern;
    }

    @Override
    protected IpMatcher newInstance() {
        return new CommonIpMatcher();
    }

    @Override
    public String toString() {
        return "CommonIpMatcher [ip=" + IpUtil.numberHex(ip) + ", ipMatch=" + ipMatch + "]";
    }

}
