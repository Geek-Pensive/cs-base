package com.yy.cs.base.ip.matchers;

import java.util.regex.Pattern;

public interface IpMatcher {

    IpMatcher build(String ipMatch);

    boolean isMatch(String ip);

    Pattern getMatchPatter();

    String getIpMatch();

}
